import static org.junit.Assert.*;
import fr.bourse.*;
import grails.test.mixin.TestFor;
import groovyx.net.http.ContentType;
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

class ScanRecoJobUnitTests {
	
	void testReco() {
		def url = "http://fr.finance.yahoo.com/actualites/erix-produit-dindexation-énergies-renouvelables-113103373.html"
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		def mediaarticlebody = html."**".find{it.@id == "mediaarticlebody"}
		def content = ""
		mediaarticlebody."**".findAll{ it.name() == "P"}.each{
			content += it.text()
		}
		println content
	}
	
	void testTimeParse() {
		def ago  = "Tradingsat.com - il y a 9 minutes"
		def h = ago.indexOf("heure")
		def heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 0
		def min = ago.indexOf("minute")
		def minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 9
		
		ago = "Tradingsat.com - il y a 29 minutes"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 0
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 29
		
		ago = "Tradingsat.com - il y a 1 heure"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 1
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 0
		
		ago = "Tradingsat.com - il y a 12 heures"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 12
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 0
		
		ago = "Cercle Finance - il y a 1 heure 4 minutes"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 1
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 4
		
		ago = "Cercle Finance - il y a 1 heure 29 minutes"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 1
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 29
		
		ago = "Cercle Finance - il y a 11 heures 4 minutes"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 11
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 4
		
		ago = "Cercle Finance - il y a 11 heures 29 minutes"
		h = ago.indexOf("heure")
		heures = h < 0 ? 0 : ago[h-3..h-1].toInteger()
		assert heures == 11
		min = ago.indexOf("minute")
		minutes = min < 0 ? 0 : ago[min-3..min-1].toInteger()
		assert minutes == 29
		
	}
	
	void testTitle() {
		def url = "http://fr.finance.yahoo.com/actualites/categorie-actions/"
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		println html
		def mediatopstory = html."**".find{it.@id == "mediatopstory_container"}
		mediatopstory."**".findAll{ it.@class.toString() == "txt" }.eachWithIndex{ div, idiv ->
			div."**".find{it.name() == "A"}.eachWithIndex{ it, i ->
				println "${idiv + 1} : $it : ${it.@href}"
			}
		}
		//assert html.'//div[@id="mediatopstorytemp"]//ul/li[1]//div[@class="txt"]/span[1]/a'.size() == 1
	}
	
	void testContent() {
		def url = "http://fr.finance.yahoo.com/actualites/categorie-actions/"
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		def mediatopstory = html."**".find{it.@id == "mediatopstory_container"}
		mediatopstory."**".find{ it.@class.toString() == "txt" }.eachWithIndex{ div, idiv ->
			div."**".find{it.name() == "A"}.eachWithIndex{ it, i ->
				println "${idiv + 1} : $it : ${it.@href}"
				getContent(it.@href.toString())
			}
		}
		//assert html.'//div[@id="mediatopstorytemp"]//ul/li[1]//div[@class="txt"]/span[1]/a'.size() == 1
	}
	
	void getContent(String url) {
		def http = new HTTPBuilder("http://fr.finance.yahoo.com$url")
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		def mediaarticlebody = html."**".find{it.@id == "mediaarticlebody"}
		mediaarticlebody."**".findAll{ it.name() == "P"}.each{
			println "$it"
		}
	}
	
	void testSomething() {
		def yqlurl = "http://query.yahooapis.com/v1/public/yql?q="
		def yqlquery = "select * from html where url=\"http://fr.finance.yahoo.com/actualites/categorie-actions/\" and xpath='//div[@id=\"mediatopstorytemp\"]//ul/li[1]//div[@class=\"txt\"]/span[1]/a'"
		def url = yqlurl + URLEncoder.encode(yqlquery, "UTF-8")
		//println "url : $url"
		//println "yql console : http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Ffr.finance.yahoo.com%2Factualites%2Fcategorie-actions%2F%22%20and%20xpath%3D'%2F%2Fdiv%5B%40id%3D%22mediatopstorytemp%22%5D%2F%2Ful%2Fli%5B1%5D%2F%2Fdiv%5B%40class%3D%22txt%22%5D%2Fspan%5B1%5D%2Fa'&diagnostics=true"
		//println "yql console : http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Ffr.finance.yahoo.com%2Factualites%2Fcategorie-actions%2F%22%20and%20xpath%3D'%2F%2Fdiv%5B%40id%3D%22mediatopstorytemp%22%5D%2F%2Ful%2Fli%5B1%5D%2F%2Fdiv%5B%40class%3D%22txt%22%5D%2Fspan%5B1%5D%2Fa'&format=json&callback=cbfunc"
		//assert url == "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Ffr.finance.yahoo.com%2Factualites%2Fcategorie-actions%2F%22%20and%20xpath%3D'%2F%2Fdiv%5B%40id%3D%22mediatopstorytemp%22%5D%2F%2Ful%2Fli%5B1%5D%2F%2Fdiv%5B%40class%3D%22txt%22%5D%2Fspan%5B1%5D%2Fa'&diagnostics=true"
		//assert url == "http://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20html%20where%20url%3D%22http%3A%2F%2Ffr.finance.yahoo.com%2Factualites%2Fcategorie-actions%2F%22%20and%20xpath%3D'%2F%2Fdiv%5B%40id%3D%22mediatopstorytemp%22%5D%2F%2Ful%2Fli%5B1%5D%2F%2Fdiv%5B%40class%3D%22txt%22%5D%2Fspan%5B1%5D%2Fa'&format=json&callback=cbfunc"
		def http = new HTTPBuilder(url)
		def html = http.get([:])
		assert html instanceof groovy.util.slurpersupport.GPathResult
		//assert html."//".results.a.size() == 1
		html."//".results.a.each {
			println "${it.name()} - ${it} : ${it.@href}"
		}
		/*println html
		println html.QUERY
		println html.QUERY.RESULTS.A.size()
		def xml = new XmlSlurper().parseText(url.toURL().text)
		println xml.query.results.a.size()
		println xml
		println url.toURL().text*/
	}
	
	void testAuthenticatedSite() {
		def authSite = new HTTPBuilder( 'http://www.hebdobourseplus.com' )
		//authSite.auth.basic 'myUserName', 'myPassword'
		//secrets = authSite.get( path:'secret-info.txt' )
		authSite.get(path: "index-hebdo-bourse-plus.html"){ res, reader ->
			res.headers.each { h ->
				//println " ${h.name} : ${h.value}"
			}
			//println "***************************************"
			//println reader
		}
		
		authSite.post(path: 'hebdo-bourse-plus-compte_connect.html', body: [email: "julien.rott@gmail.com", passe: "1781Julien"], requestContentType: URLENC) { resp ->
			println "***************************************"
			println "response status: ${resp.statusLine}"
			println "***************************************"
			assert resp.statusLine.statusCode == 200
		}
		
		def html = authSite.get(path: "index-hebdo-bourse-plus.html")
		assert html instanceof groovy.util.slurpersupport.GPathResult
		html."**".findAll{it.name() == "A" && it == "Valeurs de la semaine"}.each {
			println "${it.name()} - ${it} : ${it.@href}"
			def r = authSite.get(path: it.@href)
			println '<img src="http://www.hebdobourseplus.com/photos-hebdo-bourse/normal/atos--axa--bic--biomerieux--bnp-paribas-act-a-et-bollore--131205121015--bourse.jpg"/>'
			def td = r."**".find{it.name() == "TD" && it.@width == "660"}
			println td
		}
	}
}
