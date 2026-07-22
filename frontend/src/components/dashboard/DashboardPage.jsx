import React from 'react';
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  PiggyBank,
  Plus,
  ArrowRight,
  Eye,
  EyeOff,
  Banknote,
  Landmark,
  CreditCard,
  MoreHorizontal,
} from 'lucide-react';

// Mock data — will be replaced with API later
const accounts = [
  { id: 1, name: 'Cash', balance: 5000000, icon: Banknote, color: 'bg-emerald-500' },
  { id: 2, name: 'VPBank', balance: 15000000, icon: Landmark, color: 'bg-blue-500' },
  { id: 3, name: 'VISA Credit', balance: -2000000, icon: CreditCard, color: 'bg-red-500' },
];

const formatVND = (amount) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(amount);
};

const DashboardPage = () => {
  const [showBalance, setShowBalance] = React.useState(true);

  const totalBalance = accounts.reduce((sum, acc) => sum + acc.balance, 0);
  const totalIncome = 0;
  const totalExpense = 0;
  const diff = totalIncome - totalExpense;

  return (
    <div className="p-4 lg:p-6 space-y-5">
      {/* Total Balance */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 rounded-2xl p-6 text-white">
        <div className="flex items-center justify-between mb-2">
          <span className="text-white/80 text-sm font-medium">Total Balance</span>
          <button
            onClick={() => setShowBalance(!showBalance)}
            className="text-white/60 hover:text-white transition-colors"
          >
            {showBalance ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
          </button>
        </div>
        <div className="text-3xl font-bold mb-4">
          {showBalance ? formatVND(totalBalance) : '••••••'}
        </div>
        <div className="flex items-center gap-4 text-sm text-white/80">
          <div className="flex items-center gap-1.5">
            <TrendingUp className="w-4 h-4 text-emerald-300" />
            <span>Income: {showBalance ? formatVND(totalIncome) : '•••'}</span>
          </div>
          <div className="flex items-center gap-1.5">
            <TrendingDown className="w-4 h-4 text-red-300" />
            <span>Expense: {showBalance ? formatVND(totalExpense) : '•••'}</span>
          </div>
        </div>
      </div>

      {/* Grid: Left column (2/3) + Right column (1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">

        {/* === LEFT COLUMN (2/3) === */}
        <div className="lg:col-span-2 space-y-5">

          {/* Income/Expense Overview */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-semibold text-gray-800">Income & Expense Overview</h3>
              <span className="text-xs text-gray-400">This month</span>
            </div>
            <div className="flex items-center justify-around">
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Income</div>
                <div className="text-lg font-bold text-emerald-600">{formatVND(totalIncome)}</div>
              </div>
              <div className="h-12 w-px bg-gray-100" />
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Expense</div>
                <div className="text-lg font-bold text-red-600">{formatVND(totalExpense)}</div>
              </div>
              <div className="h-12 w-px bg-gray-100" />
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Difference</div>
                <div className={`text-lg font-bold ${diff >= 0 ? 'text-emerald-600' : 'text-red-600'}`}>
                  {formatVND(diff)}
                </div>
              </div>
            </div>
          </div>

          {/* Monthly Calendar + Income/Expense */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            {/* Monthly Spending Calendar */}
            <div className="bg-white rounded-xl border border-gray-100 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Monthly Spending Calendar</h3>
              <div className="text-center py-8">
                <PiggyBank className="w-8 h-8 text-gray-300 mx-auto mb-2" />
                <p className="text-xs text-gray-400">No data</p>
              </div>
            </div>

            {/* Income & Expense - Pie chart placeholder */}
            <div className="bg-white rounded-xl border border-gray-100 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Income & Expense</h3>
              <div className="text-center py-8">
                <div className="w-20 h-20 mx-auto rounded-full border-4 border-gray-100 flex items-center justify-center mb-2">
                  <Wallet className="w-6 h-6 text-gray-300" />
                </div>
                <p className="text-xs text-gray-400">No data</p>
              </div>
            </div>
          </div>

          {/* Recent Records */}
          <div className="bg-white rounded-xl border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
              <h3 className="text-sm font-semibold text-gray-800">Recent Records</h3>
              <button className="text-xs font-medium text-primary-600 hover:text-primary-700">
                View all →
              </button>
            </div>
            <div className="p-8 text-center">
              <div className="w-10 h-10 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-2">
                <Wallet className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-sm text-gray-500">No transactions yet</p>
              <p className="text-xs text-gray-400 mt-0.5">Start recording your first transaction</p>
              <button className="mt-3 inline-flex items-center gap-1 px-3 py-1.5 bg-gray-900 text-white text-xs font-medium rounded-lg hover:bg-gray-800 transition-colors">
                <Plus className="w-3.5 h-3.5" />
                Add transaction
              </button>
            </div>
          </div>
        </div>

        {/* === RIGHT COLUMN (1/3) === */}
        <div className="space-y-5">

          {/* Account List */}
          <div className="bg-white rounded-xl border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
              <h3 className="text-sm font-semibold text-gray-800">Accounts</h3>
              <button className="text-xs font-medium text-primary-600 hover:text-primary-700">
                + Add
              </button>
            </div>
            <div className="divide-y divide-gray-50">
              {accounts.map((acc) => (
                <div key={acc.id} className="flex items-center gap-3 px-5 py-3.5 hover:bg-gray-50 transition-colors">
                  <div className={`w-8 h-8 rounded-lg ${acc.color} flex items-center justify-center`}>
                    <acc.icon className="w-4 h-4 text-white" />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-800 truncate">{acc.name}</p>
                    <p className={`text-xs font-semibold ${acc.balance >= 0 ? 'text-gray-600' : 'text-red-500'}`}>
                      {formatVND(acc.balance)}
                    </p>
                  </div>
                  <button className="text-gray-300 hover:text-gray-500">
                    <MoreHorizontal className="w-4 h-4" />
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Income */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-gray-800">Income</h3>
              <TrendingUp className="w-4 h-4 text-emerald-500" />
            </div>
            <p className="text-lg font-bold text-emerald-600">{formatVND(0)}</p>
            <p className="text-xs text-gray-400 mt-0.5">This month</p>
          </div>

          {/* Expense */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-gray-800">Expense</h3>
              <TrendingDown className="w-4 h-4 text-red-500" />
            </div>
            <p className="text-lg font-bold text-red-600">{formatVND(0)}</p>
            <p className="text-xs text-gray-400 mt-0.5">This month</p>
          </div>

        </div>
      </div>
    </div>
  );
};

export default DashboardPage;