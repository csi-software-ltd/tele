<html>
  <head>
    <title>Административное приложение: Компании</title>
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
      function setModstatus(iStatus){      
        $('modstatus').value=iStatus;
        jQuery('.tabs a[class="active"]').removeClass('active');
        $('status'+iStatus).addClassName('active');
        $('form_submit_button').click();        
      }      
      function setCompanyArchive(lId,iModstatus){
        <g:remoteFunction action='setcompanystatus' params="'id='+lId+'&modstatus='+iModstatus" onSuccess="\$('form_submit_button').click();"/>
      }
    </g:javascript>      
  </head>
  <body onload="setModstatus(${(inrequest?.modstatus==1||inrequest?.modstatus==-1)?1:0})">
    <div class="tabs padtop fright">             
      <a id="status1" onclick="setModstatus(1)"><i class="icon-check icon-large"></i> активные</a>    
      <a id="status0" onclick="setModstatus(0)"><i class="icon-trash icon-large"></i> архивные</a>         
    </div>      
    <div class="clear"></div>
    <div class="padtop filter">   
      <g:link action="companydetail" class="button fright">Добавить &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
      <div class="clear"></div>
    </div>
    <g:formRemote name="allForm" url="[action:'companylist']" update="[success:'list']">           
      <input type="submit" id="form_submit_button" value="Показать" style="display:none"/>                             
      <input type="hidden" id="modstatus" name="modstatus" value="${(inrequest?.modstatus==1||inrequest?.modstatus==-1)?1:0}" />      
    </g:formRemote>    
    <div id="list"></div>
  </body>
</html>
