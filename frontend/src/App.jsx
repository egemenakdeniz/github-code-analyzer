import './App.css';
import { useState } from 'react';
import RepoForm from './components/RepoForm';
import LoadedRepos from './components/LoadedRepos';
import ModelButtons from './components/ModelButtons';
import Login from './components/Login';

function App() {
    const [selectedRepo, setSelectedRepo] = useState(null);
    const [reloadKey, setReloadKey] = useState(0);
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    

  const handleRepoAdded = () => {
  setReloadKey(prev => prev + 1);
  };

  const handleLoginSuccess = () => {
  setIsAuthenticated(true);
  };
  const handleLogout = async () => {
  await fetch("http://localhost:8080/api/auth/logout", {
    method: "POST",
    credentials: "include",
  });
  setIsAuthenticated(false);
};

   return (
    <div className="App">
  {!isAuthenticated ? (
    <Login onLoginSuccess={handleLoginSuccess} />
  ) : (
    <>
      <div className='logout'>
        <button onClick={handleLogout}>Çıkış Yap</button>
      </div>

      {/* Asıl içerik */}
      <div className="container">
        <div className="left-panel">
          <RepoForm onRepoAdded={handleRepoAdded} />
        </div>
        <div className="right-panel">
          <LoadedRepos
            setSelectedRepo={setSelectedRepo}
            selectedRepo={selectedRepo}
            reloadTrigger={reloadKey}
          />
          <ModelButtons
            selectedRepo={selectedRepo}
            onAnalysisComplete={handleRepoAdded}
          />
        </div>
      </div>
    </>
  )}
</div>
  );
}

export default App;
