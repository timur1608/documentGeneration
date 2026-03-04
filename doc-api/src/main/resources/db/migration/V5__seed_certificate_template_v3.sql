insert into template_groups (id, tenant_id, name, engine)
values (
           '06f35a21-c0ee-43a4-bd75-a4ebf7cd1ca6',
           '6b7c5a4b-1b6b-4d3f-8f6c-8c09b7a4a1d2',
           'certificate',
           'freemarker'
       )
    on conflict do nothing;

insert into template_versions (id, group_id, version, content)
values (
           '40e1de83-e8b3-4632-a811-6e62f8319e98',
           '06f35a21-c0ee-43a4-bd75-a4ebf7cd1ca6',
           1,
           '<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8"/>
  <title>Certificate</title>
  <style>
    :root{
      --bg:#0b1220;
      --paper:#ffffff;
      --text:#0f172a;
      --muted:#64748b;
      --gold:#d4af37;
      --line:#e2e8f0;
    }
    *{ box-sizing:border-box; }
    body{
      margin:0;
      font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Inter,Arial,sans-serif;
      background: radial-gradient(1200px 600px at 20% 10%, #1f2a44 0%, var(--bg) 55%, #060a12 100%);
      color:var(--text);
    }
    .page{
      max-width: 980px;
      margin: 28px auto;
      padding: 0 18px;
    }
    .frame{
      border-radius: 22px;
      padding: 18px;
      background: linear-gradient(135deg, rgba(212,175,55,.35), rgba(212,175,55,.08) 35%, rgba(255,255,255,.08));
      box-shadow: 0 18px 60px rgba(0,0,0,.35);
    }
    .paper{
      border-radius: 18px;
      background: var(--paper);
      border: 1px solid rgba(226,232,240,.9);
      overflow:hidden;
      position:relative;
    }
    .paper:before{
      content:"";
      position:absolute; inset:0;
      background:
        radial-gradient(500px 280px at 10% 10%, rgba(212,175,55,.12), transparent 60%),
        radial-gradient(420px 260px at 90% 18%, rgba(15,23,42,.06), transparent 62%),
        radial-gradient(520px 320px at 50% 95%, rgba(16,185,129,.08), transparent 65%);
      pointer-events:none;
    }
    .inner{
      position:relative;
      padding: 34px 36px 30px;
    }

    .top{
      display:flex;
      align-items:flex-start;
      justify-content:space-between;
      gap:16px;
      margin-bottom: 18px;
    }
    .mark{
      display:flex;
      align-items:center;
      gap:12px;
    }
    .seal{
      width:44px; height:44px;
      border-radius: 14px;
      background: linear-gradient(135deg, #111827, #334155);
      position:relative;
      box-shadow: inset 0 0 0 2px rgba(212,175,55,.35);
    }
    .seal:after{
      content:"";
      position:absolute; inset:10px;
      border-radius: 10px;
      border:1px dashed rgba(212,175,55,.55);
    }
    .issuer{
      font-weight:800;
      letter-spacing:.3px;
    }
    .meta{
      text-align:right;
      font-size: 12.5px;
      color: var(--muted);
      line-height: 1.4;
    }
    .meta b{ color:var(--text); }

    .title{
      text-align:center;
      margin: 8px 0 10px;
      font-size: 34px;
      letter-spacing: .6px;
      font-weight: 900;
    }
    .subtitle{
      text-align:center;
      color: var(--muted);
      font-size: 13px;
      letter-spacing: .22em;
      text-transform: uppercase;
      margin-bottom: 18px;
    }

    .name{
      text-align:center;
      font-size: 30px;
      font-weight: 900;
      margin: 14px 0 6px;
    }
    .desc{
      text-align:center;
      color: var(--muted);
      font-size: 14px;
      margin: 0 auto 10px;
      max-width: 720px;
    }
    .course{
      text-align:center;
      font-size: 18px;
      font-weight: 800;
      margin-top: 10px;
    }

    .divider{
      margin: 22px auto 18px;
      height: 1px;
      max-width: 720px;
      background: linear-gradient(90deg, transparent, rgba(212,175,55,.55), transparent);
    }

    .bottom{
      display:flex;
      flex-wrap:wrap;
      gap:18px;
      justify-content:space-between;
      align-items:flex-end;
      margin-top: 6px;
    }
    .sign{
      flex: 1 1 320px;
    }
    .line{
      width: 280px;
      height: 1px;
      background: var(--line);
      margin: 22px 0 8px;
    }
    .signer{
      font-weight: 800;
    }
    .role{
      color: var(--muted);
      font-size: 12.5px;
      margin-top: 2px;
    }

    .badge{
      flex: 0 0 auto;
      border: 1px solid rgba(212,175,55,.45);
      background: rgba(212,175,55,.10);
      color: #6b4f00;
      padding: 10px 12px;
      border-radius: 14px;
      font-size: 12.5px;
      font-weight: 800;
      font-variant-numeric: tabular-nums;
    }

    @media print{
      body{ background:#fff; }
      .page{ margin:0; max-width:none; padding:0; }
      .frame{ box-shadow:none; background:none; padding:0; }
      .paper{ border:none; border-radius:0; }
    }
  </style>
</head>

<body>
  <div class="page">
    <div class="frame">
      <div class="paper">
        <div class="inner">

          <div class="top">
            <div class="mark">
              <div class="seal"></div>
              <div>
                <div class="issuer">${issuerName!''''}</div>
                <div style="color:var(--muted); font-size:12.5px; margin-top:2px;">Official Document</div>
              </div>
            </div>

            <div class="meta">
              <div><b>Certificate ID:</b> ${certificateId!''''}</div>
              <div><b>Issued:</b> ${issuedDate!''''}</div>
            </div>
          </div>

          <div class="title">Certificate</div>
          <div class="subtitle">of completion</div>

          <div class="desc">This certifies that</div>
          <div class="name">${fullName!''''}</div>

          <div class="desc">has successfully completed the program</div>
          <div class="course">${courseTitle!''''}</div>

          <div class="divider"></div>

          <div class="bottom">
            <div class="sign">
              <div class="line"></div>
              <div class="signer">${signerName!''''}</div>
              <div class="role">${signerTitle!''''}</div>
            </div>

            <div class="badge">
              Verified • ${certificateId!''''}
            </div>
          </div>

        </div>
      </div>
    </div>
  </div>
</body>
</html>'
       )
    on conflict do nothing;