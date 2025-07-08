import { useState, useEffect } from "react";
import "./LoadedRepos.css";

export default function LoadedRepos() {
  const [repos, setRepos] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/repositories/loaded")
      .then((res) => res.json())
      .then((data) => {
        const enriched = data.map(r => ({ ...r, loading: false }));
        setRepos(enriched);
      });
  }, []);

  const handleRefresh = (id) => {
    setRepos(prev =>
      prev.map(r => r.id === id ? { ...r, loading: true } : r)
    );

    setTimeout(() => {
      setRepos(prev =>
        prev.map(r => r.id === id ? { ...r, loading: false } : r)
      );
    }, 1500);
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
    const payload = repos.map(r => ({
      owner: r.username,
      repo: r.repo,
      branch: r.branch
    }));

    const res = await fetch("http://localhost:8080/api/repositories/has-any-changed", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const result = await res.json();

    setRepos(prev =>
      prev.map(r => {
        const match = result.find(x =>
          x.owner === r.username &&
          x.repo === r.repo &&
          x.branch === r.branch
        );
        return match ? { ...r, changed: match.changed } : r;
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
              justifyContent: "space-between",
              alignItems: "center",
              gap: "1rem"
            }}
          >
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem" }}>
              <div
                style={{
                  width: 12,
                  height: 12,
                  borderRadius: "50%",
                  backgroundColor: getIndicatorColor(r.changed)
                }}
              />

              <span
                onClick={() => r.hasAnalysis && handleRepoClick(r)}
                style={{
                  cursor: r.hasAnalysis ? "pointer" : "default",
                  color: r.hasAnalysis ? "#0077cc" : "gray",
                  textDecoration: r.hasAnalysis ? "underline" : "none"
                }}
              >
                {r.username}/{r.repo}/{r.branch}
              </span>
            </div>

            <span
              className={r.loading ? "refresh-icon spinning" : "refresh-icon"}
              onClick={() => handleRefresh(r.id)}
              title="Bu repoyu güncelle"
            >
              ↻
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
