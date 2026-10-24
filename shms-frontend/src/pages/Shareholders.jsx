import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { searchShareholders, createShareholder } from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

const emptyForm = {
  memberName: '', taluka: '', district: '', village: '', gatNo: '', age: '',
  farmerLand: '', contactNo: '', gender: 'Male', registrationDate: '', nomineeName: '', directorName: '',
};

export default function Shareholders() {
  const [page, setPage] = useState({ content: [], totalPages: 0, number: 0 });
  const [filters, setFilters] = useState({ memberId: '', name: '', village: '', shareClass: '', status: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState(null);

  const load = (pageNumber = 0) => {
    setLoading(true);
    const params = { ...filters, page: pageNumber, size: 10 };
    Object.keys(params).forEach((k) => params[k] === '' && delete params[k]);
    searchShareholders(params)
      .then((res) => setPage(res.data.data))
      .catch(setError)
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(0); }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const handleSearch = (e) => {
    e.preventDefault();
    load(0);
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    setSaving(true);
    setSaveError(null);
    try {
      await createShareholder({ ...form, gatNo: form.gatNo ? Number(form.gatNo) : null, age: form.age ? Number(form.age) : null });
      setForm(emptyForm);
      setShowForm(false);
      load(0);
    } catch (err) {
      setSaveError(err);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="mb-0">Shareholders</h3>
        <button className="btn btn-primary" onClick={() => setShowForm((s) => !s)}>
          {showForm ? 'Cancel' : '+ Register Shareholder'}
        </button>
      </div>

      {showForm && (
        <div className="card mb-4">
          <div className="card-body">
            <h5>Register New Shareholder</h5>
            <ErrorAlert error={saveError} />
            <form onSubmit={handleCreate}>
              <div className="row g-2">
                <div className="col-md-4">
                  <input className="form-control" placeholder="Member Name *" required
                    value={form.memberName} onChange={(e) => setForm({ ...form, memberName: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control" placeholder="Village" value={form.village}
                    onChange={(e) => setForm({ ...form, village: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control" placeholder="Taluka" value={form.taluka}
                    onChange={(e) => setForm({ ...form, taluka: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control" placeholder="District" value={form.district}
                    onChange={(e) => setForm({ ...form, district: e.target.value })} />
                </div>
                <div className="col-md-2">
                  <input className="form-control" type="number" placeholder="Gat No." value={form.gatNo}
                    onChange={(e) => setForm({ ...form, gatNo: e.target.value })} />
                </div>
                <div className="col-md-2">
                  <input className="form-control" type="number" placeholder="Age" value={form.age}
                    onChange={(e) => setForm({ ...form, age: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control" placeholder="Contact No." value={form.contactNo}
                    onChange={(e) => setForm({ ...form, contactNo: e.target.value })} />
                </div>
                <div className="col-md-2">
                  <select className="form-select" value={form.gender}
                    onChange={(e) => setForm({ ...form, gender: e.target.value })}>
                    <option>Male</option>
                    <option>Female</option>
                    <option>Other</option>
                  </select>
                </div>
                <div className="col-md-3">
                  <label className="form-label small mb-0">Registration Date *</label>
                  <input className="form-control" type="date" required value={form.registrationDate}
                    onChange={(e) => setForm({ ...form, registrationDate: e.target.value })} />
                </div>
                <div className="col-md-3">
                  <input className="form-control mt-4" placeholder="Farmer Land" value={form.farmerLand}
                    onChange={(e) => setForm({ ...form, farmerLand: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control mt-4" placeholder="Nominee Name" value={form.nomineeName}
                    onChange={(e) => setForm({ ...form, nomineeName: e.target.value })} />
                </div>
                <div className="col-md-4">
                  <input className="form-control mt-4" placeholder="Director Name" value={form.directorName}
                    onChange={(e) => setForm({ ...form, directorName: e.target.value })} />
                </div>
              </div>
              <button type="submit" className="btn btn-success mt-3" disabled={saving}>
                {saving ? 'Saving...' : 'Register'}
              </button>
            </form>
          </div>
        </div>
      )}

      <div className="card mb-3">
        <div className="card-body">
          <form className="row g-2" onSubmit={handleSearch}>
            <div className="col-md-2">
              <input className="form-control" placeholder="Member ID" value={filters.memberId}
                onChange={(e) => setFilters({ ...filters, memberId: e.target.value })} />
            </div>
            <div className="col-md-3">
              <input className="form-control" placeholder="Name" value={filters.name}
                onChange={(e) => setFilters({ ...filters, name: e.target.value })} />
            </div>
            <div className="col-md-2">
              <input className="form-control" placeholder="Village" value={filters.village}
                onChange={(e) => setFilters({ ...filters, village: e.target.value })} />
            </div>
            <div className="col-md-2">
              <select className="form-select" value={filters.shareClass}
                onChange={(e) => setFilters({ ...filters, shareClass: e.target.value })}>
                <option value="">All Classes</option>
                <option value="A">Class A</option>
                <option value="C">Class C</option>
                <option value="UNASSIGNED">Unassigned</option>
              </select>
            </div>
            <div className="col-md-2">
              <select className="form-select" value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}>
                <option value="">All Statuses</option>
                <option value="ACTIVE">Active</option>
                <option value="TRANSFERRED">Transferred</option>
                <option value="INACTIVE">Inactive</option>
              </select>
            </div>
            <div className="col-md-1">
              <button className="btn btn-outline-primary w-100">Search</button>
            </div>
          </form>
        </div>
      </div>

      <ErrorAlert error={error} />
      {loading ? (
        <Loading />
      ) : (
        <>
          <table className="table table-hover bg-white">
            <thead>
              <tr>
                <th>Member ID</th><th>Name</th><th>Village</th><th>Class</th><th>Status</th><th></th>
              </tr>
            </thead>
            <tbody>
              {page.content.map((s) => (
                <tr key={s.id}>
                  <td>{s.memberId}</td>
                  <td>{s.memberName}</td>
                  <td>{s.village}</td>
                  <td><span className="badge bg-secondary">{s.shareClass}</span></td>
                  <td>{s.status}</td>
                  <td>
                    <Link to={`/shareholders/${s.memberId}`} className="btn btn-sm btn-outline-primary">
                      View
                    </Link>
                  </td>
                </tr>
              ))}
              {page.content.length === 0 && (
                <tr><td colSpan={6} className="text-center text-secondary py-4">No shareholders found</td></tr>
              )}
            </tbody>
          </table>

          {page.totalPages > 1 && (
            <nav>
              <ul className="pagination">
                {Array.from({ length: page.totalPages }).map((_, i) => (
                  <li key={i} className={`page-item ${i === page.number ? 'active' : ''}`}>
                    <button className="page-link" onClick={() => load(i)}>{i + 1}</button>
                  </li>
                ))}
              </ul>
            </nav>
          )}
        </>
      )}
    </div>
  );
}
