import { useState } from 'react';
import { useAuth } from '../context/AuthContext';

/**
 * Self-service view for SHAREHOLDER-role logins. We don't have the shareholder's
 * memberId directly on the JWT, so this relies on the backend eventually exposing
 * a "my shareholder record" endpoint; for now it's a placeholder pointing that out.
 */
export default function MyProfile() {
  const { user } = useAuth();
  const [error] = useState(
    'Self-service lookup by logged-in user isn\u2019t wired up on the backend yet (no GET /api/shareholders/me endpoint). Ask an admin for your Member ID and view it via the Shareholders search for now.'
  );

  return (
    <div>
      <h3 className="mb-4">My Profile</h3>
      <div className="alert alert-info">{error}</div>
      <p className="text-secondary">Logged in as: {user?.username}</p>
    </div>
  );
}
