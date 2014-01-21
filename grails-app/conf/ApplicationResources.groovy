modules = {
    application {
        resource url:'js/application.js'
		resource url:'js/remoteNonStopPageScroll.js', plugin: 'remote-pagination'
		resource url:'js/jquery.qtip.min.js'
    }
	
	applicationCSS {
        resource url:'css/jquery.qtip.min.css'
	}
}