<script type="text/javascript">
  new Autocomplete('beneficial', {
    serviceUrl:'${resource(dir:"client",file:"beneficial_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('company_id').value = lsData[0];
      $('iban').value = lsData[1];
      $('bbank').value = lsData[2];
      $('baddress').value = lsData[3];
      $('swift').value = lsData[4];
      $('purpose').value = lsData[5];
      $('laddress').value = lsData[6];
      $('savecompany').value = 1;
    }
  });
</script>
  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" />  
  <label for="beneficial">Бенефициар:</label>
  <span class="input-append">
    <input type="text" class="nopad normal" name="beneficial" id="beneficial" value="${req?.beneficial?:''}" />
    <span class="add-on"><i class="icon-search"></i></span>
  </span>
  <div id="beneficial_autocomplete" class="autocomplete" style="display:none"></div>
  <label for="swift">SWIFT код банка:</label>
  <input type="text" name="swift" id="swift" maxlength="11" value="${req?.swift?:''}" />  
  <label for="iban">IBAN код:</label>
  <input type="text" name="iban" id="iban" maxlength="34" value="${req?.iban?:''}" /><br />
  <label for="bbank">Банк:</label>
  <input type="text" name="bbank" id="bbank" value="${req?.bbank?:''}" />
  <label for="baddress">Адрес банка:</label>
  <input type="text" name="baddress" id="baddress" value="${req?.baddress?:''}" /><br />
  <label for="laddress">Адрес получателя:</label>
  <input type="text" name="laddress" id="laddress" value="${req?.laddress?:''}" />
<g:if test="${trantype.is_debet}">
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key"/><br />
</g:if>
  <label for="purpose">Назначение платежа:</label>
  <g:textArea name="purpose" id="purpose" value="${req?.purpose?:''}" />
  <input type="hidden" name="company_id" id="company_id" value="${req?.company_id?:0}" />
