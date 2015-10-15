<<html>
  <head>
    <title>Клиентское приложение: <g:if test="${msg}">Переписка № ${msg.id}</g:if><g:else>Новое сообщение</g:else></title>
    <meta name="layout" content="main" />
    <g:javascript>
      function remEmptyMbox(){
        <g:if test="${!(msg?.client_id?:0)}">
          var lId=${msg?.id?:0};
          <g:remoteFunction action='remEmptyMbox' onSuccess="returnToList()" params="'id='+lId" />
        </g:if> 
        <g:else>
          returnToList();
        </g:else>
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function getMboxRec(){
        if(${msg?1:0}) $('history_submit_button').click();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['subject','mtext',          
          ].forEach(function(ids){
            if($(ids))
              $(ids).removeClassName('red');
          });
        if(e.responseJSON.errorcode.length){          
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {                                               
              case 2: sErrorMsg+='<li>Не заполнено поле "Тема"</li>'; $("subject").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Не заполнено поле "Текст сообщения"</li>'; $("mtext").addClassName('red'); break;                                                                            
              
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          <g:if test="${new_msg}">              
            location.assign('${createLink(action:'messagedetail',id:msg?.id?:0)}');
          </g:if>
          <g:else>
            location.reload(true);
          </g:else>        
        }
      }
      function starToggle(){
        var lId=${msg?.id?:0};
        <g:remoteFunction action='setMessageFavourite' params="\'id=\'+lId" onSuccess="changeStar()"/>
      }
      function changeStar(){
        if($("star").hasClassName('icon-star-empty')){
          $("star").removeClassName('icon-star-empty');
          $("star").addClassName('icon-star');
        }else{
          $("star").removeClassName('icon-star');
          $("star").addClassName('icon-star-empty');
        }
      }
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
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
    </style>
  </head>
  <body onload="getMboxRec()">
    <h3 class="fleft"><g:if test="${!new_msg}">Переписка № ${msg.id}</g:if><g:else>Новое сообщение</g:else>
    &nbsp;<i id="star" class="icon-star${!msg.is_clientfavourite?'-empty':''} icon-gold icon-large" onclick="starToggle()"></i></h3>    
    <a class="button back fright" href="javascript:void(0)" onclick="remEmptyMbox();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку сообщений</a>
    <div class="clear"></div>      
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>        
    <g:if test="${msg}">
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" readonly value="${String.format('%td.%<tm.%<tY %<tT',msg?.inputdate)}" />           
    </g:if>    
    <g:formRemote name="messageDetailForm" url="${[action:'saveMessageDetail',id:msg?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="subject" <g:if test="${!new_msg}">disabled</g:if>>Тема:</label>
      <input type="text" id="subject" name="subject" value="${msg?.subject?:''}" style="width:695px" <g:if test="${!new_msg}">readonly</g:if>/><br/>                                         
      <g:if test="${msg?.lasttext}">        
      <label for="lasttext" disabled>Последнее сообщение:</label>
      <textarea id="lasttext" name="lasttext" readonly rows="5"><g:formatDate format="dd.MM.yyyy HH:mm:ss" date="${msg.moddate}"/> ${lastmboxrec?.is_fromclient?'я':'admin'} писал:&#10;${msg?.lasttext?:''}</textarea>
      </g:if>        
      <label for="mtext">Текст сообщения:</label>
      <textarea id="mtext" name="mtext" rows="5"></textarea>
     
      <hr class="admin" />
      <div class="fright">        
        <input type="reset" class="spacing" value="Отменить" onclick="remEmptyMbox()" />
        <input type="submit" class="spacing" id="submit_button" value="Отправить" />        
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="${msg?.modstatus?:1}"/>           
    </g:formRemote>  
  <g:if test="${msg}">       
    <g:form name="savePicture" method="post" url="${[controller:'client',action:'savemailpicture']}" enctype="multipart/form-data" target="upload_target">      
      <div class="fleft">
        <label for="file1">Прикрепить файл:</label>
        <input type="file" name="file1" id="file1" size="23" accept="image/jpeg" onchange="startSubmit('savePicture')"/>                
        <input type="hidden" name="mbox_id" value="${msg?.id?:0}" />
        <input type="hidden" name="nrec" value="${msg?.nrec?(msg?.nrec+1):1}" />
      </div>
    </g:form>
    <img src="${resource(dir:'images',file:'loader.gif')}" alt="" id="loader" style="display:none" />
    <iframe id="upload_target" name="upload_target" src="#" style="width:0;height:0;border:0px solid #fff;"></iframe>
  </g:if>     
    
  <g:if test="${msg}">
    <div class="clear"></div>
    <h3>История переписки</h3>
    <div id="details"></div>
    <g:formRemote name="historyForm" url="${[action:'messagehistory',id:msg.id]}" update="[success:'details']">
      <input type="submit" id="history_submit_button" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'client',action:'messages',params:[fromEdit:1]]}">
    </g:form>
    
    <!-- /Загрузка имиджей -->
    <script type="text/javascript">
      function reloadImage(){
        $('file1').value='';
      }    
      function startSubmit(sName){
        $(sName).submit();
        $('loader').show();
        return true;
      }    
      function stopUpload(sFilename,iErrNo,sMaxWeight) {        
        $('loader').hide();
        if(iErrNo==0){
          $("errorlist").up('div').hide();          
        }else{
          var sText="Ошибка загрузки";
          switch(iErrNo){
            case 1: sText="Удивительная ошибка загрузки"; break;
            case 2: sText="Ошибка загрузки"; break;
            case 3: sText="Слишком большой файл. Ограничение "+sMaxWeight+" Мб"; break;
            case 4: sText="Неверный тип файла. Используйте JPG, MS-EXCEL, MS-WORD"; break;
            case 5: sText="Ошибка чтения файла"; break;
          }
          $("errorlist").innerHTML=sText;
          $("errorlist").up('div').show();          
        }
        return true;
      }
    </script>  
  </body>
</html>
