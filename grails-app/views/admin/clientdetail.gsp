<html>
  <head>
    <title>Административное приложение: <g:if test="${client}">Редактирование клиента № ${client.id}</g:if><g:else>Добавление нового клиента</g:else></title>
    <meta name="layout" content="administrator" />    
    <g:javascript>
      var oldModstatus=${client?.modstatus?:0};
      function init(){
        <g:if test="${flash?.success}">
          $("infolist").up('div').show();
        </g:if>
        <g:if test="${!client?.parent?:0}">
          $("parent2_div").update('');
        </g:if>
      }
      function returnToList(){
        $("returnToListForm").submit();
      }      
      function processResponse(e){        
        var sErrorMsg = '';
          ['name','login','password','confirm_password',          
          'tran_rate_rub','tran_rate_eur','tran_rate_usd',
          'cashin_rate_rub','cashin_rate_eur','cashin_rate_usd',
          'cashout_rate_rub','cashout_rate_eur','cashout_rate_usd',
          'account_rub','account_usd','account_eur',
          'dealer_cashin_rate_rub','dealer_cashin_rate_usd','dealer_cashin_rate_eur',
          'dealer_cashout_rate_rub','dealer_cashout_rate_usd','dealer_cashout_rate_eur',
          'dealer_refill_rate_rub','dealer_refill_rate_usd','dealer_refill_rate_eur',
          'dealer_tran_rate_rub','dealer_tran_rate_usd','dealer_tran_rate_eur'
          ].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        if(e.responseJSON.errorcode.length){          
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {         
              case 4: sErrorMsg+='<li>Не заполнено обязательное поле "Имя"</li>'; $("name").addClassName('red'); break;
              case 5: sErrorMsg+='<li>Не заполнено обязательное поле "Логин"</li>'; $("login").addClassName('red'); break;
              case 6: sErrorMsg+='<li>Такой "Логин" уже занят</li>'; $("login").addClassName('red'); break;
            
              case 1: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${passwlength} знаков из больших и маленьких латинских букв и цифр</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red'); break;
              case 2: sErrorMsg+='<li>Слишком короткий пароль, нужно не менее ${passwlength} символов</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Пароли не совпадают</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red');break;              
              
              case 21: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Перевод: RUB"</li>'; $("tran_rate_rub").addClassName('red');break;
              case 22: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Перевод: USD"</li>'; $("tran_rate_usd").addClassName('red');break;
              case 23: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Перевод: EUR"</li>'; $("tran_rate_eur").addClassName('red');break;

              case 31: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Внесение: RUB"</li>'; $("cashin_rate_rub").addClassName('red');break;
              case 32: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Внесение: USD"</li>'; $("cashin_rate_usd").addClassName('red');break;
              case 33: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Внесение: EUR"</li>'; $("cashin_rate_eur").addClassName('red');break;

              case 41: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Выдача: RUB"</li>'; $("cashout_rate_rub").addClassName('red');break;
              case 42: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Выдача: USD"</li>'; $("cashout_rate_usd").addClassName('red');break;
              case 43: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Выдача: EUR"</li>'; $("cashout_rate_eur").addClassName('red');break;

              case 51: sErrorMsg+='<li>Некорректные данные в поле "Остатки по счетам: RUB"</li>'; $("account_rub").addClassName('red');break;
              case 52: sErrorMsg+='<li>Некорректные данные в поле "Остатки по счетам: USD"</li>'; $("account_usd").addClassName('red');break;
              case 53: sErrorMsg+='<li>Некорректные данные в поле "Остатки по счетам: EUR"</li>'; $("account_eur").addClassName('red');break;

              case 61: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Пополнение счета: RUB"</li>'; $("refill_rate_rub").addClassName('red');break;
              case 62: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Пополнение счета: USD"</li>'; $("refill_rate_usd").addClassName('red');break;
              case 63: sErrorMsg+='<li>Некорректные данные в поле "Ставки по операциям: Пополнение счета: EUR"</li>'; $("refill_rate_eur").addClassName('red');break;              
              
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")} <g:if test="${!client}">Примечание: имя клиента уникально.</g:if></li>'; break;
              
              case 71: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Внесение: RUB"</li>'; $("dealer_cashin_rate_rub").addClassName('red');break;
              case 72: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Внесение: USD"</li>'; $("dealer_cashin_rate_usd").addClassName('red');break;
              case 73: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Внесение: EUR"</li>'; $("dealer_cashin_rate_eur").addClassName('red');break;              

              case 81: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Выдача: RUB"</li>'; $("dealer_cashout_rate_rub").addClassName('red');break;
              case 82: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Выдача: USD"</li>'; $("dealer_cashout_rate_usd").addClassName('red');break;
              case 83: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Выдача: EUR"</li>'; $("dealer_cashout_rate_eur").addClassName('red');break;              

              case 91: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Пополнение счета: RUB"</li>'; $("dealer_refill_rate_rub").addClassName('red');break;
              case 92: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Пополнение счета: USD"</li>'; $("dealer_refill_rate_usd").addClassName('red');break;
              case 93: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Пополнение счета: EUR"</li>'; $("dealer_refill_rate_eur").addClassName('red');break;              

              case 101: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Перевод: RUB"</li>'; $("dealer_tran_rate_rub").addClassName('red');break;
              case 102: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Перевод: USD"</li>'; $("dealer_tran_rate_usd").addClassName('red');break;
              case 103: sErrorMsg+='<li>Некорректные данные в поле "Ставки посреднических услуг: Перевод: EUR"</li>'; $("dealer_tran_rate_eur").addClassName('red');break;                                          
            }
          });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
          
          $("modstatus").value=oldModstatus;                              
        } else {
          <g:if test="${client}">
            location.reload(true);
          </g:if>
          <g:else>
            location.assign('${createLink(controller:'admin',action:'clientdetail')}'+'/'+e.responseJSON.client_id);
          </g:else>                            
        }        
      }
      function setClientPassword(lId){
        var password=$("password").value;
        var confirm_pass=$("confirm_password").value;
        <g:remoteFunction action='setClientPassword' onSuccess="processClientPassword(e)" params="'id='+lId+'&password='+password+'&confirm_pass='+confirm_pass" />
      }
      function processClientPassword(e){
        var sErrorMsg = '';
          ['password','confirm_password',         
          ].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        if(e.responseJSON.errorcode.length){          
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {                                   
              case 1: sErrorMsg+='<li>Некорректный пароль</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red'); break;
              case 2: sErrorMsg+='<li>Слишком короткий пароль, нужно не менее ${passwlength} символов</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red'); break;
              case 3: sErrorMsg+='<li>Пароли не совпадают</li>'; $("password").addClassName('red'); $("confirm_password").addClassName('red');break;                            
                         
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("errorpasslist").innerHTML=sErrorMsg;
          $("errorpasslist").up('div').show();
        }else{          
          $("errorpasslist").innerHTML=sErrorMsg;
          $("errorpasslist").up('div').hide();
          jQuery('#changePassword').slideUp();          
          $("password").value='';
          $("confirm_password").value='';
        }
      }
      function setActive(iId){
        oldModstatus=$("modstatus").value;
        $("modstatus").value=iId;
        if(!checkAccount())
          $("modstatus").value=oldModstatus;
      }
      function getClientHistory(){
        <g:if test="${client}">       
          $('history_submit_button').click();
        </g:if>  
      }      
      if((navigator.userAgent.search('Firefox')>-1)||(navigator.userAgent.search('Opera')>-1)||(navigator.userAgent.search('Chrome')>-1)||(navigator.userAgent.search('MSIE 10.0')>-1))
        jQuery('#is_block').css('width','202px');
      else    
        jQuery('#is_block').css('width','197px');

      function checkAccount(){
        ['account_rub','account_usd','account_eur'         
          ].forEach(function(ids){
            $(ids).removeClassName('red');
          });
        var bChange=0;
        if($("account_rub").value!="${client?.account_rub}"){
          bChange=1;
          $("account_rub").addClassName('red');
        }
        if($("account_usd").value!="${client?.account_usd}"){
          bChange=1;
          $("account_usd").addClassName('red');
        }
        if($("account_eur").value!="${client?.account_eur}"){
          bChange=1;
          $("account_eur").addClassName('red');
        }
        if(bChange){
          if (confirm('Вы подтверждаете изменение остатков по счетам?')){
            $("submit_button").click();
          }else{
            return false;
          }
        }else   
          $("submit_button").click();        
      }                
      function toggleBlock(){
        var iNum=$("is_block").value;
        if(iNum=="0"){
          $("blockIcon").removeClassName("icon-unlock");
          $("blockIcon").addClassName("icon-lock");
          $("is_block").value="1";
          $("is_block_text").value="блокирован";      
          $("is_block_text").up('span').addClassName("red");          
        }else{                    
          $("blockIcon").removeClassName("icon-lock");
          $("blockIcon").addClassName("icon-unlock");
          $("is_block").value="0";
          $("is_block_text").value="не блокирован";
          $("is_block_text").up('span').removeClassName("red");
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
      function setParent2(iId){
        var curClientId=${client?.id?:0};
        if(iId!="0")
          <g:remoteFunction action='get_parent2' params="'id='+iId+'&cur_id='+curClientId" update="[success:'parent2_div']" />
        else
          $("parent2_div").update('');
      }
      function resetValue(sId){      
        if($(sId).checked==false)
          $(sId).next().value="0.00";
      }
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px}      
      input.mini{width:60px!important}
    </style>
  </head>
  <body onload="init();getClientHistory();">
    <h3 class="fleft"><g:if test="${client}">Клиент № ${client.id}</g:if><g:else>Добавление нового клиента</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку клиентов</a>
    <div class="clear"></div>
    
    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>
    <div class="error-box" style="display:none">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>
    
    <g:formRemote name="clientDetailForm" url="[action:'saveClientDetail',id:client?.id?:0]" method="post" onSuccess="processResponse(e)">          
      <label for="name">Имя:</label>
      <input type="text" id="name" name="name" value="${client?.name}"/>
      <label for="login" <g:if test="${client}">disabled</g:if>>Логин:</label>
      <input type="text" id="login" name="login" value="${client?.login}" <g:if test="${client}">disabled="true"</g:if>/>
    <g:if test="${client}">
      <label for="status" disabled>Статус:</label>
      <input type="text" id="status" readonly value="${client?.modstatus==0?'не активен':'активен'}" />
      <label for="is_block_text" disabled>Блокировка:</label>          
      <span class="input-append ${client?.is_block?'red':''}">            
        <input type="text" class="nopad normal" id="is_block_text" readonly value="${client?.is_block==0?'не блокирован':'блокирован'}" />
        <span class="add-on" onclick="toggleBlock()"><i id="blockIcon" class="icon-${client?.is_block?'lock':'unlock'}"></i></span>
      </span><br/>
      <input type="hidden" id="is_block" name="is_block" value="${client?.is_block?:0}"/>
      <label for="inputdate" disabled>Зарегистрирован:</label>
      <input type="text" name="inputdate" readonly value="${String.format('%td.%<tm.%<tY %<tT',client?.inputdate)}" />
      <label for="moddate" disabled>Изменен:</label>
      <input type="text" name="moddate" readonly value="${String.format('%td.%<tm.%<tY %<tT',client?.moddate)}" /><br/>
      
    </g:if>
      <label for="swiftonclient">Свифт:</label>
      <g:select name="swiftonclient" value="${(client?.swiftonclient==null)?1:(client?.swiftonclient?:0)}" keys="${1..0}" from="${['клиент','система']}" /><br/>
      <label for="parent">Рекомендатель 1:</label>
      <g:select name="parent" value="${client?.parent?:0}" from="${dealers}" optionKey="id" optionValue="name" noSelection="[0:'не выбран']" onChange="setParent2(this.value)"/>      
      <span id="parent2_div">
        <label for="parent2">Рекомендатель 2:</label>
        <g:select name="parent2" value="${client?.parent2?:0}" from="${dealers2}" optionKey="id" optionValue="name" noSelection="[0:'не выбран']"/><br/>
      </span><br/>      
      <label for="comment">Комментарий администратора:</label>      
      <g:textArea name="comment" value="${client?.comment}" />
      <hr class="admin" />
     
      <div class="grid_6 alpha">
        <h3>Остатки по счетам</h3>
        <table class="list">
          <tbody>
            <tr>           
              <th width="30" class="round"><i class="icon-rub icon-light"></i></th>  
              <td class="round"><input type="text" class="nopad" id="account_rub" name="account_rub" value="${client?.account_rub}" /></td>
            </tr>
            <tr>
              <th><i class="icon-usd icon-light"></i></th>
              <td><input type="text" class="nopad" id="account_usd" name="account_usd" value="${client?.account_usd}" /></td>
            </tr>
            <tr>
              <th><i class="icon-euro icon-light"></i></th>              
              <td><input type="text" class="nopad" id="account_eur" name="account_eur" value="${client?.account_eur}" /></td>            
            </tr>          
            </tr>
          </tbody>
        </table>
      </div>
    <g:if test="${clients}">
      <div class="grid_5 omega fright">
        <h3>Клиенты</h3>
        <table class="list" width="100%">
          <thead>
            <th width="30">Код</th>
            <th>Имя</th>
            <th width="130">Зарегистрирован</th>
          </thead>
          <tbody>
          <g:each in="${clients}">
            <tr align="center">           
              <td>${it.id}</td>
              <td align="left"><g:link action="clientdetail" id="${it.id}" target="_blank">${it.name}</g:link></td>
              <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${it.inputdate}"/></td>
            </tr>
          </g:each>        
          </tbody>
        </table>
      </div>
    </g:if>
      <div class="clear"></div>  
      <hr class="admin" />
      
      <div class="grid_6 alpha">
        <h3>Ставки по операциям</h3>
        <table class="list">
          <thead>
            <tr style="height:42px">
              <th>Операция</th>
              <th><i class="icon-rub icon-light"></i></th>  
              <th><i class="icon-usd icon-light"></i></th>
              <th><i class="icon-euro icon-light"></i></th>                      
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Перевод</td>
              <td>
                <input type="checkbox" id="is_tran_rub" name="is_tran_rub" value="1" <g:if test="${client?.is_tran_rub || client?.tran_rate_rub}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="tran_rate_rub" name="tran_rate_rub" value="${client?.tran_rate_rub}" />                  
              </td>
              <td>
                <input type="checkbox" id="is_tran_usd" name="is_tran_usd" value="1" <g:if test="${client?.is_tran_usd || client?.tran_rate_usd}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="tran_rate_usd" name="tran_rate_usd" value="${client?.tran_rate_usd}" />                  
              </td>
              <td>
                <input type="checkbox" id="is_tran_eur" name="is_tran_eur" value="1" <g:if test="${client?.is_tran_eur || client?.tran_rate_eur}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="tran_rate_eur" name="tran_rate_eur" value="${client?.tran_rate_eur}" />
              </td>              
            </tr>
            <tr>
              <td>Внесение</td>
              <td>
                <input type="checkbox" id="is_cashin_rub" name="is_cashin_rub" value="1" <g:if test="${client?.is_cashin_rub || client?.cashin_rate_rub}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashin_rate_rub" name="cashin_rate_rub" value="${client?.cashin_rate_rub}" />
              </td>
              <td>
                <input type="checkbox" id="is_cashin_usd" name="is_cashin_usd" value="1" <g:if test="${client?.is_cashin_usd || client?.cashin_rate_usd}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashin_rate_usd" name="cashin_rate_usd" value="${client?.cashin_rate_usd}" />
              </td>
              <td>
                <input type="checkbox" id="is_cashin_eur" name="is_cashin_eur" value="1" <g:if test="${client?.is_cashin_eur || client?.cashin_rate_eur}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashin_rate_eur" name="cashin_rate_eur" value="${client?.cashin_rate_eur}" />
              </td>              
            </tr>
            <tr>
              <td>Выдача</td>
              <td>
                <input type="checkbox" id="is_cashout_rub" name="is_cashout_rub" value="1" <g:if test="${client?.is_cashout_rub || client?.cashout_rate_rub}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashout_rate_rub" name="cashout_rate_rub" value="${client?.cashout_rate_rub}" />
              </td>
              <td>
                <input type="checkbox" id="is_cashout_usd" name="is_cashout_usd" value="1" <g:if test="${client?.is_cashout_usd || client?.cashout_rate_usd}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashout_rate_usd" name="cashout_rate_usd" value="${client?.cashout_rate_usd}" />
              </td>
              <td>
                <input type="checkbox" id="is_cashout_eur" name="is_cashout_eur" value="1" <g:if test="${client?.is_cashout_eur || client?.cashout_rate_eur}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="cashout_rate_eur" name="cashout_rate_eur" value="${client?.cashout_rate_eur}" />
              </td>            
            </tr>             
            <tr>
              <td>Пополнение</td>
              <td>
                <input type="checkbox" id="is_refill_rub" name="is_refill_rub" value="1" <g:if test="${client?.is_refill_rub || client?.refill_rate_rub}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="refill_rate_rub" name="refill_rate_rub" value="${client?.refill_rate_rub}" />
              </td>
              <td>
                <input type="checkbox" id="is_refill_usd" name="is_refill_usd" value="1" <g:if test="${client?.is_refill_usd || client?.refill_rate_usd}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="refill_rate_usd" name="refill_rate_usd" value="${client?.refill_rate_usd}" />
              </td>
              <td>
                <input type="checkbox" id="is_refill_eur" name="is_refill_eur" value="1" <g:if test="${client?.is_refill_eur || client?.refill_rate_eur}">checked</g:if> onclick="resetValue(this.id)" />
                <input type="text" class="nopad mini" id="refill_rate_eur" name="refill_rate_eur" value="${client?.refill_rate_eur}" />                
              </td>            
            </tr>
          </tbody>
        </table>
      </div>

      <div class="grid_5 omega fright">
        <h3>Ставки посреднических услуг</h3>
        <table class="list">
          <thead>
            <tr style="height:42px">
              <th>Операция</th>
              <th><i class="icon-rub icon-light"></i></th>  
              <th><i class="icon-usd icon-light"></i></th>
              <th><i class="icon-euro icon-light"></i></th>                      
            </tr>
          </thead>
          <tbody>
            <tr>
              <td>Перевод</td>
              <td><input type="text" class="nopad mini" id="dealer_tran_rate_rub" name="dealer_tran_rate_rub" value="${client?.dealer_tran_rate_rub}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_tran_rate_usd" name="dealer_tran_rate_usd" value="${client?.dealer_tran_rate_usd}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_tran_rate_eur" name="dealer_tran_rate_eur" value="${client?.dealer_tran_rate_eur}" /></td>              
            </tr>
            <tr>
              <td>Внесение</td>
              <td><input type="text" class="nopad mini" readonly id="dealer_cashin_rate_rub" name="dealer_cashin_rate_rub" value="${client?.dealer_cashin_rate_rub?:0.00}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_cashin_rate_usd" name="dealer_cashin_rate_usd" value="${client?.dealer_cashin_rate_usd}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_cashin_rate_eur" name="dealer_cashin_rate_eur" value="${client?.dealer_cashin_rate_eur}" /></td>              
            </tr>
            <tr>
              <td>Выдача</td>
              <td><input type="text" class="nopad mini" id="dealer_cashout_rate_rub" name="dealer_cashout_rate_rub" value="${client?.dealer_cashout_rate_rub}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_cashout_rate_usd" name="dealer_cashout_rate_usd" value="${client?.dealer_cashout_rate_usd}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_cashout_rate_eur" name="dealer_cashout_rate_eur" value="${client?.dealer_cashout_rate_eur}" /></td>            
            </tr>             
            <tr>
              <td>Пополнение</td>
              <td><input type="text" class="nopad mini" readonly id="dealer_refill_rate_rub" name="dealer_refill_rate_rub" value="${client?.dealer_refill_rate_rub?:0.00}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_refill_rate_usd" name="dealer_refill_rate_usd" value="${client?.dealer_refill_rate_usd}" /></td>
              <td><input type="text" class="nopad mini" id="dealer_refill_rate_eur" name="dealer_refill_rate_eur" value="${client?.dealer_refill_rate_eur}" /></td>            
            </tr>                   
          </tbody>
        </table>
      </div>
      <div class="clear"></div>
      
      <div id="changePassword" <g:if test="${client}">style="display:none"</g:if>>  
        <hr class="admin" />
        <h3>${client?'Смена пароля':'Задать пароль'}</h3>
        <div class="error-box" style="display:none">
          <span class="icon icon-warning-sign icon-3x"></span>
          <ul id="errorpasslist">
            <li></li>
          </ul>
        </div>
        <label for="password">Введете пароль:</label>
        <input type="password" id="password" name="password" />
        <label for="confirm_pass">Подтвердите пароль:</label>
        <input type="password" id="confirm_password" name="confirm_pass" />        
        <div class="fright" ${!client?'style=display:none':''}>
          <input type="button" value="${client?'Изменить':'Задать'} пароль" onclick="setClientPassword(${client?.id?:0})" />
        </div>    
        <div class="clear"></div>      
      </div>
      <hr class="admin" />      
      <div class="fright">
      <g:if test="${client}">
        <a id="setActive" class="button red" onclick="setActive(1)" <g:if test="${client.modstatus}">style="display:none"</g:if>>Активировать &nbsp;<i class="icon-angle-right icon-large"></i></a>
        <a id="resetActive" class="button red" onclick="setActive(0)" <g:if test="${!client.modstatus}">style="display:none"</g:if>>Деактивировать &nbsp;<i class="icon-angle-right icon-large"></i></a>
      </g:if>
        <a class="button" onclick="jQuery('#changePassword').slideToggle()" style="padding-left:0;${!client?'display:none':''}">Изменить пароль &nbsp;<i class="icon-angle-right icon-large"></i></a>
        <input type="reset" class="spacing" value="Отменить" onclick="returnToList()" />       
        <input type="button" class="spacing" id="submit_button_func" value="Сохранить" onclick="checkAccount()"/>
        <input type="submit" id="submit_button" value="Сохранить" style="display:none"/>
      </div>
      <input type="hidden" id="modstatus" name="modstatus" value="${client?.modstatus?:1}"/>      
    </g:formRemote>
    <g:if test="${client}">    
    <div class="clear"></div>
    <h3>История изменений</h3>
    <div id="details"></div>    
    <g:formRemote name="historyForm" url="[action:'clienthistory',id:client?.id?:0]" update="[success:'details']">      
      <input type="submit" id="history_submit_button" style="display:none" />
    </g:formRemote>
    </g:if>  
    <g:form  id="returnToListForm" name="returnToListForm" url="${[action:'clients',params:[fromEdit:1]]}">
    </g:form>
  </body>
</html>
