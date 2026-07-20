import React from 'react';
import { Menu, LogOut, User } from 'lucide-react';

const Header = ({ onMenuToggle, onLogout, userName }) => {
  return (
    <header className="h-16 bg-white border-b border-gray-200 flex items-center justify-between px-4 lg:px-6">
      {/* Left: Mobile menu toggle */}
      <button
        onClick={onMenuToggle}
        className="lg:hidden text-gray-500 hover:text-gray-700 p-2 -ml-2"
      >
        <Menu className="w-6 h-6" />
      </button>

      {/* Center/Spacer */}
      <div className="hidden lg:block" />

      {/* Right: User info + Logout */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <div className="w-8 h-8 bg-primary-100 rounded-full flex items-center justify-center">
            <User className="w-4 h-4 text-primary-600" />
          </div>
          <span className="hidden sm:inline font-medium">{userName || 'User'}</span>
        </div>
        <button
          onClick={onLogout}
          className="flex items-center gap-2 px-3 py-2 text-sm text-gray-500 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors duration-200"
          title="Logout"
        >
          <LogOut className="w-4 h-4" />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Header;