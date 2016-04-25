<html>
  <head>
    <title>Административное приложение: Отчеты</title>
    <meta name="layout" content="administrator" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function displaysection(sId){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $(sId+'link').addClassName('active');
        jQuery('form').hide();
        jQuery('#'+sId+'Form').show();
      }
      new Autocomplete('client_name', {
        serviceUrl:'${resource(dir:"admin",file:"clientname_autocomplete")}',
        width:254
      });
      new Autocomplete('bron_client_name', {
        serviceUrl:'${resource(dir:"admin",file:"clientname_autocomplete")}',
        width:254
      });
      new Autocomplete('syscomp_name', {
        serviceUrl:'${resource(dir:"admin",file:"companyname_autocomplete")}',
        width:254
      });
    </g:javascript>
  </head>
  <body>
    <div class="tabs padtop fright">
      <a id="requestreportlink" class="active" onclick="displaysection('requestreport')"><i class="icon-check icon-large"></i> выполненные запросы</a>
      <a id="clsaldoreportlink" onclick="displaysection('clsaldoreport')"><i class="icon-bar-chart icon-large"></i> остатки</a>
      <a id="transactionreportlink" onclick="displaysection('transactionreport')"><i class="icon-list-alt icon-large"></i> операции</a>
      <a id="dealerfeereportlink" onclick="displaysection('dealerfeereport')"><i class="icon-gift icon-large"></i> вознаграждение посредников</a>
      <a id="revisereportlink" onclick="displaysection('revisereport')"><i class="icon-calendar icon-large"></i> сверка</a>
      <a id="bronreportlink" onclick="displaysection('bronreport')"><i class="icon-shield icon-large"></i> брони</a>
      <a id="syscompreportlink" onclick="displaysection('syscompreport')"><i class="icon-th icon-large"></i> компании</a>
    </div>
    <div class="clear"></div>
    <div class="padtop filter">
      <g:form name="requestreportForm" controller="admin" target="_blank">
        <div class="grid_6 alpha">
          <label for="reqreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="reqreport_start" value=""/>
          <label for="reqreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="reqreport_end" value=""/>
        </div>
        <div class="fleft">
          <g:actionSubmit class="spacing" value="PDF" action="reqreport"/>
          <g:actionSubmit value="XLS" action="reqreportXLS"/>      
        </div>
      </g:form>
      <g:form style="display:none" name="clsaldoreportForm" controller="admin" target="_blank">
        <div class="inline spacing"></div>
        <div class="inline" style="float:right">
          <g:actionSubmit class="spacing" value="PDF" action="clsaldoreport"/>
          <g:actionSubmit value="XLS" action="clsaldoreportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="transactionreportForm" controller="admin" target="_blank">
        <div class="inline spacing">
          <label for="transreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="transreport_start" value=""/>
          <label for="transreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="transreport_end" value=""/>
          <label for="type" class="auto">Тип отчета:</label>
          <g:select class="auto" name="type" from="${['Стандартный','Расширеный']}" keys="${0..1}"/>
        </div>
        <div class="inline">
          <g:actionSubmit class="spacing" value="PDF" action="transreport"/>
          <g:actionSubmit value="XLS" action="transreportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="dealerfeereportForm" controller="admin" target="_blank">
        <div class="inline spacing">
          <label for="feereport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="feereport_start" value=""/>
          <label for="feereport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="feereport_end" value=""/>
        </div>
        <div class="inline">
          <g:actionSubmit class="spacing" value="PDF" action="dealerfeereport"/>
          <g:actionSubmit value="XLS" action="dealerfeereportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="revisereportForm" controller="admin" target="_blank">
        <div class="inline spacing">
          <label class="auto" for="client_name">Клиент:</label>
          <input type="text" id="client_name" name="client_name" />
          <label for="revisereport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="revisereport_start" value=""/>
          <label for="revisereport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="revisereport_end" value=""/>
        </div>
        <div class="inline">
          <g:actionSubmit value="XLS" action="revisereportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="bronreportForm" controller="admin" target="_blank">
        <div class="inline spacing">
          <label class="auto" for="bron_client_name">Клиент:</label>
          <input type="text" id="bron_client_name" name="client_name" />
          <label for="bronreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="bronreport_start" value=""/>
          <label for="bronreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="bronreport_end" value=""/>
        </div>
        <div class="inline">
          <g:actionSubmit value="XLS" action="bronreportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="syscompreportForm" controller="admin" target="_blank">
        <div class="inline spacing">
          <label class="auto" for="syscomp_name">Компания:</label>
          <input type="text" id="syscomp_name" name="company_name" />
          <label for="syscompreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="syscompreport_start" value=""/>
          <label for="syscompreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="syscompreport_end" value=""/>
        </div>
        <div class="inline">
          <g:actionSubmit value="XLS" action="syscompreportXLS"/>
        </div>
      </g:form>
      <div class="clear"></div>
    </div>
  </body>
</html>
