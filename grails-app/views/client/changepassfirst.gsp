<html>
  <head>    
    <title>Клиентское приложение: Смена пароля</title>        
    <meta name="layout" content="main" />
    <g:javascript>            
    </g:javascript>
  </head>  
  <body>    
    <h3>Смена пароля</h3>
    <div class="error-box" style="${!flash?.error?'display:none':''}">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">         
        <g:if test="${flash?.error==1}"><li>Слишком короткий пароль. Пароль не менее ${passwordlength} знаков</li></g:if>
        <g:if test="${flash?.error==2}"><li>Некорректный пароль. Пароль не менее ${passwordlength} знаков из больших и маленьких латинских букв и цифр</li></g:if>                
        <g:if test="${flash?.error==3}"><li>Пароли не совпадают</li></g:if>
        <g:if test="${flash?.error==4}"><li>Пароль должен быть новым</li></g:if>
      </ul>
    </div>      
    
    <g:form url="[controller:'client',action:'changepass']" method="post">
      <label for="pass">Новый пароль:</label>
      <input autofocus type="password" name="pass" <g:if test="${flash?.error in [1,2,3]}">class="red"</g:if> />
      <label for="confirm_pass">Повторить:</label>
      <input type="password" name="confirm_pass" <g:if test="${flash?.error==2}">class="red"</g:if> />
      <div class="fright">
        <input type="submit" value="Изменить пароль" />          
      </div>
      <div class="clear"></div>      
    </g:form>  
  </body>
</html>
