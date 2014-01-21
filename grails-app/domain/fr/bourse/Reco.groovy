package fr.bourse

class Reco {
	
	static searchable = {
		//root true
		//only = ['dateReco', 'title', 'url', 'content', 'source']
		only = ['dateReco', 'title', 'url', 'content']
		//title index: "analyzed"
		//content index: "analyzed"
		//source reference: true
	}
	
	Date dateReco
	String title
	String url = ""
	String content = ""
	
	static belongsTo = [source: Source]

    static constraints = {
		//content(nullable: true)
    }
	
	static mapping = {
		content type: 'text'
		sort dateReco: 'desc'
	}
}
