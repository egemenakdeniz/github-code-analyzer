import { useState } from 'react';

export default function RepoForm() {
  const [username, setUsername] = useState("");
  const [repo, setRepo] = useState("");
  const [branch, setBranch] = useState("main");
  const [response, setResponse] = useState(null);
  const [error, setError] = useState(null);

  const handleFetch = () => {
    if (!username.trim() || !repo.trim()) {
      setError("Kullanıcı adı ve repository adı boş bırakılamaz.");
      setResponse(null);
      return;
    }

    setError(null);
    setResponse(null);

    const params = new URLSearchParams({
      owner: username,
      repo: repo,
      branch: branch
    });

    fetch(`http://localhost:8080/api/repositories/import?${params.toString()}`, {
      method: "POST"
    })
      .then(res => {
        if (!res.ok) {
          return res.text().then(text => {
            throw new Error(text || `Hata kodu: ${res.status}`);
          });
        }
        return res.text();
      })
      .then(data => {
        setResponse({ status: "success", message: data });
        setError(null);
      })
      .catch(err => {
        setError("Hata: " + err.message);
        setResponse(null);
      });
  };

  return (
    <div style={{ maxWidth: "500px", margin: "0 auto", padding: "2rem" }}>
      <h2>GitHub Repository Ekle</h2>

      <div style={{ marginBottom: "1rem" }}>
        <label>Kullanıcı Adı</label>
        <input
          type="text"
          value={username}
          onChange={e => setUsername(e.target.value)}
          placeholder="owner"
          style={{ width: "100%", padding: "0.5rem" }}
        />
      </div>

      <div style={{ marginBottom: "1rem" }}>
        <label>Repository Adı</label>
        <input
          type="text"
          value={repo}
          onChange={e => setRepo(e.target.value)}
          placeholder="repo"
          style={{ width: "100%", padding: "0.5rem" }}
        />
      </div>

      <div style={{ marginBottom: "1rem" }}>
        <label>Branch Adı</label>
        <input
          type="text"
          value={branch}
          onChange={e => setBranch(e.target.value)}
          placeholder="main"
          style={{ width: "100%", padding: "0.5rem" }}
        />
      </div>

      <button onClick={handleFetch} style={{ padding: "0.5rem 1rem" }}>
        Getir
      </button>

      {error && (
        <div style={{ color: "red", marginTop: "1rem" }}>
          {error}
        </div>
      )}

      {response && (
        <div style={{ color: "green", marginTop: "1rem" }}>
          <strong>{response.status.toUpperCase()}</strong>: {response.message}
        </div>
      )}
    </div>
  );
}
