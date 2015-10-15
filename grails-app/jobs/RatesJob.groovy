import org.codehaus.groovy.grails.commons.ConfigurationHolder
//TODO
class RatesJob {
  def ratesService
	static triggers = {
		//simple repeatInterval: 30000, repeatCount: 0 // execute job once in 30 seconds
		cron cronExpression: ((ConfigurationHolder.config.rates.cron!=[:])?ConfigurationHolder.config.rates.cron:"0 0 8 * * ?")
	}

  def execute() {
    ratesService.getRates()
  }
}
