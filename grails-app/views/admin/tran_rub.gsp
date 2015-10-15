<script type="text/javascript">
  new Autocomplete('bik', {
    serviceUrl:'${resource(dir:"admin",file:"bik_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('bank').value = lsData[0];
      $('cor_account').value = lsData[1];
    }
  });
  new Autocomplete('name', { 
    serviceUrl:'${resource(dir:"admin",file:"name_autocomplete")}',
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
    }
  });
</script>
  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" onkeyup="computebankcomission(${bankcompercent},this.value)"/>
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
  <label for="bankcomsumma">Комиссия банка:<br/><small>в руб. ${bankcompercent}% от суммы</small></label>
  <input type="text" id="bankcomsumma" name="bankcomsumma" value="${req?.bankcomsumma}" placeholder="${bankcomsumma}" ${req?.modstatus>2?'readonly':''}/>
  <label for="vrate" class="vrate" style="${(req?.baseaccount?:'rub')=='rub'?'display:none':''}">Курс ЦБ:</label>
  <input type="text" class="vrate" style="${(req?.baseaccount?:'rub')=='rub'?'display:none':''}" id="vrate" name="vrate" value="${req?.vrate?:comvrate}" ${req?.modstatus>2?'readonly':''}/>
  <label class="comvrate" style="${(req?.baseaccount?:'rub')=='rub'?'display:none':''}" for="comvrate">Коммерч. курс:</label>
  <input class="comvrate" style="${(req?.baseaccount?:'rub')=='rub'?'display:none':''}" type="text" id="comvrate" name="comvrate" value="${req?.comvrate?:vrate}" ${req?.modstatus>2?'readonly':''}/>
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key" readonly="${req?.modstatus>2?'true':'false'}" onchange="changebaseaccount('${trantype?.code?.toLowerCase()}',this.value)"/>
  <br /><label for="prim">Назначение платежа:</label>
  <g:textArea name="prim" id="prim" value="${req?.prim?:''}" />
  <input type="hidden" name="company_id" id="company_id" value="${req?.company_id?:0}" />
  <input type="hidden" name="basebankcomsumma" value="${bankcomsumma}" />