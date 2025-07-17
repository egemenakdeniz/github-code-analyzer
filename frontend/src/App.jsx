import './App.css';
import { useState } from 'react';
import RepoForm from './components/RepoForm';
import LoadedRepos from './components/LoadedRepos';
import ModelButtons from './components/ModelButtons';

function App() {
    const [selectedRepo, setSelectedRepo] = useState(null);
    const [reloadKey, setReloadKey] = useState(0);
    
    const handleRepoAdded = () => {
    setReloadKey(prev => prev + 1);
    };
  return (
    <div className="container">
      <div className="left-panel">
        <RepoForm  onRepoAdded={handleRepoAdded}/>
      </div>
      <div className="right-panel">
        <LoadedRepos setSelectedRepo={setSelectedRepo} selectedRepo={selectedRepo} reloadTrigger={reloadKey} />
        <ModelButtons selectedRepo={selectedRepo} onAnalysisComplete={handleRepoAdded}/>
      </div>
    </div>
  );
}

export default App;
