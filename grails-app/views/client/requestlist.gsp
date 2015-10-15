<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${requests.count}</div>
    <div class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>
<g:if test="${requests.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="30">№</th>
          <th>Дата</th>
          <th>Тип</th>
          <th>Валюта</th>
          <th>Сумма</th>          
          <th>Операция</th>
          <th>Статус</th>
          <th>Комментарий</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${requests.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td>${shortDate(date:record.inputdate)}</td>
          <td align="left">${trantypes[record.trantype_id].name}</td>
          <td><i class="icon-${trantypes[record.trantype_id].code.toLowerCase()}"></i></td>
          <td align="right">${number(value:record?.summa)}</td>          
          <td><g:if test="${record.trans_id}">${record.trans_id}</g:if></td>
          <td><abbr title="${reqmodstatus[record.modstatus]?.name}"><i class="icon-${reqmodstatus[record.modstatus]?.icon}"></i></abbr></td>
          <td align="left"><small>${record.comment?:''}</small></td>
          <td>
            <a class="button" href="${g.createLink(controller:'client',action:'requestdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${requests.count}</span>
    <span class="fright">
      <g:paginate controller="client" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${requests.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
