import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class ClientController {
  static final THUMBPREFIX='t_'
  def requestService
  def imageService
  def beforeInterceptor = [action:this.&checkClient,except:['login','index','changepassfirst']]

  def checkClient() {
    if(session?.client?.id!=null){     
      session.client.message_count = Mbox.countByIs_clientreadAndClient_idAndModstatusAndNrecGreaterThan(0,session?.client?.id,1,0)
      if(session.client.message_count == 1)      
        session.client.message_id=Mbox.findByIs_clientreadAndClient_idAndModstatusAndNrecGreaterThan(0,session?.client?.id,1,0)?.id?:0
      session.client.notice_count = Request.countByModstatusAndIs_readAndClient_id(-1,0,session?.client?.id)
      if(session.client.notice_count == 1)
        session.client.notice_id = Request.findByModstatusAndIs_readAndClient_id(-1,0,session?.client?.id)?.id?:0

      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null  
    }else{
      if(actionName=='changepass' && session.client_id)      
        return 
      redirect(controller:'client', action:'index', params:[redir:1], base:(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL))
      return false;      
    }
  }    
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {    
    if (session?.client?.id){
      redirect(action:'profile')
      return
    } else return params
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def login = {
    requestService.init(this)
    def sClient=requestService.getStr('login')
    def sPassword=requestService.getStr('password')	    
    if (sClient==''){
      flash.error = 1 // set login
      redirect(controller:'client',action:'index')
      return
    }
    def oClientlog = new Clientlog()
    def blocktime = Tools.getIntVal(ConfigurationHolder.config.client.blocktime,1800)
    def unsuccess_log_limit = Tools.getIntVal(ConfigurationHolder.config.client.unsuccess_log_limit,5)
    sPassword = Tools.hidePsw(sPassword)
    def oClient = Client.find('from Client where login=:login',[login:sClient.toLowerCase()])
    if(!oClient){
      flash.error=2 // Wrong password or Client does not exists
      redirect(controller:'client',action:'index')
      return      
    }else if (oClient.is_block || oClientlog.csiCountUnsuccessDurationLogs(oClient.id)[0]>=Tools.getIntVal(ConfigurationHolder.config.client.unsuccess_duration_log_limit,30)){
      flash.error=4 // Client blocked
      oClientlog = new Clientlog(client_id:oClient.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oClientlog.save(flush:true)){
        log.debug('error on save Clientlog in Client:login')
        oClientlog.errors.each{log.debug(it)}
      }
      if(!oClient.is_block){
        oClient.is_block=1
        if (!oClient.save(flush:true)){
          log.debug('error on save Client in Client:login')
          oClient.errors.each{log.debug(it)}
        }      
      }
      def oClienthistory=new Clienthistory()
      try{
        oClienthistory.csiSetDataFromClient(oClient).save(failOnError: true)
      } catch(Exception e) {
        log.debug("Error save data in Clienthistory Client:login\n"+e.toString());                  
      } 
      
      redirect(controller:'client',action:'index')
      return	
    }else if (oClientlog.csiCountUnsuccessLogs(oClient.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error=3 // Client blocked
      oClientlog = new Clientlog(client_id:oClient.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oClientlog.save(flush:true)){
        log.debug('error on save Clientlog in Client:login')
        oClientlog.errors.each{log.debug(it)}
      }
      redirect(controller:'client',action:'index')
      return	
    }else if (oClient.password != sPassword) {
      flash.error=2 // Wrong password or Client does not exists
      oClientlog = new Clientlog(client_id:oClient.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oClientlog.save(flush:true)){
        log.debug('error on save Clientlog in Client:login')
        oClientlog.errors.each{log.debug(it)}
      }
      redirect(controller:'client',action:'index')
      return
    }
    session.client_id=oClient.id
    /*
    session.client = [id            : oClient.id,
                      login         : oClient.name,                     
                      notice_count  : 0,
                      notice_id     : 0,
                      message_count : 0,
                      message_id    : 0
                    ]   
    */                    
    oClientlog = new Clientlog(client_id:oClient.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
    if (!oClientlog.save(flush:true)){
      log.debug('error on save Clientlog in Client:login')
      oClientlog.errors.each{log.debug(it)}
    }
    oClientlog.resetSuccessDuration(oClient.id)
    
    if(oClient?.is_passwordchange)
      redirect(action:'changepassfirst')
    else{
      session.client = [id      : oClient.id,
                        login         : oClient.login,
                        name          : oClient.name,                     
                        notice_count  : 0,
                        notice_id     : 0,
                        message_count : 0,
                        message_id    : 0
                      ]
      redirect(action:'profile'/*,params:[ext:1]*/)
    }  
    return  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    session.client = null
    redirect(controller:'client',action:'index')
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////  
  def profile = {
    requestService.init(this)    
    def hsRes = requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client
    hsRes.user = Client.get(session.client.id)
    def oClientlog = new Clientlog()
    def lsLogs = oClientlog.csiGetLogs(hsRes.client.id)
    if (lsLogs.size()>1){
      hsRes.lastlog = lsLogs[1]
      hsRes.unsuccess_log_amount = oClientlog.csiCountUnsuccessLogs(hsRes.client.id, new Date()-7)[0]
      hsRes.unsuccess_limit = Tools.getIntVal(ConfigurationHolder.config.client.unsuccess_log_showlimit,3)
    }
    hsRes.passwordlength=Tools.getIntVal(ConfigurationHolder.config.client.passwordlength,8)
    
    
    hsRes.clients=Client.findAll("FROM Client WHERE parent=:id OR parent2=:id ORDER BY name DESC",[id:hsRes.user?.id?:-1l])
    return hsRes
  }
  def changepassfirst={
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    hsRes.passwordlength=Tools.getIntVal(ConfigurationHolder.config.client.passwordlength,8)
    if(session?.client?.id){
      redirect(action:'profile')
      return
    }else  
      return hsRes 
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def changepass = {
    requestService.init(this)
    def sPass = requestService.getStr('pass')
    def lId = session.client?.id
    def hsRes=[:]    
    
    if(!lId)
      lId=session.client_id    

    if(sPass.size()<Tools.getIntVal(ConfigurationHolder.config.client.passwordlength,8)){
      flash.error=1	
    }else if(!(sPass?:'').matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*')){
      flash.error=2    
    }else if (lId){
      if (sPass==requestService.getStr('confirm_pass')){
        def oClient = Client.get(lId)
        if(session.client_id && Tools.hidePsw(sPass)==oClient.password){
          flash.error=4           
        }else{                      
          oClient.changePass(lId,Tools.hidePsw(sPass))        
          flash.error=0
          
          if(session.client_id){
            session.client_id=null        
            session.client = [id      : oClient.id,
                        login         : oClient.login,
                        name          : oClient.name,                   
                        notice_count  : 0,
                        notice_id     : 0,
                        message_count : 0,
                        message_id    : 0
                      ]       
          }
        }            
      } else {        
        flash.error=3
      }
    }
    //log.debug("session.client_id="+session.client_id)
    if(!session.client_id){
      redirect(action:'profile')
      return
    }else    
      redirect(action:'changepassfirst')
      return    
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def changename = {
    requestService.init(this)
    def sName = requestService.getStr('name')
    def lId = session.client?.id    
    
    if(!sName)
      flash.error_user=1
      
    if(!flash.error_user && lId){
      def oClient = Client.get(lId)
      oClient.name=sName  
      
      if (!oClient.save(flush:true)){
        log.debug('error on save Client in Client: changename')
        oClient.errors.each{log.debug(it)}
      }else{
        session.client.name=oClient.name
      }
    }    
    redirect(action:'profile')
    return        
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Messages >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messages = {
    requestService.init(this)
    def hsRes = requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    def fromEdit = requestService.getIntDef('fromEdit',0)
    if (fromEdit&&session.lastRequest){
      session.lastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.lastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',1)
    }

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagelist = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client
    
    if (session.lastRequest?.fromEdit?:0){
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromEdit = 0
    } else {     
      hsRes+=requestService.getParams(['is_clientfavourite','offset'],[],['keyword'])         
      hsRes.inrequest.modstatus=requestService.getIntDef('modstatus',1)
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }    
   
    def oMbox = new Mbox()
    hsRes.messages = oMbox.csiSelectMbox(hsRes.client.id?:0l,hsRes.inrequest.modstatus,hsRes.inrequest.keyword?:'',0,hsRes.inrequest?.is_clientfavourite?:0,20,hsRes.inrequest.offset?:0)
      
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagedetail = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client
    
    def lId=requestService.getLongDef('id',0)
    if(lId){
      hsRes.msg = Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0) 
      if (!hsRes.msg) {
        response.sendError(404)
        return
      }
      try {
        hsRes.msg.is_clientread=1      
        hsRes.msg.save(failOnError: true)      
      } catch(Exception e) {
        log.debug("Error save data in Admin/messagedetail\n"+e.toString());                
      } 
      hsRes.lastmboxrec=Mboxrec.find("FROM Mboxrec WHERE mbox_id=:mbox_id ORDER BY id DESC",[mbox_id:hsRes.msg.id])
    }else{
      try {
        hsRes.msg = new Mbox()
        hsRes.msg.client_id=hsRes.client?.id?:0        
        hsRes.msg.save(failOnError: true)      
      } catch(Exception e) {
        log.debug("Error save data in Admin/messagedetail\n"+e.toString());                
      } 
      hsRes.new_msg=1      
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveMessageDetail = {    
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client
    hsRes.result=[errorcode:[]]    
 
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams([],[],['subject','mtext'])    
    
    if(!hsRes.inrequest.subject)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.mtext)
      hsRes.result.errorcode<<3                                        
          
    if(!hsRes.result.errorcode && lId){
      def oMessage=Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0) 
      if(oMessage){              
        imageService.init(this,'clientmailtopic','clientmailkeeppic',lId.toString()+'_') // 0
        def hsPics=imageService.getSessionPics('file1')          
        imageService.finalizeFileSession(['file1'])
        hsRes.inrequest.filename=(hsPics?.photo)?:''
         
        try {               
          hsRes.inrequest.is_fromclient=1      
          hsRes.inrequest.is_clientread=1
          oMessage.setData(hsRes.inrequest).save(failOnError:true)
          
          new Mboxrec(mbox_id:oMessage.id,
                        inputdate:new Date(),
                        mtext:hsRes.inrequest.mtext?:'',
                        filename:hsRes.inrequest.filename?:'',
                        is_fromclient:1).save(failOnError:true)                                                           
        } catch(Exception e) {
          log.debug("Error save data in Client/saveMessageDetail\n"+e.toString());        
          hsRes.result.errorcode << 100
        }            
      }
    }    
    render hsRes.result as JSON
    return    
  }
  def remEmptyMbox={   
    requestService.init(this) 
    hsRes.client = session.client
    
    def lId=requestService.getLongDef('id',0) 
    
    def oMbox=Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0,nrec:0)
    if(oMbox)
      oMbox.delete(flush:true)
    render(contentType:"application/json"){[error:false]}  
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savemailpicture = {    
    requestService.init(this)
    imageService.init(this,'clientmailtopic','clientmailkeeppic',requestService.getLongDef('mbox_id',0).toString()+'_') // 0    
    def hsData = imageService.loadPicture("file1",Tools.getIntVal(ConfigurationHolder.config.photo.weight,4194304),requestService.getLongDef('nrec',0),Tools.getIntVal(ConfigurationHolder.config.photo.thumb.size,220),Tools.getIntVal(ConfigurationHolder.config.photo.thumb.height,160))      
    
    render(view:'savepictureresult',model:hsData)
    return
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def setMessageArchive = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client
    hsRes.result=[errorcode:[]]      

    def lId=requestService.getLongDef('id',0)
    if(lId){
      def oMbox=Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0)
      try {    
        oMbox.setArchive().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Client/setMessageArchive\n"+e.toString());          
        hsRes.result.errorcode << 100
      } 
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def setMessageFavourite = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client
    hsRes.result=[errorcode:[]]      

    def lId=requestService.getLongDef('id',0)
    if(lId){
      def oMbox=Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0)
      try {   
        if(!oMbox.is_clientfavourite)
          oMbox.is_clientfavourite=1       
        else  
          oMbox.is_clientfavourite=0
        oMbox.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Client/setMessageFavourite\n"+e.toString());          
        hsRes.result.errorcode << 100
      } 
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagehistory={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)        
    hsRes.client = session.client    
    
    def lId = requestService.getLongDef('id',0)    
    def oMbox = Mbox.findWhere(id:lId,client_id:hsRes.client?.id?:0)
    if(lId && oMbox){
      def oHistory = new Mboxrec()    
      hsRes.history = oHistory.csiGetMboxRec(lId,20,requestService.getOffset())       
    }
    return hsRes
  }

  def picture = {
    requestService.init(this)

    def hsRes=[:]
    hsRes.client = session.client

    def oPicture = Picture.findWhere(filename:(Mbox.findWhere(id:requestService.getLongDef('id',0),client_id:hsRes.client?.id?:0)?.id?.toString()?:'')+'_'+requestService.getStr('filename'))
    if(oPicture){
      response.contentType = oPicture.mimetype
      response.outputStream << oPicture.filedata
    } else {
      response.sendError(404)
    }
    response.flushBuffer()
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Requests >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requests = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    hsRes.trantypes = Trantype.findAllByPermissionsNotEqual('')

    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails&&session.lastRequest){
      session.lastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.lastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-100)
    }

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requestlist = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(null,['trantype_id'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-100)
      hsRes.inrequest.date_start = requestService.getDate('date_start')
      hsRes.inrequest.date_end = requestService.getDate('date_end')
      hsRes.inrequest.offset = requestService.getOffset()
      session.lastRequest = [:]
      session.lastRequest = hsRes.inrequest
    }

    hsRes.from_profile=requestService.getIntDef('from_profile',0)

    def oRequest = new Request()
    hsRes.requests = oRequest.csiSelectRequests(hsRes.client.id?:0l,hsRes.inrequest.trantype_id,hsRes.inrequest.modstatus,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',hsRes.from_profile?4:20,hsRes.inrequest.offset,hsRes.from_profile?true:false)
    hsRes.reqmodstatus = Reqmodstatus.list().inject([:]){map, status -> map[status.id]=[name:status.name,icon:status.icon];map}
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    if(hsRes.from_profile){
      render(view:'profile_requestlist',model:hsRes)
      return
    }
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requestdetail = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client
    
    def lId=requestService.getLongDef('id',0)
    hsRes.req = Request.findByClient_idAndId(hsRes.client.id,lId)?.read()?.save(flush:true)
    if (!hsRes.req&&lId) {
      response.sendError(404)
      return
    }
    hsRes.trantypes = !lId?Trantype.findAllByPermissionsInList(Client.get(hsRes.client.id)?.properties.findAll{it.key.matches('is_.*')&&it.value==1}.collect{it.key}):Trantype.list()
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requesttype={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=requestService.getParams(['type_id'],['id'])
    hsRes.client = Client.get(session.client.id)

    hsRes.req=Request.get(hsRes.inrequest.id?:0)
    hsRes.trantype=Trantype.get(hsRes.inrequest.type_id?:0)
    //Осторожно! Извращения! Не повторять! Выполнено профессиональными каскадерами! >>>
    hsRes.baseaccounts = hsRes.client.properties.findAll{it.key.matches("(?=account_)(?=.*(${hsRes.trantype?.code?.toLowerCase()=='rub'?Trantype.list().collect{it.code.toLowerCase()}.unique().join('|'):'rub|'+hsRes.trantype?.code?.toLowerCase()})).*")}.collect{[key:it.key-'account_',value:it.value+' '+(it.key-'account_')]}
    //<<<
    if (hsRes.trantype.valuta_id!=643)
      hsRes.vrate = new Valutarate().csiSearchCurrent(hsRes.trantype.valuta_id).vrate

    render(view:hsRes.trantype?.formname,model:hsRes)
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def bik_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Bik.findAllByBikIlike(hsRes.query+'%',[max:10]).each{ 
        hsRes.suggestions << it.bik; 
        hsRes.data << it.bankname+';'+it.corraccount 
      }    
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def name_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.client = session.client
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByClient_idAndNameIlike(hsRes.client.id,hsRes.query+'%',[max:10]).each{ 
        hsRes.suggestions << it.name; 
        hsRes.data << it.id+';'+it.inn+';'+it.kpp+';'+it.ogrn+';'+it.bik+';'+it.cor_account+';'+it.bank+';'+it.city+';'+it.account+';'+it.prim 
      }    
    }
    render hsRes as JSON
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def beneficial_autocomplete={
    requestService.init(this)
    def hsRes = [:]
    hsRes.client = session.client
    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByClient_idAndBeneficialIlike(hsRes.client.id,hsRes.query+'%',[max:10]).each{ 
        hsRes.suggestions << it.beneficial; 
        hsRes.data << it.id+';'+it.iban+';'+it.bbank+';'+it.baddress+';'+it.swift+';'+it.purpose+';'+it.laddress
      }    
    }
    render hsRes as JSON
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveRequestDetail = {    
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client
    hsRes.result=[errorcode:[]]    
 
    def lId = requestService.getLongDef('id',0)    
    hsRes+=requestService.getParams(['trantype_id','modstatus','nds','savecompany'],['company_id','parent','syscompany_id'],
                                    ['inn','kpp','ogrn','name','bik','bank','bankcity','cor_account','account','prim',
                                     'beneficial','iban','bbank','baddress','swift','purpose','comment','baseaccount','laddress','syscompany_name'],null,['summa'])
    hsRes.inrequest.platdate = requestService.getDate('platdate')
    hsRes.inrequest.reqdate = requestService.getDate('reqdate')

    hsRes.request = Request.findByClient_idAndId(hsRes.client.id,lId)
    if ((!hsRes.request&&lId)||!((hsRes.request?.modstatus?:0) in -2..1)) {
      render(contentType:"application/json"){[error:true]}
      return
    }

    if(!lId&&!hsRes.inrequest.trantype_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa||hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<2
    if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [1]){
      if(!hsRes.inrequest.nds)
        hsRes.result.errorcode<<3
      if(!hsRes.inrequest.inn)
        hsRes.result.errorcode<<4
      if(!hsRes.inrequest.name)
        hsRes.result.errorcode<<5
      if(!hsRes.inrequest.bik)
        hsRes.result.errorcode<<6
      if(!hsRes.inrequest.bankcity)
        hsRes.result.errorcode<<7
      if(!hsRes.inrequest.account)
        hsRes.result.errorcode<<8
      if(!hsRes.inrequest.prim)
        hsRes.result.errorcode<<9
    }else if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [2,3]){
      if(!hsRes.inrequest.beneficial)
        hsRes.result.errorcode<<10
      if(!hsRes.inrequest.iban)
        hsRes.result.errorcode<<11
      if(!hsRes.inrequest.swift)
        hsRes.result.errorcode<<12
      if(!hsRes.inrequest.purpose)
        hsRes.result.errorcode<<13
    }else if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [10]){
      if(!hsRes.inrequest.name)
        hsRes.result.errorcode<<5
      if(!(hsRes.inrequest.syscompany_id?:hsRes.request?.syscompany_id))
        hsRes.result.errorcode<<19
    }else if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [11,12]){
      if(!hsRes.inrequest.name)
        hsRes.result.errorcode<<5
      if(!(hsRes.inrequest.syscompany_name?:hsRes.request?.syscompany_name))
        hsRes.result.errorcode<<20
    }else if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [7,8,9]){
      if(!hsRes.inrequest.reqdate)
        hsRes.result.errorcode<<21
    }
    hsRes.inrequest.rate = Client.get(hsRes.client.id)?."${Trantype.get(hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id)?.rate}"
    hsRes.inrequest.perm = Client.get(hsRes.client.id)?."${Trantype.get(hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id)?.permissions}"
    if (!hsRes.request&&!hsRes.inrequest.perm)
      hsRes.result.errorcode<<15
    if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [10,11,12]){
      if(!hsRes.inrequest.platdate)
        hsRes.result.errorcode<<18
    }

    if(!hsRes.result.errorcode){
      try {
        if(!lId) hsRes.request = new Request([client_id:hsRes.client.id])
        hsRes.result.req = hsRes.request.setData(hsRes.inrequest,hsRes.inrequest.savecompany>0?Company.getInstance(hsRes.inrequest.name,hsRes.inrequest.beneficial,hsRes.client.id).csiSetNewData().updateData(hsRes.inrequest).save(failOnError:true)?.id:hsRes.inrequest.company_id).save(failOnError:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Client/saveRequestDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requesthistory={
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client

    def lId=requestService.getLongDef('id',0)
    hsRes.req = Request.get(lId)
    def oHistory = new Requesthistory()    
    hsRes.history = oHistory.csiGetRequestHistory(lId,20,requestService.getOffset())    

    hsRes.inrequest=[:]
    hsRes.inrequest.id=lId
    return hsRes
  }

  def requestoperation = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client

    hsRes.req = Request.findByClient_idAndId(hsRes.client?.id?:0l,requestService.getLongDef('id',0))
    hsRes.operation = Transaction.findAllByRequest_idAndTrantype_idInList(hsRes.req?.id?:0l,Trantype.findAllByIs_service(0).collect{it.id})
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Operations >>>//////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operations = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.inrequest=[:]
    hsRes.inrequest.valuta = requestService.getStr('valuta')?:'' 
    hsRes.client = session.client
    hsRes.user = Client.get(session.client.id)    

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operationlist = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.client = session.client    
    
    hsRes+=requestService.getParams(['trantype_id'],['id','request_id'],['valuta'],['date_start','date_end'])
    hsRes.date_start = requestService.getDate('date_start')
    hsRes.date_end = requestService.getDate('date_end')

    def oOperation = new TransactionSearch()
    hsRes.operations = oOperation.csiSelectOperations(hsRes.client.id,hsRes.inrequest.id?:0l,hsRes.inrequest.trantype_id?:0,hsRes.inrequest.request_id?:0l,hsRes.inrequest.valuta?:'',hsRes.date_start?:'',hsRes.date_end?:'',20,requestService.getOffset())
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operationdetail = {    
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)    

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveOperationDetail = {    
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]    
    
    return hsRes
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Reports >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def reports = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    return hsRes
  }

  def reqreport = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    hsRes.report_start = requestService.getDate('reqreport_start')
    hsRes.report_end = requestService.getDate('reqreport_end')

    def oObject = new RequestReportSearch()
    hsRes.report = oObject.csiSelectRequests(hsRes.client.id,hsRes.report_start,hsRes.report_end)
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = 0g
    hsRes.report.each{
      hsRes."sum_$it.trcode" += it.summa
    }

    renderPdf(template: 'reqreport', model: hsRes, filename: "reqreport")
  }

  def reqreportXLS = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    hsRes.report_start = requestService.getDate('reqreport_start')
    hsRes.report_end = requestService.getDate('reqreport_end')

    def oObject = new RequestReportSearch()
    hsRes.report = oObject.csiSelectRequests(hsRes.client.id,hsRes.report_start,hsRes.report_end)
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = 0g
    hsRes.report.each{
      hsRes."sum_$it.trcode" += it.summa
    }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      def title = "Отчет по выполненным запросам "+(!(hsRes.report_start||hsRes.report_end)?"за все время":((hsRes.report_start?"с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(1,30*256)
        putCellValue(0, 1, title)
        fillRow(['Номер запроса','Дата запроса','Тип запроса','Валюта запроса','Сумма запроса'],3,false,Tools.getXlsTableHeaderStyle(5))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.id.toString(),
                   String.format('%tF',record.moddate),
                   record.trname,
                   record.trcode,
                   record.summa], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(5) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(5) : Tools.getXlsTableLineStyle(5))
        }
        fillRow(["ИТОГО", "", "", "", ""], rowCounter++, false)
        fillRow(["", "Сумма запросов по рублям", "", hsRes.sum_RUB], rowCounter++, false, [null]+Tools.getXlsTableFirstLineStyle(3))
        fillRow(["", "Сумма запросов по долларам", "", hsRes.sum_USD], rowCounter++, false, [null]+Tools.getXlsTableLineStyle(3))
        fillRow(["", "Сумма запросов по евро", "", hsRes.sum_EUR], rowCounter++, false, [null]+Tools.getXlsTableLastLineStyle(3))
        setColumnAutoWidth(2)
        save(response.outputStream)
      }
    }
    return
  }

  def transreport = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    hsRes.report_start = requestService.getDate('transreport_start')
    hsRes.report_end = requestService.getDate('transreport_end')
    hsRes.valutacode = requestService.getStr('valuta')

    def oObject = new TransactionSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.client.id,hsRes.valutacode?:'RUB',hsRes.report_start,hsRes.report_end)
    hsRes.debetobor = hsRes.creditobor = 0g
    hsRes.report.each{
      hsRes."${it.is_debet||it.summa<0?'debetobor':'creditobor'}" += Math.abs(it.summa) * it.vrate
    }

    renderPdf(template: 'transreport', model: hsRes, filename: "transreport")
  }

  def transreportXLS = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = session.client

    hsRes.report_start = requestService.getDate('transreport_start')
    hsRes.report_end = requestService.getDate('transreport_end')
    hsRes.valutacode = requestService.getStr('valuta')

    def oObject = new TransactionSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.client.id,hsRes.valutacode?:'RUB',hsRes.report_start,hsRes.report_end)
    hsRes.debetobor = hsRes.creditobor = 0g
    hsRes.report.each{
      hsRes."${it.is_debet||it.summa<0?'debetobor':'creditobor'}" += Math.abs(it.summa) * it.vrate
    }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      def title = "Выписка операций "+(!(hsRes.report_start||hsRes.report_end)?"за все время":((hsRes.report_start?"с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(1, 30*256)
        putCellValue(0, 1, title)
        fillRow(['Номер операции','Дата операции','Тип операции','Кредит', 'Дебет','Сальдо'],3,false,Tools.getXlsTableHeaderStyle(6))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.id.toString(),
                   String.format('%tF',record.inputdate),
                   record.ttname,
                   record.credit*record.vrate,
                   Math.abs(record.debet*record.vrate),
                   record.saldo], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(6) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(6) : Tools.getXlsTableLineStyle(6))
        }
        fillRow(["ИТОГО", "", "", "", "", ""], rowCounter++, false)
        fillRow(["", "Сальдо на начало периода", "", hsRes.report.first().saldo-hsRes.report.first().credit*hsRes.report.first().vrate+hsRes.report.first().debet*hsRes.report.first().vrate], rowCounter++, false, [null]+Tools.getXlsTableFirstLineStyle(3))
        fillRow(["", "Обороты по кредиту", "", hsRes.creditobor], rowCounter++, false, [null]+Tools.getXlsTableLineStyle(3))
        fillRow(["", "Обороты по дебету", "", hsRes.debetobor], rowCounter++, false, [null]+Tools.getXlsTableLineStyle(3))
        fillRow(["", "Сальдо на конец периода", "", hsRes.report.last().saldo], rowCounter++, false, [null]+Tools.getXlsTableLastLineStyle(3))
        setColumnAutoWidth(2)
        save(response.outputStream)
      }
    }
    return
  }

  def revisereportXLS = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(false,true)
    hsRes.client = Client.get(session.client.id)

    hsRes.report_start = requestService.getDate('revisereport_start')
    hsRes.report_end = requestService.getDate('revisereport_end')

    hsRes.report = new RequestReportSearch().csiSelectRequests(hsRes.client.id,hsRes.report_start,hsRes.report_end)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}
      def rowCounter = 3
      def title = "Сверка."+(!(hsRes.report_start||hsRes.report_end)?" за все время":((hsRes.report_start?" с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(1, 30*256)
        putCellValue(0, 1, title)
        if (hsRes.client.is_tran_eur||hsRes.client.is_tran_usd)
          fillRow(['Дата исполнения','Тип запроса','Сумма запроса','Валюта','Откуда','Куда','Процент комиссии','Сумма комиссии','Сумма на покупку валюты','Курс','Курс ЦБ','Свифт','Остаток клиента'],rowCounter++,false,Tools.getXlsTableHeaderStyle(13))
        else
          fillRow(['Дата исполнения','Тип запроса','Сумма запроса','Валюта','Откуда','Куда','Процент комиссии','Сумма комиссии','Остаток клиента'],rowCounter++,false,Tools.getXlsTableHeaderStyle(9))
        hsRes.report.eachWithIndex{ record, index ->
          if (hsRes.client.is_tran_eur||hsRes.client.is_tran_usd)
            fillRow([String.format('%tF',record.moddate),
                     record.trname,
                     record.summa,
                     record.trcode,
                     record.trantype_id in [10,11,12]?record.name:'',
                     record.trantype_id in [10,11,12]?(record.syscompany_name?:Company.get(record.syscompany_id)?.name?:''):record.trantype_id in [1]?record.name:record.trantype_id in [2,3]?record.beneficial:'',
                     record.rate,
                     Transaction.findAllByRequest_idAndTrantype_idInList(record.id,[23,24,25,26]).sum{it.summa*it.vrate}?:0,
                     Transaction.findAllByRequest_idAndTrantype_id(record.id,31).sum{it.summa*it.vrate}?:0,
                     record.comvrate>1?record.comvrate:null,
                     record.vrate>1?record.vrate:null,
                     record.swiftsumma,
                     Transaction.findByRequest_idAndTrantype_idInList(record.id,[23,24,25,26])?.saldo?:0], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(13) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(13) : Tools.getXlsTableLineStyle(13))
          else
            fillRow([String.format('%tF',record.moddate),
                     record.trname,
                     record.summa,
                     record.trcode,
                     record.trantype_id in [10,11,12]?record.name:'',
                     record.trantype_id in [10,11,12]?(record.syscompany_name?:Company.get(record.syscompany_id)?.name?:''):record.trantype_id in [1]?record.name:record.trantype_id in [2,3]?record.beneficial:'',
                     record.rate,
                     Transaction.findAllByRequest_idAndTrantype_idInList(record.id,[23,24,25,26]).sum{it.summa*it.vrate}?:0,
                     Transaction.findByRequest_idAndTrantype_idInList(record.id,[23,24,25,26])?.saldo?:0], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(9) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(9) : Tools.getXlsTableLineStyle(9))
        }
        setColumnAutoWidth(4)
        setColumnAutoWidth(5)
        save(response.outputStream)
      }
    }
    return
  }
}