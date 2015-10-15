class Clientlog {
  
  def searchService
  def sessionFactory
  
  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long client_id
  Date logtime
  String ip
  Integer success
  Integer success_duration
  
  def csiGetLogs(lId){
    def hsSql = [select :'distinct *',
                 from   :'clientlog',
                 where  :'client_id = :id AND success=1',
                 order  :'logtime desc']
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null,Clientlog.class)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessLogs(lId, dDateFrom){
    def sDateFrom = String.format('%tY-%<tm-%<td %<tH:%<tM:%<tS', dDateFrom)
    def hsSql = [select :'count(id)',
                 from   :'clientlog',
                 where  :"client_id = :id AND success=0 AND logtime>'${sDateFrom}'"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  def resetSuccessDuration(lClientId){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE clientlog SET success_duration=1 WHERE client_id=:id AND success_duration=0"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lClientId)
    qSql.executeUpdate()
    session.clear()
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessDurationLogs(lId){  
    def hsSql = [select :'count(id)',
                 from   :'clientlog',
                 where  :"client_id = :id AND success_duration=0"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiGetLogsPaging(lId,iMax,iOffset){
    def hsSql = [select :'*',
                 from   :'clientlog',
                 where  :'1=1'+
                   (lId?' AND client_id = :id':''),
                 order  :'id desc']
    def hsLong = [:]
    if(lId)
      hsLong.id=lId
    return  searchService.fetchDataByPages(hsSql, null,hsLong,null,null,null,null,iMax,iOffset,'id',true,Clientlog.class,null,false)
  }
}
