<html>
  <head>
    <title>Административное приложение: <g:if test="${company}">Компания</g:if><g:else>Добавление новой компании</g:else></title>
    <meta name="layout" content="administrator" />
    <g:javascript library="prototype/autocomplete" />
    <g:javascript>
      function init(){
        <g:if test="${flash?.success}">
          $("infolist").up('div').show();
        </g:if>        
      }
      function returnToList(){
        $("returnToListForm").submit();
      }
      function processResponse(e){
        var sErrorMsg = '';
        ['name','inn'].forEach(function(ids){
          if($(ids))
            $(ids).removeClassName('red');
        });
        
        if(e.responseJSON.errorcode.length){          
          e.responseJSON.errorcode.forEach(function(err){
            switch (err) {                                               
              case 1: sErrorMsg+='<li>${message(code:"error.blank.message",args:["Название"])}</li>'; $('name').addClassName('red'); break;
              case 2: sErrorMsg+='<li>${message(code:"error.blank.message",args:["ИНН"])}</li>'; $('inn').addClassName('red'); break;
              
              case 100: sErrorMsg+='<li>${message(code:"error.bderror.message")}</li>'; break;
            }
          });
          $("infolist").up('div').hide();
          $("errorlist").innerHTML=sErrorMsg;
          $("errorlist").up('div').show();
        } else if(e.responseJSON.company_id){
          location.assign('${createLink(controller:'admin',action:'companydetail')}'+'/'+e.responseJSON.company_id);
        } else
          location.assign('${createLink(controller:'admin',action:'companies')}');
      }
      
      new Autocomplete('bik', {
        serviceUrl:'${resource(dir:"admin",file:"bik_autocomplete")}',
        onSelect: function(value, data){
          var lsData = data.split(';');
          $('bank').value = lsData[0];
          $('cor_account').value = lsData[1];
        }
      });      
    </g:javascript>
    <style type="text/css">
      label{min-width:160px}
      input.normal{width:202px} 
      .input-append input,.input-append .add-on [class^="icon-"]:before{cursor:default!important}
    </style>
  </head>
  <body onload="init();">
    <h3 class="fleft"><g:if test="${company}">Компания</g:if><g:else>Добавление новой компании</g:else></h3>
    <a class="button back fright" href="javascript:void(0)" onclick="returnToList();"><i class="icon-angle-left icon-large"></i>&nbsp; К списку компаний</a>
    <div class="clear"></div>
    <div class="info-box" style="display:none;margin-top:0">
      <span class="icon icon-info-sign icon-3x"></span>
      <ul id="infolist">      
        <li>Изменения сохранены</li>
      </ul>
    </div>
    <div class="error-box" style="display:none;margin-top:0">
      <span class="icon icon-warning-sign icon-3x"></span>
      <ul id="errorlist">
        <li></li>
      </ul>
    </div>    
    <g:formRemote name="companyDetailForm" url="${[controller:'admin',action:'saveCompanyDetail',id:company?.id?:0]}" method="post" onSuccess="processResponse(e)">
      <label for="name">Название:</label>     
      <input type="text" name="name" id="name" value="${company?.name?:''}" />                     
      <div id="name_autocomplete" class="autocomplete" style="display:none"></div>
      <label for="ogrn">ОГРН:</label>
      <input type="text" id="ogrn" name="ogrn" value="${company?.ogrn?:''}" /><br/>
      <label for="inn">ИНН:</label>
      <input type="text" id="inn" name="inn" value="${company?.inn?:''}" />
      <label for="kpp">КПП:</label>
      <input type="text" id="kpp" name="kpp" value="${company?.kpp?:''}" /><br/>      
      <label for="bank">Банк:</label>
      <input type="text" name="bank" id="bank" value="${company?.bank?:''}" />      
      <label for="bik">БИК:</label>
      <span class="input-append">
        <input type="text" class="nopad normal" name="bik" id="bik" value="${company?.bik?:''}" style="cursor:pointer" />
        <span class="add-on"><i class="icon-search"></i></span>
      </span>
      <div id="bik_autocomplete" class="autocomplete" style="display:none"></div><br/>  
      <label for="cor_account">Корр. счет:</label>
      <input type="text" name="cor_account" id="cor_account" value="${company?.cor_account?:''}" />   
      <label for="account">Расчетный счет:</label>
      <input type="text" name="account" id="account" value="${company?.account?:''}" /><br/>                 
      <label for="beneficial">Бенефициар:</label>
      <input type="text" name="beneficial" id="beneficial" value="${company?.beneficial?:''}" />
      <label for="iban">IBAN код:</label>
      <input type="text" name="iban" id="iban" value="${company?.iban?:''}" /><br/>
      <label for="comment">Комментарий:</label>
      <g:textArea name="comment" value="${company?.comment?:''}" />
      <hr class="admin" />

      <div class="fright" id="btns">
        <input type="reset" class="spacing" value="Сброс"/>
        <input type="submit" value="Сохранить" />
      </div>
    </g:formRemote>
    <g:form  id="returnToListForm" name="returnToListForm" url="${[controller:'admin', action:'companies', params:[fromDetails:1]]}">
    </g:form>
  </body>
</html>
