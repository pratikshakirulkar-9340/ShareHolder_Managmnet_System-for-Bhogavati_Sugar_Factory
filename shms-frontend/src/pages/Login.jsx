import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const { login, error } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitting(true);
    const ok = await login(username, password);
    setSubmitting(false);
    if (ok) navigate('/dashboard');
  };

  return (
    <div className="d-flex align-items-center justify-content-center bg-light" style={{ minHeight: '100vh' }}>
      <div className="card shadow-sm" style={{ width: 380 }}>
        <div className="card-body p-4">
          <h4 className="mb-1">Shareholder Management System</h4>
          <p className="text-secondary small mb-4">Bhogavati Sahakari Sakhar Karkhana</p>

          {error && <div className="alert alert-danger py-2">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="mb-3">
              <label className="form-label">Username</label>
              <input
                className="form-control"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Password</label>
              <input
                type="password"
                className="form-control"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary w-100" disabled={submitting}>
              {submitting ? 'Logging in...' : 'Login'}
            </button>
          </form>

          <p className="text-secondary small mt-3 mb-0">
            Default seeded admin: <code>admin</code> / <code>admin123</code>
          </p>
        </div>
      </div>
    </div>
  );
}
