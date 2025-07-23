import axios from "axios";

// Axios örneği
const axiosInstance = axios.create({
  baseURL: "http://localhost:8080",
  withCredentials: true, // 🍪 Cookie desteği
});

// İstek öncesi interceptor (gerekirse header eklemek için kullanılabilir)
axiosInstance.interceptors.request.use((config) => {
  return config;
});

// Yanıt interceptor: accessToken expired olursa otomatik yenile
axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // Access token expired & daha önce retry edilmemişse
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      try {
        // Refresh endpoint'ine istek at
        await axiosInstance.post("/api/auth/refresh");

        // Yeniden orijinal isteği gönder
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        console.error("Refresh token geçersiz:", refreshError);
        window.location.href = "/"; // Logout yönlendirmesi
      }
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;