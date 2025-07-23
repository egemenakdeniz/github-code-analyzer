import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

const modelsByProvider = {
  openai: ["gpt-4", "gpt-4o", "gpt-3.5-turbo"],
  ollama: ["gemma3:4b"]
};

export default function ModelButtons({ selectedRepo, onAnalysisComplete }) {
  const [provider, setProvider] = useState("");
  const [model, setModel] = useState("");

  const handleProviderChange = (e) => {
    const selected = e.target.value;
    setProvider(selected);
    setModel(""); // provider değişince modeli sıfırla
  };

  const handleAnalyze = async () => {
    if (!selectedRepo) {
      alert("Lütfen önce bir repo seçin.");
      return;
    }

    const payload = {
  userName: selectedRepo.username,
  repoName: selectedRepo.repo,
  branchName: selectedRepo.branch,
  providerName: provider === "" ? null : provider,
  modelName: model === "" ? null : model
};

    try {
      const res = await axiosInstance.post("/api/analyze", payload);

      if (res.status === 200 && res.data.success) {
        alert(res.data.message || "Analiz tamamlandı.");
        onAnalysisComplete?.();
      } else {
        alert("Hata: " + (res.data.message || "Bilinmeyen sunucu hatası"));
      }
    } catch (error) {
      console.error("İstek hatası:", error);
      alert("İstek gönderilirken bir hata oluştu.");
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "1rem", marginTop: "2rem" }}>
      <select value={provider} onChange={handleProviderChange}>
        <option value="">Sağlayıcı Seçiniz</option>
        <option value="openai">openai</option>
        <option value="ollama">ollama</option>
      </select>

      <select value={model} onChange={(e) => setModel(e.target.value)} disabled={!provider}>
        <option value="">Model Seçiniz</option>
        {provider &&
          modelsByProvider[provider]?.map((m) => (
            <option key={m} value={m}>{m}</option>
          ))}
      </select>

      <button
        onClick={handleAnalyze}
        disabled={!selectedRepo}
        style={{ padding: "10px 16px", cursor: selectedRepo ? "pointer" : "not-allowed" }}
      >
        Analizi Başlat
      </button>
    </div>
  );
}
