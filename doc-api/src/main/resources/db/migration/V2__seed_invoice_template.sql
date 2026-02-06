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
           '2b9c7b1e-2e6f-4b5f-9f8d-0e1c2d3a4b5c',
           '11111111-1111-1111-1111-111111111111',
           1,
           '<!doctype html>
         <html>
         <head><meta charset="utf-8"/><title>Invoice</title></head>
         <body>
           <h1>Invoice ${invoiceNumber}</h1>
           <p><b>Customer:</b> ${customerName}</p>

           <h3>Items</h3>
           <ul>
             <#list items as it>
               <li>${it.name} — ${it.price}</li>
             </#list>
           </ul>

           <p><b>Total:</b> ${total}</p>
         </body>
         </html>'
       )
    on conflict do nothing;

