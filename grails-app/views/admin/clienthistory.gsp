<div id="ajax_wrap">
  <div style="padding:10px">
    <div class="fleft">Найдено: ${history.count}</div>
    <div class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${history.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </div>
    <div class="clear"></div>
  </div>  
<g:if test="${history.records}">
  <div id="resultList">
    <table class="list small" width="100%" cellpadding="0" cellspacing="0" border="0">
      <thead>
        <tr>
          <th rowspan="2">Дата<br/>измене-<br/>ния</th>
          <th rowspan="2">Акти-<br/>вен</th>
          <th rowspan="2">Бло-<br/>киро-<br/>ван</th>
          <th rowspan="2">По-<br/>сред-<br/>ник</th>          
          <th rowspan="2">Реко-<br>менда-<br>тель</th>
          <th rowspan="2">Свифт</th>          
          <th colspan="3">Перевод</th>
          <th colspan="3">Внесение</th>
          <th colspan="3">Выдача</th>
          <th colspan="3">Пополнение</th>          
        </tr>
        <tr>          
          <th width="45"><i class="icon-rub icon-light"></i></th>
          <th width="45"><i class="icon-usd icon-light"></i></th>
          <th width="45"><i class="icon-euro icon-light"></i></th>        
          <th width="45"><i class="icon-rub icon-light"></i></th>
          <th width="45"><i class="icon-usd icon-light"></i></th>
          <th width="45"><i class="icon-euro icon-light"></i></th>
          <th width="45"><i class="icon-rub icon-light"></i></th>
          <th width="45"><i class="icon-usd icon-light"></i></th>
          <th width="45"><i class="icon-euro icon-light"></i></th>
          <th width="45"><i class="icon-rub icon-light"></i></th>
          <th width="45"><i class="icon-usd icon-light"></i></th>
          <th width="45"><i class="icon-euro icon-light"></i></th>
        </tr>
      </thead>
      <tbody>
      <g:each in="${history.records}" status="i" var="record">
        <tr align="center">          
          <td>${shortDate(date:record.moddate)}</td>          
          <td <g:if test="${client.modstatus!=record?.modstatus}">class="bold"</g:if>><abbr title="${record.modstatus==1?'Активен':'Неактивен'}"><i class="icon-${record.modstatus==1?'ok':'minus'}"></i></abbr></td>
          <td <g:if test="${client.is_block!=record?.is_block}">class="bold"</g:if>><abbr title="${record.is_block==1?'Заблокирован':'Активирован'}"><i class="icon-${record.is_block==1?'ok':'minus'}"></i></abbr></td> 
          <td <g:if test="${((client.dealer_cashin_rate_rub+client.dealer_cashin_rate_usd+client.dealer_cashin_rate_eur)>0 &&(record?.dealer_cashin_rub+record?.dealer_cashin_usd+record?.dealer_cashin_eur)==0)||((client.dealer_cashin_rate_rub+client.dealer_cashin_rate_usd+client.dealer_cashin_rate_eur)==0 &&(record.dealer_cashin_rub+record.dealer_cashin_usd+record.dealer_cashin_eur)>0)
          ||((client.dealer_cashout_rate_rub+client.dealer_cashout_rate_usd+client.dealer_cashout_rate_eur)>0 &&(record?.dealer_cashout_rub+record?.dealer_cashout_usd+record?.dealer_cashout_eur)==0)||((client.dealer_cashout_rate_rub+client.dealer_cashout_rate_usd+client.dealer_cashout_rate_eur)==0 &&(record.dealer_cashout_rub+record.dealer_cashout_usd+record.dealer_cashout_eur)>0)
          ||((client.dealer_refill_rate_rub+client.dealer_refill_rate_usd+client.dealer_refill_rate_eur)>0 &&(record?.dealer_refill_rub+record?.dealer_refill_usd+record?.dealer_refill_eur)==0)||((client.dealer_refill_rate_rub+client.dealer_refill_rate_usd+client.dealer_refill_rate_eur)==0 &&(record.dealer_refill_rub+record.dealer_refill_usd+record.dealer_refill_eur)>0)
          ||((client.dealer_tran_rate_rub+client.dealer_tran_rate_usd+client.dealer_tran_rate_eur)>0 &&(record?.dealer_tran_rub+record?.dealer_tran_usd+record?.dealer_tran_eur)==0)||((client.dealer_tran_rate_rub+client.dealer_tran_rate_usd+client.dealer_tran_rate_eur)==0 &&(record.dealer_tran_rub+record.dealer_tran_usd+record.dealer_tran_eur)>0) }">class="bold"</g:if>> 
            <abbr title="${(record?.dealer_cashin_rub+record?.dealer_cashin_usd+record?.dealer_cashin_eur+
            record?.dealer_cashout_rub+record?.dealer_cashout_usd+record?.dealer_cashout_eur+
            record?.dealer_refill_rub+record?.dealer_refill_usd+record?.dealer_refill_eur+
            record?.dealer_tran_rub+record?.dealer_tran_usd+record?.dealer_tran_eur
            )>0?'Посредник':'Не посредник'}">
            <i class="icon-${(record?.dealer_cashin_rub+record?.dealer_cashin_usd+record?.dealer_cashin_eur+
            record?.dealer_cashout_rub+record?.dealer_cashout_usd+record?.dealer_cashout_eur+
            record?.dealer_refill_rub+record?.dealer_refill_usd+record?.dealer_refill_eur+
            record?.dealer_tran_rub+record?.dealer_tran_usd+record?.dealer_tran_eur
            )>0?'ok':'minus'}"></i></abbr>
          </td>          
          <td <g:if test="${client.parent!=record?.parent || client.parent2!=record?.parent2}">class="bold"</g:if>>
            <g:if test="${record.parent}"><g:link action="clientdetail" id="${record.parent}">${Client.get(record.parent?:0)?.name?:0}</g:link></g:if><g:else><abbr title="Нет рекомендателя"><i class="icon-minus"></i></abbr></g:else> /
            <g:if test="${record.parent2}"><g:link action="clientdetail" id="${record.parent2}">${Client.get(record.parent2?:0)?.name?:0}</g:link></g:if><g:else><abbr title="Нет рекомендателя №2"><i class="icon-minus"></i></abbr></g:else>
          </td>
          <td <g:if test="${client.swiftonclient!=record?.swiftonclient}">class="bold"</g:if>>${record.swiftonclient==1?'Клиент':'Система'}</td> 
          <!--<td <g:if test="${client.account_rub!=record?.saldo_rub}">class="bold"</g:if>>${number(value:record?.saldo_rub)}</td>
          <td <g:if test="${client.account_usd!=record?.saldo_usd}">class="bold"</g:if>>${number(value:record?.saldo_usd)}</td>
          <td <g:if test="${client.account_eur!=record?.saldo_eur}">class="bold"</g:if>>${number(value:record?.saldo_eur)}</td>-->          
          <td>
            <span <g:if test="${client.tran_rate_rub!=record?.tran_rate_rub}">class="bold"</g:if>>${number(value:record?.tran_rate_rub)}</span><br/><hr/>
            <span <g:if test="${client.dealer_tran_rate_rub!=record?.dealer_tran_rub}">class="bold"</g:if>>(${number(value:record?.dealer_tran_rub)})</span>
          </td>
          <td>
            <span <g:if test="${client.tran_rate_usd!=record?.tran_rate_usd}">class="bold"</g:if>>${number(value:record?.tran_rate_usd)}</span><br/><hr/>
            <span <g:if test="${client.dealer_tran_rate_usd!=record?.dealer_tran_usd}">class="bold"</g:if>>(${number(value:record?.dealer_tran_usd)})</span>
          </td>
          <td>
            <span <g:if test="${client.tran_rate_eur!=record?.tran_rate_eur}">class="bold"</g:if>>${number(value:record?.tran_rate_eur)}</span><br/><hr/>
            <span <g:if test="${client.dealer_tran_rate_eur!=record?.dealer_tran_eur}">class="bold"</g:if>>(${number(value:record?.dealer_tran_eur)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashin_rate_rub!=record?.cashin_rate_rub}">class="bold"</g:if>>${number(value:record?.cashin_rate_rub)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashin_rate_rub!=record?.dealer_cashin_rub}">class="bold"</g:if>>(${number(value:record?.dealer_cashin_rub)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashin_rate_usd!=record?.cashin_rate_usd}">class="bold"</g:if>>${number(value:record?.cashin_rate_usd)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashin_rate_usd!=record?.dealer_cashin_usd}">class="bold"</g:if>>(${number(value:record?.dealer_cashin_usd)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashin_rate_eur!=record?.cashin_rate_eur}">class="bold"</g:if>>${number(value:record?.cashin_rate_eur)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashin_rate_eur!=record?.dealer_cashin_eur}">class="bold"</g:if>>(${number(value:record?.dealer_cashin_eur)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashout_rate_rub!=record?.cashout_rate_rub}">class="bold"</g:if>>${number(value:record?.cashout_rate_rub)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashout_rate_rub!=record?.dealer_cashout_rub}">class="bold"</g:if>>(${number(value:record?.dealer_cashout_rub)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashout_rate_usd!=record?.cashout_rate_usd}">class="bold"</g:if>>${number(value:record?.cashout_rate_usd)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashout_rate_usd!=record?.dealer_cashout_usd}">class="bold"</g:if>>(${number(value:record?.dealer_cashout_usd)})</span>
          </td>
          <td>
            <span <g:if test="${client.cashout_rate_eur!=record?.cashout_rate_eur}">class="bold"</g:if>>${number(value:record?.cashout_rate_eur)}</span><br/><hr/>
            <span <g:if test="${client.dealer_cashout_rate_eur!=record?.dealer_cashout_eur}">class="bold"</g:if>>(${number(value:record?.dealer_cashout_eur)})</span>
          </td>
          <td>
            <span <g:if test="${client.refill_rate_rub!=record?.refill_rate_rub}">class="bold"</g:if>>${number(value:record?.refill_rate_rub)}</span><br/><hr/>
            <span <g:if test="${client.dealer_refill_rate_rub!=record?.dealer_refill_rub}">class="bold"</g:if>>(${number(value:record?.dealer_refill_rub)})</span>
          </td>
          <td>
            <span <g:if test="${client.refill_rate_usd!=record?.refill_rate_usd}">class="bold"</g:if>>${number(value:record?.refill_rate_usd)}</span><br/><hr/>
            <span <g:if test="${client.dealer_refill_rate_usd!=record?.dealer_refill_usd}">class="bold"</g:if>>(${number(value:record?.dealer_refill_usd)})</span>
          </td>
          <td>
            <span <g:if test="${client.refill_rate_eur!=record?.refill_rate_eur}">class="bold"</g:if>>${number(value:record?.refill_rate_eur)}</span><br/><hr/>
            <span <g:if test="${client.dealer_refill_rate_eur!=record?.dealer_refill_eur}">class="bold"</g:if>>(${number(value:record?.dealer_refill_eur)})</span>
          </td>
        </tr>
      </g:each>
      </tbody>
    </table>
  </div>
  <div style="padding:10px">
    <span class="fleft">Найдено: ${history.count}</span>
    <span class="fright">
      <g:paginate controller="admin" action="${actionName}" params="${inrequest}" 
        prev="&lt;" next="&gt;" max="20" total="${history.count}"/>
      <g:observe classes="${['step','prevLink','nextLink']}" event="click" function="clickPaginate"/>
    </span>
  </div>
</g:if>
</div>
