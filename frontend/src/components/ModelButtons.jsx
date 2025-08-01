import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';

export default function ModelButtons({ selectedRepo, onAnalysisComplete }) {
  const [provider, setProvider] = useState("");
  const [model, setModel] = useState("");

  const [modelsByProvider, setModelsByProvider] = useState({
    openai: [],
    ollama: []
  });

  useEffect(() => {
    const fetchOpenAIModels = async () => {
      try {
        const res = await axiosInstance.get("/api/models/openai");
        if (res.status === 200 && res.data.models) {
          setModelsByProvider(prev => ({
            ...prev,
            openai: res.data.models
          }));
        }
      } catch (error) {
        console.error("OpenAI modelleri alınamadı:", error);
      }
    };

    const fetchOllamaModels = async () => {
      try {
        const res = await axiosInstance.get("/api/models/ollama");
        if (res.status === 200 && res.data.models) {
          setModelsByProvider(prev => ({
            ...prev,
            ollama: res.data.models
          }));
        }
      } catch (error) {
        console.error("Ollama modelleri alınamadı:", error);
      }
    };

    fetchOpenAIModels();
    fetchOllamaModels();
  }, []);

  const handleProviderChange = (e) => {
    const selected = e.target.value;
    setProvider(selected);
    setModel(""); // sağlayıcı değiştiğinde modeli sıfırla
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
      providerName: provider || null,
      modelName: model || null
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
      const message =
        error.response?.data?.message ||
        error.message ||
        "İstek gönderilirken bir hata oluştu.";
      alert(message);
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