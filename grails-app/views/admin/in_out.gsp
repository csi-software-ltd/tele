  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" ${req?.modstatus>2?'readonly':''} onkeyup='recalculate(this.value,${req?.rate?:client."$trantype.rate"},${client.account_rub},${(trantype.id in [4,5,6]?-1:1)},${trantype.valuta_id==643?false:true});computebankcomission(${bankcompercent},this.value,${trantype.valuta_id==643?0:1})' />
  <label id="label_fullsumma" for="fullsumma">Поступило на счет:</label>
  <input type="text" id="fullsumma" readonly value="" /><br/>
	<label for="commrub">Комиссия, руб.:</label>
  <input type="text" id="commrub" readonly value="0"/>
  <label for="commval">Комиссия, валюта:</label>
  <input type="text" id="commval" readonly value="0" />
  <div class="error-box nopad" id="nomoneynotice" style="display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    Комиссия за операцию частично списана с валютного счета в силу недостатка средств на рублевом счете. 
    Информация представлена по указанному курсу ЦБ.
  </div><br/>
<g:if test="${trantype.id!=4}">
	<label class="vrate" style="${(trantype.id==7&&(req?.baseaccount?:'rub')=='rub')?'display:none':''}" for="vrate">Курс ЦБ:</label>
	<input class="vrate" style="${(trantype.id==7&&(req?.baseaccount?:'rub')=='rub')?'display:none':''}" type="text" id="vrate" name="vrate" value="${req?.vrate?:vrate}" onkeyup='recalculate($("summa").value,${req?.rate?:client."$trantype.rate"},${client.account_rub},${(trantype.id in [4,5,6]?-1:1)},${trantype.valuta_id==643?false:true});computebankcomission(${bankcompercent},$("summa").value,${trantype.valuta_id==643?0:1})' ${req?.modstatus>2?'readonly':''}/>
</g:if>
<g:if test="${trantype.is_debet}">
  <label class="comvrate" style="${(req?.baseaccount?:'rub')==trantype.code.toLowerCase()?'display:none':''}" for="comvrate">Коммерч. курс:</label>
  <input class="comvrate" style="${(req?.baseaccount?:'rub')==trantype.code.toLowerCase()?'display:none':''}" type="text" id="comvrate" name="comvrate" value="${req?.comvrate?:vrate}" ${req?.modstatus>2?'readonly':''}/>
  <label for="bankcomsumma">Комиссия банка:<br/><small>в руб. ${bankcompercent}% от суммы</small></label>
  <input type="text" id="bankcomsumma" name="bankcomsumma" value="${req?.bankcomsumma}" placeholder="${bankcomsumma}" ${req?.modstatus>2?'readonly':''}/>
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key" disabled="${req?.modstatus>2?'true':'false'}" onchange="changebaseaccount('${trantype?.code?.toLowerCase()}',this.value)"/>
  <input type="hidden" name="basebankcomsumma" value="${bankcomsumma}" />
</g:if>
  <script type="text/javascript">recalculate($("summa").value,${req?.rate?:client."$trantype.rate"},${req?.account_rub?:client.account_rub},${(trantype.id in [4,5,6]?-1:1)},${trantype.valuta_id==643?false:true})</script>
