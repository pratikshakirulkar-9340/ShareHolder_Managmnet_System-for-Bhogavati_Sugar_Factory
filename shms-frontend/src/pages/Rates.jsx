import { useEffect, useState } from 'react';
import { getRateHistory, createRate } from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

const emptyForm = {
  sugarFactoryName: 'Bhogavati Sahakari Sakhar Karkhana', sharePurchaseAmount: '',
  perMonthShareSugar: '', rateOfShareSugar: '', rateOfTonnesSugar: '', effectiveFrom: '',
};

export default function Rates() {
  const [rates, setRates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState(null);

  const load = () => {
    setLoading(true);
    getRateHistory().then((res) => setRates(res.data.data)).catch(setError).finally(() => setLoading(false));
  };

  useEffect(load, []);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true); setSaveError(null);
    try {
      const payload = { ...form };
      Object.keys(payload).forEach((k) => {
        if (payload[k] === '') delete payload[k];
        else if (k !== 'sugarFactoryName' && k !== 'effectiveFrom') payload[k] = Number(payload[k]);
      });
      await createRate(payload);
      setForm(emptyForm);
      load();
    } catch (err) {
      setSaveError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4">Rate Management</h3>
      <div className="row">
        <div className="col-md-5">
          <div className="card"><div className="card-body">
            <h6>Create New Rate</h6>
            <p className="text-secondary small">
              Creating a new rate automatically closes out the currently active one, so
              existing transactions keep referencing the rate that applied when they happened.
            </p>
            <ErrorAlert error={saveError} />
            <form onSubmit={handleSubmit}>
              <div className="mb-2">
                <label className="form-label small mb-0">Factory Name</label>
                <input className="form-control" required value={form.sugarFactoryName}
                  onChange={(e) => setForm({ ...form, sugarFactoryName: e.target.value })} />
              </div>
              <div className="mb-2">
                <label className="form-label small mb-0">Share Purchase Amount (₹)</label>
                <input type="number" step="0.01" className="form-control" required value={form.sharePurchaseAmount}
                  onChange={(e) => setForm({ ...form, sharePurchaseAmount: e.target.value })} />
              </div>
              <div className="mb-2">
                <label className="form-label small mb-0">Per-Month Share Sugar (kg)</label>
                <input type="number" step="0.01" className="form-control" required value={form.perMonthShareSugar}
                  onChange={(e) => setForm({ ...form, perMonthShareSugar: e.target.value })} />
              </div>
              <div className="mb-2">
                <label className="form-label small mb-0">Rate of Share Sugar (₹/kg)</label>
                <input type="number" step="0.01" className="form-control" required value={form.rateOfShareSugar}
                  onChange={(e) => setForm({ ...form, rateOfShareSugar: e.target.value })} />
              </div>
              <div className="mb-2">
                <label className="form-label small mb-0">Rate of Tonnes Sugar (₹/kg)</label>
                <input type="number" step="0.01" className="form-control" required value={form.rateOfTonnesSugar}
                  onChange={(e) => setForm({ ...form, rateOfTonnesSugar: e.target.value })} />
              </div>
              <div className="mb-3">
                <label className="form-label small mb-0">Effective From (defaults to today)</label>
                <input type="date" className="form-control" value={form.effectiveFrom}
                  onChange={(e) => setForm({ ...form, effectiveFrom: e.target.value })} />
              </div>
              <button className="btn btn-primary" disabled={saving}>{saving ? 'Creating...' : 'Create Rate'}</button>
            </form>
          </div></div>
        </div>

        <div className="col-md-7">
          <h6>Rate History</h6>
          <ErrorAlert error={error} />
          {loading ? <Loading /> : (
            <table className="table table-sm bg-white">
              <thead><tr><th>From</th><th>To</th><th>Purchase</th><th>Monthly Sugar</th><th>₹/kg (Share)</th><th>₹/kg (Tonnes)</th></tr></thead>
              <tbody>
                {rates.map((r) => (
                  <tr key={r.id} className={!r.effectiveTo ? 'table-success' : ''}>
                    <td>{r.effectiveFrom}</td>
                    <td>{r.effectiveTo || <strong>Current</strong>}</td>
                    <td>{r.sharePurchaseAmount}</td>
                    <td>{r.perMonthShareSugar}</td>
                    <td>{r.rateOfShareSugar}</td>
                    <td>{r.rateOfTonnesSugar}</td>
                  </tr>
                ))}
                {rates.length === 0 && <tr><td colSpan={6} className="text-center text-secondary py-3">No rates configured yet</td></tr>}
              </tbody>
            </table>
          )}
        </div>
      </div>
    </div>
  );
}
