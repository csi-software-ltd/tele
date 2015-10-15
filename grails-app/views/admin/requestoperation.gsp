<div id="ajax_wrap">
<g:if test="${operation}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>№</th>
          <th>Тип</th>
          <th>Валюта</th>
          <th>Сумма</th>
          <th>Сальдо</th>
          <th>Дата</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${operation}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${trantypes[record.trantype_id].name}</td>
          <td><i class="icon-${trantypes[record.trantype_id].code.toLowerCase()}"></i></td>
          <td>${number(value:record?.summa*record.vrate)}</td>
          <td>${number(value:record?.saldo)}</td>
          <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${record.inputdate}"/></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
</g:if>
</div>