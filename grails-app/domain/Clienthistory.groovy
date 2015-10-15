class Clienthistory {  
  def searchService
  
  static constraints = {    
  }  
  static mapping = {
    version false
  }
  
  Long client_id
  Long parent = 0
  Long parent2 = 0
  
  Integer is_block = 0
  Integer modstatus = 0
  Integer swiftonclient = 1
    
	Date moddate = new Date()  

  BigDecimal saldo_rub = 0.00
	BigDecimal saldo_usd = 0.00
	BigDecimal saldo_eur = 0.00
  
	BigDecimal tran_rate_rub = 0.00
	BigDecimal tran_rate_usd = 0.00
	BigDecimal tran_rate_eur = 0.00
	BigDecimal cashin_rate_rub = 0.00
	BigDecimal cashin_rate_usd = 0.00
	BigDecimal cashin_rate_eur = 0.00
	BigDecimal cashout_rate_rub = 0.00
	BigDecimal cashout_rate_usd = 0.00
	BigDecimal cashout_rate_eur = 0.00	
  BigDecimal refill_rate_rub = 0.00
  BigDecimal refill_rate_usd = 0.00
  BigDecimal refill_rate_eur = 0.00  
  
  BigDecimal dealer_cashin_rub = 0.00
  BigDecimal dealer_cashin_usd = 0.00
  BigDecimal dealer_cashin_eur = 0.00
  BigDecimal dealer_cashout_rub = 0.00
  BigDecimal dealer_cashout_usd = 0.00
  BigDecimal dealer_cashout_eur = 0.00  
  BigDecimal dealer_refill_rub = 0.00
  BigDecimal dealer_refill_usd = 0.00
  BigDecimal dealer_refill_eur = 0.00
  BigDecimal dealer_tran_rub = 0.00
  BigDecimal dealer_tran_usd = 0.00
  BigDecimal dealer_tran_eur = 0.00

  def csiGetClientHistory(lId,iMax,iOffset){
    def hsSql= [select :'*',
                from   :'clienthistory',
                where  :'client_id = :id',
                order  :'id desc']
    def hsLong = [id: lId]
    
    def hsRes=searchService.fetchDataByPages(hsSql,null,hsLong,null,null,
      null,null,iMax,iOffset,'id',true,Clienthistory.class)    
  }
  def csiSetDataFromClient(oClient){
    client_id=oClient.id?:0
    parent = oClient.parent?:0    
    parent2 = oClient.parent2?:0
  
    saldo_rub = oClient.account_rub?:0.00
    saldo_usd = oClient.account_usd?:0.00
    saldo_eur = oClient.account_eur?:0.00

    tran_rate_rub = oClient.tran_rate_rub?:0.00
    tran_rate_usd = oClient.tran_rate_usd?:0.00
    tran_rate_eur = oClient.tran_rate_eur?:0.00
    cashin_rate_rub = oClient.cashin_rate_rub?:0.00
    cashin_rate_usd = oClient.cashin_rate_usd?:0.00
    cashin_rate_eur = oClient.cashin_rate_eur?:0.00
    cashout_rate_rub = oClient.cashout_rate_rub?:0.00
    cashout_rate_usd = oClient.cashout_rate_usd?:0.00
    cashout_rate_eur = oClient.cashout_rate_eur?:0.00    
    refill_rate_rub = oClient.refill_rate_rub?:0.00
    refill_rate_usd = oClient.refill_rate_usd?:0.00
    refill_rate_eur = oClient.refill_rate_eur?:0.00
    
    modstatus = oClient.modstatus?:0        
    moddate = new Date()    
    
    is_block = oClient?.is_block?:0
     
    this
    
  }
}
