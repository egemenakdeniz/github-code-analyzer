import React, { useState } from 'react';
import { FaUser } from "react-icons/fa";
import axiosInstance from '../api/axiosInstance';
import { FaLock } from "react-icons/fa";
import './Login.css'
import '../index.css'



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
    <div className='wrapper'>
    <form onSubmit={handleLogin}>
      <h1>Giriş Yap</h1>
      <div className='input-box'>
      <input
        type="text"
        placeholder="Kullanıcı Adı"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        required
      />
      <FaUser className='icon'/>
      </div>
      <div className='input-box' >
      <input
        type="password"
        placeholder="Şifre"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <FaLock className='icon'/>
      </div>
      <button type="submit">Giriş Yap</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </form>
    </div>
  );
};

export default Login;