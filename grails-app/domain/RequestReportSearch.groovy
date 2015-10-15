class RequestReportSearch {
  def searchService

  static mapping = {
    table 'report_NAME'
    version false
  }

  Long id
  Long client_id
  Integer trantype_id
  Date inputdate
  Date moddate
  Integer modstatus
  BigDecimal summa
  BigDecimal account_rub
  String comment

  String trname
  String trcode
  String clname
  BigDecimal syssumma

/////////////////////////////////////////////////////////////////////////////////////////////
  def csiSelectRequests(lClientId,dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsLong=[:]
    def hsString=[:]

    hsSql.select="*, trantype.name as trname, trantype.code as trcode, client.name as clname, IFNULL((select summa*vrate from transaction where request_id=request.id and trantype_id=13),0) as syssumma"
    hsSql.from='request, trantype, client'
    hsSql.where="request.trantype_id=trantype.id and request.modstatus=3 and request.client_id=client.id"+
                (lClientId>0?' AND request.client_id =:client_id':'')+
                (dDateStart?' AND request.moddate >=:date_start':'')+
                (dDateEnd?' AND request.moddate <=:date_end':'')

    hsSql.order="request.moddate asc"

    if(lClientId>0)
      hsLong['client_id'] = lClientId
    if(dDateStart)
      hsString['date_start'] = String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end'] = String.format('%tF',dDateEnd+1)

    def hsRes = searchService.fetchData(hsSql,hsLong,null,hsString,null,RequestReportSearch.class)
  }

}