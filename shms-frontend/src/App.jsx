import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import Layout from './components/Layout';

import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Shareholders from './pages/Shareholders';
import ShareholderDetail from './pages/ShareholderDetail';
import Allotments from './pages/Allotments';
import Sugar from './pages/Sugar';
import SugarAllocation from './pages/SugarAllocation';
import Transfers from './pages/Transfers';
import Rates from './pages/Rates';
import AuditLogs from './pages/AuditLogs';
import MyProfile from './pages/MyProfile';

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route
            element={
              <ProtectedRoute>
                <Layout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><Dashboard /></ProtectedRoute>} />
            <Route path="/shareholders" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><Shareholders /></ProtectedRoute>} />
            <Route path="/shareholders/:memberId" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><ShareholderDetail /></ProtectedRoute>} />
            <Route path="/allotments" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><Allotments /></ProtectedRoute>} />
            <Route path="/sugar" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><Sugar /></ProtectedRoute>} />
            <Route path="/sugar-allocation" element={<ProtectedRoute roles={['ADMIN']}><SugarAllocation /></ProtectedRoute>} />
            <Route path="/transfers" element={<ProtectedRoute roles={['ADMIN', 'STAFF']}><Transfers /></ProtectedRoute>} />
            <Route path="/rates" element={<ProtectedRoute roles={['ADMIN']}><Rates /></ProtectedRoute>} />
            <Route path="/audit-logs" element={<ProtectedRoute roles={['ADMIN']}><AuditLogs /></ProtectedRoute>} />
            <Route path="/my-profile" element={<ProtectedRoute roles={['SHAREHOLDER']}><MyProfile /></ProtectedRoute>} />
          </Route>

          <Route path="/" element={<Navigate to="/dashboard" replace />} />
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
