package network;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import database.IncidentDAO;
import models.Incident;
import utils.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.List;

// Built-in Java HTTP server — no extra libraries needed!
// Opens a web dashboard anyone can access via browser
// This covers Chapter 8 — Web Programming

public class WebServer {

    private static final int WEB_PORT = 8080;
    private static IncidentDAO dao = new IncidentDAO();

    public static void start() {
        try {
            HttpServer server = HttpServer.create(
                new InetSocketAddress(WEB_PORT), 0);

            // Routes
            server.createContext("/",          new HomeHandler());
            server.createContext("/incidents", new IncidentsHandler());
            server.createContext("/api/incidents", new ApiHandler());
            server.createContext("/api/stats",     new StatsHandler());

            server.start();
            Logger.log("WEB", "Web dashboard started on port " + WEB_PORT);
            System.out.println("Web dashboard: http://localhost:" + WEB_PORT);

        } catch (IOException e) {
            Logger.error("WEB", "Web server error: " + e.getMessage());
            System.out.println("Web server error: " + e.getMessage());
        }
    }

    // ── HOME PAGE ─────────────────────────────────────────────────
    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Incident> incidents = dao.getAllIncidents();

            long total    = incidents.size();
            long critical = incidents.stream()
                .filter(i -> "Critical".equals(i.getSeverity())).count();
            long resolved = incidents.stream()
                .filter(i -> "Resolved".equals(i.getStatus())).count();
            long open     = incidents.stream()
                .filter(i -> "Open".equals(i.getStatus())).count();

            StringBuilder rows = new StringBuilder();
            for (Incident i : incidents) {
                String badgeColor = "Critical".equals(i.getSeverity())
                    ? "#FCEBEB" : "Moderate".equals(i.getSeverity())
                    ? "#FAEEDA" : "#E1F5EE";
                String textColor  = "Critical".equals(i.getSeverity())
                    ? "#A32D2D" : "Moderate".equals(i.getSeverity())
                    ? "#633806" : "#085041";
                String statusColor = "Resolved".equals(i.getStatus())
                    ? "#085041" : "Open".equals(i.getStatus())
                    ? "#A32D2D" : "#633806";

                rows.append("<tr>")
                    .append("<td>").append(i.getId()).append("</td>")
                    .append("<td>").append(i.getType()).append("</td>")
                    .append("<td>").append(i.getLocation()).append("</td>")
                    .append("<td><span style='background:")
                        .append(badgeColor)
                        .append(";color:").append(textColor)
                        .append(";padding:3px 10px;border-radius:12px;")
                        .append("font-size:12px'>")
                        .append(i.getSeverity()).append("</span></td>")
                    .append("<td style='color:").append(statusColor)
                        .append(";font-weight:500'>")
                        .append(i.getStatus()).append("</td>")
                    .append("<td>").append(
                        i.getReportedAt() != null
                        ? i.getReportedAt().toString()
                            .replace("T", " ").substring(0, 16)
                        : "N/A")
                        .append("</td>")
                    .append("</tr>");
            }

            String html = """
<!DOCTYPE html>
<html lang='en'>
<head>
<meta charset='UTF-8'>
<meta name='viewport' content='width=device-width,initial-scale=1'>
<meta http-equiv='refresh' content='10'>
<title>Traffic System — Web Dashboard</title>
<style>
* { margin:0; padding:0; box-sizing:border-box; }
body { font-family:'Segoe UI',sans-serif; background:#f5f5f5;
       color:#333; }
.topbar { background:#1a1a2e; color:#fff; padding:14px 32px;
          display:flex; justify-content:space-between;
          align-items:center; }
.topbar h1 { font-size:18px; font-weight:600; }
.topbar span { font-size:13px; color:#aaa; }
.live { display:inline-block; width:8px; height:8px;
        border-radius:50%; background:#1D9E75;
        margin-right:6px; animation:pulse 1.5s infinite; }
@keyframes pulse {
  0%,100%{ opacity:1; } 50%{ opacity:0.4; }
}
.stats { display:grid; grid-template-columns:repeat(4,1fr);
         gap:16px; padding:24px 32px 0; }
.stat-card { background:#fff; border-radius:12px;
             padding:20px; text-align:center;
             border:1px solid #eee; }
.stat-num { font-size:32px; font-weight:700; }
.stat-lbl { font-size:13px; color:#888; margin-top:4px; }
.section { padding:24px 32px; }
.section h2 { font-size:15px; font-weight:600;
              margin-bottom:12px; color:#444; }
table { width:100%; background:#fff; border-radius:12px;
        border-collapse:collapse; overflow:hidden;
        box-shadow:0 1px 3px rgba(0,0,0,0.08); }
th { background:#f8f8f8; padding:12px 16px; text-align:left;
     font-size:13px; font-weight:600; color:#666;
     border-bottom:1px solid #eee; }
td { padding:12px 16px; font-size:13px;
     border-bottom:1px solid #f5f5f5; }
tr:hover td { background:#fafafa; }
tr:last-child td { border-bottom:none; }
.footer { text-align:center; padding:24px;
          font-size:12px; color:#aaa; }
.api-link { display:inline-block; margin:4px 8px;
            color:#185FA5; text-decoration:none;
            font-size:13px; }
.api-link:hover { text-decoration:underline; }
</style>
</head>
<body>
<div class='topbar'>
  <h1>🚦 Traffic Incident System</h1>
  <span><span class='live'></span>Live — refreshes every 10 seconds</span>
</div>

<div class='stats'>
  <div class='stat-card'>
    <div class='stat-num' style='color:#185FA5'>""" + total + """
</div>
    <div class='stat-lbl'>Total incidents</div>
  </div>
  <div class='stat-card'>
    <div class='stat-num' style='color:#A32D2D'>""" + critical + """
</div>
    <div class='stat-lbl'>Critical</div>
  </div>
  <div class='stat-card'>
    <div class='stat-num' style='color:#085041'>""" + resolved + """
</div>
    <div class='stat-lbl'>Resolved</div>
  </div>
  <div class='stat-card'>
    <div class='stat-num' style='color:#633806'>""" + open + """
</div>
    <div class='stat-lbl'>Still open</div>
  </div>
</div>

<div class='section'>
  <h2>All Incidents</h2>
  <table>
    <thead>
      <tr>
        <th>#</th>
        <th>Type</th>
        <th>Location</th>
        <th>Severity</th>
        <th>Status</th>
        <th>Reported at</th>
      </tr>
    </thead>
    <tbody>
""" + rows + """
    </tbody>
  </table>
</div>

<div class='footer'>
  API endpoints:
  <a class='api-link' href='/api/incidents'>/api/incidents</a>
  <a class='api-link' href='/api/stats'>/api/stats</a>
  <br><br>
  Traffic Incident Reporting System —
  Addis Ababa Science and Technology University
</div>
</body>
</html>
""";

            byte[] response = html.getBytes();
            exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // ── API — returns JSON list of all incidents ──────────────────
    static class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Incident> incidents = dao.getAllIncidents();
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < incidents.size(); i++) {
                Incident inc = incidents.get(i);
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"id\":").append(inc.getId()).append(",")
                    .append("\"type\":\"").append(inc.getType()).append("\",")
                    .append("\"location\":\"")
                        .append(inc.getLocation()).append("\",")
                    .append("\"severity\":\"")
                        .append(inc.getSeverity()).append("\",")
                    .append("\"status\":\"")
                        .append(inc.getStatus()).append("\",")
                    .append("\"reportedBy\":")
                        .append(inc.getReportedById())
                    .append("}");
            }
            json.append("]");

            byte[] response = json.toString().getBytes();
            exchange.getResponseHeaders().set(
                "Content-Type", "application/json");
            exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // ── API — returns JSON stats ──────────────────────────────────
    static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<Incident> incidents = dao.getAllIncidents();

            long total    = incidents.size();
            long critical = incidents.stream()
                .filter(i -> "Critical".equals(i.getSeverity())).count();
            long resolved = incidents.stream()
                .filter(i -> "Resolved".equals(i.getStatus())).count();
            long open     = incidents.stream()
                .filter(i -> "Open".equals(i.getStatus())).count();

            String json = "{"
                + "\"total\":"    + total    + ","
                + "\"critical\":" + critical + ","
                + "\"resolved\":" + resolved + ","
                + "\"open\":"     + open
                + "}";

            byte[] response = json.getBytes();
            exchange.getResponseHeaders().set(
                "Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }

    // ── INCIDENTS PAGE ────────────────────────────────────────────
    static class IncidentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set(
                "Location", "/");
            exchange.sendResponseHeaders(302, -1);
        }
    }
}