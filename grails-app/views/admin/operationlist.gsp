<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${operations.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${operations.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${operations.records}">
  <div id="resultList">
    <table class="list small" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">№</th>
          <th>За-<br>прос</th>
          <th>Дата</th>
          <th>Клиент</th>
          <th>Тип</th>
          <th>Валю<br>-та</th>
          <th>Сумма</th>
          <th>Компания</th>
          <th>Сис.компания</th>
          <th>Комментарий</th>
          <th>Ста-<br/>тус</th>
          <th>Дейст-<br>вия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${operations.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td><g:if test="${record.request_id}">
            <g:link controller="admin" action="requestdetail" id="${record.request_id}">${record.request_id}</g:link>
          </g:if></td>          
          <td nowrap>${shortDate(date:record.inputdate)}</td>
          <td align="left"><g:link controller="admin" action="clientdetail" id="${record.client_id}">${Client.get(record.client_id)?.name?:''}</g:link></td>
          <td align="left">${trantypes[record.trantype_id].name}</td>
          <td><i class="icon-${trantypes[record.trantype_id].code.toLowerCase()}"></i></td>
          <td align="right">${number(value:record.summa*record.vrate)}</td>
          <td align="left">${Company.get(record.company_id)?.name?:Company.get(record.company_id)?.beneficial?:''}</td>
          <td align="left">${record.syscompany_name?:Company.get(record.syscompany_id)?.name?:''}</td>
          <td>${record.comment}</td>
          <td><g:if test="${record.modstatus==1}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if></td>
          <td>
            <a class="button" href="${g.createLink(controller:'admin',action:'operationdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${operations.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${operations.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
