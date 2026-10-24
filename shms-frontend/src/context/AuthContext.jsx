import { createContext, useContext, useState, useCallback } from 'react';
import { login as loginApi } from '../api/api';

const AuthContext = createContext(null);

function readStoredUser() {
  const raw = localStorage.getItem('user');
  return raw ? JSON.parse(raw) : null;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(readStoredUser());
  const [error, setError] = useState(null);

  const login = useCallback(async (username, password) => {
    setError(null);
    try {
      const { data } = await loginApi(username, password);
      const { accessToken, refreshToken, userId, username: uname, roles } = data.data;

      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      const userObj = { userId, username: uname, roles };
      localStorage.setItem('user', JSON.stringify(userObj));
      setUser(userObj);
      return true;
    } catch (err) {
      setError(err.response?.data?.message || 'Login failed');
      return false;
    }
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    setUser(null);
  }, []);

  const hasRole = useCallback(
    (...roles) => !!user && roles.some((r) => user.roles.includes(r)),
    [user]
  );

  return (
    <AuthContext.Provider value={{ user, login, logout, hasRole, error }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
