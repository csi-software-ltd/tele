import org.codehaus.groovy.grails.commons.ConfigurationHolder

class SearchService {
  boolean transactional = false
  def sessionFactory
  // def m_oConnect=null <-- TODO? set one connection object?

  def fetchDataByPages(hsSql, hsFilter,
                hsLong,hsInt,hsString,hsList,lsNotUseInCount,
                iMax,iOffset,
                sCount,bComputeCount,
                clClassName,lsDictionaryIds=null,isEager=false){//set isEager=true if using eager fetched property in domain class and object will not be detached by session.clear() method
				
    def session=sessionFactory.getCurrentSession()
    def hsRes=[records:[],count:0]

    if(lsNotUseInCount==null) lsNotUseInCount=[]
    if(hsLong==null)   hsLong=[:]
    if(hsInt==null)    hsInt=[:]
    if(hsString==null) hsString=[:]
    if(hsList==null)   hsList=[:]
    if(hsFilter==null)  hsFilter=[:]
    if(hsFilter.string_par==null)  hsFilter.string_par=[:]
    if(hsFilter.long_par==null)    hsFilter.long_par=[:]
    if(hsFilter.list_par==null)    hsFilter.list_par=[:]

    def sFrom=  ' FROM '+hsSql.from+(hsFilter.from?:'')
    def sWhere= ' WHERE '+hsSql.where+(hsFilter.where?:'')
    def sSelect=' SELECT '+hsSql.select+(hsFilter.select?:'')
    def sOrder= ' ORDER BY '+(hsFilter.order?:'')+hsSql.order
	//def sGroup= ((hsSql.group!=null)?' GROUP BY '+hsSql.group :'')
    def sGroup= ((hsSql.group!=null)?' GROUP BY '+(hsFilter.group?:'')+hsSql.group :'')
    
    if(hsFilter.string_par.size()!=0)  hsString+=hsFilter.string_par
    if(hsFilter.long_par.size()!=0)    hsLong+=hsFilter.long_par
    if(hsFilter.list_par.size()!=0)    hsList+=hsFilter.list_par
    // int todo...

    try{
      def qSql
      if(bComputeCount){
        qSql=session.createSQLQuery("SELECT count("+sCount+")"+sFrom+sWhere+sGroup)
	
        for(hsElem in hsLong){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setLong(hsElem.key,hsElem.value);
        }
        for(hsElem in hsInt){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setInteger(hsElem.key,hsElem.value);
        }
        for(hsElem in hsString){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setString(hsElem.key,hsElem.value);
        }
        for(hsElem in hsList){
          if(!(hsElem.key in lsNotUseInCount))
            qSql.setParameterList(hsElem.key,hsElem.value);
        }
        hsRes.records=qSql.list()		
        if(hsRes.records==null)
          hsRes.records=[]	  		  
        else if(hsRes.records.size()!=0){
          if((sCount==''||sCount=='*') && hsSql.group)
            hsRes.count=hsRes.records.size()
          else
            hsRes.count=hsRes.records[0]            
          hsRes.records=[]
        }		
      }
      //--------------------------------
      if((lsDictionaryIds!=null)&&(hsRes.count!=0)){
        for(sField in lsDictionaryIds){
          qSql=session.createSQLQuery("SELECT DISTINCT "+sField+" "+sFrom+sWhere+sGroup)

          for(hsElem in hsLong){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setLong(hsElem.key,hsElem.value);
          }
          for(hsElem in hsInt){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setInteger(hsElem.key,hsElem.value);
          }
          for(hsElem in hsString){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setString(hsElem.key,hsElem.value);
          }
          for(hsElem in hsList){
            if(!(hsElem.key in lsNotUseInCount))
              qSql.setParameterList(hsElem.key,hsElem.value);
          }
          hsRes[sField]=qSql.list()		 
        }
      }

      if((hsRes.count==0) && bComputeCount)
        hsRes.records=[]
      else{
        qSql=session.createSQLQuery(sSelect+sFrom+sWhere+sGroup+sOrder)      
        if(iMax>0)
          qSql.setMaxResults(iMax )
        qSql.setFirstResult(iOffset)
        for(hsElem in hsLong)
          qSql.setLong(hsElem.key,hsElem.value);
        for(hsElem in hsInt)
          qSql.setInteger(hsElem.key,hsElem.value);
        for(hsElem in hsString)
          qSql.setString(hsElem.key,hsElem.value);
        for(hsElem in hsList)
          qSql.setParameterList(hsElem.key,hsElem.value);
        qSql.addEntity(clClassName)		
        hsRes.records=qSql.list()		
        if(!bComputeCount)
          hsRes.count=hsRes.records?.size()
      }
    }catch (Exception e) {
      log.debug("Error fetchDataByPages\n"+e.toString()+"\n"+
                sSelect+"\n"+sFrom+"\n"+sWhere+"\n"+sGroup+"\n"+sOrder);
      hsRes.count=0
      hsRes.records=[]
    }  
    if (!isEager)
      session.clear()

    return hsRes
  }
  //////////////////////////////////////////////////////////////////////////
  def fetchData(hsSql,hsLong,hsInt,hsString,hsList,clClassName=null,iMax=-1,isEager=false){
    def session=sessionFactory.getCurrentSession()
    def hsRes=[]

    if(hsLong==null)   hsLong=[:]
    if(hsInt==null)    hsInt=[:]
    if(hsString==null) hsString=[:]
    if(hsList==null)   hsList=[:]

    def sSelect=' SELECT '+hsSql.select
    def sFrom=  ' FROM '+hsSql.from
    def sWhere= ((hsSql.where!=null)?' WHERE '+hsSql.where:'')
    def sOrder= ((hsSql.order!=null)?' ORDER BY '+hsSql.order:'')
    //hsSql.order= ' ORDER BY '+(hsFilter.order?:'')+(hsSql.order?:'')
    def sGroup= ((hsSql.group!=null)?' GROUP BY '+hsSql.group :'')
    
    try{
      def qSql
      qSql=session.createSQLQuery(sSelect+sFrom+sWhere+sGroup+sOrder)
      for(hsElem in hsLong)
        qSql.setLong(hsElem.key,hsElem.value);
      for(hsElem in hsInt)
        qSql.setInteger(hsElem.key,hsElem.value);
      for(hsElem in hsString)
        qSql.setString(hsElem.key,hsElem.value);
      for(hsElem in hsList)
        qSql.setParameterList(hsElem.key,hsElem.value);
      if(clClassName!=null)
        qSql.addEntity(clClassName)
      if(iMax>0)
        qSql.setMaxResults(iMax)
      if (!isEager)
        session.clear()
      return qSql.list()
    }catch (Exception e) {
      log.debug("Error fetchData\n"+e.toString()+"\n"+
                sSelect+"\n"+sFrom+"\n"+sWhere+"\n"+sGroup+"\n"+sOrder);
      return []
    }
    return []
  }
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  def getLastInsert(){
    def sSql="select last_insert_id()"
    def session = sessionFactory.getCurrentSession()    
    try{
      def qSql=session.createSQLQuery(sSql)
      def lsRecords=qSql.list()
      if(lsRecords.size()>0){
        session.clear()
        return lsRecords[0].toLong()
      }
    }catch (Exception e) {
      log.debug("Error SearchService::getLastInsert\n"+e.toString());
    }
    session.clear()
    return 0
  }  
}
