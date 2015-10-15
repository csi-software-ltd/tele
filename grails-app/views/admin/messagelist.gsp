<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${messages.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${messages.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${messages.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr style="line-height:15px">          
          <th width="20">&nbsp;</th>
          <th>№</th>
          <th width="130">Кому</th>          
          <th width="80">Дата изменения</th>
          <th width="390">Тема переписки</th>
          <th>Файл</th>
          <th>Писем</th>
          <th>Статус</th>
          <th>Действия</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${messages.records}" var="record" status="i">
        <tr align="center" <g:if test="${!record.is_read}">class="noread"</g:if>>
          <td onclick="starToggle(${record.id})"><i id="star${record.id}" class="icon-star${!record.is_favourite?'-empty':''} icon-gold icon-large"></i></td>
          <td>${record.id}</td>
          <td>${Client.get(record.client_id?:0)?.name?:0}</td>          
          <td>${shortDate(date:record.moddate)}</td>
          <td align="left" onclick="$('edit${i}').click()"><div style="height:20px;overflow:hidden">${record.subject} <font color="#777">- ${record.lasttext}</font></div></td>
          <td><g:if test="${record.is_attach}"><i class="icon-paper-clip"></i></g:if></td>
          <td>${record.nrec?:0}</td>          
          <td><i class="icon-${record.modstatus==1?'envelope'+(record.is_read?'-alt':''):'trash'}"></i></td>
          <td>
            <a id="edit${i}" class="button" href="${g.createLink(action:'messagedetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;&nbsp;&nbsp;
            <g:if test="${record.modstatus}"><a class="button" onclick="setMessageArchive(${record?.id?:0})" title="Архивировать"><i class="icon-trash"></i></a></g:if>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${messages.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${messages.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
  <script type="text/javascript">
    <g:if test="${messages.records.size()>1}">
      $("offset").value="${inrequest?.offset?:0}";
    </g:if>
    <g:else>
      $("offset").value="${inrequest?.offset?(inrequest?.offset-20):0}";
    </g:else>
  </script>
</g:if>
</div>
