export default function ModelButtons({ selectedRepo , onAnalysisComplete}) {

     const handleAnalyze = async (modelName) => {
    if (!selectedRepo) {
      alert("Lütfen önce bir repo seçin.");
      return;
    }

    const payload = {
      userName: selectedRepo.username,
      repoName: selectedRepo.repo,
      branchName: selectedRepo.branch,
      modelName
    };

    try {
      const res = await fetch("http://localhost:8080/api/analyze", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });

      const data = await res.json();

      if (res.ok && data.success) {
        alert(data.message);//
        onAnalysisComplete?.();
      } else {
        alert("Hata: " + (data.message || "Bilinmeyen hata"));
      }
    } catch (e) {
      alert("İstek gönderilirken hata oluştu.");
    }
  };

     return (
    <div style={{ marginTop: "2rem" }}>
      <button
        disabled={!selectedRepo}
        onClick={() => handleAnalyze("gpt")}
      >
        GPT-4
      </button>

      <button
        disabled={!selectedRepo}
        onClick={() => handleAnalyze("ollama")}
      >
        Ollama
      </button>
    </div>
  );
}
  