class RequestCompany {
  def searchService
  static mapping = {
    table 'adm_NAME'
    version false
    cache false
  }

  Long id
  Integer trantype_id
  Date moddate
  BigDecimal summa
  Integer syscompany_id
  String name

  String company_name
  BigDecimal debetsum
  BigDecimal creditsum

  def csiSelectSummary(hsRequest){
    def hsSql=[select:'',from:'',where:'',order:'',group:'']
    def hsString=[:]

    hsSql.select="*, company.name as company_name, sum(if(request.trantype_id=1,summa,0)) as debetsum, sum(if(request.trantype_id=10,summa,0)) as creditsum"
    hsSql.from='request, company'
    hsSql.where="request.trantype_id in (1,10) and request.syscompany_id=company.id"+
                (hsRequest?.report_start?' AND request.moddate >=:date_start':'')+
                (hsRequest?.report_end?' AND request.moddate <=:date_end':'')
    hsSql.group="request.syscompany_id"
    hsSql.order="request.moddate asc"

    if(hsRequest?.report_start)
      hsString['date_start']=String.format('%tF',hsRequest.report_start.clearTime())
    if(hsRequest?.report_end)
      hsString['date_end']=String.format('%tF',hsRequest.report_end.clearTime())

    searchService.fetchData(hsSql,null,null,hsString,null,RequestCompany.class)
  }

  def csiSelectCompanyRequests(hsRequest){
    def hsSql=[select:'',from:'',where:'',order:'']
    def hsString=[:]

    hsSql.select="*, company.name as company_name, (if(request.trantype_id=1,summa,0)) as debetsum, (if(request.trantype_id=10,summa,0)) as creditsum"
    hsSql.from='request, company'
    hsSql.where="request.trantype_id in (1,10) and request.syscompany_id=company.id"+
                (hsRequest?.company_name?' AND company.name=:cname':'')+
                (hsRequest?.report_start?' AND request.moddate >=:date_start':'')+
                (hsRequest?.report_end?' AND request.moddate <=:date_end':'')
    hsSql.order="request.moddate asc"

    if(hsRequest?.company_name)
      hsString['cname']=hsRequest.company_name
    if(hsRequest?.report_start)
      hsString['date_start']=String.format('%tF',hsRequest.report_start.clearTime())
    if(hsRequest?.report_end)
      hsString['date_end']=String.format('%tF',hsRequest.report_end.clearTime())

    searchService.fetchData(hsSql,null,null,hsString,null,RequestCompany.class)
  }
}