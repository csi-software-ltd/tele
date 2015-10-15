class DealerFeeAdminReportSearch {
  def searchService

  static mapping = {
    table 'report_NAME'
    version false
    cache false
  }

  Long id
  BigDecimal feesumma
  String clname
  Integer trcount

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectOperations(dDateStart,dDateEnd){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, client.name as clname, sum(transaction.summa*transaction.vrate) as feesumma, count(transaction.id) as trcount"
    hsSql.from='transaction, client'
    hsSql.where="trantype_id=16 and transaction.client_id=client.id"+
                (dDateStart?' AND transaction.inputdate >=:date_start':'')+
                (dDateEnd?' AND transaction.inputdate <=:date_end':'')
    hsSql.group="transaction.client_id"
    hsSql.order="transaction.client_id asc"

    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchData(hsSql,null,null,hsString,null,DealerFeeAdminReportSearch.class)
  }

}