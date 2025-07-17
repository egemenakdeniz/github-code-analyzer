import { useEffect, useState } from "react";

export default function ReportList() {
  const [reports, setReports] = useState([]);

  const query = new URLSearchParams(window.location.search);
  const owner = query.get("owner");
  const repo = query.get("repo");
  const branch = query.get("branch");

  useEffect(() => {
    if (!owner || !repo || !branch) return;

    fetch(`http://localhost:8080/api/reports/of-repo?owner=${owner}&repo=${repo}&branch=${branch}`)
      .then(res => res.json())
      .then(data => {
        console.log("Gelen veri:", data);
        setReports(data);
      });
  }, [owner, repo, branch]);

  return (
    <div style={{ padding: "30px"}}>
      <h2>{owner}/{repo}/{branch} için raporlar</h2>
      {reports.length === 0 && <p>Hiç rapor bulunamadı.</p>}
      {reports.map((r, i) => (
        <div
          key={i}
          onClick={() => window.open(`http://localhost:8080/api/reports/open-pdf?path=${encodeURIComponent(r.filePath)}`,'_blank','width=1000,height=800,top=100,left=200,noopener,noreferrer')}
          style={{
            cursor: "pointer",
            padding: "10px",
            borderBottom: "1px solid #ccc",
            marginBottom: "5px",
            color: "#0077cc",
            textDecoration: "underline"
          }}
        >
          🧠 {r.modelName} — 📅 {new Date(r.generatedAt).toLocaleString()}
        </div>
      ))}
    </div>
  );
}
