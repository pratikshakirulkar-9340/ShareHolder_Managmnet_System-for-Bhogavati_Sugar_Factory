import axiosClient from './axiosClient';

// --- Auth ---
export const login = (username, password) =>
  axiosClient.post('/auth/login', { username, password });
export const register = (payload) => axiosClient.post('/auth/register', payload);

// --- Shareholders ---
export const searchShareholders = (params) =>
  axiosClient.get('/shareholders/search', { params });
export const getShareholder = (memberId) => axiosClient.get(`/shareholders/${memberId}`);
export const createShareholder = (payload) => axiosClient.post('/shareholders', payload);
export const updateShareholder = (memberId, payload) =>
  axiosClient.put(`/shareholders/${memberId}`, payload);
export const deleteShareholder = (memberId) => axiosClient.delete(`/shareholders/${memberId}`);

// --- Rates ---
export const getCurrentRate = () => axiosClient.get('/rates/current');
export const getRateHistory = () => axiosClient.get('/rates');
export const createRate = (payload) => axiosClient.post('/rates', payload);

// --- Allotments ---
export const allotShares = (payload) => axiosClient.post('/allotments', payload);
export const getAllotmentsByMember = (memberId) =>
  axiosClient.get(`/allotments/member/${memberId}`);

// --- Share Sugar (Class A) ---
export const recordShareSugar = (payload) => axiosClient.post('/share-sugar', payload);
export const getShareSugarByMember = (memberId) =>
  axiosClient.get(`/share-sugar/member/${memberId}`);
export const getShareSugarByDateRange = (from, to) =>
  axiosClient.get('/share-sugar', { params: { from, to } });

// --- Tonnes Sugar (Class C) ---
export const recordTonnesSugar = (payload) => axiosClient.post('/tonnes-sugar', payload);
export const getTonnesSugarByMember = (memberId) =>
  axiosClient.get(`/tonnes-sugar/member/${memberId}`);
export const getTonnesSugarByDateRange = (from, to) =>
  axiosClient.get('/tonnes-sugar', { params: { from, to } });

// --- Sugar Allocation (bonus) ---
export const applySugarAllocation = (payload) => axiosClient.post('/sugar-allocations', payload);
export const getSugarAllocationHistory = () => axiosClient.get('/sugar-allocations');

// --- Transfers ---
export const requestTransfer = (payload) => axiosClient.post('/transfers', payload);
export const decideTransfer = (id, payload) =>
  axiosClient.put(`/transfers/${id}/decision`, payload);
export const getAllTransfers = () => axiosClient.get('/transfers');
export const getPendingTransfers = () => axiosClient.get('/transfers/pending');

// --- Documents ---
export const getDocumentsByMember = (memberId) =>
  axiosClient.get(`/documents/member/${memberId}`);
export const uploadDocument = (memberId, documentType, file) => {
  const formData = new FormData();
  formData.append('memberId', memberId);
  formData.append('documentType', documentType);
  formData.append('file', file);
  return axiosClient.post('/documents', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};
export const deleteDocument = (id) => axiosClient.delete(`/documents/${id}`);

// A plain <a href> can't attach the JWT Authorization header, so we fetch the file as a
// blob via axios (which does attach it) and trigger the browser's save dialog manually.
export const downloadDocument = async (id, fileName) => {
  const response = await axiosClient.get(`/documents/${id}/download`, { responseType: 'blob' });
  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', fileName || `document-${id}`);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

// --- Audit Log ---
export const getAuditLogs = (params) => axiosClient.get('/audit-logs', { params });

// --- Dashboard ---
export const getDashboardSummary = () => axiosClient.get('/dashboard/summary');
