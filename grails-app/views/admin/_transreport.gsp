<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <style type="text/css">
      @font-face {
        src: url('http://nbps.ru:8080/font/arial.ttf');
        -fs-pdf-font-embed: embed;
        -fs-pdf-font-encoding: cp1251;
      }
      @page {
        size: 29.7cm 21cm;
      }
      body { font-family: "Arial Unicode MS", Arial, sans-serif; }
      table { border-top: 2px solid #000; border-left: 1px solid #000; }
      table th { border-bottom: 2px solid #000; border-right: 1px solid #000 }
      table td { border-bottom: 1px solid #000; border-right: 1px solid #000 }
    </style>
  </head>
  <body style="width:1020px">
  <g:if test="${!report}">
    <h1>Нет данных за указанный период</h1>
  </g:if><g:else>
    <div>
      <div style="float:left;font-size:9pt">Выписка операций <g:if test="${!(report_start||report_end)}">за все время</g:if><g:else>${report_start?'с '+String.format('%tF',report_start):''} по ${String.format('%tF',report_end?:new Date())}</g:else></div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Номер операции</th>
            <th>Дата операции</th>
            <th>Тип операции</th>
            <th>Сумма операции</th>
            <th>Сальдо</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${report}" var="record">
          <tr>
            <td>${record.id}</td>
            <td>${String.format('%tF',record.inputdate)}</td>
            <td>${record.trname}</td>
            <td>${number(value:record.summa*record.vrate)}</td>
            <td>${(record.trantype_id in [13,22])?number(value:record.saldo):''}</td>
          </tr>
        </g:each>
          <tr>
            <td colspan="2">ИТОГО</td>
            <td colspan="3"></td>
          </tr>
          <tr>
            <td></td>
            <td colspan="2">Сальдо на начало периода</td>
            <td>${number(value:report.first().saldo-report.first().summa*report.first().vrate)}</td>
            <td></td>
          </tr>
          <tr>
            <td></td>
            <td colspan="2">Обороты за период</td>
            <td>${number(value:mainobor)}</td>
            <td></td>
          </tr>
        <g:if test="${type}">
          <tr>
            <td></td>
            <td colspan="2">Обороты по затратам</td>
            <td>${number(value:addobor)}</td>
            <td></td>
          </tr>
        </g:if>
          <tr>
            <td></td>
            <td colspan="2">Сальдо на конец периода</td>
            <td>${number(value:report.findAll{it.trantype_id in [13,22]}.last().saldo)}</td>
            <td></td>
          </tr>
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
