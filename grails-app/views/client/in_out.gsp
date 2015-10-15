  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" ${req?.modstatus>1?'readonly':''} onkeyup='recalculate(this.value,${req?.rate?:client."$trantype.rate"},${client.account_rub},${trantype.valuta_id==643?1:req?.vrate?:vrate?:1},${(trantype.id in [4,5,6]?-1:1)})'/>
  <label id="label_fullsumma" for="fullsumma">Списано со счета:</label>
  <input type="text" id="fullsumma" readonly value="" /><br/>
	<label for="commrub">Комиссия, руб.:</label>
  <input type="text" id="commrub" readonly value="0"/>
  <label for="commval">Комиссия, валюта:</label>
  <input type="text" id="commval" readonly value="0" />
  <div class="error-box nopad" id="nomoneynotice" style="display:none">
    <span class="icon icon-warning-sign icon-3x"></span>
    Комиссия за операцию частично списана с валютного счета в силу недостатка средств на рублевом счете. 
    Информация представлена по текущему курсу ЦБ.
  </div><br/>
  <script type="text/javascript">recalculate($("summa").value,${req?.rate?:client."$trantype.rate"},${req?.account_rub?:client.account_rub},${trantype.valuta_id==643?1:req?.vrate?:vrate?:1},${(trantype.id in [4,5,6]?-1:1)})</script>
<g:if test="${trantype.valuta_id!=643}">
  <label>Курс валюты:</label>
  <input type="text" readonly value="${req?.vrate?:vrate?:1}"/>
</g:if><g:if test="${trantype.is_debet}">
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key" disabled="${req?.modstatus>1?'true':'false'}"/>
</g:if>
