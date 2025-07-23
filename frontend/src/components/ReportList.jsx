import { useEffect, useState } from "react";
import axiosInstance from "../api/axiosInstance";

export default function ReportList() {
  const [reports, setReports] = useState([]);

  const query = new URLSearchParams(window.location.search);
  const owner = query.get("owner");
  const repo = query.get("repo");
  const branch = query.get("branch");

  useEffect(() => {
    if (!owner || !repo || !branch) return;

    const fetchReports = async () => {
      try {
        const res = await axiosInstance.get(`/api/reports/of-repo`, {
          params: { owner, repo, branch },
        });
        setReports(res.data);
      } catch (err) {
        console.error("Raporları alma hatası:", err);
        setReports([]);
      }
    };

    fetchReports();
  }, [owner, repo, branch]);

  const handleOpenPdf = (reportId) => {
    const url = `http://localhost:8080/api/reports/open-pdf?reportId=${reportId}`;
    window.open(url, '_blank', 'width=1000,height=800,top=100,left=200,noopener,noreferrer');
  };

  return (
    <div style={{ padding: "30px" }}>
      <h2>{owner}/{repo}/{branch} için Raporlar</h2>

      {reports.length === 0 ? (
        <p>Hiç rapor bulunamadı.</p>
      ) : (
        reports.map((r, i) => (
          <div
            key={i}
            onClick={() => handleOpenPdf(r.reportId)}
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
        ))
      )}
    </div>
  );
}
