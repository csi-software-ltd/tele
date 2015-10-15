<html>
  <head>
    <title>Административное приложение: Операции</title>
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
    </g:javascript>
  </head>
  <body onload="\$('form_submit_button').click();">
    <g:formRemote name="allForm" url="[action:'operationlist']" update="[success:'list']">
      <div class="padtop filter">
        <label class="auto" for="id">№:</label>
        <input type="text" class="mini" name="id" />
        <label class="auto" for="request_id">Запрос:</label>
        <input type="text" class="mini" name="request_id" />
        <label class="auto" for="trantype_id">Тип:</label>
        <g:select name="trantype_id" value="" from="${Trantype.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" style="width:280px"/>
        <label class="auto" for="client_id">Клиент:</label>
        <g:select name="client_id" value="${inrequest?.client_id?:0}" from="${Client.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" style="width:130px"/><br/>
        <label for="date_start" class="auto">Дата c:</label>
        <g:datepicker class="normal nopad" name="date_start" value=""/>
        <label for="date_end" class="auto">по:</label>
        <g:datepicker class="normal nopad" name="date_end" value=""/>
        <label class="auto" for="syscompany_id">Сис. компания:</label>
        <g:select name="syscompany_id" value="${inrequest?.syscompany_id?:0}" from="${Company.findAllByIs_system(1)}" optionValue="name" optionKey="id" noSelection="${['0':'все']}" style="width:130px"/><br/>
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс" />
          <input type="submit" id="form_submit_button" value="Показать" />
          <g:link action="outgoings" class="button">Издержки &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </div>
        <div class="clear"></div>
      </div>
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>
