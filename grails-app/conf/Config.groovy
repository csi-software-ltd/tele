grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// The default codec used to encode data with ${}
grails.views.default.codec = "html" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.views.javascript.library = "prototype"
// enable Sitemesh preprocessing of GSP pages
//grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
//grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
//grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
//grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
//grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
//grails.hibernate.cache.queries = false
grails.config.locations = [ "classpath:${appName}-config.properties"]

/*
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
        // TODO: grails.serverURL = "http://www.changeme.com"
    }
}
*/
// log4j configuration
import org.apache.log4j.DailyRollingFileAppender
// log4j configuration
log4j = {
  appenders {
    appender new DailyRollingFileAppender(
        name:"file", 
        datePattern:"'.'yyyy-MM-dd", 
        layout:pattern(conversionPattern:'%d [%p] {%c{2}} %m%n'),    
        file:"${logger.file}"); 
    appender new DailyRollingFileAppender(
        name:"stacktraceLog", 
        datePattern:"'.'yyyy-MM-dd", 
        layout:pattern(conversionPattern:'%d [%p] {%c{2}} %m%n'),
        file:"${logger.stack}"); 
/*        
    appender new org.apache.log4j.jdbc.JDBCAppender(
        name:"DB",   
        URL:"${dataSource.url}",
        driver:"com.mysql.jdbc.Driver",
        user:"${dataSource.username}",
        password:"${dataSource.password}",
        sql:"INSERT INTO log (inputdate,level,message) VALUES('%d{yyyy.MM.dd HH:mm:ss}','%p','%m')"
    );*/
    console name:"logger", layout:pattern(conversionPattern: "%d [%t] %-5p %c(%l)  - %m%n")
  }
  root {  
      error 'stdout', 'file'//, 'DB'
      additivity = true
    }  
   error   'org.codehaus.groovy.grails.web.servlet',  //  controllers
      'org.codehaus.groovy.grails.web.pages', //  GSP
      'org.hibernate',
      'grails.app.services.org.grails.plugin.resource',
      'grails.app.taglib.org.grails.plugin.resource',
      'grails.app.resourceMappers.org.grails.plugin.resource',
      'grails.app.services.NavigationService',
      'grails.app.services.grails.plugin.rendering.document',
      'grails.app.resourceMappers.org.grails.plugin.cachedresources',
      'grails.app.resourceMappers.org.grails.plugin.zippedresources'
    error stacktraceLog:"StackTrace"
    warn 'org.mortbay.log'
    error 'grails.plugin'
    debug 'grails.app'
    //  trace "${logger.hibernatelevel}"  
    debug logger:"com.linkedin.grails" 
}
