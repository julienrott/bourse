package fr.bourse

class BourseController {
	
	def recoService

    def index() {
		log.debug params
		//[recos:Reco.list(sort: "dateReco", order: "desc")]
        params.max = Math.min(params.max ? params.int('max') : 20, 100)
		if (params.search) {
			log.debug "searching $params.search"
			def recos = Reco.search("$params.search", [sort: "dateReco", order: "desc"])
			log.debug "$recos.total recos found"
			[recos: recos.searchResults, total: recos.total]
		}
		else {
			[recos: Reco.list(params), total: Reco.count()]
		}
	}

    def filter = {
		log.debug params
        Thread.sleep(1000)
        render(template: 'filter', model: [recos: Reco.list(params), total: Reco.count()])
    }
}
