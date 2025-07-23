import { useState, useEffect } from "react";
import "./LoadedRepos.css";
import axiosInstance from "../api/axiosInstance";

export default function LoadedRepos({ selectedRepo, setSelectedRepo, reloadTrigger }) {
  const [repos, setRepos] = useState([]);

  useEffect(() => {
    axiosInstance
      .get("/api/repositories/loaded")
      .then((res) => {
        const enriched = res.data.map(r => ({
          id: r.id,
          username: r.userName,
          repo: r.repoName,
          branch: r.branchName,
          hasAnalysis: r.hasAnalysis,
        }));
        setRepos(enriched);
      })
      .catch((err) => {
        console.error("Yükleme hatası:", err);
      });
  }, [reloadTrigger]);

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

      const res = await axiosInstance.post("/api/repositories/update", payload);
      const data = res.data;

      setRepos(prev =>
        prev.map(r => r.id === repoId ? { ...r, loading: false } : r)
      );

      alert(data.message);

      await handleCheckAll(); // Güncelleme sonrası yeniden kontrol
    } catch (err) {
      setRepos(prev =>
        prev.map(r => r.id === repoId ? { ...r, loading: false } : r)
      );
      console.error("Hata:", err);
      alert("Güncelleme sırasında bir hata oluştu: " + err.message);
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
    try {
      const res = await axiosInstance.get("/api/repositories/has-any-changed");
      const result = res.data;
      console.log("Gelen sonuç:", result);
      alert("Kontrol Başarılı");

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
    } catch (err) {
      console.error("Hata:", err);
      alert("Tümünü kontrol ederken bir hata oluştu: " + err.message);
    }
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
              padding: "10px 12px",
              borderRadius: "8px",
              border: "1px solid #ddd",
              backgroundColor: selectedRepo?.id === r.id ? "#f0f8ff" : "white",
              marginBottom: "8px"
            }}
          >
            {/* Sol: Radio + Nokta + Repo adı */}
            <div style={{ display: "flex", alignItems: "center", gap: "10px", flex: 1 }}>
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

              <span
                onClick={() => r.hasAnalysis && handleRepoClick(r)}
                style={{
                  cursor: r.hasAnalysis ? "pointer" : "default",
                  color: r.hasAnalysis ? "#0077cc" : "#aaa",
                  textDecoration: r.hasAnalysis ? "underline" : "none",
                  fontSize: "0.9rem",
                  whiteSpace: "normal",
                  wordBreak: "break-word",
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
