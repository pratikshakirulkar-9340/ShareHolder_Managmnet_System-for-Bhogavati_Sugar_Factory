import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const links = [
  { to: '/dashboard', label: 'Dashboard', roles: ['ADMIN', 'STAFF'] },
  { to: '/shareholders', label: 'Shareholders', roles: ['ADMIN', 'STAFF'] },
  { to: '/allotments', label: 'Share Allotment', roles: ['ADMIN', 'STAFF'] },
  { to: '/sugar', label: 'Sugar Transactions', roles: ['ADMIN', 'STAFF'] },
  { to: '/sugar-allocation', label: 'Sugar Allocation', roles: ['ADMIN'] },
  { to: '/transfers', label: 'Share Transfers', roles: ['ADMIN', 'STAFF'] },
  { to: '/rates', label: 'Rates', roles: ['ADMIN'] },
  { to: '/audit-logs', label: 'Audit Logs', roles: ['ADMIN'] },
  { to: '/my-profile', label: 'My Profile', roles: ['SHAREHOLDER'] },
];

export default function Sidebar() {
  const { hasRole } = useAuth();

  return (
    <nav className="d-flex flex-column bg-dark text-white p-3" style={{ width: 220, minHeight: '100vh' }}>
      <h5 className="mb-4">SHMS</h5>
      {links
        .filter((l) => hasRole(...l.roles))
        .map((l) => (
          <NavLink
            key={l.to}
            to={l.to}
            className={({ isActive }) =>
              'nav-link text-white mb-1 rounded px-2 py-1 ' + (isActive ? 'bg-primary' : '')
            }
          >
            {l.label}
          </NavLink>
        ))}
    </nav>
  );
}
