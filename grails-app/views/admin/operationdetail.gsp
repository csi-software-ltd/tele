<html>
  <head>
    <title>Административное приложение: Операция № ${transaction.id}</title>
    <meta name="layout" content="administrator" />
    <g:javascript>
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        if(e.responseJSON.errorcode.length){
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else
          location.reload(true);
      }
    </g:javascript>
  </head>
  <body>
    <h3 class="fleft">Операция № ${transaction.id} - ${trantype.name}</h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку операций</a>
    <div class="clear"></div>
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>
    <hr class="admin" />
    <g:formRemote name="requestDetailForm" url="${[controller:'admin',action:'saveOperationDetail',id:transaction?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="summa">Клиент:</label>
      <input type="text" disabled value="${Client.get(transaction.client_id)?.name} [${transaction.client_id}]" /><br/>
      <label for="summa">Сумма:</label>
      <input type="text" disabled value="${number(value:transaction.summa*transaction.vrate)}" />
      <label for="summa">Валюта:</label>
      <input type="text" disabled value="${trantype.code}" /><br/>
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="${transaction.comment}" />
      <hr class="admin" />

      <div class="fright" id="btns">
        <input type="reset" class="spacing" value="Сброс"/>
        <input type="submit" value="Сохранить" />
      </div>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'admin', action:'operations']}">
    </g:form>
  </body>
</html>
