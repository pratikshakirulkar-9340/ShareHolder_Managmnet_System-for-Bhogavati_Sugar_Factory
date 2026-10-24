import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Topbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="d-flex justify-content-between align-items-center bg-white border-bottom px-4 py-2">
      <div className="fw-semibold text-secondary">Bhogavati Sugar Factory - Shareholder Management</div>
      <div className="d-flex align-items-center gap-3">
        <span className="text-secondary small">
          {user?.username} <span className="badge bg-secondary ms-1">{user?.roles?.join(', ')}</span>
        </span>
        <button className="btn btn-outline-danger btn-sm" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </div>
  );
}
