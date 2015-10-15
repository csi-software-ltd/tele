<html>
  <head>
    <title>Административное приложение: Отчеты</title>
    <meta name="layout" content="administrator" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function displaysection(sId){
        jQuery('.tabs a[class="active"]').removeClass('active');
        $(sId+'link').addClassName('active');
        jQuery('form').hide();
        jQuery('#'+sId+'Form').show();
      }
    </g:javascript>    
  </head>
  <body>
    <div class="tabs padtop fright">
      <a id="requestreportlink" class="active" onclick="displaysection('requestreport')"><i class="icon-check icon-large"></i> выполненные запросы</a>
      <a id="transactionreportlink" onclick="displaysection('transactionreport')"><i class="icon-list-alt icon-large"></i> выписка по операциям</a>
      <a id="dealerfeereportlink" onclick="displaysection('dealerfeereport')"><i class="icon-gift icon-large"></i> вознаграждение посредников</a>
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
      <div class="clear"></div>
    </div>
  </body>
</html>
