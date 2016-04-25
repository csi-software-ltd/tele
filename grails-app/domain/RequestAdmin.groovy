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
  Date reqdate

  Integer trantype_id
  Integer modstatus
  Integer nds

  BigDecimal summa
  Float vrate

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
  BigDecimal cl_account_rub
  BigDecimal cl_account_usd
  BigDecimal cl_account_eur

  def csiSelectRequests(lClientId,lsTrantypeId,iModstatus,dDateStart,dDateEnd,lId,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    def hsList=[:]

    hsSql.select="*, client.name as client_name, client.account_rub as cl_account_rub, client.account_usd as cl_account_usd, client.account_eur as cl_account_eur"
    hsSql.from='request, client'
    hsSql.where="request.client_id=client.id"+
                ((lClientId>0)?' AND request.client_id =:client_id':'')+
                (lsTrantypeId?.size()>0?' AND request.trantype_id in (:trantype_id)':'')+
                ((iModstatus!=-100)?' AND request.modstatus =:modstatus':' AND request.modstatus!=0')+
                (dDateStart?' AND request.inputdate >=:date_start':'')+
                (dDateEnd?' AND request.inputdate <=:date_end':'')+
                ((lId>0)?' AND request.id =:id':'')

    hsSql.order="request.moddate desc"

    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(lId>0)
      hsLong['id']=lId
    if(lsTrantypeId?.size()>0)
      hsList['trantype_id']=lsTrantypeId
    if(iModstatus!=-100)
      hsLong['modstatus']=iModstatus
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      hsList,null,iMax,iOffset,'request.id',true,RequestAdmin.class)
  }

  def csiSelectBronRequests(sClientName,lsTrantypeId,iModstatus,dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
    def hsList=[:]

    hsSql.select="*, client.name as client_name, client.account_rub as cl_account_rub, client.account_usd as cl_account_usd, client.account_eur as cl_account_eur"
    hsSql.from='request, client'
    hsSql.where="request.client_id=client.id"+
                (sClientName!=''?' AND client.name like CONCAT("%",:name,"%")':'')+
                (lsTrantypeId?.size()>0?' AND request.trantype_id in (:trantype_id)':'')+
                ((iModstatus!=-100)?' AND request.modstatus =:modstatus':' AND request.modstatus!=0')+
                (dDateStart?' AND request.reqdate >=:date_start':'')+
                (dDateEnd?' AND request.reqdate <=:date_end':'')

    hsSql.order="request.reqdate asc"

    if(sClientName!='')
      hsString['name']=sClientName
    if(lsTrantypeId?.size()>0)
      hsList['trantype_id']=lsTrantypeId
    if(iModstatus!=-100)
      hsLong['modstatus']=iModstatus
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart.clearTime())
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd.clearTime())

    searchService.fetchData(hsSql,hsLong,null,hsString,hsList,RequestAdmin.class)
  }

}