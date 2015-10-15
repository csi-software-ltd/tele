<html>
  <head>    
    <title>${infotext?.title?:''}</title>        
    <meta name="layout" content="main" />
    <g:javascript>
      function initialize(){	  
        <g:if test="${temp_notification!=null}">
          alert('${temp_notification?.text}');	  
        </g:if>      	  
        $('form_submit_button').click();
      }      
    </g:javascript>
  </head>  
  <body onload="initialize()">
    <div class="grid_6 alpha">
      <h3>Остатки по счетам</h3>
      <table class="list">
        <tbody>
          <tr>           
            <th width="30" class="round"><i class="icon-rub icon-light"></i></th>  
            <td class="round"><input type="text" class="nopad ${user?.account_rub<0?'red':''}" readonly value="${number(value:user?.account_rub)}" /></td>
          </tr>
        <g:each in="${Valuta.list()}">
          <tr>
            <th><i class="icon-${it.code.toLowerCase()} icon-light"></i></th>
            <td><input type="text" class="nopad" readonly value="${number(value:user['account_'+it.code.toLowerCase()])}" /></td>
          </tr>
        </g:each>
        </tbody>
      </table>
    </div>  
    <div class="grid_6 omega fright">
      <h3>Курсы валют</h3>
      <label class="auto">Сегодня:</label>
      <input type="text" readonly value="${g.formatDate(date:new Date(),format:'dd.MM.yyyy HH:mm')}" style="width:auto" />
      <table class="list">
        <tbody> 
        <g:each in="${rates_current}" var="item" status="i">        
          <tr>
            <th width="30" <g:if test="${i==0}">class="round"</g:if>><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td <g:if test="${i==0}">class="round"</g:if>><input type="text" class="nopad" readonly value="${number(value:item.vrate,fdigs:4)}" /></td>
          </tr>
        </g:each>
        </tbody>
      </table>      
    </div>    
    
    <div class="clear"></div>
    <hr class="admin" />
    
    <div class="grid_6 alpha">
      <h3>Ставки по операциям</h3>
      <table class="list">
        <thead>
          <tr style="height:42px">
            <th>Операция</th>
            <th width="96"><i class="icon-rub icon-light"></i></th>  
            <th width="96"><i class="icon-usd icon-light"></i></th>
            <th width="96"><i class="icon-euro icon-light"></i></th>              
          </tr>
        </thead>
        <tbody>
        <g:if test="${user?.is_tran_rub || user?.is_tran_usd || user?.is_tran_eur}">
          <tr align="center">
            <td align="left">Перевод</td>
            <td><g:if test="${user?.is_tran_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.tran_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_tran_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.tran_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_tran_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.tran_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
          </tr>
        </g:if><g:if test="${user?.is_cashin_rub || user?.is_cashin_usd || user?.is_cashin_eur}">
          <tr align="center">
            <td align="left">Внесение</td>
            <td><g:if test="${user?.is_cashin_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashin_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_cashin_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashin_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_cashin_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashin_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
          </tr>
        </g:if><g:if test="${user?.is_cashout_rub || user?.is_cashout_usd || user?.is_cashout_eur}">
          <tr align="center">
            <td align="left">Выдача</td>
            <td><g:if test="${user?.is_cashout_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashout_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_cashout_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashout_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_cashout_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.cashout_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>            
          </tr>
        </g:if><g:if test="${user?.is_refill_rub || user?.is_refill_usd || user?.is_refill_eur}">
          <tr align="center">
            <td align="left">Пополнение</td>
            <td><g:if test="${user?.is_refill_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.refill_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_refill_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.refill_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.is_refill_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.refill_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>            
          </tr>
        </g:if>
        </tbody>
      </table>
    </div>
    
    <div class="grid_6 omega fright">
      <h3>Ставки посреднических услуг</h3>
      <table class="list">
        <thead>
          <tr style="height:42px">
            <th>Операция</th>
            <th width="96"><i class="icon-rub icon-light"></i></th>  
            <th width="96"><i class="icon-usd icon-light"></i></th>
            <th width="96"><i class="icon-euro icon-light"></i></th>              
          </tr>
        </thead>
        <tbody>
        <g:if test="${user?.dealer_tran_rate_rub || user?.dealer_tran_rate_usd || user?.dealer_tran_rate_eur}">
          <tr align="center" height="55">
            <td align="left">Перевод</td>
            <td><g:if test="${user?.dealer_tran_rate_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_tran_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_tran_rate_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_tran_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_tran_rate_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_tran_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
          </tr>
        </g:if>
          <tr align="center" height="55">
            <td align="left">Внесение</td>
            <td><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashin_rate_rub)}" /></td>
            <td><g:if test="${user?.dealer_cashin_rate_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashin_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_cashin_rate_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashin_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
          </tr>
        <g:if test="${user?.dealer_cashout_rate_rub || user?.dealer_cashout_rate_usd || user?.dealer_cashout_rate_eur}">
          <tr align="center" height="55">
            <td align="left">Выдача</td>
            <td><g:if test="${user?.dealer_cashout_rate_rub}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashout_rate_rub)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_cashout_rate_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashout_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_cashout_rate_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_cashout_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>            
          </tr>
        </g:if>
          <tr align="center" height="55">
            <td align="left">Пополнение</td>
            <td><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_refill_rate_rub)}" /></td>
            <td><g:if test="${user?.dealer_refill_rate_usd}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_refill_rate_usd)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>
            <td><g:if test="${user?.dealer_refill_rate_eur}"><input type="text" class="nopad mini" readonly value="${number(value:user?.dealer_refill_rate_eur)}" /></g:if><g:else><i class="icon-minus icon-muted"></i></g:else></td>            
          </tr>
        </tbody>
      </table>
    </div>
    <div class="clear"></div>
    <hr class="admin" />    
    
    <div id="list" class="grid_6 alpha"></div>
    
  <g:if test="${clients}">
    <div class="grid_6 omega fright">
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
            <td align="left">${it.name}</td>
            <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${it.inputdate}"/></td>
          </tr>
        </g:each>        
        </tbody>
      </table>
    </div>
  </g:if>
   
    <div class="clear"></div>
    <hr class="admin" />    
   
    <g:form url="[controller:'client',action:'changename']" method="post">
      <h3>Смена имени</h3>
      <div class="error-box" style="${!flash?.error_user?'display:none':''}">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <g:if test="${flash?.error_user==1}"><li>Не было заполнено обязательное поле "Имя"</li></g:if>                                           
        </ul>
      </div>            
      <label for="name" style="min-width:124px">Имя:</label>
      <input type="text" id="name" name="name" value="${user?.name?:''}" />
      <label for="login">Логин:</label>
      <input type="text" id="login" disabled="true" value="${user?.login?:''}" />
      <div class="fright">
        <input type="submit" value="Изменить" />          
      </div>
      <div class="clear"></div>
      <hr class="admin" />
    </g:form>

    <g:form url="[controller:'client',action:'changepass']" method="post">
      <h3>Смена пароля</h3>
      <div class="error-box" style="${!flash?.error?'display:none':''}">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <g:if test="${flash?.error==1}"><li>Слишком короткий пароль. Пароль не менее ${passwordlength} знаков</li></g:if>
          <g:if test="${flash?.error==2}"><li>Некорректный пароль. Пароль не менее ${passwordlength} знаков из больших и маленьких латинских букв и цифр.</li></g:if>
          <g:if test="${flash?.error==3}"><li>Пароли не совпадают</li></g:if>                                   
        </ul>
      </div>      
      <label for="pass">Новый пароль:</label>
      <input type="password" name="pass" <g:if test="${flash?.error in [1,2,3]}">class="red"</g:if> />
      <label for="confirm_pass">Повторить:</label>
      <input type="password" name="confirm_pass" <g:if test="${flash?.error==2}">class="red"</g:if> />
      <div class="fright">
        <input type="submit" value="Изменить" />          
      </div>
      <div class="clear"></div>      
    </g:form> 
    <g:formRemote name="allForm" url="[controller:'client',action:'requestlist']" update="[success:'list']">
      <input type="hidden" name="from_profile" value="1" />
      <input type="submit" id="form_submit_button" style="display:none" />    
    </g:formRemote>    
  </body>
</html>
