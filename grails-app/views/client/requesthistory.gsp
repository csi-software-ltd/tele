<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${history.count}</div>
    <div class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${history.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${history.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>№</th>
          <th>Дата изменения</th>
          <th>Статус</th>
          <th>Сумма</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${history.records}" status="i" var="record">
        <tr align="center">          
          <td>${i+1}</td>
          <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${record.inputdate}"/></td>          
          <td <g:if test="${req.modstatus!=record?.modstatus}">class="bold"</g:if>>${Reqmodstatus.get(record.modstatus)?.name}</td>
          <td <g:if test="${req.summa!=record?.summa}">class="bold"</g:if>>${number(value:record?.summa)}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${history.count}</span>
    <span class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${history.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
