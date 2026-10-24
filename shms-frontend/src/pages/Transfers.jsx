import { useEffect, useState } from 'react';
import { getAllTransfers, requestTransfer, decideTransfer } from '../api/api';
import { useAuth } from '../context/AuthContext';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

export default function Transfers() {
  const { hasRole } = useAuth();
  const [transfers, setTransfers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    fromMemberId: '', toMemberId: '', nomineeName: '', reasonType: 'DEATH', reasonDetail: '', transferDate: '',
  });
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState(null);

  const load = () => {
    setLoading(true);
    getAllTransfers().then((res) => setTransfers(res.data.data)).catch(setError).finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true); setSaveError(null);
    try {
      const payload = { ...form };
      if (!payload.toMemberId) delete payload.toMemberId;
      await requestTransfer(payload);
      setShowForm(false);
      setForm({ fromMemberId: '', toMemberId: '', nomineeName: '', reasonType: 'DEATH', reasonDetail: '', transferDate: '' });
      load();
    } catch (err) {
      setSaveError(err);
    } finally {
      setSaving(false);
    }
  };

  const handleDecision = async (id, decision) => {
    if (!window.confirm(`${decision} this transfer?`)) return;
    await decideTransfer(id, { decision });
    load();
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0">Share Transfers</h3>
        <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>
          {showForm ? 'Cancel' : '+ Request Transfer'}
        </button>
      </div>

      {showForm && (
        <div className="card mb-4"><div className="card-body">
          <ErrorAlert error={saveError} />
          <form onSubmit={handleSubmit}>
            <div className="row g-2">
              <div className="col-md-3">
                <input className="form-control" placeholder="From Member ID *" required value={form.fromMemberId}
                  onChange={(e) => setForm({ ...form, fromMemberId: e.target.value })} />
              </div>
              <div className="col-md-3">
                <input className="form-control" placeholder="To Member ID (optional)" value={form.toMemberId}
                  onChange={(e) => setForm({ ...form, toMemberId: e.target.value })} />
              </div>
              <div className="col-md-3">
                <select className="form-select" value={form.reasonType}
                  onChange={(e) => setForm({ ...form, reasonType: e.target.value })}>
                  <option value="DEATH">Death</option>
                  <option value="SALE">Sale</option>
                  <option value="GIFT">Gift</option>
                </select>
              </div>
              <div className="col-md-3">
                <input type="date" className="form-control" required value={form.transferDate}
                  onChange={(e) => setForm({ ...form, transferDate: e.target.value })} />
              </div>
              <div className="col-md-6">
                <input className="form-control" placeholder="Nominee Name" value={form.nomineeName}
                  onChange={(e) => setForm({ ...form, nomineeName: e.target.value })} />
              </div>
              <div className="col-md-6">
                <input className="form-control" placeholder="Reason Detail" value={form.reasonDetail}
                  onChange={(e) => setForm({ ...form, reasonDetail: e.target.value })} />
              </div>
            </div>
            <button className="btn btn-success mt-3" disabled={saving}>{saving ? 'Submitting...' : 'Request Transfer'}</button>
          </form>
        </div></div>
      )}

      <ErrorAlert error={error} />
      {loading ? <Loading /> : (
        <table className="table table-hover bg-white">
          <thead>
            <tr><th>From</th><th>To</th><th>Reason</th><th>Date</th><th>Status</th>{hasRole('ADMIN') && <th>Action</th>}</tr>
          </thead>
          <tbody>
            {transfers.map((t) => (
              <tr key={t.id}>
                <td>{t.fromMemberId} <span className="text-secondary">({t.fromMemberName})</span></td>
                <td>{t.toMemberId || '-'}</td>
                <td>{t.reasonType}</td>
                <td>{t.transferDate}</td>
                <td>
                  <span className={`badge bg-${t.status === 'PENDING' ? 'warning' : t.status === 'APPROVED' ? 'success' : 'danger'}`}>
                    {t.status}
                  </span>
                </td>
                {hasRole('ADMIN') && (
                  <td>
                    {t.status === 'PENDING' && (
                      <>
                        <button className="btn btn-sm btn-success me-2" onClick={() => handleDecision(t.id, 'APPROVED')}>Approve</button>
                        <button className="btn btn-sm btn-danger" onClick={() => handleDecision(t.id, 'REJECTED')}>Reject</button>
                      </>
                    )}
                  </td>
                )}
              </tr>
            ))}
            {transfers.length === 0 && <tr><td colSpan={6} className="text-center text-secondary py-4">No transfers yet</td></tr>}
          </tbody>
        </table>
      )}
    </div>
  );
}
