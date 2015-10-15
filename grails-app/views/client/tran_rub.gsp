<script type="text/javascript">
  new Autocomplete('bik', {
    serviceUrl:'${resource(dir:"client",file:"bik_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('bank').value = lsData[0];
      $('cor_account').value = lsData[1];
    }
  });
  new Autocomplete('name', { 
    serviceUrl:'${resource(dir:"client",file:"name_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('company_id').value = lsData[0];
      $('inn').value = lsData[1];
      $('kpp').value = lsData[2];
      $('ogrn').value = lsData[3];
      $('bik').value = lsData[4];
      $('cor_account').value = lsData[5];
      $('bank').value = lsData[6];
      $('bankcity').value = lsData[7];
      $('account').value = lsData[8];
      $('prim').value = lsData[9];
      $('savecompany').value = 1;
    }
  });
</script>
  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" />
  <label for="nds">НДС, %:</label>
  <input type="text" id="nds" name="nds" value="${req?.nds?:''}" /><br />
  <label for="inn">ИНН:</label>
  <input type="text" name="inn" id="inn" value="${req?.inn?:''}" />
  <label for="inn">КПП:</label>
  <input type="text" name="kpp" id="kpp" value="${req?.kpp?:''}" /><br />
  <label for="name">Компания:</label>
  <span class="input-append">
    <input type="text" class="nopad normal" name="name" id="name" value="${req?.name?:''}" onchange="$('company_id').value='0'"/>
    <span class="add-on"><i class="icon-search"></i></span>
  </span>
  <div id="name_autocomplete" class="autocomplete" style="display:none"></div>  
  <label for="ogrn">ОГРН:</label>
  <input type="text" name="ogrn" id="ogrn" value="${req?.ogrn?:''}" /><br />
  <label for="bik">БИК:</label>
  <span class="input-append">
    <input type="text" class="nopad normal" name="bik" id="bik" value="${req?.bik?:''}" />
    <span class="add-on"><i class="icon-search"></i></span>
  </span>
  <div id="bik_autocomplete" class="autocomplete" style="display:none"></div>  
  <label for="cor_account">Корр. счет:</label>
  <input type="text" name="cor_account" id="cor_account" value="${req?.cor_account?:''}" /><br />
  <label for="bank">Банк:</label>
  <input type="text" name="bank" id="bank" value="${req?.bank?:''}" />  
  <label for="bankcity">Город банка:</label>
  <input type="text" name="bankcity" id="bankcity" value="${req?.bankcity?:''}" /><br />
  <label for="account">Расчетный счет:</label>
  <input type="text" name="account" id="account" value="${req?.account?:''}" />
<g:if test="${trantype.is_debet}">
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key"/>
</g:if><br />
  <label for="prim">Назначение платежа:</label>
  <g:textArea name="prim" id="prim" value="${req?.prim?:''}" />
  <input type="hidden" name="company_id" id="company_id" value="${req?.company_id?:0}" />