<g:if test="${requests.records}">
  <h3>Последние запросы</h3>
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>          
          <th>Дата</th>
          <th>Тип</th>
          <th>Валюта</th>
          <th>Сумма</th>                    
          <th>Статус</th>          
        </tr>
      </thead>
      <tbody>
      <g:each in="${requests.records}" status="i" var="record">
        <tr align="center">          
          <td>${shortDate(date:record.moddate)}</td>
          <td align="left">${trantypes[record.trantype_id].name}</td>
          <td><i class="icon-${trantypes[record.trantype_id].code.toLowerCase()}"></i></td>
          <td align="right">${number(value:record?.summa)}</td>                    
          <td><abbr title="${reqmodstatus[record.modstatus]?.name}"><i class="icon-${reqmodstatus[record.modstatus]?.icon}"></i></abbr></td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div> 
</g:if>
