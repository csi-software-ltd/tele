<html>
  <head>
    <title>${infotext?.title?:''}</title>
    <meta name="layout" content="main" />
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
      function setValuta(sValuta){      
        $('valuta').value=sValuta;
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('valuta_'+sValuta).addClassName('active');
        $('form_submit_button').click();        
      }  
    </g:javascript>      
  </head>
  <body onload="setValuta('')">
    <g:formRemote name="allForm" url="[action:'operationlist']" update="[success:'list']">
      <div class="tabs padtop fright">
        <a class="active" id="valuta_" onclick="setValuta('')"><i class="icon-list icon-large"></i> все</a>
        <a class="${(inrequest?.valuta=='rub')?'active':''}" id="valuta_rub" onclick="setValuta('rub')"><i class="icon-rub icon-large"></i> ${number(value:user?.account_rub)}</a>
      <g:each in="${Valuta.list()}">
        <a class="${(inrequest?.valuta==it.code.toLowerCase())?'active':''}" id="valuta_${it.code.toLowerCase()}" onclick="setValuta('${it.code.toLowerCase()}')"><i class="icon-${it.code.toLowerCase()} icon-large"></i> ${number(value:user['account_'+it.code.toLowerCase()])}</a>
      </g:each>
      </div>
      <div class="clear"></div>
      <div class="padtop filter">
        <label class="auto" for="id">№:</label>
        <input type="text" class="mini" name="id" />
        <label class="auto" for="request_id">Запрос:</label>
        <input type="text" class="mini" name="request_id" />
        <label class="auto" for="trantype_id">Тип:</label>
        <g:select class="auto" name="trantype_id" value="" from="${Trantype.findAllByIs_needOrIs_service(1,0)}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/><br/>
        <label for="date_start" class="auto">Дата c:</label>
        <g:datepicker class="normal nopad" name="date_start" value=""/>
        <label for="date_end" class="auto">по:</label>
        <g:datepicker class="normal nopad" name="date_end" value=""/>
        <div class="fright">
          <input type="reset" class="spacing" value="Сброс" />
          <input type="submit" id="form_submit_button" value="Показать" />
        </div>
        <div class="clear"></div>
      </div>
      <input type="hidden" name="valuta" id="valuta" value="${inrequest?.valuta?:''}" />
    </g:formRemote>
    <div id="list"></div>
  </body>
</html>
