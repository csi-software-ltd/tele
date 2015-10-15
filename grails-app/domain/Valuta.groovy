import org.codehaus.groovy.grails.commons.ConfigurationHolder
class Valuta {    
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  Integer regorder
  String code
  String name  
  String symbol    

  String toString() {"${this.code}" }  
  
}
