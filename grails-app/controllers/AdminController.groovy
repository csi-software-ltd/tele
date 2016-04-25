import grails.converters.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import pl.touk.excel.export.WebXlsxExporter

class AdminController {
  def requestService
  def imageService
  
  def beforeInterceptor = [action:this.&checkAdmin,except:['login','index']]

  def checkAdmin() {
    if(session?.admin?.id!=null){
      session.admin.message_count = Mbox.countByIs_readAndModstatusAndNrecGreaterThan(0,1,0)
      if(session.admin.message_count == 1)
        session.admin.message_id=Mbox.findByIs_readAndModstatusAndNrecGreaterThan(0,1,0)?.id?:0
      session.admin.notice_count = Request.countByModstatus(1)
      if(session.admin.notice_count == 1)
        session.admin.notice_id = Request.findByModstatus(1)?.id?:0

      def oTemp_notification=Temp_notification.findWhere(id:1,status:1)
      session.attention_message=oTemp_notification?oTemp_notification.text:null
    }else{
      redirect(controller:'admin', action:'index', params:[redir:1], base:(ConfigurationHolder.config.grails.secureServerURL?:ConfigurationHolder.config.grails.serverURL))
      return false;
    }
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def checkAccess(iActionId){
    def bDenied = true
    session.admin.menu.each{
	    if (iActionId==it.id) bDenied = false
	  }
    if (bDenied) {
	    redirect(action:'profile');
	    return
	  }
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def index = {
    if (session?.admin?.id){
      redirect(action:'profile')
      return
    } else return params
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def login = {
    requestService.init(this)
    def sAdmin=requestService.getStr('login')
    def sPassword=requestService.getStr('password')	    
    if (sAdmin==''){
      flash.error = 1 // set login
      redirect(controller:'admin',action:'index')//TODO change action
      return
    }
    def oAdminlog = new Adminlog()
    def blocktime = Tools.getIntVal(ConfigurationHolder.config.admin.blocktime,1800)
    def unsuccess_log_limit = Tools.getIntVal(ConfigurationHolder.config.admin.unsuccess_log_limit,5)
    sPassword=Tools.hidePsw(sPassword)
    def oAdmin=Admin.find('from Admin where login=:login',
                             [login:sAdmin.toLowerCase()])
    if(!oAdmin){
      flash.error=2 // Wrong password or admin does not exists
      redirect(controller:'admin',action:'index')
      return      
    }else if (oAdminlog.csiCountUnsuccessDurationLogs(oAdmin.id)[0]>=Tools.getIntVal(ConfigurationHolder.config.admin.unsuccess_duration_log_limit,30)){
      flash.error=3 // Admin blocked
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:3,success_duration:-1)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return	
    }else if (oAdminlog.csiCountUnsuccessLogs(oAdmin.id, new Date(System.currentTimeMillis()-blocktime*1000))[0]>=unsuccess_log_limit){
      flash.error=3 // Admin blocked
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:2,success_duration:-1)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return	
    }else if (oAdmin.password != sPassword) {
      flash.error=2 // Wrong password or admin does not exists
      oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:0,success_duration:0)
      if (!oAdminlog.save(flush:true)){
        log.debug('error on save Adminlog in Admin:login')
        oAdminlog.errors.each{log.debug(it)}
      }
      redirect(controller:'admin',action:'index')
      return
    }
    def oAdminmenu = new Adminmenu()
    session.admin = [id            : oAdmin.id,
                     login         : oAdmin.login,
                     group         : oAdmin.admingroup_id,
                     menu          : oAdminmenu.csiGetMenu(oAdmin.admingroup_id),
                     accesslevel   : oAdmin.accesslevel,
                     notice_count  : 0,
                     notice_id     : 0,
                     message_count : 0,
                     message_id    : 0
                    ]   
    oAdminlog = new Adminlog(admin_id:oAdmin.id,logtime:new Date(),ip:request.remoteAddr,success:1,success_duration:1)
    if (!oAdminlog.save(flush:true)){
      log.debug('error on save Adminlog in Admin:login')
      oAdminlog.errors.each{log.debug(it)}
    }
    oAdminlog.resetSuccessDuration(oAdmin.id)
    redirect(action:'profile',params:[ext:1])
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def logout = {
    requestService.init(this)
    session.admin = null
    redirect(controller:'admin',action: 'index')
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def menu = {
    requestService.init(this)
    def iPage = requestService.getIntDef('id',1)	
    switch (iPage){	
      case 1: redirect(action:'profile'); return
      case 2: redirect(action:'clients'); return
      case 3: redirect(action:'messages'); return
      case 4: redirect(action:'requests'); return
      case 5: redirect(action:'operations'); return
      case 6: redirect(action:'statistics'); return
      case 7: redirect(action:'reports'); return
      case 8: redirect(action:'companies'); return
      default: redirect(action:'profile'); return
    }
    return [admin:session.admin,action_id:iPage]
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Administrator`s profile >>>/////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profile = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes+=[administrator:Admin.get(session.admin.id),action_id:1]	
    hsRes.admin = session.admin
    def oAdminlog = new Adminlog()
    def lsLogs = oAdminlog.csiGetLogs(hsRes.admin.id)
    if (lsLogs.size()>1){
      hsRes.lastlog = lsLogs[1]
      hsRes.unsuccess_log_amount = oAdminlog.csiCountUnsuccessLogs(hsRes.admin.id, new Date()-7)[0]
      hsRes.unsuccess_limit = Tools.getIntVal(ConfigurationHolder.config.admin.unsuccess_log_showlimit,3)
    }
    hsRes.passwordlength=Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,8)
    def oClient = new Client()
    hsRes.saldo = oClient.csiGetClientsSaldo()[0]
    hsRes.income = Accountsys.list()[0]
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def profilesave = {
    checkAccess(1)
    requestService.init(this)
    def hsRes = requestService.getParams([],[],['email','tel'])	
    hsRes.inrequest.id=session.admin.id

    def result=[errorcode:[]]    
    if (hsRes.inrequest.id){      
      if (hsRes.inrequest.tel && !hsRes.inrequest.tel.matches('\\+\\d{1}\\(\\d{3}\\)\\d{7}'))
        result.errorcode << 1
      if (hsRes.inrequest.email && !Tools.checkEmailString(hsRes.inrequest.email))
        result.errorcode << 2         
      if(!result.errorcode){
        def oAdmin = Admin.get(hsRes.inrequest.id)                     
        oAdmin.email = hsRes.inrequest.email?:'' 
        oAdmin.tel = hsRes.inrequest.tel?:''         
        if (!oAdmin.save(flush:true)){
          log.debug('error on save Admin: Administrators.usersave')
          oAdmin.errors.each{log.debug(it)}
        } 
      }
    }
    if (result.errorcode.size()>0) {
      result.error = true
      render result as JSON
      return
    } else
      render(contentType:"application/json"){[error:false]}       
    return
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def changepass = {
    checkAccess(1)
    requestService.init(this)    
    def sPass = requestService.getStr('pass')
    def lId = session.admin.id
    
    def result=[errorcode:[]]
    if(sPass.size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,8)){
      result.errorcode << 3	      
    }else if(!(sPass?:'').matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*')){
      result.errorcode << 4    
    }else if (lId>1){
      if (sPass==requestService.getStr('confirm_pass')){
        def oAdmin = new Admin()
        oAdmin.changePass(lId,Tools.hidePsw(sPass))                
      } else {        
        result.errorcode << 2
      }
    }
    if (result.errorcode.size()>0) {
      result.error = true
      render result as JSON
      return
    } else
      render(contentType:"application/json"){[error:false]}       
    return    
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Clients >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def clients = {
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 2
    hsRes.admin = session.admin
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
   
    if (fromEdit){
      session.adminlastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.adminlastRequest
    }

    return hsRes
  }

  def clientname_autocomplete = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.admin = session.admin

    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Client.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    render hsRes as JSON
  }

  def clientlist = {
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 2
    hsRes.admin = session.admin
    
    if (session.adminlastRequest?.fromEdit?:0){
      hsRes.inrequest = session.adminlastRequest
      session.adminlastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['is_dealer','is_block','modstatus','offset'],['client_id'],['name'])      
      session.adminlastRequest = [:]
      session.adminlastRequest = hsRes.inrequest
    }
   
    def oClient = new Client()
    hsRes.clients = oClient.csiSelectClients(hsRes.inrequest.name?:'',hsRes.inrequest.client_id?:0l,
                                             hsRes.inrequest.is_dealer?:0,hsRes.inrequest.is_block?:0,
                                             hsRes.inrequest.modstatus?:0,20,hsRes.inrequest.offset?:0)
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def loginAsClient={
    requestService.init(this)   
    def lId=requestService.getLongDef('id',0)    
    def oClient = Client.find('from Client where id=:id',[id:lId])
    
    if(!oClient){
      redirect(controller:'admin',action:'clients')
      return
    }else{
      session.client = [id            : oClient.id,
                        login         : oClient.login,  
                        name          : oClient.name,                        
                        notice_count  : 0,
                        notice_id     : 0,
                        message_count : 0,
                        message_id    : 0
                      ]                     
      if(session?.client.id){
        redirect(controller:'client',action:'profile')
        return      
      }
    }    
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def clientdetail={
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 2
    hsRes.admin = session.admin

    def lId=requestService.getLongDef('id',0)    
    hsRes.client = Client.get(lId)
    if (!hsRes.client&&lId) {
      response.sendError(404)
      return
    }
    hsRes.dealers=Client.findAll("FROM Client WHERE (dealer_cashin_rate_rub>0 OR dealer_cashin_rate_usd>0 OR dealer_cashin_rate_eur>0 OR dealer_cashout_rate_rub>0 OR dealer_cashout_rate_usd>0 OR dealer_cashout_rate_eur>0 OR dealer_refill_rate_rub>0 OR dealer_refill_rate_usd>0 OR dealer_refill_rate_eur>0 OR dealer_tran_rate_rub>0 OR dealer_tran_rate_usd>0 OR dealer_tran_rate_eur>0) AND modstatus=1 AND id!=:id ORDER BY name",[id:lId])
    hsRes.dealers2=Client.findAll("FROM Client WHERE (dealer_cashin_rate_rub>0 OR dealer_cashin_rate_usd>0 OR dealer_cashin_rate_eur>0 OR dealer_cashout_rate_rub>0 OR dealer_cashout_rate_usd>0 OR dealer_cashout_rate_eur>0 OR dealer_refill_rate_rub>0 OR dealer_refill_rate_usd>0 OR dealer_refill_rate_eur>0 OR dealer_tran_rate_rub>0 OR dealer_tran_rate_usd>0 OR dealer_tran_rate_eur>0) AND modstatus=1 AND id!=:id AND id!=:parent ORDER BY name",[id:lId,parent:hsRes.client?.parent?:0l])

    hsRes.passwlength=Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,8)
    
    if(hsRes.client)
      hsRes.clients=Client.findAll("FROM Client WHERE parent=:id OR parent2=:id ORDER BY name DESC",[id:hsRes.client?.id?:-1l])
    
    return hsRes
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveClientDetail={
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]    
    
    def lClient_id = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['modstatus','is_block','is_refill_rub','is_refill_usd','is_refill_eur','is_tran_rub','is_tran_usd','is_tran_eur',
                                    'is_cashin_rub','is_cashin_usd','is_cashin_eur','is_cashout_rub','is_cashout_usd','is_cashout_eur','swiftonclient'],['parent','parent2'],
                                    ['password','confirm_pass','name','comment','login'],
                                    [],
                                    ['account_rub','account_usd','account_eur','tran_rate_rub','tran_rate_usd','tran_rate_eur','cashin_rate_rub','cashin_rate_usd','cashin_rate_eur','cashout_rate_rub','cashout_rate_usd','cashout_rate_eur','refill_rate_rub','refill_rate_usd','refill_rate_eur',
                                    'dealer_cashin_rate_rub','dealer_cashin_rate_usd','dealer_cashin_rate_eur','dealer_cashout_rate_rub','dealer_cashout_rate_usd','dealer_cashout_rate_eur',
                                    'dealer_refill_rate_rub','dealer_refill_rate_usd','dealer_refill_rate_eur','dealer_tran_rate_rub','dealer_tran_rate_usd','dealer_tran_rate_eur'])
    if(!lClient_id){                    
      if(!(hsRes.inrequest.password?:'').matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*')){      
        hsRes.result.errorcode << 1    
      }
      if((hsRes.inrequest.password?:'').size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,8)){      
       hsRes.result.errorcode << 2	    
      }
      if((hsRes.inrequest.password?:'')!=(hsRes.inrequest.confirm_pass?:'')){
        hsRes.result.errorcode << 3
      }          
      if(!(hsRes.inrequest.login?:'').size()){
        hsRes.result.errorcode << 5
      }else if(Client.findByLogin(hsRes.inrequest.login))
        hsRes.result.errorcode << 6
    } 
    
    if(!(hsRes.inrequest.name?:'').size()){
      hsRes.result.errorcode << 4
    }
   
    if((hsRes.inrequest.tran_rate_rub?:0)>=100 || (hsRes.inrequest.tran_rate_rub?:0)<0){      
      hsRes.result.errorcode << 21 
    }
    if((hsRes.inrequest.tran_rate_usd?:0)>=100 || (hsRes.inrequest.tran_rate_usd?:0)<0){      
      hsRes.result.errorcode << 22
    }
    if((hsRes.inrequest.tran_rate_eur?:0)>=100 || (hsRes.inrequest.tran_rate_eur?:0)<0){      
      hsRes.result.errorcode << 23
    }    
    if((hsRes.inrequest.cashin_rate_rub?:0)>=100 || (hsRes.inrequest.cashin_rate_rub?:0)<-100){      
      hsRes.result.errorcode << 31
    }
    if((hsRes.inrequest.cashin_rate_usd?:0)>=100 || (hsRes.inrequest.cashin_rate_usd?:0)<-100){      
      hsRes.result.errorcode << 32 
    }
    if((hsRes.inrequest.cashin_rate_eur?:0)>=100 || (hsRes.inrequest.cashin_rate_eur?:0)<-100){      
      hsRes.result.errorcode << 33
    }    
    if((hsRes.inrequest.cashout_rate_rub?:0)>=100 || (hsRes.inrequest.cashout_rate_rub?:0)<0){      
      hsRes.result.errorcode << 41 
    }
    if((hsRes.inrequest.cashout_rate_usd?:0)>=100 || (hsRes.inrequest.cashout_rate_usd?:0)<0){      
      hsRes.result.errorcode << 42
    }
    if((hsRes.inrequest.cashout_rate_eur?:0)>=100 || (hsRes.inrequest.cashout_rate_eur?:0)<0){      
      hsRes.result.errorcode << 43
    }
    if((hsRes.inrequest.account_rub?:0)>100000000000 || (hsRes.inrequest.account_rub?:0)<-100000000000){      
      hsRes.result.errorcode << 51 
    }
    if((hsRes.inrequest.account_usd?:0)>100000000000|| (hsRes.inrequest.account_usd?:0)<0){      
      hsRes.result.errorcode << 52
    }
    if((hsRes.inrequest.account_eur?:0)>100000000000 || (hsRes.inrequest.account_eur?:0)<0){      
      hsRes.result.errorcode << 53
    }
    if((hsRes.inrequest.refill_rate_rub?:0)>=100 || (hsRes.inrequest.refill_rate_rub?:0)<0){      
      hsRes.result.errorcode << 61 
    }
    if((hsRes.inrequest.refill_rate_usd?:0)>=100 || (hsRes.inrequest.refill_rate_usd?:0)<0){      
      hsRes.result.errorcode << 62
    }
    if((hsRes.inrequest.refill_rate_eur?:0)>=100 || (hsRes.inrequest.refill_rate_eur?:0)<0){      
      hsRes.result.errorcode << 63
    }
    
    if((hsRes.inrequest.dealer_cashin_rate_rub?:0)>=100 || (hsRes.inrequest.dealer_cashin_rate_rub?:0)<0){      
      hsRes.result.errorcode << 71 
    }
    if((hsRes.inrequest.dealer_cashin_rate_usd?:0)>=100 || (hsRes.inrequest.dealer_cashin_rate_usd?:0)<0){      
      hsRes.result.errorcode << 72
    }
    if((hsRes.inrequest.dealer_cashin_rate_eur?:0)>=100 || (hsRes.inrequest.dealer_cashin_rate_eur?:0)<0){      
      hsRes.result.errorcode << 73 
    }
    if((hsRes.inrequest.dealer_cashout_rate_rub?:0)>=100 || (hsRes.inrequest.dealer_cashout_rate_rub?:0)<0){      
      hsRes.result.errorcode << 81 
    }
    if((hsRes.inrequest.dealer_cashout_rate_usd?:0)>=100 || (hsRes.inrequest.dealer_cashout_rate_usd?:0)<0){      
      hsRes.result.errorcode << 82
    }
    if((hsRes.inrequest.dealer_cashout_rate_eur?:0)>=100 || (hsRes.inrequest.dealer_cashout_rate_eur?:0)<0){      
      hsRes.result.errorcode << 83
    }
    if((hsRes.inrequest.dealer_refill_rate_rub?:0)>=100 || (hsRes.inrequest.dealer_refill_rate_rub?:0)<0){      
      hsRes.result.errorcode << 91 
    }
    if((hsRes.inrequest.dealer_refill_rate_usd?:0)>=100 || (hsRes.inrequest.dealer_refill_rate_usd?:0)<0){      
      hsRes.result.errorcode << 92
    }
    if((hsRes.inrequest.dealer_refill_rate_eur?:0)>=100 || (hsRes.inrequest.dealer_refill_rate_eur?:0)<0){      
      hsRes.result.errorcode << 93 
    }
    if((hsRes.inrequest.dealer_tran_rate_rub?:0)>=100 || (hsRes.inrequest.dealer_tran_rate_rub?:0)<0){      
      hsRes.result.errorcode << 101 
    }
    if((hsRes.inrequest.dealer_tran_rate_usd?:0)>=100 || (hsRes.inrequest.dealer_tran_rate_usd?:0)<0){      
      hsRes.result.errorcode << 102
    }
    if((hsRes.inrequest.dealer_tran_rate_eur?:0)>=100 || (hsRes.inrequest.dealer_tran_rate_eur?:0)<0){      
      hsRes.result.errorcode << 103 
    }
    
    
    if(!hsRes.result.errorcode){    
      def oClient=[:]      
      def isDataChange=0
      if(lClient_id)
        oClient=Client.get(lClient_id)
      else
        oClient=new Client()        
      try {    
        if(lClient_id)
          isDataChange=oClient.checkDataChange(hsRes.inrequest)
        else
          isDataChange=1
        
        if(oClient.is_block && !hsRes.inrequest.is_block)
          new Clientlog().resetSuccessDuration(oClient.id)        
          
        oClient.csiSetData(hsRes.inrequest,lClient_id).save(failOnError:true)                
        
        hsRes.result.modstatus=oClient.modstatus
        
        if(isDataChange){
          flash.success=1
          new Clienthistory(client_id:oClient.id,
                          moddate:new Date(),
                          parent:hsRes.inrequest.parent?:0,
                          parent2:hsRes.inrequest.parent2?:0,
                          is_block:hsRes.inrequest.is_block?:0,
                          modstatus:hsRes.inrequest.modstatus?:0,
                          saldo_rub:hsRes.inrequest.account_rub?:0,
                          saldo_usd:hsRes.inrequest.account_usd?:0,
                          saldo_eur:hsRes.inrequest.account_eur?:0,
                          tran_rate_rub:hsRes.inrequest.tran_rate_rub?:0,
                          tran_rate_usd:hsRes.inrequest.tran_rate_usd?:0,
                          tran_rate_eur:hsRes.inrequest.tran_rate_eur?:0,
                          cashin_rate_rub:hsRes.inrequest.cashin_rate_rub?:0,
                          cashin_rate_usd:hsRes.inrequest.cashin_rate_usd?:0,
                          cashin_rate_eur:hsRes.inrequest.cashin_rate_eur?:0,
                          cashout_rate_rub:hsRes.inrequest.cashout_rate_rub?:0,
                          cashout_rate_usd:hsRes.inrequest.cashout_rate_usd?:0,
                          cashout_rate_eur:hsRes.inrequest.cashout_rate_eur?:0,                          
                          refill_rate_rub:hsRes.inrequest.refill_rate_rub?:0,
                          refill_rate_usd:hsRes.inrequest.refill_rate_usd?:0,
                          refill_rate_eur:hsRes.inrequest.refill_rate_eur?:0,                          
                          dealer_cashin_rub:hsRes.inrequest.dealer_cashin_rate_rub?:0,
                          dealer_cashin_usd:hsRes.inrequest.dealer_cashin_rate_usd?:0,
                          dealer_cashin_eur:hsRes.inrequest.dealer_cashin_rate_eur?:0,
                          dealer_cashout_rub:hsRes.inrequest.dealer_cashout_rate_rub?:0,
                          dealer_cashout_usd:hsRes.inrequest.dealer_cashout_rate_usd?:0,
                          dealer_cashout_eur:hsRes.inrequest.dealer_cashout_rate_eur?:0,
                          dealer_refill_rub:hsRes.inrequest.dealer_refill_rate_rub?:0,
                          dealer_refill_usd:hsRes.inrequest.dealer_refill_rate_usd?:0,
                          dealer_refill_eur:hsRes.inrequest.dealer_refill_rate_eur?:0,
                          dealer_tran_rub:hsRes.inrequest.dealer_tran_rate_rub?:0,
                          dealer_tran_usd:hsRes.inrequest.dealer_tran_rate_usd?:0,
                          dealer_tran_eur:hsRes.inrequest.dealer_tran_rate_eur?:0,
                          swiftonclient:hsRes.inrequest.swiftonclient?:0                          
                          ).save(failOnError:true,flush:true)
        }                  
        if(oClient.account_rub!=(hsRes.inrequest.account_rub?:0)) new Transaction().updatemainTransaction([client_id:oClient.id,trantype_id:19,rate:0,summa:(hsRes.inrequest.account_rub?:0)-oClient.account_rub]).save(failOnError:true)
        if(oClient.account_usd!=(hsRes.inrequest.account_usd?:0)) new Transaction().updatemainTransaction([client_id:oClient.id,trantype_id:20,rate:0,summa:(hsRes.inrequest.account_usd?:0)-oClient.account_usd]).save(failOnError:true)
        if(oClient.account_eur!=(hsRes.inrequest.account_eur?:0)) new Transaction().updatemainTransaction([client_id:oClient.id,trantype_id:21,rate:0,summa:(hsRes.inrequest.account_eur?:0)-oClient.account_eur]).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/saveClientDetail\n"+e.toString());
        hsRes.result.errorcode << 100
      }
      hsRes.result.client_id=oClient.id
    }    
    render hsRes.result as JSON
    return
  }
  def setClientPassword={
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]    
    
    def lClient_id = requestService.getLongDef('id',0)   
    hsRes+=requestService.getParams([],[],['password','confirm_pass'],[],[])               
    
    if(lClient_id){    
      if(!(hsRes.inrequest.password?:'').matches('.*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?!.*[\\W_А-я]).*')){      
        hsRes.result.errorcode << 1    
      }
      if((hsRes.inrequest.password?:'').size()<Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,8)){      
       hsRes.result.errorcode << 2	    
      }
      if((hsRes.inrequest.password?:'')!=(hsRes.inrequest.confirm_pass?:'')){
        hsRes.result.errorcode << 3
      }      
      if(!hsRes.result.errorcode){            
        def oClient=Client.get(lClient_id)                  
        try {    
          oClient.csiSetPassword(Tools.hidePsw(hsRes.inrequest.password),true).save(failOnError:true)        
        } catch(Exception e) {
          log.debug("Error save data in Admin/setClientPassword\n"+e.toString());
          
          hsRes.result.errorcode << 100
        }  
      }
    }
    render hsRes.result as JSON
    return    
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def clienthistory={
    checkAccess(2)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)    
    hsRes.action_id = 2
    hsRes.admin = session.admin    
    
    def lId=requestService.getLongDef('id',0)
    hsRes.client = Client.get(lId)
    def oHistory = new Clienthistory()    
    hsRes.history = oHistory.csiGetClientHistory(lId,20,requestService.getOffset())    

    hsRes.inrequest=[:]
    hsRes.inrequest.id=lId
    return hsRes
  }  
  def get_parent2={
    checkAccess(2)
    requestService.init(this)
    def hsRes=[:]
    
    def lCurId = requestService.getLongDef('cur_id',0)    
    def lId = requestService.getLongDef('id',0)
    if(lId)
      hsRes.dealers=Client.findAll("FROM Client WHERE (dealer_cashin_rate_rub>0 OR dealer_cashin_rate_usd>0 OR dealer_cashin_rate_eur>0 OR dealer_cashout_rate_rub>0 OR dealer_cashout_rate_usd>0 OR dealer_cashout_rate_eur>0 OR dealer_refill_rate_rub>0 OR dealer_refill_rate_usd>0 OR dealer_refill_rate_eur>0 OR dealer_tran_rate_rub>0 OR dealer_tran_rate_usd>0 OR dealer_tran_rate_eur>0) AND modstatus=1 AND id!=:cur_id AND id!=:id ORDER BY name",[cur_id:lCurId,id:lId])
    return hsRes
  }  
    
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Messages >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messages = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 3
    hsRes.admin = session.admin
    
    def fromEdit = requestService.getIntDef('fromEdit',0)
    if (fromEdit&&session.adminlastRequest){
      session.adminlastRequest.fromEdit = fromEdit
      hsRes.inrequest = session.adminlastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',1)
    }

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagelist = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 3
    hsRes.admin = session.admin
    
    if (session.adminlastRequest?.fromEdit?:0){
      hsRes.inrequest = session.adminlastRequest
      session.adminlastRequest.fromEdit = 0
    } else {
      hsRes+=requestService.getParams(['is_favourite','offset'],['client_id'],['keyword'])    
      hsRes.inrequest.modstatus=requestService.getIntDef('modstatus',1)
      session.adminlastRequest = hsRes.inrequest
    }        
    
    def oMbox = new Mbox()
    hsRes.messages = oMbox.csiSelectMbox(hsRes.inrequest.client_id?:0l,hsRes.inrequest.modstatus,hsRes.inrequest.keyword?:'',hsRes.inrequest.is_favourite?:0,0,20,hsRes.inrequest.offset?:0)
      
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagedetail = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 3
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    hsRes.to_all=requestService.getIntDef('to_all',0)
    if(lId){
      hsRes.msg = Mbox.get(lId)
      if (!hsRes.msg) {
        response.sendError(404)
        return
      }
      try {
        hsRes.msg.is_read=1      
        hsRes.msg.save(failOnError: true)      
      } catch(Exception e) {
        log.debug("Error save data in Admin/messagedetail\n"+e.toString());                
      } 
      hsRes.lastmboxrec=Mboxrec.find("FROM Mboxrec WHERE mbox_id=:mbox_id ORDER BY id DESC",[mbox_id:hsRes.msg.id])
    }else{
      try {
        hsRes.msg = new Mbox()  
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
    checkAccess(3)
    requestService.init(this)    
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[],image_errorcode:[]]    
 
    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['to_all','client_type'],['client_id'],['subject','mtext'])
         
    
    if(!hsRes.inrequest.client_id && !hsRes.inrequest.to_all)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.subject)
      hsRes.result.errorcode<<2
    if(!hsRes.inrequest.mtext)
      hsRes.result.errorcode<<3                                        
          
    if(!hsRes.result.errorcode && lId){                          
        
      if(hsRes.inrequest.to_all){
        hsRes.inrequest.filename=''
        hsRes.inrequest.is_read=1
        
        def lsClients=[]
        if(hsRes.inrequest?.client_type)
          lsClients=Client.findAll("FROM Client WHERE (dealer_cashin_rate_rub>0 OR dealer_cashin_rate_usd>0 OR dealer_cashin_rate_eur>0 OR dealer_cashout_rate_rub>0 OR dealer_cashout_rate_usd>0 OR dealer_cashout_rate_eur>0 OR dealer_refill_rate_rub>0 OR dealer_refill_rate_usd>0 OR dealer_refill_rate_eur>0 OR dealer_tran_rate_rub>0 OR dealer_tran_rate_usd>0 OR dealer_tran_rate_eur>0) AND modstatus=1")
        else
          lsClients=Client.findAllWhere(modstatus:1)
        
        for(oClient in lsClients){          
          def oMessage=new Mbox()          
          hsRes.inrequest.client_id=oClient?.id
          
          try {                              
          oMessage.setData(hsRes.inrequest).save(failOnError:true,flush:true)
          
          new Mboxrec(mbox_id:oMessage.id,
                    inputdate:new Date(),
                    mtext:hsRes.inrequest.mtext?:'',
                    filename:hsRes.inrequest.filename?:'',
                    is_fromclient:0
                    ).save(failOnError:true,flush:true)                                                           
        } catch(Exception e) {
          log.debug("Error save data in Admin/saveMessageDetail\n"+e.toString());        
          hsRes.result.errorcode << 100
        }
        
        }
      }else{
        def oMessage=Mbox.get(lId)                              
     
        imageService.init(this,'mailtopic','mailkeeppic',lId.toString()+'_') // 0
        def hsPics=imageService.getSessionPics('file1')          
        imageService.finalizeFileSession(['file1'])
        hsRes.inrequest.filename=(hsPics?.photo)?:''      
          
        try {                    
          hsRes.inrequest.is_read=1
          oMessage.setData(hsRes.inrequest).save(failOnError:true,flush:true)
          
          new Mboxrec(mbox_id:oMessage.id,
                    inputdate:new Date(),
                    mtext:hsRes.inrequest.mtext?:'',
                    filename:hsRes.inrequest.filename?:'',
                    is_fromclient:0
                    ).save(failOnError:true,flush:true)                                                           
        } catch(Exception e) {
          log.debug("Error save data in Admin/saveMessageDetail\n"+e.toString());        
          hsRes.result.errorcode << 100
        } 
      }              
    }
    render hsRes.result as JSON
    return    
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def savemailpicture={
    checkAccess(3)
    requestService.init(this)
    imageService.init(this,'mailtopic','mailkeeppic',requestService.getLongDef('mbox_id',0).toString()+'_') // 0    
    def hsData = imageService.loadPicture("file1",Tools.getIntVal(ConfigurationHolder.config.photo.weight,4194304),requestService.getLongDef('nrec',0),Tools.getIntVal(ConfigurationHolder.config.photo.thumb.size,220),Tools.getIntVal(ConfigurationHolder.config.photo.thumb.height,160))      
    
    render(view:'savepictureresult',model:hsData)
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def setMessageArchive = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]      

    def lId=requestService.getLongDef('id',0)
    if(lId){
      def oMbox=Mbox.get(lId)
      try {    
        oMbox.setArchive().save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/setMessageArchive\n"+e.toString());          
        hsRes.result.errorcode << 100
      } 
    }
    render hsRes.result as JSON
    return
  }
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def setMessageFavourite = {
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.result=[errorcode:[]]      

    def lId=requestService.getLongDef('id',0)
    if(lId){
      def oMbox=Mbox.get(lId)
      try {
        if(!oMbox.is_favourite)
          oMbox.is_favourite=1       
        else  
          oMbox.is_favourite=0                 
        oMbox.save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/setMessageFavourite\n"+e.toString());          
        hsRes.result.errorcode << 100
      } 
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def messagehistory={
    checkAccess(3)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)    
    hsRes.action_id = 3
    hsRes.admin = session.admin    
    
    def lId=requestService.getLongDef('id',0)    
    hsRes.msg = Mbox.get(lId)
    if(lId && hsRes.msg){
      def oHistory = new Mboxrec()    
      hsRes.history = oHistory.csiGetMboxRec(lId,20,requestService.getOffset())       
    }

    return hsRes
  }  
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def picture = {
    requestService.init(this)

    def hsRes=[:]
    hsRes.client = session.client

    def oPicture = Picture.findWhere(filename:(Mbox.get(requestService.getLongDef('id',0))?.id?.toString()?:'')+'_'+requestService.getStr('filename'))
    if(oPicture){
      response.contentType = oPicture.mimetype
      response.outputStream << oPicture.filedata
    } else {
      response.sendError(404)
    }
    response.flushBuffer()
  }

  def remEmptyMbox={
    checkAccess(3)
    requestService.init(this) 
    def lId=requestService.getLongDef('id',0) 
    def oMbox=Mbox.findWhere(id:lId,nrec:0)
    if(oMbox)
      oMbox.delete(flush:true)
    render(contentType:"application/json"){[error:false]}  
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Requests >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requests = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)   
    hsRes.action_id = 4
    hsRes.admin = session.admin

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
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes+=requestService.getParams(['trantype_id'],['client_id','request_id'])
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-100)
      hsRes.inrequest.date_start = requestService.getDate('date_start')
      hsRes.inrequest.date_end = requestService.getDate('date_end')
      hsRes.inrequest.offset = requestService.getOffset()
      session.lastRequest = hsRes.inrequest
    }

    def oRequest = new RequestAdmin()
    hsRes.requests = oRequest.csiSelectRequests(hsRes.inrequest.client_id?:0l,hsRes.inrequest.trantype_id?[hsRes.inrequest.trantype_id]:null,hsRes.inrequest.modstatus,hsRes.inrequest.date_start?:'',hsRes.inrequest.date_end?:'',hsRes.inrequest.request_id?:0l,20,hsRes.inrequest.offset)
    hsRes.reqmodstatus = Reqmodstatus.findAllByIdNot(0).inject([:]){map, status -> map[status.id]=[name:status.name,icon:status.icon];map}
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requestdetail = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    def lId=requestService.getLongDef('id',0)
    hsRes.req = Request.get(lId)
    if (!hsRes.req&&lId) {
      response.sendError(404)
      return
    }
    hsRes.trantypes = Trantype.findAllById(hsRes.req?.trantype_id?:0)
    hsRes.isCanCancel = hsRes.req?.id==Transaction.getLastRequestId()
    hsRes.formid = java.util.UUID.randomUUID().toString()
    session.formid=[currentformid:hsRes.formid]

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def gettrantype = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin
    
    def lId=requestService.getLongDef('id',0)
    hsRes.trantypes = Trantype.findAllByPermissionsInList(Client.get(lId)?.properties.findAll{it.key.matches('is_.*')&&it.value==1}.collect{it.key})
    
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requesttype = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes+=requestService.getParams(['type_id'],['id','client_id'])

    hsRes.req=Request.get(hsRes.inrequest.id?:0)
    hsRes.trantype=Trantype.get(hsRes.inrequest.type_id?:0)
    hsRes.client = Client.get(hsRes.inrequest.client_id?:0)
    //Осторожно! Извращения! Не повторять! Выполнено профессиональными каскадерами! >>>
    hsRes.baseaccounts = hsRes.client?.properties?.findAll{it.key.matches("(?=account_)(?=.*(${hsRes.trantype?.code?.toLowerCase()=='rub'?Trantype.list().collect{it.code.toLowerCase()}.unique().join('|'):'rub|'+hsRes.trantype?.code?.toLowerCase()})).*")}.collect{[key:it.key-'account_',value:it.value+' '+(it.key-'account_')]}
    //<<<
    if (hsRes.trantype.valuta_id!=643){
      hsRes.vrate = new Valutarate().csiSearchCurrent(hsRes.trantype.valuta_id).vrate
      hsRes.swiftsumma = Tools.getIntVal(ConfigurationHolder.config.swiftsumma.summa."${hsRes.trantype.code}",100)
    }
    if((hsRes.req?.baseaccount?:'rub')!='rub'){
      hsRes.comvrate = new Valutarate().csiSearchCurrent(Valuta.findByCode(hsRes.req.baseaccount.toUpperCase())?.id).vrate
    }
    hsRes.bankcomsumma = Math.rint(Tools.getFloatVal(ConfigurationHolder.config.bankcomission.summa.percent,0.5)*(hsRes.req?.summa?:0)*(hsRes.vrate?:1f))/100
    hsRes.bankcompercent = Tools.getFloatVal(ConfigurationHolder.config.bankcomission.summa.percent,0.5)
    hsRes.bankcomconvsumma = Tools.getIntVal(ConfigurationHolder.config.bankcomconvsumma.summa.rub,100)

    render(view:hsRes.trantype?.formname,model:hsRes)
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def bik_autocomplete = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

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
  def name_autocomplete = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByNameIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name;
        hsRes.data << it.id+';'+it.inn+';'+it.kpp+';'+it.ogrn+';'+it.bik+';'+it.cor_account+';'+it.bank+';'+it.city+';'+it.account+';'+it.prim
      }
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def beneficial_autocomplete = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    hsRes.data = []
    if(hsRes.query?:''){
      Company.findAllByBeneficialIlike(hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.beneficial;
        hsRes.data << it.id+';'+it.iban+';'+it.bbank+';'+it.baddress+';'+it.swift+';'+it.purpose+';'+it.laddress
      }
    }
    render hsRes as JSON
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveRequestDetail = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    def lId = requestService.getLongDef('id',0)
    hsRes+=requestService.getParams(['trantype_id','modstatus','nds'],['client_id','company_id','parent','syscompany_id'],
                                    ['inn','kpp','ogrn','name','bik','bank','bankcity','cor_account','account','prim',
                                     'beneficial','iban','bbank','baddress','swift','purpose','comment','baseaccount','laddress','formid','syscompany_name'],null,['summa','vrate','comvrate','bankcomsumma','bankcomconvsumma','swiftsumma','basebankcomsumma'])
    hsRes.inrequest.platdate = requestService.getDate('platdate')
    hsRes.inrequest.reqdate = requestService.getDate('reqdate')
    hsRes.inrequest.strbankcomsumma = requestService.getStr('bankcomsumma')

    hsRes.request = Request.get(lId)
    if ((!hsRes.request&&lId)||!((hsRes.request?.modstatus?:0) in -2..2)||session.formid?.currentformid!=(hsRes.inrequest?.formid?:'')) {
      render(contentType:"application/json"){[error:true]}
      return
    } else session.formid = [currentformid:java.util.UUID.randomUUID().toString()]
    hsRes.result=[errorcode:[],formid:session.formid.currentformid]

    if(!lId&&!hsRes.inrequest.trantype_id)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.summa||hsRes.inrequest.summa<=0)
      hsRes.result.errorcode<<2
    if(hsRes.inrequest.trantype_id==1){
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
      if(!(hsRes.inrequest.syscompany_id?:hsRes.request?.syscompany_id))
        hsRes.result.errorcode<<22
    }else if(hsRes.inrequest.trantype_id in [2,3]){
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
    if(!lId&&!hsRes.inrequest.client_id)
      hsRes.result.errorcode << 14
    else if (!hsRes.request&&!Trantype.findByPermissionsInListAndId(Client.get(hsRes.inrequest.client_id)?.properties?.findAll{it.key.matches('is_.*')&&it.value==1}?.collect{it.key},hsRes.inrequest.trantype_id))
      hsRes.result.errorcode << 15
    if (hsRes.inrequest.modstatus==-1&&!hsRes.inrequest.comment)
      hsRes.result.errorcode << 16
    if (hsRes.inrequest.modstatus in [2,3]&&!Client.get(hsRes.request?.client_id)?.checkAvailableSum(hsRes.request))
      hsRes.result.errorcode << 17
    if((hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id) in [10,11,12]){
      if(!hsRes.inrequest.platdate)
        hsRes.result.errorcode<<18
    }

    hsRes.inrequest.rate = Client.get(hsRes.inrequest.client_id?:hsRes.request?.client_id)?."${Trantype.get(hsRes.inrequest.trantype_id?:hsRes.request?.trantype_id)?.rate}"

    if(!hsRes.result.errorcode||hsRes.result.errorcode==[17]){
      try {
        if(!lId) hsRes.request = new Request([client_id:hsRes.inrequest.client_id])
        hsRes.result.req = hsRes.request.setData(hsRes.inrequest,hsRes.inrequest.company_id).doRequest().save(failOnError:true,flush:true)?.id?:0
      } catch(Exception e) {
        log.debug("Error save data in Admin/saveRequestDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def requesthistory = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes.req = Request.get(requestService.getLongDef('id',0))
    hsRes.history = Requesthistory.findAllByRequest_id(hsRes.req?.id,[sort:'id',order:'desc'])

    return hsRes
  }

  def requestoperation = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes.req = Request.get(requestService.getLongDef('id',0))
    hsRes.operation = Transaction.findAllByRequest_id(hsRes.req?.id)
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    return hsRes
  }

  def cancelllastrequest = {
    checkAccess(4)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 4
    hsRes.admin = session.admin

    hsRes.req = Request.get(requestService.getLongDef('id',-1))
    if (hsRes.req?.id==Transaction.getLastRequestId()) {
      try {
        hsRes.req.cancellRequest().save(failOnError:true,flush:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/cancelllastrequest\n"+e.toString())
      }
    }

    render(contentType:"application/json"){[error:false]}
    return
  }

  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Operations >>>//////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operations = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 5
    hsRes.admin = session.admin

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operationlist = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 5
    hsRes.admin = session.admin
    
    hsRes+=requestService.getParams(['trantype_id'],['id','request_id','client_id','syscompany_id'],['valuta'],['date_start','date_end'])
    hsRes.date_start = requestService.getDate('date_start')
    hsRes.date_end = requestService.getDate('date_end')

    def oOperation = new Transaction()
    hsRes.operations = oOperation.csiSelectOperations(hsRes.inrequest.trantype_id?:0,hsRes.inrequest.client_id?:0l,hsRes.inrequest.id?:0l,hsRes.inrequest.request_id?:0l,hsRes.date_start?:'',hsRes.date_end?:'',hsRes.inrequest.syscompany_id?:0,20,requestService.getOffset())
    hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def outgoings = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 5
    hsRes.admin = session.admin

    hsRes.monthname = message(code:'calendar.monthName').split(',')[new Date().getMonth()]
    return hsRes
  }

  def saveoutgoings = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes+=requestService.getParams(null,null,['comment'],null,['summa'])
    hsRes.result=[errorcode:[]]

    if(!hsRes.inrequest.summa||hsRes.inrequest.summa<0)
      hsRes.result.errorcode << 1
    if(!hsRes.inrequest.comment)
      hsRes.result.errorcode << 2

    if(!hsRes.result.errorcode){
      try {
        new Transaction().updateSysOutgoingsTransaction(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/saveoutgoings\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def operationdetail = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 5
    hsRes.admin = session.admin

    def lId=requestService.getLongDef('id',0)
    hsRes.transaction = Transaction.get(lId)
    if (!hsRes.transaction) {
      response.sendError(404)
      return
    }
    hsRes.trantype = Trantype.get(hsRes.transaction.trantype_id)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def saveOperationDetail = {
    checkAccess(5)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)

    hsRes+=requestService.getParams(null,['id'],['comment'])

    hsRes.transaction = Transaction.get(hsRes.inrequest.id)
    if (!hsRes.transaction) {
      render(contentType:"application/json"){[error:true]}
      return
    }
    hsRes.result=[errorcode:[]]

    if(!hsRes.result.errorcode){
      try {
        hsRes.transaction.updateAdminData(hsRes.inrequest).save(failOnError:true)
      } catch(Exception e) {
        log.debug("Error save data in Admin/saveOperationDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }
    render hsRes.result as JSON
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Statistics >>>//////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def statistics = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 6
    hsRes.admin = session.admin
    hsRes.clients=Client.findAll("FROM Client ORDER BY name")

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def statisticlist = {
    checkAccess(6)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    //hsRes.action_id = 6
    //hsRes.admin = session.admin
   
    def oClientlog = new Clientlog()
    hsRes+=oClientlog.csiGetLogsPaging(requestService.getLongDef('client_id',0),20,requestService.getOffset()) 
    
    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Reports >>>/////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def reports = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    return hsRes
  }

  def reqreport = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('reqreport_start')
    hsRes.report_end = requestService.getDate('reqreport_end')

    def oObject = new RequestReportSearch()
    hsRes.report = oObject.csiSelectRequests(0l,hsRes.report_start,hsRes.report_end)
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = 0g
    hsRes.report.each{
      hsRes."sum_$it.trcode" += it.summa
    }

    renderPdf(template:'reqreport',model:hsRes,filename:'reqreport.pdf')
  }

  def reqreportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('reqreport_start')
    hsRes.report_end = requestService.getDate('reqreport_end')

    def oObject = new RequestReportSearch()
    hsRes.report = oObject.csiSelectRequests(0l,hsRes.report_start,hsRes.report_end)
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = hsRes.sum_cash = hsRes.sum_transit = hsRes.sum_conv = hsRes.sum_refill = 0g
    hsRes.report.each{
      hsRes."sum_$it.trcode" += it.summa
      hsRes.sum_cash += (it.trantype_id==7 ? it.summa : 0)
      hsRes.sum_transit += (it.trantype_id==1 ? it.summa : 0)
      hsRes.sum_conv += (it.trantype_id in [2,3] ? it.summa*it.vrate : 0)
      hsRes.sum_refill += (it.trantype_id==10 ? it.summa : 0)
    }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 9
      def title = "Отчет по выполненным запросам "+(!(hsRes.report_start||hsRes.report_end)?"за все время":((hsRes.report_start?"с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(1,30*256)
        putCellValue(0, 1, title)
        putCellValue(2, 0, 'кеш')
        putCellValue(2, 1, hsRes.sum_cash)
        putCellValue(3, 0, 'транзит')
        putCellValue(3, 1, hsRes.sum_transit)
        putCellValue(4, 0, 'конверт')
        putCellValue(4, 1, hsRes.sum_conv)
        putCellValue(5, 0, 'пополнение')
        putCellValue(5, 1, hsRes.sum_refill)
        putCellValue(6, 0, 'приход')
        fillRow(['Номер запроса','Дата запроса','Клиент','Тип запроса','Валюта запроса','Сумма запроса','Комиссия системы, руб'],8,false,Tools.getXlsTableHeaderStyle(7))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.id.toString(),
                   String.format('%tF',record.moddate),
                   record.clname,
                   record.trname,
                   record.trcode,
                   record.summa,
                   record.syssumma], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(7) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(7) : Tools.getXlsTableLineStyle(7))
        }
        fillRow(["ИТОГО", "", "", "", "", ""], rowCounter++, false)
        fillRow(["", "Сумма запросов по рублям", "", hsRes.sum_RUB], rowCounter++, false, [null]+Tools.getXlsTableFirstLineStyle(3))
        fillRow(["", "Сумма запросов по долларам", "", hsRes.sum_USD], rowCounter++, false, [null]+Tools.getXlsTableLineStyle(3))
        fillRow(["", "Сумма запросов по евро", "", hsRes.sum_EUR], rowCounter++, false, [null]+Tools.getXlsTableLastLineStyle(3))
        setColumnAutoWidth(2)
        setColumnAutoWidth(3)
        save(response.outputStream)
      }
    }
    return
  }

  def clsaldoreport = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report = new Client().csiSelectReportClients()
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = 0g
    hsRes.report.records.each{
      hsRes.sum_RUB += it.account_rub
      hsRes.sum_USD += it.account_usd
      hsRes.sum_EUR += it.account_eur
    }

    renderPdf(template:'clsaldoreport',model:hsRes,filename:'clsaldoreport.pdf')
  }

  def clsaldoreportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin


    hsRes.report = new Client().csiSelectReportClients()
    hsRes.sum_RUB = hsRes.sum_USD = hsRes.sum_EUR = 0g
    hsRes.report.records.each{
      hsRes.sum_RUB += it.account_rub
      hsRes.sum_USD += it.account_usd
      hsRes.sum_EUR += it.account_eur
    }

    if (hsRes.report.records.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      def title = "Отчет по клиентским остаткам"
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 1, title)
        fillRow(['Название','Остаток, руб','Остаток, usd','Остаток, eur'],3,false,Tools.getXlsTableHeaderStyle(4))
        hsRes.report.records.eachWithIndex{ record, index ->
          fillRow([record.name,
                   record.account_rub,
                   record.account_usd,
                   record.account_eur], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(4) : index == hsRes.report.records.size()-1 ? Tools.getXlsTableLastLineStyle(4) : Tools.getXlsTableLineStyle(4))
        }
        fillRow(["ИТОГО", hsRes.sum_RUB, hsRes.sum_USD, hsRes.sum_EUR], rowCounter++, false, Tools.getXlsTableLastLineStyle(4))
        setColumnAutoWidth(0)
        save(response.outputStream)
      }
    }
    return
  }

  def transreport = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('transreport_start')
    hsRes.report_end = requestService.getDate('transreport_end')
    hsRes.type = requestService.getIntDef('type',0)

    def oObject = new TransactionAdminReportSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.report_start,hsRes.report_end,hsRes.type)
    hsRes.mainobor = hsRes.addobor = 0g
    hsRes.report.each{
      hsRes."${(it.trantype_id in [13,22])?'mainobor':'addobor'}" += it.summa * it.vrate
    }

    renderPdf(template:'transreport',model:hsRes,filename:'transreport.pdf')
  }

  def transreportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('transreport_start')
    hsRes.report_end = requestService.getDate('transreport_end')
    hsRes.type = requestService.getIntDef('type',0)

    def oObject = new TransactionAdminReportSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.report_start,hsRes.report_end,hsRes.type)
    hsRes.mainobor = hsRes.addobor = 0g
    hsRes.report.each{
      hsRes."${(it.trantype_id in [13,22])?'mainobor':'addobor'}" += it.summa * it.vrate
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
        setColumnWidth(1,30*256)
        putCellValue(0, 1, title)
        fillRow(['Номер операции','Дата операции','Тип операции','Сумма операции','Сальдо'],3,false,Tools.getXlsTableHeaderStyle(5))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.id.toString(),
                   String.format('%tF',record.inputdate),
                   record.trname,
                   record.summa*record.vrate,
                   (record.trantype_id in [13,22])?record.saldo:''], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(5) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(5) : Tools.getXlsTableLineStyle(5))
        }
        fillRow(["ИТОГО", "", "", "", ""], rowCounter++, false)
        fillRow(["", "Сальдо на начало периода", "", hsRes.report.first().saldo-hsRes.report.first().summa*hsRes.report.first().vrate, ""], rowCounter++, false)
        fillRow(["", "Обороты за период", "", hsRes.mainobor, ""], rowCounter++, false)
        if (hsRes.type) fillRow(["", "Обороты по затратам", "", hsRes.addobor, ""], rowCounter++, false)
        fillRow(["", "Сальдо на конец периода", "", hsRes.report.findAll{it.trantype_id in [13,22]}.last().saldo, ""], rowCounter++, false)
        setColumnAutoWidth(2)
        save(response.outputStream)
      }
    }
    return
  }

  def dealerfeereport = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('feereport_start')
    hsRes.report_end = requestService.getDate('feereport_end')

    def oObject = new DealerFeeAdminReportSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.report_start,hsRes.report_end)
    hsRes.overall = 0g
    hsRes.overallcount = 0
    hsRes.report.each{
      hsRes.overall += it.feesumma
      hsRes.overallcount += it.trcount
    }

    renderPdf(template:'feereport',model:hsRes,filename:'feereport.pdf')
  }

  def dealerfeereportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('feereport_start')
    hsRes.report_end = requestService.getDate('feereport_end')

    def oObject = new DealerFeeAdminReportSearch()
    hsRes.report = oObject.csiSelectOperations(hsRes.report_start,hsRes.report_end)
    hsRes.overall = 0g
    hsRes.overallcount = 0
    hsRes.report.each{
      hsRes.overall += it.feesumma
      hsRes.overallcount += it.trcount
    }

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      def rowCounter = 4
      def title = "Отчет по вознаграждениям посредников "+(!(hsRes.report_start||hsRes.report_end)?"за все время":((hsRes.report_start?"с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 3, title)
        fillRow(['Посредник','Сумма вознаграждений','Кол-во операций'],3,false,Tools.getXlsTableHeaderStyle(3))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.clname, record.feesumma, record.trcount.toString()], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(3) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(3) : Tools.getXlsTableLineStyle(3))
        }
        fillRow(["ИТОГО", "", ""], rowCounter++, false)
        fillRow(["Сумма всех вознаграждений", hsRes.overall, ""], rowCounter++, false)
        fillRow(["Кол-во операций", hsRes.overallcount.toString(), ""], rowCounter++, false)
        save(response.outputStream)
      }
    }
    return
  }

  def revisereportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('revisereport_start')
    hsRes.report_end = requestService.getDate('revisereport_end')
    hsRes.client_name = requestService.getStr('client_name')

    hsRes.report = new RequestReportSearch().csiSelectRequests(0l,hsRes.report_start,hsRes.report_end,hsRes.client_name?:'')

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}
      def rowCounter = 3
      def title = "Сверка.${hsRes.client_name?' Клиент: '+hsRes.client_name:' Все клиенты.'}"+(!(hsRes.report_start||hsRes.report_end)?" за все время":((hsRes.report_start?" с ${String.format('%tF',hsRes.report_start)}":"")+" по ${String.format('%tF',hsRes.report_end?:new Date())}"))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        setColumnWidth(2,30*256)
        putCellValue(0, 2, title)
        fillRow(['Клиент','Дата исполнения','Тип запроса','Сумма запроса','Валюта','Откуда','Куда','Процент комиссии','Процент посредника','Сумма комиссии с клиента','Сумма на покупку валюты','Банковская комиссия','Курс','Курс ЦБ','Свифт','Остаток клиента','Наш доход','Бонус посредника'],rowCounter++,false,Tools.getXlsTableHeaderStyle(18))
        hsRes.report.eachWithIndex{ record, index ->
          fillRow([record.clname,
                   String.format('%tF',record.moddate),
                   record.trname,
                   record.summa,
                   record.trcode,
                   record.trantype_id in [10,11,12]?record.name:'',
                   record.trantype_id in [10,11,12]?(record.syscompany_name?:Company.get(record.syscompany_id)?.name?:''):record.trantype_id in [1]?record.name:record.trantype_id in [2,3]?record.beneficial:'',
                   record.rate,
                   Transaction.findAllByRequest_idAndTrantype_id(record.id,16).sum{it.summa*100/record.summa}?:0,
                   Transaction.findAllByRequest_idAndTrantype_idInList(record.id,[23,24,25,26]).sum{it.summa*it.vrate}?:0,
                   Transaction.findAllByRequest_idAndTrantype_id(record.id,31).sum{it.summa*it.vrate}?:0,
                   record.bankcomsumma,
                   record.comvrate>1?record.comvrate:null,
                   record.vrate>1?record.vrate:null,
                   record.swiftsumma,
                   Transaction.findByRequest_idAndTrantype_idInList(record.id,[23,24,25,26])?.saldo?:0,
                   record.syssumma,
                   Transaction.findAllByRequest_idAndTrantype_id(record.id,16).sum{it.summa}?:0], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(18) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(18) : Tools.getXlsTableLineStyle(18))
        }
        setColumnAutoWidth(0)
        setColumnAutoWidth(5)
        setColumnAutoWidth(6)
        save(response.outputStream)
      }
    }
    return
  }

  def bronreportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.report_start = requestService.getDate('bronreport_start')
    hsRes.report_end = requestService.getDate('bronreport_end')
    hsRes.client_name = requestService.getStr('client_name')

    hsRes.report = new RequestAdmin().csiSelectBronRequests(hsRes.client_name?:'',[7,8,9],2,hsRes.report_start,hsRes.report_end)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}
      def rowCounter = 3
      def title = "Отчет по бронированию средств.${hsRes.client_name?' Клиент: '+hsRes.client_name:''}"+(!(hsRes.report_start||hsRes.report_end)?" за все время":((hsRes.report_start?" с ${String.format('%tF',hsRes.report_start)}":"")+(hsRes.report_end?" по ${String.format('%tF',hsRes.report_end?:new Date())}":"")))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 1, title)
        hsRes.report.groupBy{it.reqdate}.each{ date ->
          putCellValue(rowCounter, 0, 'Дата выдачи')
          putCellValue(rowCounter++, 1, String.format('%tF',date.key?:new Date()))
          fillRow(['Клиент','Сумма к выдаче','Валюта','Текущий остаток'],rowCounter++,false,Tools.getXlsTableHeaderStyle(4))
          date.value.eachWithIndex{ record, index ->
            fillRow([record.client_name, record.summa, hsRes.trantypes[record.trantype_id].code, record.cl_account_rub], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(4) : index == date.value.size()-1 ? Tools.getXlsTableLastLineStyle(4) : Tools.getXlsTableLineStyle(4))
          }
          fillRow(["ИТОГО", date.value.sum{it.summa*it.vrate}], rowCounter++, false)
          rowCounter++
        }
        rowCounter++
        fillRow(["Общая сумма бронирования", hsRes.report.sum{it.summa*it.vrate}], rowCounter++, false, Tools.getXlsTableLineStyle(2))
        save(response.outputStream)
      }
    }
    return
  }

  def syscompreportXLS = {
    checkAccess(7)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 7
    hsRes.admin = session.admin

    hsRes.inrequest=[:]
    hsRes.inrequest.report_start = requestService.getDate('syscompreport_start')
    hsRes.inrequest.report_end = requestService.getDate('syscompreport_end')
    hsRes.inrequest.company_name = requestService.getStr('company_name')

    hsRes.report = new RequestCompany()."${hsRes.inrequest.company_name?'csiSelectCompanyRequests':'csiSelectSummary'}"(hsRes.inrequest)

    if (hsRes.report.size()==0) {
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 4, "Нет данных за указанный период")
        save(response.outputStream)
      }
    } else {
      hsRes.trantypes = Trantype.list().inject([:]){map, trantype -> map[trantype.id]=[name:trantype.name,code:trantype.code];map}
      def rowCounter = 3
      def title = "Движение средств по ${hsRes.inrequest.company_name?' компании '+hsRes.inrequest.company_name:' системным компаниям'}"+(!(hsRes.inrequest.report_start||hsRes.inrequest.report_end)?" за все время":((hsRes.inrequest.report_start?" с ${String.format('%tF',hsRes.inrequest.report_start)}":"")+(hsRes.inrequest.report_end?" по ${String.format('%tF',hsRes.inrequest.report_end?:new Date())}":"")))
      new WebXlsxExporter().with {
        setResponseHeaders(response)
        putCellValue(0, 1, title)
        if (hsRes.inrequest.company_name){
          fillRow(['Дата','Клиентская компания','Тип платежа','Приход','Расход'],rowCounter++,false,Tools.getXlsTableHeaderStyle(5))
          hsRes.report.eachWithIndex{ record, index ->
            fillRow([record.moddate,
                     record.name,
                     hsRes.trantypes[record.trantype_id].name,
                     record.creditsum,
                     record.debetsum], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(5) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(5) : Tools.getXlsTableLineStyle(5))
          }
          fillRow(['','',"ИТОГО", hsRes.report.sum{it.creditsum}, hsRes.report.sum{it.debetsum}], rowCounter++, false, Tools.getXlsTableLineStyle(5))
        } else {
          fillRow(['Системная компания','Приход','Расход'],rowCounter++,false,Tools.getXlsTableHeaderStyle(3))
          hsRes.report.eachWithIndex{ record, index ->
            fillRow([record.company_name,
                     record.creditsum,
                     record.debetsum], rowCounter++, false, index == 0 ? Tools.getXlsTableFirstLineStyle(3) : index == hsRes.report.size()-1 ? Tools.getXlsTableLastLineStyle(3) : Tools.getXlsTableLineStyle(3))
          }
          fillRow(["ИТОГО", hsRes.report.sum{it.creditsum}, hsRes.report.sum{it.debetsum}], rowCounter++, false, Tools.getXlsTableLineStyle(3))
        }
        save(response.outputStream)
      }
    }
    return
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////Companies >>>////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def companies = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 8
    hsRes.admin = session.admin

    def fromDetails = requestService.getIntDef('fromDetails',0)
    if (fromDetails&&session.lastRequest){
      session.lastRequest.fromDetails = fromDetails
      hsRes.inrequest = session.lastRequest
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-1)
    }

    return hsRes
  }

  def companyname_autocomplete = {
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.admin = session.admin

    hsRes.query = requestService.getStr('query')
    hsRes.suggestions = []
    if(hsRes.query?:''){
      Company.findAllByIs_systemAndNameIlike(1,hsRes.query+'%',[max:10]).each{
        hsRes.suggestions << it.name
      }
    }
    render hsRes as JSON
  }

  def companylist = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 8
    hsRes.admin = session.admin

    if (session.lastRequest?.fromDetails?:0) {
      hsRes.inrequest = session.lastRequest
      session.lastRequest.fromDetails = 0
    } else {
      hsRes.inrequest=[:]
      hsRes.inrequest.modstatus = requestService.getIntDef('modstatus',-1)
      hsRes.inrequest.name = requestService.getStr('name')
      hsRes.inrequest.offset = requestService.getOffset()
      session.lastRequest = hsRes.inrequest
    }

    def oCompany = new Company()
    hsRes.companies = oCompany.csiSelectCompanies(hsRes.inrequest.modstatus,hsRes.inrequest.name?:'',20,hsRes.inrequest.offset)

    return hsRes
  }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////////
  def companydetail = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 8
    hsRes.admin = session.admin

    def lId=requestService.getLongDef('id',0)
    hsRes.company = Company.findWhere(id:lId,is_system:1)
    if (!hsRes.company&&lId) {
      response.sendError(404)
      return
    }

    return hsRes
  }

  def setcompanystatus = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 8
    hsRes.admin = session.admin

    def lId=requestService.getLongDef('id',0)

    def oCompany = Company.findWhere(id:lId,is_system:1)
    if (!oCompany) {
      response.sendError(404)
      return
    }
    oCompany.modstatus=requestService.getIntDef('modstatus',0)

    if (!oCompany.save(flush:true)){
      log.debug('error on save Company in Admin:setcompanystatus')
      oCompany.errors.each{log.debug(it)}
    }
    render(contentType:"application/json"){[error:false]}
    return
  }
  def saveCompanyDetail = {
    checkAccess(8)
    requestService.init(this)
    def hsRes=requestService.getContextAndDictionary(true)
    hsRes.action_id = 8
    hsRes.admin = session.admin

    hsRes+=requestService.getParams([],[],['inn','kpp','ogrn','name','bik','bank','cor_account','account','beneficial','iban','comment'])

    def lId=requestService.getLongDef('id',0)

    def oCompany = Company.findWhere(id:lId,is_system:1)
    if (lId && !oCompany) {
      response.sendError(404)
      return
    }
    hsRes.result=[errorcode:[],company_id:0]

    if(!hsRes.inrequest.name)
      hsRes.result.errorcode<<1
    if(!hsRes.inrequest.inn)
      hsRes.result.errorcode<<2

    if(!hsRes.result.errorcode){
      try {
        if(!lId) oCompany=new Company()
        hsRes.result.company_id = oCompany.updateData(hsRes.inrequest).csiSetData(hsRes.inrequest,lId).save(failOnError:true,flush:true)?.id?:0
        flash.success=1
      } catch(Exception e) {
        log.debug("Error save data in Admin/saveCompanyDetail\n"+e.toString())
        hsRes.result.errorcode << 100
      }
    }

    render hsRes.result as JSON
    return
  }

}
