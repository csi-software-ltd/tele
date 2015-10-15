<script type="text/javascript">
  new Autocomplete('beneficial', {
    serviceUrl:'${resource(dir:"admin",file:"beneficial_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('company_id').value = lsData[0];
      $('iban').value = lsData[1];
      $('bbank').value = lsData[2];
      $('baddress').value = lsData[3];
      $('swift').value = lsData[4];
      $('purpose').value = lsData[5];
      $('laddress').value = lsData[6];
    }
  });
</script>
  <label for="summa">Сумма:</label>
  <input type="text" id="summa" name="summa" value="${req?.summa?:''}" onkeyup="computebankcomission(${bankcompercent},this.value,1)"/>
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
  <label for="vrate">Курс ЦБ:</label>
  <input type="text" id="vrate" name="vrate" value="${req?.vrate?:vrate}" onkeyup='computebankcomission(${bankcompercent},$("summa").value,1)' ${req?.modstatus>2?'readonly':''}/>
  <label for="bankcomsumma">Комиссия банка:<br/><small>в руб. ${bankcompercent}% от суммы</small></label>
  <input type="text" id="bankcomsumma" name="bankcomsumma" value="${req?.bankcomsumma}" placeholder="${bankcomsumma}" ${req?.modstatus>2?'readonly':''}/>
  <label class="convertion" style="${(req?.baseaccount?:'rub')!='rub'?'display:none':''}" for="comvrate">Коммерч. курс:</label>
  <input class="convertion" style="${(req?.baseaccount?:'rub')!='rub'?'display:none':''}" type="text" id="comvrate" name="comvrate" value="${req?.comvrate?:vrate}" ${req?.modstatus>2?'readonly':''}/>
  <label class="convertion" style="${(req?.baseaccount?:'rub')!='rub'?'display:none':''}" for="bankcomconvsumma">Конвертация:<br/><small>комиссия банка</small></label>
  <input class="convertion" style="${(req?.baseaccount?:'rub')!='rub'?'display:none':''}" type="text" id="bankcomconvsumma" name="bankcomconvsumma" value="${req?.bankcomconvsumma?:bankcomconvsumma}" ${req?.modstatus>2?'readonly':''}/>
  <label for="swiftsumma">Свифт, ${trantype.code}:</label>
  <input type="text" id="swiftsumma" name="swiftsumma" value="${req?.swiftsumma?:swiftsumma}" ${req?.modstatus>2?'readonly':''}/>
  <label for="baseaccount">Обеспечение:</label>
  <g:select name="baseaccount" value="${req?.baseaccount?:'rub'}" from="${baseaccounts}" optionValue="value" optionKey="key" readonly="${req?.modstatus>2?'true':'false'}" onchange="toggleconversion(this.value)"/>
  <br/><label for="purpose">Назначение платежа:</label>
  <g:textArea name="purpose" id="purpose" value="${req?.purpose?:''}" />
  <input type="hidden" name="company_id" id="company_id" value="${req?.company_id?:0}" />
  <input type="hidden" name="basebankcomsumma" value="${bankcomsumma}" />