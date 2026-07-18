import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import AuthPage from './components/auth/AuthPage';

// Placeholder Dashboard - will be implemented separately
const Dashboard = () => (
  <div className="min-h-screen bg-gray-50 flex items-center justify-center">
    <div className="text-center">
      <h1 className="text-3xl font-bold text-gray-900 mb-4">Dashboard</h1>
      <p className="text-gray-500">Welcome to PFM! You are logged in.</p>
      <button
        onClick={() => {
          localStorage.clear();
          window.location.href = '/login';
        }}
        className="mt-6 text-primary-600 hover:text-primary-700 font-medium"
      >
        Logout
      </button>
    </div>
  </div>
);

// Protected Route component
const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem('accessToken');
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
};

function App() {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}

export default App;