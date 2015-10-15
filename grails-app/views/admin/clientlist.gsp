<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${clients.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${clients.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${clients.records}">
  <div id="resultList">
    <table class="list small" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Код</th>
          <th rowspan="2">Логин</th>
          <th rowspan="2">Имя</th>
          <th rowspan="2">Дата регистрации</th>
          <th rowspan="2">Акти<br>-вен</th>
          <th rowspan="2">Блок</th>
          <th rowspan="2">По-<br>сред<br>-ник</th>
          <th colspan="3">Остаток счета</th>          
          <th rowspan="2">Действия</th>
        </tr>
        <tr>
          <th><i class="icon-rub icon-light"></i></th>
          <th><i class="icon-usd icon-light"></i></th>
          <th class="border"><i class="icon-euro icon-light"></i></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${clients.records}" status="i" var="record">
        <tr align="center">
          <td>${record.id}</td>
          <td align="left">${record.login}</td>
          <td align="left">${record.name}</td>          
          <td><g:formatDate format="dd.MM.yyyy HH:mm" date="${record.inputdate}"/></td>
          <td><g:if test="${record.modstatus==1}"><abbr title="активный"><i class="icon-ok"></i></abbr></g:if></td>
          <td><g:if test="${record.is_block==1}"><abbr title="заблокирован"><i class="icon-ok"></i></abbr></g:if></td>
          <td><g:if test="${record.dealer_cashin_rate_rub||record.dealer_cashin_rate_usd||record.dealer_cashin_rate_eur||record.dealer_cashout_rate_rub||record.dealer_cashout_rate_usd||record.dealer_cashout_rate_eur||record.dealer_refill_rate_rub||record.dealer_refill_rate_usd||record.dealer_refill_rate_eur||record.dealer_tran_rate_rub||record.dealer_tran_rate_usd||record.dealer_tran_rate_eur}"><abbr title="посредник"><i class="icon-ok"></i></abbr></g:if></td>
          <td class="${record.account_rub<0?'red':''}">${number(value:record.account_rub)}</td>
          <td>${number(value:record.account_usd)}</td>
          <td>${number(value:record.account_eur)}</td>
          <td>
            <a class="button" href="${g.createLink(action:'clientdetail',id:record.id)}" title="Редактировать"><i class="icon-pencil"></i></a> &nbsp;&nbsp;&nbsp; 
            <a class="button" href="${g.createLink(action:'loginAsClient',id:record.id)}" target="_blank" title="Войти под именем"><i class="icon-lock"></i></a>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${clients.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${clients.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
