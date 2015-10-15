class RequestAdmin {
  def searchService
  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

  Long id
  Long client_id
  Long company_id
  Long trans_id

  Date inputdate
  Date moddate

  Integer trantype_id
  Integer modstatus
  Integer nds

  BigDecimal summa

  String inn
  String kpp
  String ogrn
  String name
  String bik
  String bank
  String bankcity
  String cor_account
  String account
  String prim

  String beneficial	
	String iban
	String bbank
  String baddress
	String swift
	String purpose

  String comment

  String client_name

  def csiSelectRequests(lClientId,iTrantypeId,iModstatus,dDateStart,dDateEnd,lId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, client.name as client_name"
    hsSql.from='request, client'
    hsSql.where="request.client_id=client.id"+
                ((lClientId>0)?' AND request.client_id =:client_id':'')+
                ((iTrantypeId>0)?' AND request.trantype_id =:trantype_id':'')+
                ((iModstatus!=-100)?' AND request.modstatus =:modstatus':' AND request.modstatus!=0')+
                (dDateStart?' AND request.inputdate >=:date_start':'')+
                (dDateEnd?' AND request.inputdate <=:date_end':'')+
                ((lId>0)?' AND request.id =:id':'')

    hsSql.order="request.moddate desc"

    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(lId>0)
      hsLong['id']=lId
    if(iTrantypeId>0)
      hsLong['trantype_id']=iTrantypeId
    if(iModstatus!=-100)
      hsLong['modstatus']=iModstatus
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'request.id',true,RequestAdmin.class)
  }

}
