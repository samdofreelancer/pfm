import React, { useState, useEffect } from 'react';
import { accountApi } from '../../services/api';

const AccountsPage = () => {
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [showForm, setShowForm] = useState(false);
  const [formData, setFormData] = useState({
    type: 'CASH',
    name: '',
    description: '',
    initialBalance: '0',
    currency: 'VND',
  });

  useEffect(() => {
    loadAccounts();
  }, []);

  const loadAccounts = async () => {
    try {
      // TODO: Get actual user ID from auth context
      const userId = 'demo-user-id';
      const response = await accountApi.getAccounts(userId);
      setAccounts(response.data);
    } catch (err) {
      setError('Failed to load accounts');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteAccount = async (accountId) => {
    if (!window.confirm('Are you sure you want to delete this account? This action cannot be undone.')) {
      return;
    }

    setError('');
    setSuccess('');

    try {
      const userId = 'demo-user-id';
      await accountApi.deleteAccount(accountId, userId);
      setSuccess('Account deleted successfully');
      loadAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete account');
    }
  };

  const handleCreateAccount = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    try {
      // TODO: Get actual user ID from auth context
      const userId = 'demo-user-id';
      await accountApi.createAccount({
        ...formData,
        userId,
        initialBalance: parseFloat(formData.initialBalance),
      });
      setSuccess('Account created successfully');
      setShowForm(false);
      setFormData({
        type: 'CASH',
        name: '',
        description: '',
        initialBalance: '0',
        currency: 'VND',
      });
      loadAccounts();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create account');
    }
  };

  const getAccountTypeLabel = (type) => {
    const labels = {
      CASH: 'Tiền mặt',
      BANK_ACCOUNT: 'Tài khoản ngân hàng',
      CREDIT_CARD: 'Thẻ tín dụng',
      DEBIT_CARD: 'Thẻ ghi nợ',
      E_WALLET: 'Ví điện tử',
    };
    return labels[type] || type;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64" data-testid="accounts-loading">
        <div className="text-gray-500">Loading...</div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6" data-testid="accounts-page">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900" data-testid="accounts-heading">Accounts</h1>
        <button
          onClick={() => setShowForm(!showForm)}
          data-testid="add-account-button"
          className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
        >
          {showForm ? 'Cancel' : 'Add Account'}
        </button>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg" data-testid="error-message">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-50 border border-green-200 text-green-700 px-4 py-3 rounded-lg" data-testid="success-message">
          {success}
        </div>
      )}

      {showForm && (
        <div className="bg-white rounded-xl border border-gray-200 p-6" data-testid="create-account-form">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Create New Account</h2>
          <form onSubmit={handleCreateAccount} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Account Type
              </label>
              <select
                value={formData.type}
                onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                data-testid="account-type-select"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
              >
                <option value="CASH">Tiền mặt</option>
                <option value="BANK_ACCOUNT">Tài khoản ngân hàng</option>
                <option value="CREDIT_CARD">Thẻ tín dụng</option>
                <option value="DEBIT_CARD">Thẻ ghi nợ</option>
                <option value="E_WALLET">Ví điện tử</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Account Name
              </label>
              <input
                type="text"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                data-testid="account-name-input"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="e.g., Vietcombank Account"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Description
              </label>
              <textarea
                value={formData.description}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                data-testid="account-description-input"
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                placeholder="Optional description"
                rows={3}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Initial Balance
                </label>
                <input
                  type="number"
                  step="0.01"
                  value={formData.initialBalance}
                  onChange={(e) => setFormData({ ...formData, initialBalance: e.target.value })}
                  data-testid="account-balance-input"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Currency
                </label>
                <select
                  value={formData.currency}
                  onChange={(e) => setFormData({ ...formData, currency: e.target.value })}
                  data-testid="account-currency-select"
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary-500"
                >
                  <option value="VND">VND</option>
                  <option value="USD">USD</option>
                  <option value="EUR">EUR</option>
                </select>
              </div>
            </div>

            <button
              type="submit"
              data-testid="create-account-submit"
              className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors"
            >
              Create Account
            </button>
          </form>
        </div>
      )}

      <div className="bg-white rounded-xl border border-gray-200 p-6" data-testid="accounts-list">
        <h2 className="text-xl font-semibold text-gray-900 mb-4">Your Accounts</h2>
        {accounts.length === 0 ? (
          <p className="text-gray-500" data-testid="no-accounts-message">No accounts yet. Create your first account to get started.</p>
        ) : (
          <div className="space-y-3" data-testid="accounts-items">
            {accounts.map((account) => (
              <div
                key={account.id}
                data-testid={`account-item-${account.id}`}
                className="flex items-center justify-between p-4 bg-gray-50 rounded-lg"
              >
                <div>
                  <h3 className="font-medium text-gray-900" data-testid={`account-name-${account.id}`}>{account.name}</h3>
                  <p className="text-sm text-gray-500" data-testid={`account-type-${account.id}`}>
                    {getAccountTypeLabel(account.type)} • {account.currency}
                  </p>
                </div>
                <div className="flex items-center gap-4">
                  <div className="text-right">
                    <p className="font-semibold text-gray-900" data-testid={`account-balance-${account.id}`}>
                      {account.balance?.toLocaleString()} {account.currency}
                    </p>
                    <p className="text-sm text-gray-500" data-testid={`account-status-${account.id}`}>
                      {account.isActive ? 'Active' : 'Inactive'}
                    </p>
                  </div>
                  <button
                    onClick={() => handleDeleteAccount(account.id)}
                    data-testid={`delete-account-${account.id}`}
                    className="px-3 py-1 text-sm text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                    title="Delete account"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AccountsPage;