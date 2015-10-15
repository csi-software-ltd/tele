class TransactionAdminReportSearch {
  def searchService

  static mapping = {
    table 'report_NAME'
    version false
    cache false
  }

  Long id
  Integer trantype_id
  Date inputdate
  BigDecimal summa
  BigDecimal saldo
  Float vrate

  String trname

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  def csiSelectOperations(dDateStart,dDateEnd,iType){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, tt.name as trname"
    hsSql.from='transaction tr, trantype tt'
    hsSql.where="tt.id=tr.trantype_id"+
                (dDateStart?' AND tr.inputdate >=:date_start':'')+
                (dDateEnd?' AND tr.inputdate <=:date_end':'')+
                (iType?' AND tr.trantype_id in (select id from trantype where is_add=1)':' AND tr.trantype_id in (13,22)')
    hsSql.order="tr.inputdate asc"

    if(dDateStart)
      hsString['date_start']=String.format('%tF',dDateStart)
    if(dDateEnd)
      hsString['date_end']=String.format('%tF',dDateEnd+1)

    def hsRes=searchService.fetchData(hsSql,null,null,hsString,null,TransactionAdminReportSearch.class)
  }

}