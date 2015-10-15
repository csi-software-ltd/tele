class Accountsys {
  static mapping = {
    version false
  }

  Integer id
  BigDecimal account_rub = 0.00
	BigDecimal account_usd = 0.00
	BigDecimal account_eur = 0.00

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

  Accountsys updateAccount(_summa,_iTrantypeId,_dVrate){
    switch(_iTrantypeId) {
      case 13:
      case 22:
        this.account_rub += _summa * _dVrate
      break
      default:
        //nothing to do here
      break
    }
    this
  }

}