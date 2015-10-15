class Transaction {
  def searchService
  
  static mapping = {
    version false
    sort "id"
  }

  Long id
  Long client_id
  Long request_id
  Integer trantype_id
  Date inputdate = new Date()
  Date moddate = new Date()
  Integer modstatus = 1
  BigDecimal summa = 0.00
  BigDecimal saldo = 0.00
  Float vrate = 1f
  Integer nds = 0
  Long company_id = 0l
  Integer syscompany_id = 0l
  String syscompany_name = ''
  String name = ''
  String inn = ''
  String bik = ''
  String bank = ''
  String bankcity = ''
  String cor_account = ''
  String account = ''
  String prim = ''
  String beneficial = ''
	String iban = ''
	String bbank = ''
	String baddress = ''
	String swift = ''
	String purpose = ''
  String comment = ''

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Transaction updatemainTransaction(_request){
		setGeneralData(_request)
		def oTrantype = Trantype.get(_request.trantype_id)
		summa = _request.summa
		trantype_id = _request.trantype_id?:0
    client_id = _request.client_id?:0l
    syscompany_id = _request.syscompany_id?:0l
    syscompany_name = _request.syscompany_name?:''
    vrate = 1f
		saldo = Client.get(_request.client_id)?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
		this
	}

	Transaction updateSysCommissionTransaction(_request,gDebetSumma){
		request_id = _request.id?:0
    def mainTrantype = Trantype.get(_request.trantype_id)
		def oTrantype = Trantype.get(13)
    def oMainClient = Client.get(_request.client_id)
    def oClient = (_request.trantype_id in [1,2,3,7,8,9])?Client.get(Client.get(_request.client_id)?.parent):(_request.trantype_id in [5,6,11,12] && _request.rate>0)?Client.get(Client.get(_request.client_id)?.parent):null
		def oSecondClient = (_request.trantype_id in [1,2,3,7,8,9])?Client.get(Client.get(_request.client_id)?.parent2):(_request.trantype_id in [5,6,11,12] && _request.rate>0)?Client.get(Client.get(_request.client_id)?.parent2):null
		summa = (_request.summa*_request.rate/(100-_request.rate))-(_request.summa*((oClient?."dealer_${mainTrantype?.rate}"?:0)/100))-(_request.summa*((oSecondClient?."dealer_${mainTrantype?.rate}"?:0)/100))-((_request.bankcomsumma?:0)/(mainTrantype?.valuta_id==643?1f:_request.vrate))-(_request.bankcomconvsumma/(mainTrantype?.valuta_id==643?1f:_request.vrate))
    if((_request.trantype_id in [2,3,8,9])&&_request.baseaccount=='rub')
      summa -= (_request.comvrate-_request.vrate)*gDebetSumma/(mainTrantype?.valuta_id==643?1f:_request.vrate)
    else if ((_request.trantype_id in [1,7])&&_request.baseaccount!='rub')
      summa -= (_request.vrate-_request.comvrate)*gDebetSumma/(mainTrantype?.valuta_id==643?1f:_request.vrate)
    if (_request.swiftsumma!=0&&!oMainClient.swiftonclient)
      summa -= _request.swiftsumma
		trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = mainTrantype?.valuta_id==643?1f:_request.vrate
		saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
		this
	}

	Transaction updateDealerCommissionTransaction(_request,oClient){
		request_id = _request.id?:0
    def mainTrantype = Trantype.get(_request.trantype_id)
		def oTrantype = Trantype.get(16)
		summa = _request.summa*((oClient."dealer_${mainTrantype?.rate}"?:0)/100)
		trantype_id = oTrantype?.id?:0
    client_id = oClient?.id?:0
    vrate = mainTrantype?.valuta_id==643?1f:_request.vrate
		saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true).account_rub?:0
		this
	}

  Transaction updateSysOutgoingsTransaction(_request){
    request_id = _request.id?:0
    comment = _request.comment?:''
    def oTrantype = Trantype.get(22)
    summa = -_request.summa
    trantype_id = oTrantype?.id?:0
    client_id = 0
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,1f).save(failOnError:true)?.account_rub?:0
    this
  }

  Transaction updateBankComissionTransaction(_request){
    request_id = _request.id?:0
    def oTrantype = Trantype.get(27)
    summa = (_request.bankcomsumma?:0)/_request.vrate
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.vrate
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
    this
  }

  Transaction updateBankConvertComissionTransaction(_request){
    request_id = _request.id?:0
    def oTrantype = Trantype.get(37)
    summa = _request.bankcomconvsumma/_request.vrate
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.vrate
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
    this
  }

  Transaction updatePaymentComissionTransaction(_request){
    request_id = _request.id?:0
    def mainTrantype = Trantype.get(_request.trantype_id)
    summa = Math.abs(_request.summa*_request.rate/(100-_request.rate))
    def oClient = Client.get(_request.client_id)
    def oTrantype = _request.rate<0?Trantype.get(23):summa*_request.vrate<oClient.account_rub?Trantype.get(24):Trantype.findByIdInListAndCode([24,25,26],mainTrantype?.code?:'')
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.rate<0||summa*_request.vrate<oClient.account_rub?(mainTrantype?.valuta_id==643?1f:_request.vrate):1f
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateSwiftComissionTransaction(_request){
    request_id = _request.id?:0
    def oClient = Client.get(_request.client_id)
    summa = _request.swiftsumma*_request.vrate<oClient.account_rub?_request.swiftsumma*_request.vrate:_request.swiftsumma
    def oTrantype = _request.swiftsumma*_request.vrate<oClient.account_rub?Trantype.get(28):Trantype.findByIdInListAndCode([28,29,30],Trantype.get(_request.trantype_id)?.code?:'')
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = 1f
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateSwiftComissionOnSysTransaction(_request){
    request_id = _request.id?:0
    def oClient = Client.get(_request.client_id)
    summa = _request.swiftsumma*_request.vrate
    def oTrantype = Trantype.get(39)
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = 1f
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
    this
  }

  Transaction updateBuyCurrencyDebetTransaction(_request){
    request_id = _request.id?:0
    def oClient = Client.get(_request.client_id)
    def oTrantype = Trantype.get(31)
    summa = oClient.account_rub<_request.summa*_request.vrate?oClient.account_rub/_request.vrate:_request.summa
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.vrate
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateBuyCurrencyCreditTransaction(_request,gDebetSumma){
    request_id = _request.id?:0
    def oClient = Client.get(_request.client_id)
    def oTrantype = Trantype.findByIdInListAndCode([33,35],Trantype.get(_request.trantype_id)?.code?:'')
    summa = gDebetSumma
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = 1f
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateExcahgeDiffTransaction(_request,gDebetSumma){
    request_id = _request.id?:0
    def oTrantype = Trantype.get(38)
    summa = (_request.comvrate-_request.vrate)*gDebetSumma/_request.vrate
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.vrate
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
    this
  }

  Transaction updateSellCurrencyCreditTransaction(_request,gDebetSumma){
    request_id = _request.id?:0
    summa = gDebetSumma*_request.vrate
    def oClient = Client.get(_request.client_id)
    def oTrantype = Trantype.get(32)
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = 1f
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateSellCurrencyDebetTransaction(_request){
    request_id = _request.id?:0
    def oClient = Client.get(_request.client_id)
    def oTrantype = Trantype.findByIdInListAndCode([34,36],(_request.baseaccount?:'rub').toUpperCase())
    summa = oClient."account_${_request.baseaccount}"<_request.summa/_request.vrate?oClient."account_${_request.baseaccount}":_request.summa/_request.vrate
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = 1f
    saldo = oClient?.updateAccount(summa,oTrantype,vrate).save(failOnError:true)."account_${oTrantype?.code.toLowerCase()}"?:0
    this
  }

  Transaction updateExcahgeDiffSellTransaction(_request,gDebetSumma){
    request_id = _request.id?:0
    def oTrantype = Trantype.get(38)
    summa = (_request.vrate-_request.comvrate)*gDebetSumma/_request.vrate
    trantype_id = oTrantype?.id?:0
    client_id = _request.client_id?:0
    vrate = _request.vrate
    saldo = Accountsys.get(1)?.updateAccount(summa,trantype_id,vrate).save(failOnError:true)?.account_rub?:0
    this
  }

  def csiSelectOperations(iTrantypeId,lClientId,lId,lRequestId,dDateStart,dDateEnd,lSysCompanyId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    
    hsSql.select="*"
    hsSql.from='transaction'
    hsSql.where="1=1"+
                ((iTrantypeId>0)?' AND trantype_id =:trantype_id':'')+
                ((lClientId>0)?' AND client_id =:client_id':'')+
                ((lId>0)?' AND id =:id':'')+
                ((lRequestId>0)?' AND request_id =:request_id':'')+
                ((lSysCompanyId>0)?' AND syscompany_id =:syscompany_id':'')+
                (dDateStart?' AND inputdate >=:date_start':'')+
                (dDateEnd?' AND inputdate <=:date_end':'')
    hsSql.order="id desc"
    
    if(iTrantypeId>0)
      hsLong['trantype_id']=iTrantypeId
    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(lId>0)
      hsLong['id']=lId
    if(lRequestId>0)
      hsLong['request_id']=lRequestId
    if(lSysCompanyId>0)
      hsLong['syscompany_id']=lSysCompanyId
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,Transaction.class)
  }

	private void setGeneralData(_request){
		request_id = _request.id?:0
		nds = _request.nds?:0
		company_id = _request.company_id?:0
		name = _request.name?:''
		inn = _request.inn?:''
		bik = _request.bik?:''
		bank = _request.bank?:''
		bankcity = _request.bankcity?:''
		cor_account = _request.cor_account?:''
		account = _request.account?:''
		prim = _request.prim?:''
		beneficial = _request.beneficial?:''
		iban = _request.iban?:''
		bbank = _request.bbank?:''
		baddress = _request.baddress?:''
		swift = _request.swift?:''
		purpose = _request.purpose?:''
	}

  Transaction updateAdminData(_request){
    comment = _request?.comment?:''
    this
  }

  static Long getLastRequestId(){
    Transaction.last()?.request_id
  }

}