package fr.bourse

class RecoService {

    def serviceMethod() {

    }
	
	def save(reco) {
		Reco.withNewSession {
			if(!reco.save(flush: true)) {
				reco.errors.each{
					log.error it
				}
			}
		}
	}
	
	def updateContent(id, content) {
		Reco.withNewSession {
			def reco = Reco.get(id as Long)
			reco.content = content as String
			reco.save()
		}
	}
}
