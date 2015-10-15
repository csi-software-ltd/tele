<html>
  <head>
    <title>Административное приложение: Запросы на операции</title>
    <meta name="layout" content="administrator" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript>
      function clickPaginate(event){
        event.stop();
        var link = event.element();
        if(link.href == null){
          return;
        }
        new Ajax.Updater(
          { success: $('ajax_wrap') },
          link.href,
          { evalScripts: true });
      }
      function setModstatus(iStatus){      
        $('modstatus').value=iStatus;
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('status'+iStatus).addClassName('active');
        $('form_submit_button').click();        
      }
      function resetdata(){
        $('client_id')[$('client_id').selectedIndex].defaultSelected=false;
        $('trantype_id')[$('trantype_id').selectedIndex].defaultSelected=false;
        $('date_start').defaultValue='';
        $('date_end').defaultValue='';
        $('request_id').defaultValue='';
      }
    </g:javascript>      
  </head>
  <body onload="setModstatus(${inrequest?.modstatus})">
    <g:formRemote name="allForm" url="[action:'requestlist']" update="[success:'list']">
      <div class="tabs padtop fright">
        <a id="status-100" onclick="setModstatus(-100)"><i class="icon-list icon-large"></i> все (${Request.countByModstatusNot(0)})</a>
      <g:each in="${Reqmodstatus.findAllByIdNot(0,[sort:'order',order:'asc'])}" var="item" status="i">        
        <a id="status${item.id}" onclick="setModstatus(${item.id})"><i class="icon-${item.icon} icon-large"></i> ${item.name} (${Request.countByModstatus(item.id)})</a>        
      </g:each>
      </div>      
      <div class="clear"></div>
      <div class="padtop filter">
        <label class="auto" for="request_id">№:</label>
        <input type="text" class="mini" id="request_id" name="request_id" value="${inrequest?.request_id?:''}" />
        <label class="auto" for="client_id">Клиент:</label>
        <g:select class="auto" name="client_id" value="${inrequest?.client_id?:0}" from="${Client.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>
        <label class="auto" for="trantype_id">Тип:</label>
        <g:select class="auto" name="trantype_id" value="" from="${trantypes}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/><br>
        <label for="date_start" class="auto">Дата c:</label>
        <g:datepicker class="normal nopad" name="date_start" value="${inrequest?.date_start?String.format('%td.%<tm.%<tY',inrequest.date_start):''}"/>
        <label for="date_end" class="auto">по:</label>
        <g:datepicker class="normal nopad" name="date_end" value="${inrequest?.date_end?String.format('%td.%<tm.%<tY',inrequest.date_end):''}"/>
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс" onclick="resetdata()"/>
          <input type="submit" id="form_submit_button" value="Показать" />
          <g:link action="requestdetail" class="button">Новый &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </div>
        <div class="clear"></div>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="${inrequest?.modstatus?:-100}" />      
    </g:formRemote>    
    <div id="list"></div>
  </body>
</html>
