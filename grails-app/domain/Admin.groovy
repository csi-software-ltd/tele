class Admin {
  
  def sessionFactory
  
  static constraints = {
  }
  
  static mapping = {
    version false
  }
  
  Long id
  String login
  String password  
  String email
  Integer admingroup_id
  Integer accesslevel
  String tel

  def changePass(lId,sPass){
    def session = sessionFactory.getCurrentSession()
    def sSql = "UPDATE admin SET password=:pass WHERE id=:id"
    def qSql = session.createSQLQuery(sSql)
    qSql.setLong('id',lId)
    qSql.setString('pass',sPass)
    qSql.executeUpdate()
    session.clear()
  }
}
