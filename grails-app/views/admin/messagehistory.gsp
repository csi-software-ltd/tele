<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${history?.count?:0}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${history?.count?:0}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${history?.records}">
  <div id="resultList">
    <table class="list" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th width="80">Дата изменения</th>
          <th width="130">Автор</th>
          <th>Текст сообщения</th>                   
          <th width="40">Файл</th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history.records}" var="record" status="i">
        <tr align="center">          
          <td>${shortDate(date:record.inputdate)}</td>
          <td><g:if test="${record.is_fromclient}">${Client.get(Mbox.get(record.mbox_id)?.client_id?:0)?.name?:''}</g:if><g:else>admin</g:else></td>
          <td align="left"><g:shortString text="${record.mtext}" length="70" /> <span class="fright spacing"><i class="icon-chevron-left" onclick="jQuery('#text'+${i}).slideToggle();this.className=((this.className=='icon-chevron-right')?'icon-chevron-left':'icon-chevron-right');" title="Подробнее"></i></span></td>
          <td><g:if test="${record.filename}"><a href="${createLink(controller:'admin', action:'picture',params:[id:record.mbox_id,filename:record.filename])}" target="_blank"><i class="icon-paper-clip icon-large" title="${record.filename}"></i></a></g:if></td>
        </tr>        
        <tr id="text${i}">
          <td colspan="4" style="padding:8px 15px!important">
            <g:if test="${record.filename&&record.filename[-3..-1]=='jpg'}"><figure style="width:auto;float:left;padding-right:10px"><img src="${createLink(controller:'admin', action:'picture',params:[id:record.mbox_id,filename:'t_'+record.filename])}" alt="" /></figure></g:if>
            <font color="#777">${record.mtext}</font>
          </td>
        </tr>        
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${history?.count?:0}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${params}" 
        prev="&lt;" next="&gt;" max="20" total="${history?.count?:0}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
