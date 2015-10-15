class Requesthistory {  
  def searchService
  
  static constraints = {    
  }  
  static mapping = {
    version false
  }
  
  Long request_id  
  Integer modstatus = 0  
  Date inputdate
  BigDecimal summa = 0.00
  
  def csiGetRequestHistory(lId,iMax,iOffset){
    def hsSql= [select :'*',
                from   :'requesthistory',
                where  :'request_id = :id',
                order  :'id desc']
    def hsLong = [id: lId]
    
    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Requesthistory.class)    
  }  
}
