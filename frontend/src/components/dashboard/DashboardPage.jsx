import React from 'react';
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  PiggyBank,
  ArrowRight,
  Plus,
} from 'lucide-react';

const summaryCards = [
  {
    title: 'Total Balance',
    amount: '$0.00',
    icon: Wallet,
    color: 'bg-blue-500',
    bgLight: 'bg-blue-50',
    textColor: 'text-blue-600',
  },
  {
    title: 'Income',
    amount: '$0.00',
    icon: TrendingUp,
    color: 'bg-green-500',
    bgLight: 'bg-green-50',
    textColor: 'text-green-600',
  },
  {
    title: 'Expenses',
    amount: '$0.00',
    icon: TrendingDown,
    color: 'bg-red-500',
    bgLight: 'bg-red-50',
    textColor: 'text-red-600',
  },
  {
    title: 'Savings',
    amount: '$0.00',
    icon: PiggyBank,
    color: 'bg-purple-500',
    bgLight: 'bg-purple-50',
    textColor: 'text-purple-600',
  },
];

const recentTransactions = [
  // Placeholder — will be populated when transactions feature is built
];

const DashboardPage = () => {
  return (
    <div className="p-4 lg:p-6 space-y-6">
      {/* Page Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-sm text-gray-500 mt-1">
          Welcome back! Here's your financial overview.
        </p>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        {summaryCards.map((card) => (
          <div
            key={card.title}
            className="bg-white rounded-xl border border-gray-200 p-5 hover:shadow-md transition-shadow duration-200"
          >
            <div className="flex items-center justify-between mb-4">
              <div className={`p-2.5 rounded-lg ${card.bgLight}`}>
                <card.icon className={`w-5 h-5 ${card.textColor}`} />
              </div>
            </div>
            <p className="text-sm text-gray-500">{card.title}</p>
            <p className="text-2xl font-bold text-gray-900 mt-1">
              {card.amount}
            </p>
          </div>
        ))}
      </div>

      {/* Recent Transactions */}
      <div className="bg-white rounded-xl border border-gray-200">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <h2 className="text-lg font-semibold text-gray-900">
            Recent Transactions
          </h2>
          <button className="flex items-center gap-1 text-sm text-primary-600 hover:text-primary-700 font-medium">
            View All
            <ArrowRight className="w-4 h-4" />
          </button>
        </div>

        {recentTransactions.length === 0 ? (
          <div className="p-8 text-center">
            <div className="w-12 h-12 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-3">
              <Wallet className="w-6 h-6 text-gray-400" />
            </div>
            <p className="text-gray-500 text-sm">No transactions yet</p>
            <p className="text-gray-400 text-xs mt-1">
              Start by adding your first transaction
            </p>
            <button className="mt-4 inline-flex items-center gap-2 px-4 py-2 bg-primary-600 text-white text-sm font-medium rounded-lg hover:bg-primary-700 transition-colors duration-200">
              <Plus className="w-4 h-4" />
              Add Transaction
            </button>
          </div>
        ) : (
          <div className="divide-y divide-gray-100">
            {recentTransactions.map((tx, idx) => (
              <div key={idx} className="p-4">
                {/* Transaction row — to be implemented */}
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Quick Stats Row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Budget Overview */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">
              Budget Overview
            </h2>
            <PiggyBank className="w-5 h-5 text-gray-400" />
          </div>
          <p className="text-gray-500 text-sm text-center py-6">
            No budgets set up yet
          </p>
        </div>

        {/* Goals Progress */}
        <div className="bg-white rounded-xl border border-gray-200 p-5">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">
              Savings Goals
            </h2>
            <TargetIcon className="w-5 h-5 text-gray-400" />
          </div>
          <p className="text-gray-500 text-sm text-center py-6">
            No goals set up yet
          </p>
        </div>
      </div>
    </div>
  );
};

// Inline Target icon since we already use it in Sidebar
const TargetIcon = ({ className }) => (
  <svg
    xmlns="http://www.w3.org/2000/svg"
    width="20"
    height="20"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="2"
    strokeLinecap="round"
    strokeLinejoin="round"
    className={className}
  >
    <circle cx="12" cy="12" r="10" />
    <circle cx="12" cy="12" r="6" />
    <circle cx="12" cy="12" r="2" />
  </svg>
);

export default DashboardPage;