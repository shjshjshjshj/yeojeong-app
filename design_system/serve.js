const http = require("http");
const fs = require("fs");
const path = require("path");
const root = __dirname;
const server = http.createServer((req, res) => {
  const name = req.url === "/" ? "style_tile.html" : decodeURIComponent(req.url.replace(/^\//, ""));
  const file = path.join(root, name);
  if (!file.startsWith(root) || !fs.existsSync(file)) { res.writeHead(404); res.end("not found"); return; }
  res.writeHead(200, { "Content-Type": name.endsWith(".html") ? "text/html; charset=utf-8" : "text/plain; charset=utf-8" });
  fs.createReadStream(file).pipe(res);
});
server.listen(8765, "127.0.0.1", () => console.log("serving " + root + " on http://127.0.0.1:8765"));
