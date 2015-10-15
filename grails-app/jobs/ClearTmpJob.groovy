import org.codehaus.groovy.grails.commons.ConfigurationHolder
class ClearTmpJob {
  static triggers = {
     //simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
    simple repeatInterval: Tools.getIntVal(ConfigurationHolder.config.pic.clear.timeout,900000) // execute job once in 900 seconds
  }

  def execute() {      
    def oPicturetemp=new Picturetemp() 
    def lsFiles=oPicturetemp.csiGetOldFiles(Tools.getIntVal(ConfigurationHolder.config.timelifepic,(60*15)))
    //println(lsFiles)
    def fileRemove
    def lsIds=[]
    for(hsFile in lsFiles) {
      try {
        log.debug("LOG>> Delete old file "+hsFile.fullname)
      	/*fileRemove=new File(hsFile.fullname)
      	if(fileRemove.exists()){
          fileRemove.delete()
          log.debug("LOG>>  OK")
        }*/
        def oPictureTMP=Picture.findByFilename(hsFile.fullname)
        if(oPictureTMP)
          oPictureTMP.delete(flush:true)
      }catch (Exception e) {
        log.debug("Cannont delete "+hsFile.fullname+"\n"+e.toString())
      }
      lsIds<<hsFile.id
    }    
    oPicturetemp.csiDeleteByIds(lsIds)
  }
}
