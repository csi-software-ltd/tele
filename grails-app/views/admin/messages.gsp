<html>
  <head>
    <title>Административное приложение: Переписка</title>
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
      function setMessageArchive(lId){
        <g:remoteFunction action='setMessageArchive' params="\'id=\'+lId" onSuccess="\$('form_submit_button').click();"/>
      }
      function starToggle(lId){
        <g:remoteFunction action='setMessageFavourite' params="\'id=\'+lId" onSuccess="changeStar(lId)"/>
      }
      function changeStar(lId){
        if($("star"+lId).hasClassName('icon-star-empty')){
          $("star"+lId).removeClassName('icon-star-empty');
          $("star"+lId).addClassName('icon-star');
        }else{
          $("star"+lId).removeClassName('icon-star');
          $("star"+lId).addClassName('icon-star-empty');
        }
      }
      function toggleFavourite(){
        if($("is_favourite").value=="1"){
          $("is_favourite").value=0;
          $("favourite").removeClassName('active');
        }else{
          $("is_favourite").value=1;        
          $("favourite").addClassName('active');
        }
        $("modstatus").value=-1;
        $("inbox").removeClassName('active');
        $("archive").removeClassName('active');
        $("form_submit_button").click();
      }
      function setModstatus(iStatus){      
        if(iStatus==1){
          $("modstatus").value=1;
          $("inbox").addClassName('active');
          $("archive").removeClassName('active');   
        }else{
          $("modstatus").value=0;
          $("inbox").removeClassName('active');
          $("archive").addClassName('active');          
        }  
        $("is_favourite").value=0;
        $("favourite").removeClassName('active');
        $("form_submit_button").click();
      }
      function showListing(){
        $('offset').value=0;
        $('form_submit_button').click();
      }
    </g:javascript>      
  </head>
  <body onload="\$('form_submit_button').click();">
    <g:formRemote name="allForm" url="[action:'messagelist']" update="[success:'list']">
      <div class="tabs padtop fright">
        <a class="${inrequest?.modstatus==1?'active':''}" id="inbox" onclick="setModstatus(1)"><i class="icon-envelope icon-large"></i> входящие</a>
        <a class="${inrequest?.modstatus==0?'active':''}" id="archive" onclick="setModstatus(0)"><i class="icon-trash icon-large"></i> архив</a>
        <a class="${inrequest?.is_favourite?'active':''}" id="favourite" onclick="toggleFavourite()"><i class="icon-star icon-large"></i> избранное</a>        
      </div>      
      <div class="clear"></div>
      <div class="padtop filter">
        <label class="auto" for="client_id">Клиент:</label>
        <g:select class="auto" name="client_id" value="${inrequest?.client_id?:0}" from="${Client.list()}" optionValue="name" optionKey="id" noSelection="${['0':'все']}"/>        
        <label class="auto" for="keyword">Фраза:</label>
        <input type="text" name="keyword" value="${inrequest?.keyword?:''}" />          
        <div class="fright">          
          <input type="reset" class="spacing" value="Сброс" />
          <input type="button" value="Показать" onclick="showListing()"/>
          <input type="submit" id="form_submit_button" value="" style="display:none" /><br/>
          <g:link action="messagedetail" class="button">Написать &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
          <g:link action="messagedetail" params="[to_all:1]" class="button red" style="padding-left:0">Написать всем &nbsp;<i class="icon-angle-right icon-large"></i></g:link>
        </div>
        <div class="clear"></div>
      </div>      
      <input type="hidden" id="modstatus" name="modstatus" value="${inrequest?.modstatus?:1}" />
      <input type="hidden" id="is_favourite" name="is_favourite" value="${inrequest?.is_favourite?:0}" />
      <input type="hidden" id="offset" name="offset" value="0" />
    </g:formRemote>    
    <div id="list"></div>
  </body>
</html>
