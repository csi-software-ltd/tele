<html>
  <head>
    <title><g:if test="${req}">Запрос № ${req.id}</g:if><g:else>Новый запрос</g:else></title>
    <meta name="layout" content="administrator" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.default.min.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'kendo.common.min.css')}" type="text/css" />
    <g:javascript library="kendo.culture.ru-RU.min" />
    <g:javascript library="kendo.web.min" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>      
      var clId = ${req?.client_id?:0};      
      function returnToList(){
        $("returnToListForm").submit();
      }
      function getRequestOperation(){
        if(${req?.modstatus==3?1:0}) $('operation_submit_button').click();
      }
      function getRequestHistory(){
        if(${req?1:0}) $('history_submit_button').click();
      }
      function processResponse(e){
        $('formid').value = e.responseJSON.formid
        var sErrorMsg = '';
        ['trantype_id','summa','nds','inn','account','prim','iban','swift','purpose','bankcity','client_id','comment','syscompany_id','syscompany_name'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        ['name','bik','beneficial','platdate','reqdate'].forEach(function(ids){
          if($(ids))
            $(ids).up('span').removeClassName('red');
        });
        if(e.responseJSON.errorcode.length){          
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {                                               
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Тип запроса"])}</li>'; $('trantype_id').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сумма"])}</li>'; $('summa').addClassName('red'); break;
              case 3: sErrorMsg+='<li>${message(code:"error.blank.message",args:["НДС, %"])}</li>'; $('nds').addClassName('red'); break;
              case 4: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ИНН"])}</li>'; $('inn').addClassName('red'); break;
              case 5: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Компания"])}</li>'; $('name').up('span').addClassName('red'); break;
              case 6: sErrorMsg+='<li>${message(code:"error.blank.message",args:["БИК"])}</li>'; $('bik').up('span').addClassName('red'); break;
              case 7: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Город банка"])}</li>'; $('bankcity').addClassName('red'); break;              
              case 8: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Расчетный счет"])}</li>'; $('account').addClassName('red'); break;
              case 9: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение платежа"])}</li>'; $('prim').addClassName('red'); break;
              case 10: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Бенефициар"])}</li>'; $('beneficial').up('span').addClassName('red'); break;
              case 11: sErrorMsg+='<li>${message(code:"error.blank.message",args:["IBAN код"])}</li>'; $('iban').addClassName('red'); break;
              case 12: sErrorMsg+='<li>${message(code:"error.blank.message",args:["SWIFT код банка"])}</li>'; $('swift').addClassName('red'); break;
              case 13: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Назначение платежа"])}</li>'; $('purpose').addClassName('red'); break;
              case 14: sErrorMsg+='<li>${message(code:"error.blank.message",args:["От кого"])}</li>'; $('client_id').addClassName('red'); break;
              case 15: sErrorMsg+='<li>Невозможно создать запрос. Для этого пользователя не указана ставка по данной операции.</li>'; $('trantype_id').addClassName('red'); break;
              case 16: sErrorMsg+='<li>${message(code:"error.reason.blank.message")}</li>'; $('comment').addClassName('red'); break;
              case 17: sErrorMsg+='<li>${message(code:"error.account.not.enough")}</li>'; break;
              case 18: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата платежа"])}</li>'; $('platdate').up('span').addClassName('red'); break;
              case 19: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Кому"])}</li>'; $('syscompany_id').addClassName('red'); break;
              case 20: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Кому"])}</li>'; $('syscompany_name').addClassName('red'); break;
              case 21: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Дата выдачи"])}</li>'; $('reqdate').up('span').addClassName('red'); break;
              case 22: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Сис. компания"])}</li>'; $('syscompany_id').addClassName('red'); break;
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(e.responseJSON.req){
          location.assign('${createLink(controller:'admin',action:'requestdetail')}'+'/'+e.responseJSON.req);
        } else
          location.assign('${createLink(controller:'admin',action:'requests')}');
      }
      function getTrantypes(lId){
        clId = lId;
        <g:remoteFunction controller="admin" action="gettrantype" params="\'id=\'+lId" onSuccess="processTypeResponse(e)" onLoading="\$('loader').show()" onLoaded="\$('loader').hide()"/>
        jQuery('#details').slideUp();
        $('btns').hide();
      }
      function processTypeResponse(e){
        var html='<option value="0">не выбран</option>';
        e.responseJSON.trantypes.forEach(function(it){
          html+='<option value="'+it.id+'">'+it.name+'</option>';          
        });
        $('trantype_id').innerHTML=html;
      }
      function loadForm(iNum){
        var lId=${req?.id?:0};
        if(iNum>0)
          <g:remoteFunction controller="admin" action="requesttype" params="\'id=\'+lId+'&type_id=\'+iNum+'&client_id=\'+clId" update="details" onComplete="loadDetails(iNum);" onSuccess="\$('btns').show();" onLoading="\$('loader').show();" onLoaded="\$('loader').hide();"/>          
      }
      function loadDetails(iNum){
        if(iNum=='4' || iNum=='5' || iNum=='6')
          jQuery('#label_fullsumma').html('Поступит на счет:');
        else if(iNum=='7' || iNum=='8' || iNum=='9')
          jQuery('#label_fullsumma').html('Списано со счета:');          
        jQuery('#details').slideDown();
      }
      function submitForm(iStatus){
        $('modstatus').value = iStatus
        $('submit_button').click()
      }
      function recalculate(summa,rate,balance,iType,bNeedrate){
        var vrate = 1;
        if($('vrate')&&bNeedrate)
          vrate = parseFloat($F('vrate'))
        if(parseFloat(summa)) {
          if(parseFloat(summa)*parseFloat(rate)*parseFloat(vrate) / ( 100 - parseFloat(rate) )< parseFloat(balance) || parseFloat(vrate)==1){
            $('fullsumma').value = parseFloat(summa);
            $('commrub').value = parseFloat(summa)*parseFloat(rate)*parseFloat(vrate) / ( 100 - parseFloat(rate) );
            $('commval').value = 0;
            jQuery('#nomoneynotice').fadeOut();
          } else {
            $('fullsumma').value = parseFloat(summa)+iType*parseFloat(summa)*parseFloat(rate)/(100-parseFloat(rate));
            $('commrub').value = 0;
            $('commval').value = parseFloat(summa)*parseFloat(rate) / (100 - parseFloat(rate));
            jQuery('#nomoneynotice').fadeIn();
          }
        } else {
          $('fullsumma').value = '';
          $('commval').value = 0;
          $('commrub').value = 0;
        }
      }
      function changebaseaccount(sType,sValue){
        if(sType!=sValue){ jQuery('.vrate').show(); jQuery('.comvrate').show();
        } else if(sType==sValue&&sType!='rub'){ jQuery('.vrate').show(); jQuery('.comvrate').hide();
        } else { jQuery('.vrate').hide(); jQuery('.comvrate').hide(); }
      }
      function toggleconversion(sValue){
        if(sValue=='rub'){ jQuery('.convertion').show();
        } else { jQuery('.convertion').hide(); }
      }
      function cancelloperation(){
        <g:remoteFunction controller="admin" action="cancelllastrequest" id="${req?.id?:-1}" onSuccess="location.reload(true)"/>
      }
      function computebankcomission(percent,summa,iNeedvrate){
        if($('bankcomsumma')){
          var vrate = 1;
          if($('vrate')&&iNeedvrate)
            vrate = parseFloat($F('vrate'))
          if(parseFloat(summa)) {
            $('bankcomsumma').value = (parseFloat(summa)*parseFloat(percent)*vrate/100).toFixed(2);
          } else {
            $('bankcomsumma').value = 0;
          }
        }
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="loadForm(${req?.trantype_id?:0});getRequestHistory();getRequestOperation();">
    <h3 class="fleft"><g:if test="${req}">Запрос № ${req.id}</g:if><g:else>Новый запрос</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку запросов</a>
    <div class="clear"></div>
    <g:formRemote name="requestDetailForm" url="${[controller:'admin',action:'saveRequestDetail',id:req?.id?:0]}" method="post" onSuccess="processResponse(e)">
    <g:if test="${req}">
      <label for="inputdate" disabled>Дата создания:</label>
      <input type="text" name="inputdate" readonly value="${String.format('%td.%<tm.%<tY %<tT',req.inputdate)}" />
      <label for="moddate" disabled>Дата изменения:</label>
      <input type="text" name="moddate" readonly value="${String.format('%td.%<tm.%<tY %<tT',req.moddate)}" />
      <label for="req_modstatus" disabled>Статус:</label>
      <g:select name="req_modstatus" value="${req.modstatus}" from="${Reqmodstatus.list()}" optionValue="name" optionKey="id" disabled="true" />
      <label for="trans_id" disabled>Операция:</label>
      <input type="text" name="trans_id" readonly value="${req.trans_id?:''}" />
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="${req.comment}" />
      <hr class="admin" />
    </g:if>

      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <li></li>
        </ul>
      </div>

      <label for="client_id">От кого:</label>
      <g:select name="client_id" value="${req?.client_id?:0}" from="${Client.findAllByModstatus(1)}" optionValue="name" optionKey="id" noSelection="${[0:'не выбран']}" onchange="getTrantypes(this.value)" disabled="${req?'true':'false'}" />
      <label for="trantype_id">Тип запроса:</label>
      <g:select name="trantype_id" id="trantype_id" value="${req?.trantype_id?:0}" from="${trantypes}" optionValue="name" optionKey="id" noSelection="${[0:'не выбран']}" onchange="loadForm(this.value)" disabled="${req?'true':'false'}"/>
      <img src="${resource(dir:'images',file:'loader.gif')}" alt="" id="loader" style="display:none" /><br/>
      
      <div id="details"></div>
      <hr class="admin" />
      
      <div class="fright" id="btns" style="display:none">
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />
      <g:if test="${(req?.modstatus?:0) in [0,1,2]}">
        <input type="button" class="spacing" value="Сохранить" onclick="submitForm(${req?.modstatus?:0})"/>
      </g:if><g:if test="${!(req?.modstatus?:0)}">
        <a class="button" onclick="submitForm(1)">Отправить &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if><g:if test="${req?.modstatus==-1}">
        <a class="button red" onclick="submitForm(1)">Восстановить &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if><g:if test="${req?.modstatus==1}">
        <a class="button" onclick="submitForm(2)">В работу &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if><g:if test="${req?.modstatus in [1,2]}">
        <input type="button" id="doButton" value="Выполнить" onclick="if(confirm('Выполнить запрос?')) submitForm(3)"/>
      </g:if><g:if test="${req?.modstatus in [1,2]}">
        <a class="button red" onclick="submitForm(-1)">Отказать &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if><g:if test="${isCanCancel}">
        <input type="button" class="spacing" style="background:red;color:gold" value="Отменить выполнение" onclick="if(confirm('Отменить выполнение?')) cancelloperation();"/>
      </g:if>
        <input style="display:none" type="submit" id="submit_button" value="Сохранить" />
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="0"/>
      <input type="hidden" id="formid" name="formid" value="${formid}"/>
    </g:formRemote>
  <g:if test="${req}">
    <div class="clear"></div>
    <h3>История изменений</h3>
    <div id="history"></div>
    <g:formRemote name="historyForm" url="[action:'requesthistory',id:req.id]" update="[success:'history']">
      <input type="submit" id="history_submit_button" style="display:none" />
    </g:formRemote>
  </g:if>
  <g:if test="${req?.modstatus==3}">
    <div class="clear"></div>
    <h3>Связанные операции</h3>
    <div id="operation"></div>
    <g:formRemote name="operationForm" url="[action:'requestoperation',id:req.id]" update="[success:'operation']">
      <input type="submit" id="operation_submit_button" style="display:none" />
    </g:formRemote>
  </g:if>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'admin', action:'requests',params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
