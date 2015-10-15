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
      <div style="float:left;font-size:9pt">Отчет по выполненным запросам <g:if test="${!(report_start||report_end)}">за все время</g:if><g:else>${report_start?'с '+String.format('%tF',report_start):''} по ${String.format('%tF',report_end?:new Date())}</g:else></div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Номер запроса</th>
            <th>Дата запроса</th>
            <th>Клиент</th>
            <th>Тип запроса</th>
            <th>Валюта запроса</th>
            <th>Сумма запроса</th>
            <th>Комиссия системы, руб</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${report}" var="record">
          <tr>
            <td>${record.id}</td>
            <td>${String.format('%tF',record.moddate)}</td>
            <td>${record.clname}</td>
            <td>${record.trname}</td>
            <td>${record.trcode}</td>
            <td>${number(value:record.summa)}</td>
            <td>${number(value:record.syssumma)}</td>
          </tr>
        </g:each>
          <tr>
            <td colspan="2">ИТОГО</td>
            <td colspan="5"></td>
          </tr>
          <tr>
            <td></td>
            <td colspan="3">Сумма запросов по рублям</td>
            <td>${number(value:sum_RUB)}</td>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td></td>
            <td colspan="3">Сумма запросов по долларам</td>
            <td>${number(value:sum_USD)}</td>
            <td colspan="2"></td>
          </tr>
          <tr>
            <td></td>
            <td colspan="3">Сумма запросов по евро</td>
            <td>${number(value:sum_EUR)}</td>
            <td colspan="2"></td>
          </tr>
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
