import fr.bourse.*

import groovyx.net.http.HTTPBuilder

class ScanRecoJob {
	def recoService
	
	static triggers = {
		simple name: 'mySimpleTrigger',
		startDelay: 30000,
		repeatInterval: grails.util.Environment.current == grails.util.Environment.DEVELOPMENT ? 30 * 1000 : 5 * 60 * 1000
	}

	def group = "MyGroup"

	def execute(){
		executeLaTribune()
		executeYahoo()
		
	}
	
	private def executeYahoo() {
		def url = "http://fr.finance.yahoo.com/actualites/categorie-actions/"
		
		def source = Source.findByNom("yahoo")
		if (source == null) {
			source = new Source(nom: "yahoo", url: url).save()
		}
		
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		def mediatopstorytemp = html."**".find{it.@id == "mediatopstory_container"}
		mediatopstorytemp."**".findAll{ it.@class.toString() == "txt" }.eachWithIndex{ div, idiv ->
			cacheYahooDiv(div, idiv, source)
		}
	}
	
	private cacheYahooDiv(def div, int idiv, Source source) {
		def ago  = div."**".find{it.name() == "CITE"}.text()
		def h = ago.indexOf("heure")
		def heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		def min = ago.indexOf("minute")
		def minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		def now = Calendar.getInstance()
		now.add(Calendar.HOUR_OF_DAY, -heures)
		now.add(Calendar.MINUTE, -minutes)
		
		div."**".find{it.name() == "A"}.eachWithIndex{ it, i ->
			if (grails.util.Environment.current == grails.util.Environment.DEVELOPMENT && idiv<3) {
				cacheYahooA(it, source, now)
			}
			if (grails.util.Environment.current == grails.util.Environment.PRODUCTION) {
				cacheYahooA(it, source, now)
			}
		}
	}

	private cacheYahooA(def a, Source source, Calendar dateReco) {
		def title = a.text().trim()
		def reco = Reco.findByTitle(title)
		if (reco == null) {
			reco = new Reco()
			reco.source = source
			reco.title = title
			reco.dateReco = dateReco.getTime()
			reco.url = "http://fr.finance.yahoo.com${a.@href.toString()}"
			if(!reco.save()) {
				reco.errors.each{
					log.error it
				}
			}
		}
		getContentYahoo(reco.id)
	}
	
	private def getContentYahoo(idreco) {
		def reco = Reco.get(idreco)
		if (reco.content.size() > 0) return
		
		def http = new HTTPBuilder(reco.url)
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		def mediaarticlebody = html."**".find{it.@id == "mediaarticlebody"}
		def content = ""
		mediaarticlebody."**".findAll{ it.name() == "P"}.each{
			content += it.text()
		}
		reco.content = content
	}

	private def executeLaTribune() {
		def url = "http://bourse.latribune.fr/actualites/recommandations/"
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		html."**".findAll { it.@class.toString().contains("simple_list") }.each{ ul ->
			ul."**".findAll { it.name() == "LI" }.eachWithIndex{ li, i ->
				if (grails.util.Environment.current == grails.util.Environment.DEVELOPMENT && /*i>0 &&*/ i<3) {
					cache(li)
				}
				if (grails.util.Environment.current == grails.util.Environment.PRODUCTION) {
					cache(li)
				}
			}
		}
	}
	
	private def cache(def li) {
		def first = li.H3.text().size() > 0
		def title = first ? li.H3.text().trim() : li.A.text().trim()
		def query = Reco.where{ title == title}
		Reco reco = query.find()
		
		if(reco == null) {
			reco = new Reco()
			
			def now = Calendar.getInstance()
			def date
			
			if (first) {
				date = new GregorianCalendar(now.get(Calendar.YEAR),
						li.STRONG.text()[3..4].toInteger()-1,
						li.STRONG.text()[0..1].toInteger(),
						li.STRONG.text()[13..14].toInteger(),
						li.STRONG.text()[16..17].toInteger())
				
				reco.title = li.H3.text().trim()
				reco.content = li.P.text().encodeAsHTML()
			}
			else {
				date = new GregorianCalendar(now.get(Calendar.YEAR),
						 li.STRONG.text().length() > 8 ? li.STRONG.text()[3..4].toInteger()-1 : now.get(Calendar.MONTH),
						 li.STRONG.text().length() > 8 ? li.STRONG.text()[0..1].toInteger() : now.get(Calendar.DAY_OF_MONTH),
						 li.STRONG.text()[li.STRONG.text().length() > 8 ? 6..7 : 0..1].toInteger(),
						 li.STRONG.text()[li.STRONG.text().length() > 8 ? 9..10 : 3..4].toInteger())

				reco.title = li.A.text().trim()
				reco.url = li.A.@href
			}
			
			reco.dateReco = date.getTime()
			reco.source = Source.findByNom("latribune")
			if(!reco.save()) {
				reco.errors.each{
					log.error it
				}
			}
			if (!first) {
				getContent(reco.id)
			}
		}
		else {
			if(!first && reco.url.equals("")) {
				reco.url = li.A.@href.text()
				if(!reco.save()) {
					reco.errors.each{
						log.error it
					}
				}
			}
		}
	}
	
	private def getContent(idreco) {
		def reco = Reco.get(idreco)
		def http = new HTTPBuilder(reco.url)
		def html = http.get([:])
		def content = html."**".findAll { it.@class.toString().contains("simple_list") }.each{ ul ->
			def li = ul."**".findAll { it.name() == "LI" }.first()
			def rec = Reco.get("$idreco" as Long)
			rec.content = li.P.text().encodeAsHTML()
			if(!rec.save()) {
				rec.errors.each{
					log.error it
				}
			}
		}
	}
	
}
