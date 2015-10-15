class Company {
  def searchService
  static constraints = {
  }
  static mapping = {
    version false
  }

  Long id
	Long client_id
  String name  
  Integer is_system
  Integer modstatus    

	String inn
	String kpp
	String ogrn
  String okpo = ''
  String bik
	String bank
	String city
  String cor_account
	String account
	String prim

	String beneficial
	String baddress
	String iban
	String bbank
	String swift
	String purpose

  String laddress = ''
	String director = ''
	String tel = ''
  String comment =''

  ////////////////////////////////////
  Company updateData(lsRequest){
    name = lsRequest.name?:''
    inn = lsRequest.inn?:''
    kpp = lsRequest.kpp?:''
    ogrn = lsRequest.ogrn?:''
    bik = lsRequest.bik?:''
    bank = lsRequest.bank?:''
    city = lsRequest.bankcity?:''
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

    this
  }
  
  Company csiSetNewData(){
    modstatus = 1
    is_system = 0
    this
  }

  static Company getInstance(_name,_beneficial,_clId){
    if (_name) Company.findOrCreateByNameAndClient_id(_name,_clId)
    else Company.findOrCreateByBeneficialAndClient_id(_beneficial,_clId)
  }
  
  def csiSelectCompanies(iModstatus,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]    

    hsSql.select="*"
    hsSql.from="company"
    hsSql.where="is_system=1 AND modstatus =:modstatus"                
    hsSql.order="name desc"

    hsLong['modstatus']=iModstatus
    

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Company.class)
  }
  
  Company csiSetData(lsRequest,lId){    
    comment = lsRequest.comment?:''
    if(!lId){
      is_system=1        
      modstatus=1
      client_id=0l
    } 
    
    this
  }
}