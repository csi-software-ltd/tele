class Adminlog {
  
  def searchService
  def sessionFactory
  
  static constraints = {
  }
  static mapping = {
    version false
  }
  Long id
  Long admin_id
  Date logtime
  String ip
  Integer success
  Integer success_duration
  
  def csiGetLogs(lId){
    def hsSql = [select :'distinct *',
                 from   :'adminlog',
                 where  :'admin_id = :id AND success=1',
                 order  :'logtime desc']
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null,Adminlog.class)
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessLogs(lId, dDateFrom){
    def sDateFrom = String.format('%tY-%<tm-%<td %<tH:%<tM:%<tS', dDateFrom)
    def hsSql = [select :'count(id)',
                 from   :'adminlog',
                 where  :"admin_id = :id AND success=0 AND logtime>'${sDateFrom}'"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
  def resetSuccessDuration(lAdminId){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE adminlog SET success_duration=1 WHERE admin_id=:id AND success_duration=0"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lAdminId)
    qSql.executeUpdate()
    session.clear()
  }
  ///////////////////////////////////////////////////////////////////////////////////
  def csiCountUnsuccessDurationLogs(lId){  
    def hsSql = [select :'count(id)',
                 from   :'adminlog',
                 where  :"admin_id = :id AND success_duration=0"]
    def hsLong = [id: lId]
    return searchService.fetchData(hsSql,hsLong,null,null,null)
  }
}
