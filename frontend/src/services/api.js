import axios from 'axios';

const API_BASE_URL = '/api/v1';

let accessToken = null;
let isRefreshing = false;
let refreshSubscribers = [];

const subscribeTokenRefresh = (cb) => {
  refreshSubscribers.push(cb);
};

const onRefreshed = (token) => {
  refreshSubscribers.forEach((cb) => cb(token));
  refreshSubscribers = [];
};

export const setAccessToken = (token) => {
  accessToken = token;
};

export const clearAccessToken = () => {
  accessToken = null;
};

export const getAccessToken = () => accessToken;

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

// Dedicated client for refresh calls to avoid interceptor recursion
const refreshClient = axios.create({ baseURL: API_BASE_URL, withCredentials: true });

api.interceptors.request.use(
  (config) => {
    const token = getAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config || {};

    // Debug: log when a response error is handled
    try {
      // eslint-disable-next-line no-console
      console.log('[api] response error for', originalRequest.method, originalRequest.url, 'status', error.response?.status);
    } catch (e) {}

    // Don't attempt to refresh when the failed request is the refresh endpoint
    // or when caller explicitly asked to skip refresh handling (e.g. logout)
    // or when the request is to auth endpoints (login, register) that may return 401 for invalid credentials
    const requestUrl = originalRequest.url || '';
    const skipRefresh = originalRequest.headers && (originalRequest.headers['x-skip-refresh'] || originalRequest.headers['X-Skip-Refresh']);
    const isAuthEndpoint = requestUrl.includes('/auth/login') || requestUrl.includes('/auth/register');
    if (requestUrl.includes('/auth/refresh') || skipRefresh || isAuthEndpoint) {
      try {
        // eslint-disable-next-line no-console
        console.log('[api] skipping refresh for', originalRequest.url, 'skipRefresh=', !!skipRefresh, 'isAuthEndpoint=', isAuthEndpoint);
      } catch (e) {}
      return Promise.reject(error);
    }

    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      if (isRefreshing) {
        // If a refresh is already in progress, queue the request
        return new Promise((resolve, reject) => {
          subscribeTokenRefresh((token) => {
            if (!token) return reject(error);
            originalRequest.headers.Authorization = `Bearer ${token}`;
            resolve(api(originalRequest));
          });
        });
      }

      isRefreshing = true;

      try {
        try {
          // eslint-disable-next-line no-console
          console.log('[api] performing token refresh triggered by', originalRequest.url);
        } catch (e) {}
        // Use the dedicated refresh client (no interceptors)
        const response = await refreshClient.post('/auth/refresh', {});

        const { accessToken: newAccessToken } = response.data;
        setAccessToken(newAccessToken);
        onRefreshed(newAccessToken);
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        // notify queued requests about failure
        onRefreshed(null);
        clearAccessToken();
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials, { withCredentials: true }),
  register: (data) => api.post('/auth/register', data, { withCredentials: true }),
  refresh: () => refreshClient.post('/auth/refresh', {}, { withCredentials: true }),
  // add x-skip-refresh header so logout won't trigger refresh retry logic
  logout: () => {
    const headers = { 'x-skip-refresh': '1' };
    try {
      // eslint-disable-next-line no-console
      console.log('[authApi] logout: sending headers', headers);
    } catch (e) {}
    return api.post('/auth/logout', {}, { withCredentials: true, headers });
  },
  getProfile: () => api.get('/auth/profile', { withCredentials: true }),
  updateProfile: (data) => api.put('/auth/profile', data, { withCredentials: true }),
  changePassword: (data) => api.post('/auth/change-password', data, { withCredentials: true }),
  deleteUser: (email) => api.delete('/auth/users', { data: { email }, withCredentials: true }),
};

export default api;