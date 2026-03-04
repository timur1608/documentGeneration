insert into template_groups (id, tenant_id, name, engine)
values (
           '11111111-1111-1111-1111-111111111111',
           '6b7c5a4b-1b6b-4d3f-8f6c-8c09b7a4a1d2',
           'invoice',
           'freemarker'
       )
    on conflict do nothing;

insert into template_versions (id, group_id, version, content)
values (
           '7a4d0c3b-9e7b-4db9-9c1c-6a0c1f2e3d4a',
           '11111111-1111-1111-1111-111111111111',
           2,
           '<!doctype html>
           <html lang="en">
           <head>
             <meta charset="utf-8"/>
             <meta name="viewport" content="width=device-width, initial-scale=1"/>
             <title>Invoice</title>
             <style>
               :root{
                 --bg:#f6f7fb;
                 --card:#ffffff;
                 --text:#111827;
                 --muted:#6b7280;
                 --line:#e5e7eb;
                 --accent:#2563eb;
                 --accent-soft:#eff6ff;
               }
               *{ box-sizing:border-box; }
               body{
                 margin:0;
                 font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Inter,Arial,sans-serif;
                 color:var(--text);
                 background:var(--bg);
                 line-height:1.45;
               }
               .page{
                 max-width: 860px;
                 margin: 32px auto;
                 padding: 0 16px;
               }
               .card{
                 background:var(--card);
                 border:1px solid var(--line);
                 border-radius: 16px;
                 box-shadow: 0 8px 24px rgba(17,24,39,.06);
                 overflow:hidden;
               }
               .header{
                 padding: 22px 24px;
                 background: linear-gradient(135deg, var(--accent-soft), #ffffff 55%);
                 border-bottom:1px solid var(--line);
                 display:flex;
                 gap:16px;
                 align-items:flex-start;
                 justify-content:space-between;
               }
               .title{
                 margin:0;
                 font-size: 22px;
                 letter-spacing: .2px;
               }
               .sub{
                 margin-top:6px;
                 color:var(--muted);
                 font-size: 13px;
               }
               .badge{
                 display:inline-block;
                 padding: 8px 10px;
                 border-radius: 999px;
                 border:1px solid rgba(37,99,235,.25);
                 background: rgba(37,99,235,.08);
                 color: var(--accent);
                 font-weight: 600;
                 font-size: 12px;
                 white-space:nowrap;
               }
               .content{
                 padding: 22px 24px 26px;
               }
               .row{
                 display:flex;
                 flex-wrap:wrap;
                 gap: 18px;
                 margin-bottom: 18px;
               }
               .info{
                 flex: 1 1 280px;
                 border:1px solid var(--line);
                 border-radius: 12px;
                 padding: 14px 14px;
                 background:#fff;
               }
               .label{
                 color:var(--muted);
                 font-size: 12px;
                 margin-bottom:6px;
               }
               .value{
                 font-size: 14px;
                 font-weight:600;
               }

               table{
                 width:100%;
                 border-collapse: separate;
                 border-spacing: 0;
                 overflow:hidden;
                 border:1px solid var(--line);
                 border-radius: 12px;
                 background:#fff;
               }
               thead th{
                 text-align:left;
                 font-size: 12px;
                 color: var(--muted);
                 font-weight: 700;
                 padding: 12px 14px;
                 background: #f9fafb;
                 border-bottom:1px solid var(--line);
               }
               tbody td{
                 padding: 12px 14px;
                 border-bottom:1px solid var(--line);
                 vertical-align:top;
                 font-size: 14px;
               }
               tbody tr:last-child td{ border-bottom:none; }
               .num{ text-align:right; font-variant-numeric: tabular-nums; white-space:nowrap; }
               .muted{ color:var(--muted); }

               .totals{
                 margin-top: 16px;
                 display:flex;
                 justify-content:flex-end;
               }
               .total-box{
                 min-width: 280px;
                 border:1px solid var(--line);
                 border-radius: 12px;
                 padding: 14px 14px;
                 background: #fff;
               }
               .total-line{
                 display:flex;
                 justify-content:space-between;
                 gap:12px;
                 margin: 6px 0;
                 font-size: 14px;
               }
               .grand{
                 margin-top:10px;
                 padding-top:10px;
                 border-top:1px dashed var(--line);
                 font-size: 16px;
                 font-weight: 800;
               }

               .footer{
                 margin-top: 14px;
                 padding: 0 2px;
                 color: var(--muted);
                 font-size: 12px;
               }

               @media print{
                 body{ background:#fff; }
                 .page{ margin:0; max-width: none; padding:0; }
                 .card{ box-shadow:none; border-radius:0; border:none; }
                 .header{ border-bottom:1px solid #ddd; }
               }
             </style>
           </head>

           <body>
             <div class="page">
               <div class="card">

                 <div class="header">
                   <div>
                     <h1 class="title">Invoice</h1>
                     <div class="sub">Thank you for your business</div>
                   </div>
                   <div class="badge">#${invoiceNumber}</div>
                 </div>

                 <div class="content">
                   <div class="row">
                     <div class="info">
                       <div class="label">Customer</div>
                       <div class="value">${customerName}</div>
                     </div>

                     <div class="info">
                       <div class="label">Invoice number</div>
                       <div class="value">#${invoiceNumber}</div>
                     </div>
                   </div>

                   <table>
                     <thead>
                       <tr>
                         <th style="width:70%;">Item</th>
                         <th class="num" style="width:30%;">Price</th>
                       </tr>
                     </thead>
                     <tbody>
                       <#list items as it>
                         <tr>
                           <td>
                             <div style="font-weight:600;">${it.name}</div>
                             <#-- если вдруг появится описание: -->
                             <#-- <div class="muted" style="margin-top:2px;">${it.description}</div> -->
                           </td>
                           <td class="num">${it.price}</td>
                         </tr>
                       </#list>

                       <#-- на случай пустого списка -->
                       <#if (items?size == 0)>
                         <tr>
                           <td class="muted">No items</td>
                           <td class="num muted">—</td>
                         </tr>
                       </#if>
                     </tbody>
                   </table>

                   <div class="totals">
                     <div class="total-box">
                       <div class="total-line grand">
                         <span>Total</span>
                         <span class="num">${total}</span>
                       </div>
                     </div>
                   </div>

                   <div class="footer">
                     If you have questions about this invoice, reply to this message or contact support.
                   </div>
                 </div>

               </div>
             </div>
           </body>
           </html>'
       )
    on conflict do nothing;