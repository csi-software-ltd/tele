class Request {
  def searchService

  static mapping = {
    version false
  }
  static constraints = {
    platdate(nullable:true)
    reqdate(nullable:true)
    bankcomsumma(nullable:true)
  }

  Long id
  Long client_id
  Long company_id = 0
  Long trans_id = 0
  Long parent = 0
  String baseaccount = ''

  Date inputdate = new Date()
  Date moddate = new Date()
  Date platdate
  Date reqdate

  Integer trantype_id
  Integer modstatus = 0
  Integer nds = 0

  BigDecimal summa = 0.00
  Float vrate = 1f
  Float comvrate = 1f
  BigDecimal rate = 0.00
  BigDecimal account_rub = 0g
  BigDecimal bankcomsumma
  BigDecimal bankcomconvsumma = 0g
  BigDecimal swiftsumma = 0g
  Integer syscompany_id = 0l
  String syscompany_name = ''

  String inn
  String kpp
  String ogrn
  String name
  String bik
  String bank
  String bankcity
  String cor_account
  String account
  String prim = ''

  String beneficial	
	String iban
	String bbank
  String baddress
	String swift
  String purpose
	String laddress

  String comment = ''
  Integer is_read = 1

  def afterInsert(){
    if (modstatus==1){
      Request.withNewSession {
        new Requesthistory(request_id:id, inputdate:new Date(), modstatus:modstatus, summa:summa).save(flush:true)
      }
    }
  }

  def beforeUpdate() {
    if (modstatus!=0&&(isDirty('modstatus')||isDirty('summa'))) {
      Request.withNewSession {
        new Requesthistory(request_id:id, inputdate:new Date(), modstatus:modstatus, summa:summa).save(flush:true)
      }
    }
  }

  def csiSelectRequests(lClientId,iTrantypeId,iModstatus,dDateStart,dDateEnd,iMax,iOffset,bOrderByModdate=false){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*"
    hsSql.from='request'
    hsSql.where="1=1"+
                ((lClientId>0)?' AND client_id =:client_id':'')+
                ((iTrantypeId>0)?' AND trantype_id =:trantype_id':'')+
                ((iModstatus!=-100)?' AND modstatus =:modstatus':'')+
                (dDateStart?' AND inputdate >=:date_start':'')+
                (dDateEnd?' AND inputdate <=:date_end':'')

    hsSql.order=bOrderByModdate?"moddate desc":"id desc"

    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(iTrantypeId>0)
      hsLong['trantype_id']=iTrantypeId
    if(iModstatus!=-100)
      hsLong['modstatus']=iModstatus
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'id',true,Request.class)
  }
  ////////////////////////////////////
  Request setData(lsRequest,lCompanyId){
    trantype_id = lsRequest.trantype_id?:trantype_id
    def oTrantype = Trantype.get(trantype_id)
    moddate = new Date()
    if (lsRequest?.modstatus==-1&&modstatus!=-1)
      is_read = 0
    summa = lsRequest.summa?:0
    nds = lsRequest.nds?:0
    rate = (lsRequest.rate?:101)<100?lsRequest.rate:rate
    baseaccount = lsRequest.baseaccount?:oTrantype?.code?.toLowerCase()?:'rub'

    if (oTrantype?.valuta_id!=643||baseaccount!='rub') {
      vrate = lsRequest.vrate?:new Valutarate().csiSearchCurrent(oTrantype.valuta_id)?.vrate?:new Valutarate().csiSearchCurrent(Valuta.findByCode(baseaccount.toUpperCase())?.id?:0)?.vrate?:1f
    } else vrate = 1f
    if (oTrantype.is_debet&&baseaccount!=oTrantype?.code?.toLowerCase()) {
      comvrate = lsRequest.comvrate?:new Valutarate().csiSearchCurrent(Valuta.findByCode(baseaccount.toUpperCase())?.id?:Valuta.findByCode(oTrantype?.code)?.id?:0)?.vrate?:1f
    } else comvrate = 1f
    if (oTrantype?.is_debet)
      bankcomsumma = lsRequest.bankcomsumma?:lsRequest.strbankcomsumma?.isBigDecimal()?lsRequest.strbankcomsumma.toBigDecimal():lsRequest.basebankcomsumma
    if (trantype_id in 2..3&&baseaccount=='rub')
      bankcomconvsumma = lsRequest.bankcomconvsumma?:0g
    else bankcomconvsumma = 0g
    swiftsumma = lsRequest.swiftsumma?:0g

    company_id = lCompanyId?:0
    syscompany_id = lsRequest.syscompany_id?:syscompany_id
    syscompany_name = lsRequest.syscompany_name?:syscompany_name
    name = lsRequest.name?:''
    inn = lsRequest.inn?:''
    kpp = lsRequest.kpp?:''
    ogrn = lsRequest.ogrn?:''
    bik = lsRequest.bik?:''
    bank = lsRequest.bank?:''
    bankcity = lsRequest.bankcity?:''
    cor_account = lsRequest.cor_account?:''
    account = lsRequest.account?:''
    prim = lsRequest.prim?:''

    beneficial = lsRequest.beneficial?:''
    iban = lsRequest.iban?:''
    bbank = lsRequest.bbank?:''
    baddress = lsRequest.baddress?:''
    laddress = lsRequest.laddress?:''
    swift = lsRequest.swift?:''
    purpose = lsRequest.purpose?:''

    comment = lsRequest.comment?:''

    platdate = lsRequest.platdate
    reqdate = lsRequest.reqdate

    if (!(lsRequest.modstatus in [2,3]) || Client.get(this.client_id)?.checkAvailableSum(this))
      modstatus = lsRequest.modstatus?:0

    this
  }

  Request read(){
    is_read = 1
    this
  }

  Request doRequest(){
    if(isDirty('modstatus')&&modstatus==3&&Client.get(this.client_id)?.checkAvailableSum(this)){
      def oClient = Client.get(client_id)
      def oTrantype = Trantype.get(trantype_id)
      account_rub = oClient?.account_rub?:0g
      def oDebetTransaction
      if((trantype_id in [2,3,8,9]) && baseaccount=='rub' && oClient.account_rub>0){
        oDebetTransaction = new Transaction().updateBuyCurrencyDebetTransaction(this).save(failOnError:true)
        new Transaction().updateBuyCurrencyCreditTransaction(this,oDebetTransaction.summa).save(failOnError:true)
        new Transaction().updateExcahgeDiffTransaction(this,oDebetTransaction.summa).save(failOnError:true)
      }
      if(trantype_id in [1,7] && (baseaccount?:'rub')!='rub' && oClient."account_$baseaccount">0){
        oDebetTransaction = new Transaction().updateSellCurrencyDebetTransaction(this).save(failOnError:true)
        new Transaction().updateSellCurrencyCreditTransaction(this,oDebetTransaction.summa).save(failOnError:true)
        new Transaction().updateExcahgeDiffSellTransaction(this,oDebetTransaction.summa).save(failOnError:true)
      }
      trans_id = new Transaction().updatemainTransaction(this).save(failOnError:true)?.id?:0
      if(swiftsumma!=0&&oClient.swiftonclient) new Transaction().updateSwiftComissionTransaction(this).save(failOnError:true)
      else if (swiftsumma!=0&&!oClient.swiftonclient) new Transaction().updateSwiftComissionOnSysTransaction(this).save(failOnError:true)
      if(this.rate!=0){
        new Transaction().updatePaymentComissionTransaction(this).save(failOnError:true)
        new Transaction().updateSysCommissionTransaction(this,oDebetTransaction?.summa?:0).save(failOnError:true)
      }
      def oFirstClient = Client.get(oClient?.parent)
      def oSecondClient = Client.get(oClient?.parent2)
      if(oFirstClient?."dealer_${oTrantype?.rate}">0&&((trantype_id in [1,2,3,7,8,9])||(trantype_id in [5,6,11,12] && rate>0))) new Transaction().updateDealerCommissionTransaction(this,oFirstClient).save(failOnError:true)
      if(oSecondClient?."dealer_${oTrantype?.rate}">0&&((trantype_id in [1,2,3,7,8,9])||(trantype_id in [5,6,11,12] && rate>0))) new Transaction().updateDealerCommissionTransaction(this,oSecondClient).save(failOnError:true)
      if(bankcomsumma&&rate!=0) new Transaction().updateBankComissionTransaction(this).save(failOnError:true)
      if(bankcomconvsumma!=0&&rate!=0) new Transaction().updateBankConvertComissionTransaction(this).save(failOnError:true)
    }
    this
  }

  Request cancellRequest(){
    Transaction.findAllByRequest_id(id).each{
      def oTrantype = Trantype.get(it.trantype_id)
      if(oTrantype.is_need==1||oTrantype.is_service==0) {
        Client.get(it.client_id)?.updateAccount(-it.summa,oTrantype,it.vrate).save(failOnError:true)
      } else {
        Accountsys.get(1)?.updateAccount(-it.summa,it.trantype_id,it.vrate).save(failOnError:true)
      }
      it.delete()
    }
    trans_id = 0
    modstatus = 1
    this
  }

}