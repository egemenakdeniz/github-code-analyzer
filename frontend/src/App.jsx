import './App.css';
import { useState } from 'react';
import RepoForm from './components/RepoForm';
import LoadedRepos from './components/LoadedRepos';
import ModelButtons from './components/ModelButtons';

function App() {
    const [selectedRepo, setSelectedRepo] = useState(null); // 🔧 burada seçilen repo tutulur
  return (
    <div className="container">
      <div className="left-panel">
        <RepoForm />
      </div>
      <div className="right-panel">
        <LoadedRepos setSelectedRepo={setSelectedRepo} selectedRepo={selectedRepo} />
        <ModelButtons selectedRepo={selectedRepo} />
      </div>
    </div>
  );
}

export default App;
