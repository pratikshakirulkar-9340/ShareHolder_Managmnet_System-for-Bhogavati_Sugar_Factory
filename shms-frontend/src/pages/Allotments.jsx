import { useState } from 'react';
import { allotShares } from '../api/api';
import ErrorAlert from '../components/ErrorAlert';

export default function Allotments() {
  const [form, setForm] = useState({
    memberId: '', landOwner: true, evidenceVerified: true,
    purchasingDate: '', plantingDate: '', typeOfSugarcane: '', nomineeName: '', directorName: '',
  });
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setResult(null);
    try {
      const { data } = await allotShares(form);
      setResult(data.data);
    } catch (err) {
      setError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <h3 className="mb-4">Share Allotment</h3>
      <div className="row">
        <div className="col-md-7">
          <div className="card">
            <div className="card-body">
              <ErrorAlert error={error} />
              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label">Member ID *</label>
                  <input className="form-control" required value={form.memberId}
                    onChange={(e) => setForm({ ...form, memberId: e.target.value })}
                    placeholder="e.g. 1U (the temporary ID from registration)" />
                </div>

                <div className="form-check mb-2">
                  <input type="checkbox" className="form-check-input" id="landOwner" checked={form.landOwner}
                    onChange={(e) => setForm({ ...form, landOwner: e.target.checked })} />
                  <label className="form-check-label" htmlFor="landOwner">Owns land</label>
                </div>
                <div className="form-check mb-3">
                  <input type="checkbox" className="form-check-input" id="evidence" checked={form.evidenceVerified}
                    onChange={(e) => setForm({ ...form, evidenceVerified: e.target.checked })} />
                  <label className="form-check-label" htmlFor="evidence">Land evidence verified</label>
                </div>
                <p className="text-secondary small">
                  Both checked → <strong>Class A</strong> (share purchase + monthly sugar quota).
                  Either unchecked → <strong>Class C</strong> (tonnage-based entitlement instead).
                </p>

                <div className="mb-3">
                  <label className="form-label">Purchasing Date *</label>
                  <input type="date" className="form-control" required value={form.purchasingDate}
                    onChange={(e) => setForm({ ...form, purchasingDate: e.target.value })} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Planting Date</label>
                  <input type="date" className="form-control" value={form.plantingDate}
                    onChange={(e) => setForm({ ...form, plantingDate: e.target.value })} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Type of Sugarcane</label>
                  <input className="form-control" value={form.typeOfSugarcane}
                    onChange={(e) => setForm({ ...form, typeOfSugarcane: e.target.value })} />
                </div>
                <div className="mb-3">
                  <label className="form-label">Nominee Name</label>
                  <input className="form-control" value={form.nomineeName}
                    onChange={(e) => setForm({ ...form, nomineeName: e.target.value })} />
                </div>

                <button className="btn btn-primary" disabled={saving}>
                  {saving ? 'Allotting...' : 'Allot Shares'}
                </button>
              </form>
            </div>
          </div>
        </div>

        <div className="col-md-5">
          {result && (
            <div className="card border-success">
              <div className="card-body">
                <h6 className="text-success">Allotment Successful</h6>
                <p className="mb-1"><strong>New Member ID:</strong> {result.memberId}</p>
                <p className="mb-1"><strong>Class:</strong> {result.typeOfShare}</p>
                <p className="mb-1"><strong>Share Purchase Amount:</strong> {result.sharePurchaseAmount}</p>
                <p className="mb-0"><strong>Per-Month Sugar:</strong> {result.perMonthSugar}</p>
                <p className="text-secondary small mt-2 mb-0">
                  Use this new Member ID (not the one you typed above) for sugar transactions.
                </p>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
