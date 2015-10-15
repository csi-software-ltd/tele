class Valutarate { 
  def searchService     
  static constraints = {
  }
  
  static mapping = {
    version false
  }
  
  Long id
  String code  
  String name
  Float vrate  
  Integer valuta_id  
  Date vdate

  def csiSearchCurrent(){
    def hsSql=[select:'',from:'',where:'']
    def hsLong=[:]
    hsSql.select="*"
    hsSql.from="valutarate"    
    hsSql.where="(vdate=(SELECT max(vdate) from valutarate))AND(valuta_id IN (840,978))"

    return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class)
  }

  def csiSearchCurrent(iValutaId){
    def hsSql=[select:'',from:'',where:'']
    def hsLong=[:]
    hsSql.select="*"
    hsSql.from="valutarate"
    hsSql.where="vdate=(SELECT max(vdate) from valutarate) AND valuta_id =:valuta_id"
    hsLong['valuta_id'] = iValutaId
    return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class,-1,true)[0]
  }

  def csiSearchToday(iValutaId){
    def hsSql=[select:'',from:'',where:'']
    def hsLong=[:]
    hsSql.select='*'
    hsSql.from='valutarate'
    hsSql.where='vdate=CURDATE()'+((iValutaId>0)?' AND valuta_id=:valuta_id':'')
    if(iValutaId>0){
      hsLong['valuta_id']=iValutaId
      return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class,-1,true)[0]
    } else
      return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class)    
  }
  
  def csiSearchTomorrow(iValutaId){
    def hsSql=[select:'',from:'',where:'']
    def hsLong=[:]
    hsSql.select='*'
    hsSql.from='valutarate'
    hsSql.where='vdate=CURDATE()+1'+((iValutaId>0)?' AND valuta_id=:valuta_id':'')    
    if(iValutaId>0){
      hsLong['valuta_id']=iValutaId
      return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class,-1,true)[0]
    } else
      return searchService.fetchData(hsSql,hsLong,null,null,null,Valutarate.class)  
  }
}
