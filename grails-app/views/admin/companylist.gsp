<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${companies.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${companies.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${companies.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th>Название</th>
          <th width="160">ИНН</th>         
          <th>Банк</th>
          <th width="70">Статус</th>
          <th width="90">Действия</th>                    
        </tr>
      </thead>
      <tbody>
      <g:each in="${companies.records}" status="i" var="record">
        <tr align="center">                   
          <td align="left">${record.name}</td>
          <td>${record.inn}</td>         
          <td align="left">${record.bank}</td>
          <td>
            <abbr title="${record.modstatus?'активная':'архивная'}">
              <i class="icon-${record.modstatus?'ok':'trash'}"></i>
            </abbr>          
          </td>                             
          <td>            
            <a class="button" href="${g.createLink(controller:'admin',action:'companydetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;&nbsp;&nbsp; 
            <a class="button" href="#" onclick="setCompanyArchive(${record.id},${record.modstatus?0:1})" title="${record.modstatus?'Архивировать':'Восстановить'}"><i class="icon-${record.modstatus?'trash':'ok'}"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${companies.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${companies.count}" offset="${inrequest.offset}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
