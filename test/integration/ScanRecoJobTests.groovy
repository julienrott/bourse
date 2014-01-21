import static org.junit.Assert.*;
import fr.bourse.*;
import grails.test.mixin.TestFor;

class ScanRecoJobTests {
	void testSomething() {
		/*def source = new Source(nom: "nom", url: "url").save()
		
		def reco = new Reco(title : title, dateReco: new Date(), source: source)
		
		if (!reco.save()) {
			reco.errors.each {
				println it
			} 
		}*/
		
		def title = "Duc: chiffre d'affaires trimestriel de 50,43 (+11,2%)"
				
		def recos = Reco.list()
		assert recos.size() == 3
		
		def reco = Reco.findByTitle(title)
		if(reco == null) {
			println "reco null"
		}
		else {
			println "reco not null $reco"
		}
		println reco
		assert reco.title == title
		
		Reco reco2 = Reco.findByTitle("s")
		assert reco2 == null
	}
}
