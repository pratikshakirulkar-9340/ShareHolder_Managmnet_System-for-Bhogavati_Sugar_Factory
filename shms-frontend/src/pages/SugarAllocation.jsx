import { useEffect, useState } from 'react';
import { getSugarAllocationHistory, applySugarAllocation } from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

export default function SugarAllocation() {
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState({ bonusAmount: '', allocationYear: new Date().getFullYear() });
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState(null);
  const [result, setResult] = useState(null);

  const load = () => {
    setLoading(true);
    getSugarAllocationHistory().then((res) => setHistory(res.data.data)).catch(setError).finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!window.confirm('This applies a bonus to EVERY Class A shareholder. Continue?')) return;
    setSaving(true); setSaveError(null); setResult(null);
    try {
      const { data } = await applySugarAllocation({
        bonusAmount: Number(form.bonusAmount), allocationYear: Number(form.allocationYear),
      });
      setResult(data.data);
      load();
    } catch (err) {
      setSaveError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4">Sugar Allocation (Bonus)</h3>
      <p className="text-secondary">
        Applies a one-time bonus top-up to every Class A shareholder's remaining sugar balance.
      </p>
      <div className="row">
        <div className="col-md-5">
          <div className="card"><div className="card-body">
            <ErrorAlert error={saveError} />
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label className="form-label">Bonus Amount (kg) *</label>
                <input type="number" step="0.01" className="form-control" required value={form.bonusAmount}
                  onChange={(e) => setForm({ ...form, bonusAmount: e.target.value })} />
              </div>
              <div className="mb-3">
                <label className="form-label">Allocation Year *</label>
                <input type="number" className="form-control" required value={form.allocationYear}
                  onChange={(e) => setForm({ ...form, allocationYear: e.target.value })} />
              </div>
              <button className="btn btn-danger" disabled={saving}>{saving ? 'Applying...' : 'Apply Bonus to All Class A'}</button>
            </form>
            {result && (
              <div className="alert alert-success mt-3 mb-0">
                Applied {result.bonusAmount} kg bonus to {result.affectedShareholderCount} shareholders.
              </div>
            )}
          </div></div>
        </div>
        <div className="col-md-7">
          <h6>Allocation History</h6>
          <ErrorAlert error={error} />
          {loading ? <Loading /> : (
            <table className="table table-sm bg-white">
              <thead><tr><th>Year</th><th>Bonus (kg)</th><th>Shareholders Affected</th></tr></thead>
              <tbody>
                {history.map((h) => (
                  <tr key={h.id}><td>{h.allocationYear}</td><td>{h.bonusAmount}</td><td>{h.affectedShareholderCount}</td></tr>
                ))}
                {history.length === 0 && <tr><td colSpan={3} className="text-center text-secondary py-3">No allocations applied yet</td></tr>}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
