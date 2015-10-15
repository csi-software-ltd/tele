import org.codehaus.groovy.grails.commons.ConfigurationHolder
class ClearEmptyMsgJob {
  static triggers = {
    cron cronExpression: ((ConfigurationHolder.config.clearmsg.cron!=[:])?ConfigurationHolder.config.clearmsg.cron:"0 0 1 * * ?")
  }

  def execute() {        
    def lsMbox=Mbox.findAllByNrec(0)
    //log.debug("lsMbox="+lsMbox)
    for(oMbox in lsMbox) {
      try {
        oMbox.delete()
      }catch (Exception e) {
        log.debug("ClearEmptyMsgJob: Cannot delete Mbox\n"+e.toString())
      } 
    }     
  }
}
