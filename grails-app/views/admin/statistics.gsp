<html>
  <head>
    <title>Административное приложение: Статистика</title>
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
      function resetClient(){
        $("client_id").selectedIndex = 0;
      }
    </g:javascript>
  </head>
  <body onload="\$('form_submit_button').click();">
    <g:formRemote name="allForm" url="[action:'statisticlist']" update="[success:'list']">
      <div class="padtop filter">        
        <label class="auto" for="client_id">Клиент:</label>
        <g:select class="auto" id="client_id" name="client_id" value="${inrequest?.client_id?:0}" from="${clients}" optionValue="name" optionKey="id" noSelection="${['0':'не задано']}"/>        
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс" onclick="resetClient()"/>
          <input type="submit" id="form_submit_button" value="Показать" />          
        </div>
        <div class="clear"></div>
      </div>
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>
