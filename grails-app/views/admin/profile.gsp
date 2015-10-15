<html>
  <head>
    <title>Административное приложение: Профиль администратора</title>
    <meta name="layout" content="administrator" />
    <g:javascript>
      function initialize(){	  
      <g:if test="${temp_notification!=null}">
        alert('${temp_notification?.text}');	  
      </g:if>      	  
      }      
      function processProfileResponse(e){
        if(e.responseJSON.error){
          var sErrorMsg = '';
          ['tel','email'].forEach(function(ids){
            $(ids).removeClassName('red');
          });
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {             
              case 1: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Телефон"])}</li>'; $("tel").addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.incorrect.message",args:["Email"])}</li>'; $("email").addClassName('red'); break;
            }
          });
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else {
          location.reload(true);
        }
      }
      function processPasswordResponse(e){
        if(e.responseJSON.error){
          var sErrorMsg = '';
          ['pass','confirm_pass'].forEach(function(ids){
            $(ids).removeClassName('red');
          });
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {             
              case 2: sErrorMsg+='<li>Пароли не совпадают</li>'; $('confirm_pass').addClassName('red'); break;
              case 3: sErrorMsg+='<li>Слишком короткий пароль</li>'; $('pass').addClassName('red'); break;
              case 4: sErrorMsg+='<li>Некорректный пароль. Пароль не менее ${passwordlength} знаков из больших и маленьких латинских букв и цифр.</li>'; $('pass').addClassName('red'); break;
            }
          });
          $("errorpasslist").innerHTML=sErrorMsg;
          $("errorpasslist").up('div').show();
          $("msglist").hide();
        } else {
          $("errorpasslist").up('div').hide();
          $("msglist").show();
          jQuery('#changePassword').slideUp();
        }
      }
    </g:javascript>
  </head>  
  <body onload="initialize()">    
    <div class="grid_6">      
      <h3>Курсы валют</h3>
      <label class="auto">Сегодня:</label>
      <input type="text" readonly value="${formatDate(date:new Date(),format:'dd.MM.yyyy HH:mm')}" style="width:auto" />
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
    </div><g:if test="${rates_next}">   
    <div class="grid_6">      
      <h3>&nbsp;</h3>
      <label class="auto">Завтра:</label>
      <input type="text" readonly value="${formatDate(date:new Date()+1,format:'dd.MM.yyyy HH:mm')}" style="width:auto" />
      <table class="list">
        <tbody> 
          <g:each in="${rates_next}" var="item" status="i">        
          <tr>
            <th width="30" <g:if test="${i==0}">class="round"</g:if>><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td <g:if test="${i==0}">class="round"</g:if>><input type="text" class="nopad" readonly value="${number(value:item.vrate,fdigs:4)}" /></td>
          </tr>
          </g:each>
        </tbody>
      </table>      
    </div></g:if>
    <div class="clear"></div>
    <hr class="admin" />
    
    <div class="grid_6">
      <h3>Остатки по клиентским счетам</h3>
      <table class="list">
        <tbody>        
          <tr>           
            <th width="30" class="round"><i class="icon-rub icon-light"></i></th>  
            <td class="round"><input type="text" class="nopad" readonly value="${number(value:saldo[0])}" /></td>
          </tr>
        <g:each in="${Valuta.list()}" var="item" status="i">
          <tr>
            <th><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td><input type="text" class="nopad" readonly value="${number(value:saldo[i+1])}" /></td>
          </tr>        
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="grid_6">
      <h3>Доходы по операциям</h3>
      <table class="list">
        <tbody>        
          <tr>           
            <th width="30" class="round"><i class="icon-rub icon-light"></i></th>  
            <td class="round"><input type="text" class="nopad" readonly value="${number(value:income['account_rub'])}" /></td>
          </tr>
        <g:each in="${Valuta.list()}" var="item">
          <tr>
            <th><i class="icon-${item.code.toLowerCase()} icon-light"></i></th>
            <td><input type="text" class="nopad" readonly value="${number(value:income['account_'+item.code.toLowerCase()])}" /></td>
          </tr>        
        </g:each>
        </tbody>
      </table>
    </div>
    <div class="clear"></div>
    <hr class="admin" />    
    
    <g:formRemote class="grid_12" name="profile" url="[controller:'admin',action:'profilesave']" method="post" onSuccess="processProfileResponse(e)">
      <h3>Изменение профиля</h3>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorlist">
          <li></li>
        </ul>
      </div>
      <label for="email" style="width:124px">Email:</label>        
      <input type="text" id="email" name="email" value="${administrator?.email}" />
      <label for="email">Телефон:</label>
      <input type="text" id="tel" name="tel" value="${administrator?.tel}" />      
      <div class="fright">        
        <a class="button" onclick="jQuery('#changePassword').slideToggle()">Изменить пароль &nbsp;<i class="icon-angle-right icon-large"></i></a>
        <input type="submit" value="Изменить профиль" />
      </div>
    </g:formRemote>
    <div class="clear"></div>    
    <g:formRemote name="changePassword" url="[controller:'admin',action:'changepass']" method="post" onSuccess="processPasswordResponse(e)" style="display:none">
      <hr class="admin" />
      <h3>Изменение пароля</h3>
      <div class="error-box" style="display:none">
        <span class="icon icon-warning-sign icon-3x"></span>
        <ul id="errorpasslist">
          <li></li>
        </ul>
      </div>
      <div class="info-box" id="msglist" style="display:none"> 
        <span class="icon icon-info-sign icon-3x"></span>
        Пароль изменен
      </div>
      <label for="pass">Новый пароль:</label>
      <input type="password" id="pass" name="pass" />
      <label for="confirm_pass">Повторить:</label>
      <input type="password" id="confirm_pass" name="confirm_pass" />
      <div class="fright">
        <input type="submit" value="Изменить пароль" />          
      </div>
    </g:formRemote>  
    <div class="clear"></div>      
    <hr class="admin" />
    
    <div class="grid_12">
      <p><i>Последний вход пользователя: <b>${(lastlog?.logtime!=null)?String.format('%td.%<tm.%<tY %<tH:%<tM',lastlog?.logtime):''}</b> с IP адреса <b>${lastlog?.ip}</b>
    <g:if test="${(unsuccess_log_amount)&&(unsuccess_log_amount > unsucess_limit)}">
      <br/><font color="red">Неуспешных попыток доступа за последние 7 дней: <b>${unsuccess_log_amount}</b></font>
    </g:if></i></p>
    </div>
  </body>
</html>
