<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>                   
          <th>Дата</th>
          <th>Клиент</th>
          <th>IP</th>
          <th>Успешность</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${records}" status="i" var="record">
        <tr align="center" <g:if test="${record.success==0}">style="color:red"</g:if>>         
          <td nowrap>${shortDate(date:record.logtime)}</td>
          <td nowrap>${Client.get(record.client_id)?.name?:''}</td>
          <td nowrap>${record.ip}</td>
          <td><abbr title="${record.success==1?'успешный':'не успешный'}"><i class="icon-${record.success==1?'ok':'remove'}"></i></abbr></td>         
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
