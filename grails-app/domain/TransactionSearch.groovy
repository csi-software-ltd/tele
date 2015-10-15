class TransactionSearch {
  def searchService

  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

  Long id
  Long client_id
  Long request_id
  Integer trantype_id
  Date inputdate
  Date moddate
  Integer modstatus
  BigDecimal summa
  BigDecimal saldo
  Float vrate
  Integer nds
  Long company_id
  String name
  String inn
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

  BigDecimal debet
  BigDecimal credit
  Integer is_debet
  String ttname

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectOperations(lClientId,lId,iTrantypeId,lRequestId,sValuta,dDateStart,dDateEnd,iMax,iOffset){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]
 //IFNULL((select sum(summa) from transaction, trantype where trantype.id=transaction.trantype_id and client_id != 0 and is_service = 1 and request_id = tr.request_id and tt.is_need!=1),0) as com,
    hsSql.select="*, IF(tt.is_debet=1 OR summa<0,summa,0) as debet, IF(tt.is_debet=0 AND summa>0,summa,0) as credit, tt.name as ttname"
    hsSql.from='transaction tr, trantype tt'
    hsSql.where="tt.id=tr.trantype_id and (is_service = 0 OR tt.is_need = 1)"+
                ((iTrantypeId>0)?' AND tr.trantype_id =:trantype_id':'')+
                ((lClientId>0)?' AND tr.client_id =:client_id':'')+
                ((lId>0)?' AND tr.id =:id':'')+
                ((lRequestId>0)?' AND tr.request_id =:request_id':'')+
                (dDateStart?' AND tr.inputdate >=:date_start':'')+
                (dDateEnd?' AND tr.inputdate <=:date_end':'')+
                ((sValuta!='')?' AND tt.code =:valuta':'')
    hsSql.order="tr.id desc"

    if(iTrantypeId>0)
      hsLong['trantype_id']=iTrantypeId
    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(lId>0)
      hsLong['id']=lId
    if(lRequestId>0)
      hsLong['request_id']=lRequestId
    if(sValuta)
      hsString['valuta']=sValuta.toUpperCase()
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,hsString,
      null,null,iMax,iOffset,'tr.id',true,TransactionSearch.class)
  }

  def csiSelectOperations(lClientId,sValuta,dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, IF(tt.is_debet=1 OR summa<0,summa,0) as debet, IF(tt.is_debet=0 AND summa>0,summa,0) as credit, tt.name as ttname"
    hsSql.from='transaction tr, trantype tt'
    hsSql.where="tt.id=tr.trantype_id and (is_service = 0 OR tt.is_need = 1)"+
                ((lClientId>0)?' AND tr.client_id =:client_id':'')+
                (dDateStart?' AND tr.inputdate >=:date_start':'')+
                (dDateEnd?' AND tr.inputdate <=:date_end':'')+
                ((sValuta!='')?' AND tt.code =:valuta':'')
    hsSql.order="tr.inputdate asc"

    if(lClientId>0)
      hsLong['client_id']=lClientId
    if(sValuta)
      hsString['valuta']=sValuta.toUpperCase()
    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchData(hsSql,hsLong,null,hsString,null,TransactionSearch.class)
  }

}