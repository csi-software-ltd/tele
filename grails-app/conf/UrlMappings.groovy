class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(controller : "client", action:"index")
		"/admin"(controller : "admin", action:"index")
		"/robots.txt"(controller:'index',action:'robots')
		"404"(controller : "error", action:'page_404')
		"500"(controller : "error", action:'page_500') 
	}
}
