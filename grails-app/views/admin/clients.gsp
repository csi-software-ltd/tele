<html>
  <head>
    <title>Административное приложение: Клиенты</title>
    <meta name="layout" content="administrator" />
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
    <g:formRemote name="allForm" url="[action:'clientlist']" update="[success:'list']">
      <div class="padtop filter">
        <label class="auto" for="client_id">Код:</label>
        <input type="text" class="mini" name="client_id" value="${inrequest?.client_id?:''}"/>
        <label class="auto" for="name">Название:</label>
        <input type="text" name="name" />
        <label class="auto" for="modstatus">Статус:</label>
        <g:select class="auto" name="modstatus" value="" keys="${-1..1}" from="${['все','неактивный','активный']}" value="${inrequest?.modstatus?:-1}"/><br/>
        <label class="auto" for="is_dealer">
          <input type="checkbox" name="is_dealer" value="1" <g:if test="${inrequest?.is_dealer}">checked</g:if> />
          Посредник
        </label>
        <label class="auto" for="is_block">
          <input type="checkbox" name="is_block" value="1" <g:if test="${inrequest?.is_block}">checked</g:if> />
          Блокирован
        </label>
        <div class="fright">          
          <input type="reset" class="spacing" value="Сброс" />
          <input type="submit" id="form_submit_button" value="Показать" />
          <g:link action="clientdetail" class="button">Добавить &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </div>
        <div class="clear"></div>
      </div>      
    </g:formRemote>    
    <div id="list"></div>    
  </body>
</html>
