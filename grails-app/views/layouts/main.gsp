<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ru" lang="ru">
  <head>
    <title><g:layoutTitle default="TeleBank" /></title>
    <meta http-equiv="content-language" content="ru" />
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />      
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />               
    <meta name="copyright" content="TeleBank" />    
    <meta name="resource-type" content="document" />
    <meta name="document-state" content="dynamic" />
    <meta name="revisit" content="1" />
    <meta name="viewport" content="width=1000,maximum-scale=1.0" />     
    <meta name="robots" content="noindex,nofollow" />
    <meta name="cmsmagazine" content="55af4ed6d7e3fafc627c933de458fa04" />
    <link rel="shortcut icon" href="${resource(file:'favicon.ico',absolute:true)}" type="image/x-icon" />    
    <link rel="stylesheet" href="${resource(dir:'css',file:'reset.css')}" type="text/css" />    
    <link rel="stylesheet" href="${resource(dir:'css',file:'grid.css')}" type="text/css" />  
    <link rel="stylesheet" href="${resource(dir:'css',file:'superfish.css')}" type="text/css" />      
    <link rel="stylesheet" href="${resource(dir:'css',file:'style.css')}" type="text/css" />
    <link rel="stylesheet" href="${resource(dir:'css',file:'font-awesome.min.css')}" type="text/css" />    
    <g:layoutHead />
    <g:javascript library="jquery-1.10.1.min" />    
    <g:javascript library="application" />
    <g:javascript library="prototype/prototype" />
    <!--[if lt IE 7]>
  		<div class='aligncenter'><a href="http://www.microsoft.com/windows/internet-explorer/default.aspx?ocid=ie6_countdown_bannercode"><img src="http://storage.ie6countdown.com/assets/100/images/banners/warning_bar_0000_us.jpg" border="0"></a></div>  
    <![endif]-->
    <!--[if lt IE 9]>    
      <g:javascript library="html5" />
      <link rel="stylesheet" href="${resource(dir:'css',file:'ie.css')}" type="text/css" />   		  		
    <![endif]-->    
    <r:layoutResources/>
  </head>
  <body onload="${pageProperty(name:'body.onload')}">    
    <header>
      <div class="main">
        <a class="logo" title="Telebank — главная страница" href="${createLink(action:'profile')}">TELEBANK</a>
        <h1 align="center" class="fleft">Клиентское приложение</h1>      
        <div class="user inline fright">
        <g:if test="${client}">
          <span class="icon-lock icon-large icon-light"></span> <span class="user-login" id="user">${client?.name?:''}</span>
          <a class="icon-bell-alt icon-large icon-light" title="Уведомления" href="${client.notice_count==1?createLink(controller:'client',action:'requestdetail',id:client.notice_id):createLink(controller:'client',action:'requests')}">
          <g:if test="${client.notice_count}">
            <div class="new">${client.notice_count}</div>
          </g:if>
          </a>
          <a class="icon-envelope icon-large icon-light" title="Сообщения" href="${client.message_count==1?createLink(controller:'client',action:'messagedetail',id:client.message_id):createLink(controller:'client',action:'messages')}">
          <g:if test="${client.message_count}">
            <div class="new">${client.message_count}</div>
          </g:if>
          </a>                  
          <a class="icon-signout icon-1x icon-light" title="Выход" href="${g.createLink(controller:'client',action:'logout')}"> </a>
        </g:if><g:else>&nbsp;</g:else>
        </div>
        <div class="clear"></div>
      </div>
    </header>
  <g:if test="${client}">
    <nav>              
      <div class="main">
        <ul class="sf-menu">
        <g:each in="${clientmenu}" var="item">
          <li class="${(controllerName==item.controller && (actionName==item.action || actionName==item.relatedpages))?'current':''}">
            <g:link controller="${item.controller}" action="${item.action}">${item?.name}</g:link>
          </li>          
        </g:each>
        </ul>      
      </div>
    </nav>    
  </g:if>
    <section id="content">
      <div class="container_12">             
      <g:if test="${session.attention_message!='' && session.attention_message!=null && actionName!='index'}"> 
        <div class="info-box">
          <span class="icon icon-info-sign icon-3x"></span>
          ${session.attention_message}
        </div>    
      </g:if>
      <g:if test="${controllerName=='client' && !(actionName in ['index','profile'])}">
        <g:each in="${clientmenu}" var="item"><g:if test="${controllerName==item.controller && actionName==item.action}">
        <h1 class="padtop ${(actionName in ['messages','requests','operations','reports'])?'fleft':''}">${item.name}</h1>
        </g:if></g:each>     
      </g:if>
        <g:layoutBody />            
        <div class="clear"></div>
      </div>
    </section>    
    <r:layoutResources/>
  <g:if test="${client}">
    <script type="text/javascript">
      jQuery(window).scroll(function(){
        if(jQuery(this).scrollTop()>0)
          jQuery("nav").css({position:'fixed',top:0});
        else
          jQuery("nav").css('position','relative');        
      });  
    </script>    
  </g:if>
  </body>
</html>
