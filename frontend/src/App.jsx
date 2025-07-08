import './App.css';
import RepoForm from './components/RepoForm';
import LoadedRepos from './components/LoadedRepos';
import ModelButtons from './components/ModelButtons';

function App() {
  return (
    <div className="container">
      <div className="left-panel">
        <RepoForm />
      </div>
      <div className="right-panel">
        <LoadedRepos />
        <ModelButtons />
      </div>
    </div>
  );
}

export default App;
