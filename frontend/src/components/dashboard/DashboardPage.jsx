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

// Mock data — sẽ thay bằng API sau
const accounts = [
  { id: 1, name: 'Tiền mặt', balance: 5000000, icon: Banknote, color: 'bg-emerald-500' },
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
      {/* Tổng số dư */}
      <div className="bg-gradient-to-r from-primary-600 to-primary-800 rounded-2xl p-6 text-white">
        <div className="flex items-center justify-between mb-2">
          <span className="text-white/80 text-sm font-medium">Tổng số dư</span>
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
            <span>Thu: {showBalance ? formatVND(totalIncome) : '•••'}</span>
          </div>
          <div className="flex items-center gap-1.5">
            <TrendingDown className="w-4 h-4 text-red-300" />
            <span>Chi: {showBalance ? formatVND(totalExpense) : '•••'}</span>
          </div>
        </div>
      </div>

      {/* Grid: Left column (2/3) + Right column (1/3) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">

        {/* === LEFT COLUMN (2/3) === */}
        <div className="lg:col-span-2 space-y-5">

          {/* Tổng quan thu/chi chênh lệch */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-sm font-semibold text-gray-800">Tổng quan thu chi</h3>
              <span className="text-xs text-gray-400">Tháng này</span>
            </div>
            <div className="flex items-center justify-around">
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Thu</div>
                <div className="text-lg font-bold text-emerald-600">{formatVND(totalIncome)}</div>
              </div>
              <div className="h-12 w-px bg-gray-100" />
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Chi</div>
                <div className="text-lg font-bold text-red-600">{formatVND(totalExpense)}</div>
              </div>
              <div className="h-12 w-px bg-gray-100" />
              <div className="text-center">
                <div className="text-xs text-gray-500 mb-1">Chênh lệch</div>
                <div className={`text-lg font-bold ${diff >= 0 ? 'text-emerald-600' : 'text-red-600'}`}>
                  {formatVND(diff)}
                </div>
              </div>
            </div>
          </div>

          {/* Lịch chi tiêu tháng + Tình hình thu chi */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            {/* Lịch chi tiêu tháng */}
            <div className="bg-white rounded-xl border border-gray-100 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Lịch chi tiêu tháng</h3>
              <div className="text-center py-8">
                <PiggyBank className="w-8 h-8 text-gray-300 mx-auto mb-2" />
                <p className="text-xs text-gray-400">Chưa có dữ liệu</p>
              </div>
            </div>

            {/* Tình hình thu chi - Pie chart thay thế */}
            <div className="bg-white rounded-xl border border-gray-100 p-5">
              <h3 className="text-sm font-semibold text-gray-800 mb-4">Tình hình thu chi</h3>
              <div className="text-center py-8">
                <div className="w-20 h-20 mx-auto rounded-full border-4 border-gray-100 flex items-center justify-center mb-2">
                  <Wallet className="w-6 h-6 text-gray-300" />
                </div>
                <p className="text-xs text-gray-400">Chưa có dữ liệu</p>
              </div>
            </div>
          </div>

          {/* Ghi chép gần đây */}
          <div className="bg-white rounded-xl border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
              <h3 className="text-sm font-semibold text-gray-800">Ghi chép gần đây</h3>
              <button className="text-xs font-medium text-primary-600 hover:text-primary-700">
                Xem tất cả →
              </button>
            </div>
            <div className="p-8 text-center">
              <div className="w-10 h-10 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-2">
                <Wallet className="w-5 h-5 text-gray-400" />
              </div>
              <p className="text-sm text-gray-500">Chưa có giao dịch</p>
              <p className="text-xs text-gray-400 mt-0.5">Bắt đầu ghi chép giao dịch đầu tiên</p>
              <button className="mt-3 inline-flex items-center gap-1 px-3 py-1.5 bg-gray-900 text-white text-xs font-medium rounded-lg hover:bg-gray-800 transition-colors">
                <Plus className="w-3.5 h-3.5" />
                Thêm giao dịch
              </button>
            </div>
          </div>
        </div>

        {/* === RIGHT COLUMN (1/3) === */}
        <div className="space-y-5">

          {/* Danh sách tài khoản */}
          <div className="bg-white rounded-xl border border-gray-100">
            <div className="flex items-center justify-between px-5 py-4 border-b border-gray-50">
              <h3 className="text-sm font-semibold text-gray-800">Tài khoản</h3>
              <button className="text-xs font-medium text-primary-600 hover:text-primary-700">
                + Thêm
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

          {/* Thu tiền */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-gray-800">Thu tiền</h3>
              <TrendingUp className="w-4 h-4 text-emerald-500" />
            </div>
            <p className="text-lg font-bold text-emerald-600">{formatVND(0)}</p>
            <p className="text-xs text-gray-400 mt-0.5">Tháng này</p>
          </div>

          {/* Chi tiền */}
          <div className="bg-white rounded-xl border border-gray-100 p-5">
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-gray-800">Chi tiền</h3>
              <TrendingDown className="w-4 h-4 text-red-500" />
            </div>
            <p className="text-lg font-bold text-red-600">{formatVND(0)}</p>
            <p className="text-xs text-gray-400 mt-0.5">Tháng này</p>
          </div>

        </div>
      </div>
    </div>
  );
};

export default DashboardPage;