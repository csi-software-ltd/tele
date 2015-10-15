<div id="ajax_wrap">
<g:if test="${history}">
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
      <g:each in="${history}" status="i" var="record">
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
</g:if>
</div>
