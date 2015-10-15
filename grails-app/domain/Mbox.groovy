class Mbox {  
  def searchService
  
  static constraints = {    
  }  
  static mapping = {
    version false
  }

  Long id
  Long client_id = 0
  
  Integer modstatus = 1
  Integer nrec = 0
  Integer is_read = 0
  Integer is_clientread = 0
  Integer is_attach = 0
  Integer is_favourite = 0 
  Integer is_clientfavourite = 0
  Integer is_fromclient = 0
    
  Date inputdate = new Date() 
  Date moddate = new Date()  
  
	String subject = ''
	String lasttext = ''
  
  def csiSelectMbox(lClientId,iModstatus,sKeyword,bFavourite,bClientFavourite,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]    
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='mbox'
    hsSql.where="nrec != 0"+//new msg, not saved
                ((sKeyword!='')?' AND ((subject like CONCAT("%",:keyword,"%")) OR (lasttext like CONCAT("%",:keyword,"%")))':'')+                
                ((lClientId>0)?' AND client_id =:client_id':'')+
                ((iModstatus>-1 && !bFavourite && !bClientFavourite)?' AND modstatus =:modstatus':'')+
                ((bFavourite)?' AND is_favourite =1':'')+
                ((bClientFavourite)?' AND is_clientfavourite =1':'')
                
    hsSql.order="moddate desc"

    if(sKeyword!='')
      hsString['keyword']=sKeyword
    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(iModstatus>-1 && !bFavourite && !bClientFavourite)
      hsLong['modstatus']=iModstatus

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,Mbox.class)
  }
  ////////////////////////////////////
  Mbox setData(lsRequest){    
    client_id = lsRequest.client_id?:client_id
    subject = lsRequest.subject?:'' 
    lasttext = lsRequest.mtext?:''    
    modstatus = lsRequest.modstatus?:1
    moddate = new Date()
    is_attach = lsRequest.filename?1:0 
    is_fromclient = lsRequest?.is_fromclient?:0
    is_clientread = lsRequest?.is_clientread?:0
    is_read = lsRequest?.is_read?:0
    nrec += 1        
    this
  } 
  ///////////////////////// 
  Mbox setArchive(){
    modstatus = 0
    this
  }
}
