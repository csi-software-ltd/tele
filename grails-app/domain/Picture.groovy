class Picture {  
  
  static constraints = {    
  }
  static mapping = {
    version false
  }
  
  String filename
  byte[] filedata
  String mimetype = 'image/jpeg'
  
  String toString() {"${this.filename}" }    
  
}
