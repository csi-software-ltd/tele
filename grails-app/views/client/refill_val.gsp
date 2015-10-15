<script type="text/javascript">
  new Autocomplete('name', {
    serviceUrl:'${resource(dir:"client",file:"name_autocomplete")}',
    onSelect: function(value, data){
      var lsData = data.split(';');
      $('company_id').value = lsData[0];
    }
  });
</script>
  <label for="summa">Сумма:</label>
  <input type="text" id="summa" ${req?.modstatus>1?'readonly':''} name="summa" value="${req?.summa?:''}" /> 
  <label for="name">Компания:</label>
  <span class="input-append">
    <input type="text" class="nopad normal" name="name" id="name" value="${req?.name?:''}" onchange="$('company_id').value='0'" ${req?.modstatus>1?'readonly':''}/>
    <span class="add-on"><i class="icon-search"></i></span>
  </span>
  <div id="name_autocomplete" class="autocomplete" style="display:none"></div><br/>
  <label for="syscompany_name">Кому:</label>
  <input type="text" id="syscompany_name" name="syscompany_name" value="${req?.syscompany_name?:''}" ${req?.modstatus>1?'readonly':''} />
  <label for="platdate">Дата платежа:</label>
<g:if test="${req?.modstatus<2}">
  <g:datepicker class="normal nopad" name="platdate" value="${req?.platdate?String.format('%td.%<tm.%<tY',req.platdate):''}"/>
</g:if><g:else>
  <input type="text" name="platdate" readonly value="${String.format('%td.%<tm.%<tY',req.platdate)}" />
</g:else><br />
  <label for="prim">Номер платежного поручения:</label>
  <g:textArea name="prim" id="prim" value="${req?.prim?:''}" readonly="${req?.modstatus>1?true:false}"/>
  <input type="hidden" name="company_id" id="company_id" value="${req?.company_id?:0}" />