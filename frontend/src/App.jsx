import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom';
import AuthPage from './components/auth/AuthPage';
import { authApi, clearAccessToken, setAccessToken } from './services/api';

const Dashboard = () => {
  const navigate = useNavigate();

  const handleLogout = async () => {
    await authApi.logout();
    clearAccessToken();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">Dashboard</h1>
        <p className="text-gray-500">Welcome to PFM! You are logged in.</p>
        <button
          onClick={handleLogout}
          className="mt-6 text-primary-600 hover:text-primary-700 font-medium"
        >
          Logout
        </button>
      </div>
    </div>
  );
};

const ProtectedRoute = ({ children, isLoading, isAuthenticated }) => {
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center text-gray-500">Checking authentication...</div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const response = await authApi.refresh();
        setAccessToken(response.data.accessToken);
        setIsAuthenticated(true);
      } catch {
        clearAccessToken();
        setIsAuthenticated(false);
      } finally {
        setIsLoading(false);
      }
    };

    initializeAuth();
  }, []);

  return (
    <Routes>
      <Route
        path="/login"
        element={
          isAuthenticated ? (
            <Navigate to="/dashboard" replace />
          ) : (
            <AuthPage onLoginSuccess={() => setIsAuthenticated(true)} />
          )
        }
      />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated} isLoading={isLoading}>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to={isAuthenticated ? '/dashboard' : '/login'} replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;