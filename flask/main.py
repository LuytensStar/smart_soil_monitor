import os
import time

import requests
from dotenv import load_dotenv
from flask import Flask, jsonify, render_template_string

app = Flask(__name__)
load_dotenv()

ESP_IP = os.getenv("ESP_IP")
METRICS_URL = f"http://{ESP_IP}/metrics"


@app.route("/data")
def data():
    try:

        resp = requests.get(f"http://{ESP_IP}/metrics", timeout=1)
        resp.raise_for_status()
        data = resp.json()
    except Exception as e:

        print("Error fetching from ESP:", e)
        data = {"brightness": 0, "moisture": 0}

    data["ts"] = int(time.time())
    return jsonify(data)


HTML = """
<!doctype html><html lang="en"><head>
  <meta charset="utf-8"><title>Soil monitor</title>
  <link rel="stylesheet"
    href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <style>.bar{height:2rem}</style>
</head><body class="p-4">
  <h3>State on <span id=t>–</span></h3>
  <div class="mb-3">
    <label>Brightness</label>
    <div class="progress"><div id=bri class="progress-bar bar"></div></div>
  </div>
  <div class="mb-3">
    <label>Soil moisture</label>
    <div class="progress"><div id=soil class="progress-bar bar bg-success"></div></div>
  </div>
<script>
async function refresh(){
  let r = await fetch('/data'); let d = await r.json();
  document.getElementById('bri').style.width  = d.brightness + '%';
  document.getElementById('soil').style.width = d.moisture   + '%';
  document.getElementById('bri').innerText    = d.brightness + '%';
  document.getElementById('soil').innerText   = d.moisture   + '%';
  document.getElementById('t').innerText      =
      new Date(d.ts*1000).toLocaleTimeString();
}
setInterval(refresh, 2000); refresh();
</script></body></html>
"""


@app.route("/")
def ui():
    return render_template_string(HTML)


if __name__ == "__main__":
    app.run(host=os.getenv('HOST'), port=os.getenv('PORT'), debug=True)
