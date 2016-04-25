class Client {
  def searchService
  def sessionFactory

  static mapping = {
    version false
    sort "name"
  }

  Long id
  Long parent = 0
  Long parent2 = 0

  Integer is_block = 0
  Integer modstatus = 0
  Integer type_id = 1
  Integer is_passwordchange = 0

	String name
	String password
  Integer swiftonclient = 1
	String comment = ''
  String ip
  String login

  Date inputdate
	Date moddate = new Date()
  
  BigDecimal account_rub = 0.00
	BigDecimal account_usd = 0.00
	BigDecimal account_eur = 0.00

	BigDecimal tran_rate_rub = 0.00
	BigDecimal tran_rate_usd = 0.00
	BigDecimal tran_rate_eur = 0.00
	BigDecimal cashin_rate_rub = 0.00
	BigDecimal cashin_rate_usd = 0.00
	BigDecimal cashin_rate_eur = 0.00
	BigDecimal cashout_rate_rub = 0.00
	BigDecimal cashout_rate_usd = 0.00
	BigDecimal cashout_rate_eur = 0.00	

  BigDecimal dealer_cashin_rate_rub = 0.00
  BigDecimal dealer_cashin_rate_usd = 0.00
  BigDecimal dealer_cashin_rate_eur = 0.00
  BigDecimal dealer_cashout_rate_rub = 0.00
  BigDecimal dealer_cashout_rate_usd = 0.00
  BigDecimal dealer_cashout_rate_eur = 0.00
  BigDecimal dealer_refill_rate_rub = 0.00
  BigDecimal dealer_refill_rate_usd = 0.00
  BigDecimal dealer_refill_rate_eur = 0.00
  BigDecimal dealer_tran_rate_rub = 0.00
  BigDecimal dealer_tran_rate_usd = 0.00
  BigDecimal dealer_tran_rate_eur = 0.00

  BigDecimal refill_rate_rub = 0.00
  BigDecimal refill_rate_eur = 0.00
  BigDecimal refill_rate_usd = 0.00

  Integer is_tran_rub = 0
  Integer is_tran_eur = 0
  Integer is_tran_usd = 0
  
  Integer is_cashin_rub = 0
  Integer is_cashin_eur = 0
  Integer is_cashin_usd = 0
  
  Integer is_cashout_rub = 0
  Integer is_cashout_eur = 0
  Integer is_cashout_usd = 0
  
  Integer is_refill_rub = 0
  Integer is_refill_eur = 0
  Integer is_refill_usd = 0

  def csiSelectClients(sName,lId,iIsDealer,iIsBlock,iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='client'
    hsSql.where="1=1"+
                ((sName!='')?' AND name like CONCAT("%",:name,"%")':'')+
                ((lId>0)?' AND id =:client_id':'')+
                ((iIsDealer>0)?""" AND ((dealer_cashin_rate_rub>0)OR(dealer_cashin_rate_usd>0)OR(dealer_cashin_rate_eur>0)OR
                                       (dealer_cashout_rate_rub>0)OR(dealer_cashout_rate_usd>0)OR(dealer_cashout_rate_eur>0)OR
                                       (dealer_refill_rate_rub>0)OR(dealer_refill_rate_usd>0)OR(dealer_refill_rate_eur>0)OR
                                       (dealer_tran_rate_rub>0)OR(dealer_tran_rate_usd>0)OR(dealer_tran_rate_eur>0))""":'')+
                ((iIsBlock>0)?' AND is_block=:is_block':'')+
                ((iModstatus>-1)?' AND modstatus =:modstatus':'')
    hsSql.order="name asc"

    if(sName!='')
      hsString['name']=sName
    if(lId>0)
      hsLong['client_id']=lId
    if(iIsBlock>0)
      hsLong['is_block']=iIsBlock
    if(iModstatus>-1)
      hsLong['modstatus']=iModstatus

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,Client.class)
  }

  def csiSelectReportClients(){
    def hsSql=[select:'',from:'',where:'',order:'']

    hsSql.select="*"
    hsSql.from='client'
    hsSql.where="""modstatus != 0 AND NOT ((dealer_cashin_rate_rub>0)OR(dealer_cashin_rate_usd>0)OR(dealer_cashin_rate_eur>0)OR
                                           (dealer_cashout_rate_rub>0)OR(dealer_cashout_rate_usd>0)OR(dealer_cashout_rate_eur>0)OR
                                           (dealer_refill_rate_rub>0)OR(dealer_refill_rate_usd>0)OR(dealer_refill_rate_eur>0)OR
                                           (dealer_tran_rate_rub>0)OR(dealer_tran_rate_usd>0)OR(dealer_tran_rate_eur>0))"""
    hsSql.order="name asc"

    searchService.fetchDataByPages(hsSql,null,null,null,null,null,null,-1,0,'id',true,Client.class)
  }
  ////////////////////////////////////
  def csiGetClientsSaldo(){
    def hsSql= [select :'sum(account_rub), sum(account_usd), sum(account_eur)',
                from   :'client',
                where  :'modstatus = 1']
    return searchService.fetchData(hsSql,null,null,null,null,null)     
  }	
  ////////////////////////////////////
  def changePass(lId,sPass){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE client SET password=:pass, is_passwordchange=0 WHERE id=:id"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lId)
    qSql.setString('pass',sPass)
    qSql.executeUpdate()
    session.clear()
  }  
  ////////////////////////////////////
  Client csiSetData(lsRequest,lClient_id){
    parent = lsRequest.parent?:0    
    parent2 = lsRequest.parent2?:0
    
    comment = lsRequest.comment?:''
  
    tran_rate_rub = lsRequest.tran_rate_rub?:0.00
    tran_rate_usd = lsRequest.tran_rate_usd?:0.00
    tran_rate_eur = lsRequest.tran_rate_eur?:0.00
    cashin_rate_rub = lsRequest.cashin_rate_rub?:0.00
    cashin_rate_usd = lsRequest.cashin_rate_usd?:0.00
    cashin_rate_eur = lsRequest.cashin_rate_eur?:0.00
    cashout_rate_rub = lsRequest.cashout_rate_rub?:0.00
    cashout_rate_usd = lsRequest.cashout_rate_usd?:0.00
    cashout_rate_eur = lsRequest.cashout_rate_eur?:0.00
    
    refill_rate_rub = lsRequest.refill_rate_rub?:0.00
    refill_rate_usd = lsRequest.refill_rate_usd?:0.00
    refill_rate_eur = lsRequest.refill_rate_eur?:0.00
    
    dealer_cashin_rate_rub = lsRequest.dealer_cashin_rate_rub?:0.00
    dealer_cashin_rate_usd = lsRequest.dealer_cashin_rate_usd?:0.00
    dealer_cashin_rate_eur = lsRequest.dealer_cashin_rate_eur?:0.00
    dealer_cashout_rate_rub = lsRequest.dealer_cashout_rate_rub?:0.00
    dealer_cashout_rate_usd = lsRequest.dealer_cashout_rate_usd?:0.00
    dealer_cashout_rate_eur = lsRequest.dealer_cashout_rate_eur?:0.00
    dealer_refill_rate_rub = lsRequest.dealer_refill_rate_rub?:0.00
    dealer_refill_rate_usd = lsRequest.dealer_refill_rate_usd?:0.00
    dealer_refill_rate_eur = lsRequest.dealer_refill_rate_eur?:0.00
    dealer_tran_rate_rub = lsRequest.dealer_tran_rate_rub?:0.00
    dealer_tran_rate_usd = lsRequest.dealer_tran_rate_usd?:0.00
    dealer_tran_rate_eur = lsRequest.dealer_tran_rate_eur?:0.00
    
    is_refill_rub = lsRequest.is_refill_rub?:0
    is_refill_usd = lsRequest.is_refill_usd?:0
    is_refill_eur = lsRequest.is_refill_eur?:0
    
    is_tran_rub = lsRequest.is_tran_rub?:0
    is_tran_usd = lsRequest.is_tran_usd?:0
    is_tran_eur = lsRequest.is_tran_eur?:0
    
    is_cashin_rub = lsRequest.is_cashin_rub?:0
    is_cashin_usd = lsRequest.is_cashin_usd?:0
    is_cashin_eur = lsRequest.is_cashin_eur?:0
    
    is_cashout_rub = lsRequest.is_cashout_rub?:0
    is_cashout_usd = lsRequest.is_cashout_usd?:0
    is_cashout_eur = lsRequest.is_cashout_eur?:0

    if(refill_rate_rub>0) 
      is_refill_rub =1 
    if(refill_rate_usd>0) 
      is_refill_usd =1
    if(refill_rate_eur>0) 
      is_refill_eur =1    

    if(cashin_rate_rub>0) 
      is_cashin_rub =1 
    if(cashin_rate_usd>0) 
      is_cashin_usd =1
    if(cashin_rate_eur>0) 
      is_cashin_eur =1  
      
    if(cashout_rate_rub>0) 
      is_cashout_rub =1 
    if(cashout_rate_usd>0) 
      is_cashout_usd =1
    if(cashout_rate_eur>0) 
      is_cashout_eur =1   

    if(tran_rate_rub>0) 
      is_tran_rub =1 
    if(tran_rate_usd>0) 
      is_tran_usd =1
    if(tran_rate_eur>0) 
      is_tran_eur =1           

    modstatus = lsRequest.modstatus?:0        
    moddate = new Date()
    is_block = lsRequest.is_block?:0    
    swiftonclient = lsRequest.swiftonclient?:0 
    
    name = lsRequest.name?:''
    
    if(!lClient_id){        
      login = lsRequest.login?:''       
      password = Tools.hidePsw(lsRequest.password)
      inputdate = new Date()
      ip = ''
      is_passwordchange = 1//new from admin
    }    
    this
  } 
/////////////////////////  
  Client csiSetPassword(sPassword,bAdmin=false){
    password = sPassword
    if(bAdmin)
      is_passwordchange = 1
    else   
      is_passwordchange = 0
    this
  }

  Boolean checkAvailableSum(_request){
    if (!_request) return false
    if (Trantype.get(_request.trantype_id)?.is_debet&&this.account_rub<0) return false
    if (_request.trantype_id in [2,3,8,9]){
      if(_request.baseaccount=='rub'&&
        (this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"+this.account_rub/_request.vrate<_request.summa)
        /*||(this.account_rub-_request.summa*_request.vrate<_request.swiftsumma*_request.vrate&&this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"<_request.summa*_request.rate/(100-_request.rate)+_request.swiftsumma)
        ||(this.account_rub-_request.summa*_request.vrate-_request.swiftsumma*_request.vrate<_request.summa*_request.rate*_request.vrate/(100-_request.rate)&&this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"<_request.summa*_request.rate/(100-_request.rate))*/) return false
      if((_request.baseaccount?:'rub')!='rub'&&
        (this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"<_request.summa)
        /*||(this.account_rub<_request.swiftsumma*_request.vrate&&this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"<_request.summa*100/(100-_request.rate)+_request.swiftsumma)
        ||(this.account_rub-_request.swiftsumma*_request.vrate<_request.summa*_request.rate*_request.vrate/(100-_request.rate)&&this."account_${Trantype.get(_request.trantype_id)?.code.toLowerCase()}"<_request.summa*100/(100-_request.rate))*/) return false
    }
    if (_request.trantype_id in [1,7]){
      if(this.account_rub<_request.summa&&(_request.baseaccount?:'rub')=='rub') return false
      if((_request.baseaccount?:'rub')!='rub'&&this.account_rub+this."account_$_request.baseaccount"*_request.vrate<_request.summa) return false
    }
    return true
  }

  Client updateAccount(_summa,_oTrantype,_dVrate){
    switch(_oTrantype?.id) {
      case 1:
      case 2:
      case 3:
      case 7:
      case 8:
      case 9:
      case 25:
      case 26:
      case 28:
      case 29:
      case 30:
      case 34:
      case 36:
        this."account_${_oTrantype?.code.toLowerCase()}" -= _summa
      break
      case 4:
      case 5:
      case 6:
      case 10:
      case 11:
      case 12:
      case 19:
      case 20:
      case 21:
      case 32:
      case 33:
      case 35:
        this."account_${_oTrantype?.code.toLowerCase()}" += _summa
      break
      case 16:
      case 17:
      case 18:
      case 23:
        this.account_rub += _summa * _dVrate
      break
      case 24:
      case 31:
        this.account_rub -= _summa * _dVrate
      break
    }
    this
  }
  ////////////////////////////////////
  Boolean checkDataChange(lsRequest){
    def isDataChange=false
    
    if((parent!= (lsRequest.parent?:0))
      ||(parent2!= (lsRequest.parent2?:0))
      ||(tran_rate_rub!= (lsRequest.tran_rate_rub?:0))     
      ||(tran_rate_usd!= (lsRequest.tran_rate_usd?:0))
      ||(tran_rate_eur!= (lsRequest.tran_rate_eur?:0))
      ||(cashin_rate_rub!= (lsRequest.cashin_rate_rub?:0))
      ||(cashin_rate_usd!= (lsRequest.cashin_rate_usd?:0))
      ||(cashin_rate_eur!= (lsRequest.cashin_rate_eur?:0))
      ||(cashout_rate_rub!= (lsRequest.cashout_rate_rub?:0))
      ||(cashout_rate_usd!= (lsRequest.cashout_rate_usd?:0))
      ||(cashout_rate_eur!= (lsRequest.cashout_rate_eur?:0))      
      ||(refill_rate_rub!= (lsRequest.refill_rate_rub?:0))
      ||(refill_rate_usd!= (lsRequest.refill_rate_usd?:0))
      ||(refill_rate_eur!= (lsRequest.refill_rate_eur?:0))      
      ||(is_block!= (lsRequest.is_block?:0))      
      ||(modstatus!= (lsRequest.modstatus?:0))
      ||(dealer_cashin_rate_rub!= (lsRequest.dealer_cashin_rate_rub?:0))
      ||(dealer_cashin_rate_usd!= (lsRequest.dealer_cashin_rate_usd?:0))
      ||(dealer_cashin_rate_eur!= (lsRequest.dealer_cashin_rate_eur?:0))
      ||(dealer_cashout_rate_rub!= (lsRequest.dealer_cashout_rate_rub?:0))
      ||(dealer_cashout_rate_usd!= (lsRequest.dealer_cashout_rate_usd?:0))
      ||(dealer_cashout_rate_eur!= (lsRequest.dealer_cashout_rate_eur?:0))
      ||(dealer_tran_rate_rub!= (lsRequest.dealer_tran_rate_rub?:0))
      ||(dealer_tran_rate_usd!= (lsRequest.dealer_tran_rate_usd?:0))
      ||(dealer_tran_rate_eur!= (lsRequest.dealer_tran_rate_eur?:0))
      ||(dealer_refill_rate_rub!= (lsRequest.dealer_refill_rate_rub?:0))
      ||(dealer_refill_rate_usd!= (lsRequest.dealer_refill_rate_usd?:0))
      ||(dealer_refill_rate_eur!= (lsRequest.dealer_refill_rate_eur?:0))
      ||(swiftonclient!= (lsRequest.swiftonclient?:0)))      
        isDataChange=true
    
    return isDataChange
  } 
}