<html>
  <head>
    <title>Административное приложение: Списание на поддержку</title>
    <meta name="layout" content="administrator" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['summa','prim'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение платежа"])}</li>'; $('prim').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.sysaccount.not.enough")}</li>'; $('summa').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else
          location.assign('${createLink(controller:'admin',action:'operations')}');
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body>
    <h3 class="fleft">Списание на поддержку</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку операций</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[controller:'admin',action:'saveoutgoings']}" method="post" onSuccess="processResponse(e)">

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <li></li>
        </ul>
      </div>

      <div id="details">
        <label for="summa">Сумма:</label>
        <input type="text" id="summa" name="summa" value="" /><br/>
        <label for="comment">Назначение платежа:</label>
        <g:textArea name="comment" id="comment" value="Списание на поддержку за ${monthname} месяц" />
      </div>
      <hr class="admin" />
      <div class="fright" id="btns">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
        <input type="submit" id="submit_button" value="Сохранить" />
      </div>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'admin', action:'operations']}">
    </g:form>
  </body>
</html>
