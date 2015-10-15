<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${operations.count}</div>
    <div class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${operations.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${operations.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">№</th>
          <th>За-<br>прос</th>
          <th>Дата</th>
          <th>Тип</th>
          <th>Валюта</th>
          <th>Кредит</th>
          <th>Дебет</th>
          <th>Сальдо</th>
          <th>Компания</th>
          <th>Комментарий</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${operations.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td><g:if test="${record.request_id}">
            <g:if test="${record.trantype_id in [16,17,18]}">${record.request_id}</g:if>
            <g:else><g:link controller="client" action="requestdetail" id="${record.request_id}">${record.request_id}</g:link></g:else>
          </g:if></td>          
          <td nowrap>${shortDate(date:record.inputdate)}</td>
          <td align="left">${trantypes[record.trantype_id].name}</td>
          <td><i class="icon-${trantypes[record.trantype_id].code.toLowerCase()}"></i></td>
          <td align="right">${number(value:record.credit*record.vrate)}</td>
          <td align="right">${number(value:Math.abs(record.debet*record.vrate))}</td>
          <td align="right">${number(value:record.saldo)}</td>
          <td>${record.name?:record.beneficial?:''}</td>
          <td>${record.comment}</td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${operations.count}</span>
    <span class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${operations.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
