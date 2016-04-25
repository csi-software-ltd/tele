<html>
  <head>
    <title>${infotext?.title?:''}</title>
    <meta name="layout" content="main" />
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
      <a id="revisereportlink" onclick="displaysection('revisereport')"><i class="icon-calendar icon-large"></i> сверка</a>
    </div>
    <div class="clear"></div>
    <div class="padtop filter">
      <g:form name="requestreportForm" controller="client" target="_blank">
        <div class="grid_6 alpha">
          <label for="reqreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="reqreport_start" value=""/>
          <label for="reqreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="reqreport_end" value=""/>
        </div>
        <div class="fleft">
          <g:actionSubmit value="PDF" class="spacing" action="reqreport"/>
          <g:actionSubmit value="XLS" action="reqreportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="transactionreportForm" controller="client" target="_blank">
        <div class="inline spacing">
          <label for="transreport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="transreport_start" value=""/>
          <label for="transreport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="transreport_end" value=""/>
          <label for="valuta" class="auto">Тип отчета:</label>
          <g:select class="auto" name="valuta" from="${['RUB','USD','EUR']}"/>
        </div>
        <div class="inline">
          <g:actionSubmit value="PDF" class="spacing" action="transreport"/>
          <g:actionSubmit value="XLS" action="transreportXLS"/>
        </div>
      </g:form>
      <g:form style="display:none" name="revisereportForm" controller="client" target="_blank">
        <div class="inline spacing">
          <label for="revisereport_start" class="auto">Дата c:</label>
          <g:datepicker class="normal nopad" name="revisereport_start" value=""/>
          <label for="revisereport_end" class="auto">по:</label>
          <g:datepicker class="normal nopad" name="revisereport_end" value=""/>
        </div>
        <div class="inline">
          <g:actionSubmit value="XLS" action="revisereportXLS"/>
        </div>
      </g:form>
      <div class="clear"></div>
    </div>
  </body>
</html>
