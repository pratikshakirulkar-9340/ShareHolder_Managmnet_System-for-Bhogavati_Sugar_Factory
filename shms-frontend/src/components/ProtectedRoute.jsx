import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

/**
 * Wraps a route so it requires login, and optionally a specific role.
 * Usage: <ProtectedRoute roles={['ADMIN']}><SomePage /></ProtectedRoute>
 */
export default function ProtectedRoute({ children, roles }) {
  const { user, hasRole } = useAuth();

  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (roles && roles.length > 0 && !hasRole(...roles)) {
    return (
      <div className="alert alert-danger m-4">
        You don't have permission to view this page.
      </div>
    );
  }
  return children;
}
