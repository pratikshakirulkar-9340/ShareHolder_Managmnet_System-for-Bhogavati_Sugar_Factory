import { useState } from 'react';
import { recordShareSugar, recordTonnesSugar } from '../api/api';
import ErrorAlert from '../components/ErrorAlert';

export default function Sugar() {
  const [tab, setTab] = useState('A');

  return (
    <div>
      <h3 className="mb-4">Sugar Transactions</h3>
      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button className={`nav-link ${tab === 'A' ? 'active' : ''}`} onClick={() => setTab('A')}>
            Share Sugar (Class A)
          </button>
        </li>
        <li className="nav-item">
          <button className={`nav-link ${tab === 'C' ? 'active' : ''}`} onClick={() => setTab('C')}>
            Tonnes Sugar (Class C)
          </button>
        </li>
      </ul>
      {tab === 'A' ? <ShareSugarForm /> : <TonnesSugarForm />}
    </div>
  );
}

function ShareSugarForm() {
  const [form, setForm] = useState({ memberId: '', suppliedSugar: '', suppliedDate: '' });
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setSaving(true); setError(null); setResult(null);
    try {
      const { data } = await recordShareSugar({ ...form, suppliedSugar: Number(form.suppliedSugar) });
      setResult(data.data);
      setForm({ memberId: form.memberId, suppliedSugar: '', suppliedDate: '' });
    } catch (err) {
      setError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="row">
      <div className="col-md-6">
        <div className="card"><div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={submit}>
            <div className="mb-3">
              <label className="form-label">Member ID (Class A) *</label>
              <input className="form-control" required value={form.memberId}
                onChange={(e) => setForm({ ...form, memberId: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Supplied Sugar (kg) *</label>
              <input type="number" step="0.01" className="form-control" required value={form.suppliedSugar}
                onChange={(e) => setForm({ ...form, suppliedSugar: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Supplied Date *</label>
              <input type="date" className="form-control" required value={form.suppliedDate}
                onChange={(e) => setForm({ ...form, suppliedDate: e.target.value })} />
            </div>
            <button className="btn btn-primary" disabled={saving}>{saving ? 'Recording...' : 'Record Supply'}</button>
          </form>
        </div></div>
      </div>
      <div className="col-md-6">
        {result && (
          <div className="card border-success"><div className="card-body">
            <h6 className="text-success">Recorded</h6>
            <p className="mb-1"><strong>Entitlement:</strong> {result.entitlement} kg</p>
            <p className="mb-1"><strong>Previous Remaining:</strong> {result.previousRemaining} kg</p>
            <p className="mb-1"><strong>Remaining Now:</strong> {result.remainingSugar} kg</p>
            <p className="mb-0"><strong>Amount:</strong> ₹{result.totalAmount}</p>
          </div></div>
        )}
      </div>
    </div>
  );
}

function TonnesSugarForm() {
  const [form, setForm] = useState({ memberId: '', totalTonnes: '', suppliedSugar: '', suppliedDate: '' });
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setSaving(true); setError(null); setResult(null);
    try {
      const { data } = await recordTonnesSugar({
        ...form, totalTonnes: Number(form.totalTonnes), suppliedSugar: Number(form.suppliedSugar),
      });
      setResult(data.data);
      setForm({ memberId: form.memberId, totalTonnes: '', suppliedSugar: '', suppliedDate: '' });
    } catch (err) {
      setError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="row">
      <div className="col-md-6">
        <div className="card"><div className="card-body">
          <ErrorAlert error={error} />
          <form onSubmit={submit}>
            <div className="mb-3">
              <label className="form-label">Member ID (Class C) *</label>
              <input className="form-control" required value={form.memberId}
                onChange={(e) => setForm({ ...form, memberId: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Total Tonnes *</label>
              <input type="number" step="0.01" className="form-control" required value={form.totalTonnes}
                onChange={(e) => setForm({ ...form, totalTonnes: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Supplied Sugar (kg) *</label>
              <input type="number" step="0.01" className="form-control" required value={form.suppliedSugar}
                onChange={(e) => setForm({ ...form, suppliedSugar: e.target.value })} />
            </div>
            <div className="mb-3">
              <label className="form-label">Supplied Date *</label>
              <input type="date" className="form-control" required value={form.suppliedDate}
                onChange={(e) => setForm({ ...form, suppliedDate: e.target.value })} />
            </div>
            <button className="btn btn-primary" disabled={saving}>{saving ? 'Recording...' : 'Record Supply'}</button>
          </form>
        </div></div>
      </div>
      <div className="col-md-6">
        {result && (
          <div className="card border-success"><div className="card-body">
            <h6 className="text-success">Recorded</h6>
            <p className="mb-1"><strong>Entitlement:</strong> {result.entitlement} kg</p>
            <p className="mb-1"><strong>Previous Remaining:</strong> {result.previousRemaining} kg</p>
            <p className="mb-1"><strong>Remaining Now:</strong> {result.remainingSugar} kg</p>
            <p className="mb-0"><strong>Amount:</strong> ₹{result.amount}</p>
          </div></div>
        )}
      </div>
    </div>
  );
}
