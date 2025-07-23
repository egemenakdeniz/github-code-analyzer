import React, { useState } from 'react';
import axiosInstance from '../api/axiosInstance';

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

const handleLogin = async (e) => {
    e.preventDefault();
    setError('');

    try {
      const response = await axiosInstance.post('/api/auth/login', {
        username,
        password,
      });

      if (response.status === 200 || response.status === 204) {
        onLoginSuccess();
      }
    } catch (err) {
      if (err.response?.status === 401) {
        setError('Giriş başarısız. Kullanıcı adı veya şifre hatalı.');
      } else {
        setError('Sunucuya bağlanırken hata oluştu.');
      }
    }
  };

  return (
    <form onSubmit={handleLogin} style={{ maxWidth: 300, margin: '100px auto', display: 'flex', flexDirection: 'column' }}>
      <h2>Giriş Yap</h2>
      <input
        type="text"
        placeholder="Kullanıcı Adı"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
      />
      <input
        type="password"
        placeholder="Şifre"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <button type="submit">Giriş</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
  );
};

export default Login;