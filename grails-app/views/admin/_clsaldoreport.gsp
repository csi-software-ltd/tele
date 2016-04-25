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
  <g:if test="${!report.records}">
    <h1>Нет данных за указанный период</h1>
  </g:if><g:else>
    <div>
      <div style="float:left;font-size:9pt">Отчет по клиентским остаткам</div><br/>
      <div style="clear:both;text-align:center"></div><br/>
      <table style="width:1020px;font-size:9pt">
        <thead>
          <tr>
            <th>Название</th>
            <th>Остаток, руб</th>
            <th>Остаток, usd</th>
            <th>Остаток, eur</th>
          </tr>
        </thead>
        <tbody>
        <g:each in="${report.records}" var="record">
          <tr>
            <td>${record.name}</td>
            <td>${number(value:record.account_rub)}</td>
            <td>${number(value:record.account_usd)}</td>
            <td>${number(value:record.account_eur)}</td>
          </tr>
        </g:each>
          <tr>
            <td>ИТОГО</td>
            <td>${number(value:sum_RUB)}</td>
            <td>${number(value:sum_USD)}</td>
            <td>${number(value:sum_EUR)}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </g:else>
  </body>
</html>
