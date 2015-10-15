class Mboxrec {  
  def searchService
  
  static constraints = {    
  }  
  static mapping = {
    version false
  }
  
  Long mbox_id   
  Integer is_fromclient 
    
	Date inputdate = new Date()  
 
  String mtext
  String filename
  
  def csiGetMboxRec(lId,iMax,iOffset){
    def hsSql= [select :'*',
                from   :'mboxrec',
                where  :'mbox_id = :id',
                order  :'inputdate desc']
    def hsLong = [id: lId]
    
    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Mboxrec.class)    
  }
}
