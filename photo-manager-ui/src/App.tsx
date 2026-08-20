import './App.css';
import { Navigate, Route, Routes } from 'react-router-dom';
import DashboardPage from './features/dashboard/components/DashboardPage';
import DuplicatesPage from './features/duplicates/components/DuplicatesPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<DashboardPage />} />
      <Route path="/duplicates" element={<DuplicatesPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
