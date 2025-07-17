import { useState, useEffect } from "react";
import "./LoadedRepos.css";

export default function LoadedRepos({ selectedRepo, setSelectedRepo }) {
    const [repos, setRepos] = useState([]);
  

useEffect(() => {
  fetch("http://localhost:8080/api/repositories/loaded")
    .then((res) => res.json())
    .then((data) => {
      const enriched = data.map(r => ({
        id: r.id,
        username: r.userName,
        repo: r.repoName,
        branch: r.branchName,
        hasAnalysis: r.hasAnalysis,
      }));
      setRepos(enriched);
    });
}, []);

const handleRefresh = async (repoId) => {
  try {
    const repo = repos.find(r => r.id === repoId);
    if (!repo) return;

    const payload = {
      userName: repo.username,
      repoName: repo.repo,
      branchName: repo.branch,
    };

    setRepos(prev =>
      prev.map(r => r.id === repoId ? { ...r, loading: true } : r)
    );

    const res = await fetch("http://localhost:8080/api/repositories/update", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!res.ok) throw new Error("Güncelleme başarısız");

    setRepos(prev =>
      prev.map(r => r.id === repoId ? { ...r, loading: false } : r)
    );

    alert("Repository başarıyla güncellendi.");
  } catch (err) {
    setRepos(prev =>
      prev.map(r => r.id === repoId ? { ...r, loading: false } : r)
    );
    console.error("Hata:", err);
    alert("Güncelleme sırasında bir hata oluştu.");
  }
};

  const handleRepoClick = (r) => {
    const url = `${window.location.origin}/report-list?owner=${r.username}&repo=${r.repo}&branch=${r.branch}`;
    window.open(
      url,
      '_blank',
      'width=1000,height=800,top=100,left=200,noopener,noreferrer'
    );
  };

 const handleCheckAll = async () => {
  
  const res = await fetch("http://localhost:8080/api/repositories/has-any-changed");
  const result = await res.json();
  console.log("Gelen sonuç:", result);

  setRepos(prev =>
    prev.map(r => {
      const match = result.find(x =>
        x.userName === r.username &&
        x.repoName === r.repo &&
        x.branchName === r.branch
      );

      return match
        ? { ...r, changed: match.upToDate === false }
        : r;
    })
  );
};

  const getIndicatorColor = (changed) => {
    if (changed === true) return "red";
    if (changed === false) return "green";
    return "gray";
  };

  return (
  <div>
    <h3>HALİ HAZIRDA YÜKLENMİŞ OLANLAR</h3>

    <button onClick={handleCheckAll} style={{ marginBottom: "1rem" }}>
      Tümünü Kontrol Et
    </button>

    <div className="loaded-area">
      {repos.map((r) => (
        <div
          key={r.id}
          style={{
            display: "flex",
            justifyContent: "space-between", // ✅ Refresh sağa gider
            alignItems: "center",
            padding: "10px 12px",
            borderRadius: "8px",
            border: "1px solid #ddd",
            backgroundColor: selectedRepo?.id === r.id ? "#f0f8ff" : "white",
            marginBottom: "8px"
          }}
        >
          {/* Sol: Radio + Nokta + Repo adı */}
          <div style={{ display: "flex", alignItems: "center", gap: "10px", flex: 1 }}>
            {/* Radio + Durum noktası */}
            <div style={{ display: "flex", alignItems: "center", gap: "4px" }}>
              <input
                type="radio"
                name="repoSelection"
                checked={selectedRepo?.id === r.id}
                onChange={() => setSelectedRepo(r)}
              />
              <div
                style={{
                  width: 28,
                  height: 14,
                  borderRadius: "50%",
                  backgroundColor: getIndicatorColor(r.changed)
                }}
              />
            </div>

            {/* Repo adı */}
            <span
              onClick={() => r.hasAnalysis && handleRepoClick(r)}
              style={{
                cursor: r.hasAnalysis ? "pointer" : "default",
                color: r.hasAnalysis ? "#0077cc" : "#aaa",
                textDecoration: r.hasAnalysis ? "underline" : "none",
                fontSize: "0.9rem",
                whiteSpace: "normal",        // ✅ çok uzun ise alt satıra geçsin
                wordBreak: "break-word",     // ✅ bölerek sığsın
                lineHeight: "1.3"
              }}
            >
              {r.username}/{r.repo}/{r.branch}
            </span>
          </div>

          {/* Sağ: Refresh ikonu */}
          <span
            className={r.loading ? "refresh-icon spinning" : "refresh-icon"}
            onClick={() => handleRefresh(r.id)}
            title="Bu repoyu güncelle"
            style={{ cursor: "pointer", fontSize: "1.2rem", marginLeft: "10px" }}
          >
            ↻
          </span>
        </div>
      ))}
    </div>
  </div>
);
}
