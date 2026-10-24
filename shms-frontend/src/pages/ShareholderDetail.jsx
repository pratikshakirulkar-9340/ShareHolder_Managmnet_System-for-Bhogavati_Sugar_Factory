import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
  getShareholder, getAllotmentsByMember, getShareSugarByMember, getTonnesSugarByMember,
  getDocumentsByMember, uploadDocument, deleteDocument, downloadDocument,
} from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

const TABS = ['Profile', 'Allotments', 'Sugar History', 'Documents'];
const DOC_TYPES = ['AADHAAR', 'PAN', 'LAND_RECORD_7_12', 'DEATH_CERTIFICATE', 'SHARE_CERTIFICATE', 'OTHER'];

export default function ShareholderDetail() {
  const { memberId } = useParams();
  const [shareholder, setShareholder] = useState(null);
  const [allotments, setAllotments] = useState([]);
  const [shareSugar, setShareSugar] = useState([]);
  const [tonnesSugar, setTonnesSugar] = useState([]);
  const [documents, setDocuments] = useState([]);
  const [tab, setTab] = useState('Profile');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [docType, setDocType] = useState('AADHAAR');
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);

  const loadAll = () => {
    setLoading(true);
    Promise.all([
      getShareholder(memberId),
      getAllotmentsByMember(memberId),
      getShareSugarByMember(memberId).catch(() => ({ data: { data: [] } })),
      getTonnesSugarByMember(memberId).catch(() => ({ data: { data: [] } })),
      getDocumentsByMember(memberId),
    ])
      .then(([sh, al, ss, ts, docs]) => {
        setShareholder(sh.data.data);
        setAllotments(al.data.data);
        setShareSugar(ss.data.data);
        setTonnesSugar(ts.data.data);
        setDocuments(docs.data.data);
      })
      .catch(setError)
      .finally(() => setLoading(false));
  };

  useEffect(loadAll, [memberId]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) return;
    setUploading(true);
    try {
      await uploadDocument(memberId, docType, file);
      setFile(null);
      loadAll();
    } catch (err) {
      setError(err);
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteDoc = async (id) => {
    if (!window.confirm('Delete this document?')) return;
    await deleteDocument(id);
    loadAll();
  };

  if (loading) return <Loading />;
  if (error) return <ErrorAlert error={error} />;
  if (!shareholder) return null;

  return (
    <div>
      <h3>{shareholder.memberName} <span className="text-secondary fs-6">({shareholder.memberId})</span></h3>
      <div className="mb-3">
        <span className="badge bg-secondary me-2">Class {shareholder.shareClass}</span>
        <span className="badge bg-info text-dark">{shareholder.status}</span>
      </div>

      <ul className="nav nav-tabs mb-3">
        {TABS.map((t) => (
          <li className="nav-item" key={t}>
            <button className={`nav-link ${tab === t ? 'active' : ''}`} onClick={() => setTab(t)}>{t}</button>
          </li>
        ))}
      </ul>

      {tab === 'Profile' && (
        <div className="card">
          <div className="card-body">
            <div className="row">
              <div className="col-md-4"><strong>Village:</strong> {shareholder.village || '-'}</div>
              <div className="col-md-4"><strong>Taluka:</strong> {shareholder.taluka || '-'}</div>
              <div className="col-md-4"><strong>District:</strong> {shareholder.district || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Gat No.:</strong> {shareholder.gatNo || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Age:</strong> {shareholder.age || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Gender:</strong> {shareholder.gender || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Contact:</strong> {shareholder.contactNo || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Farmer Land:</strong> {shareholder.farmerLand || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Registration Date:</strong> {shareholder.registrationDate}</div>
              <div className="col-md-4 mt-2"><strong>Nominee:</strong> {shareholder.nomineeName || '-'}</div>
              <div className="col-md-4 mt-2"><strong>Director:</strong> {shareholder.directorName || '-'}</div>
            </div>
          </div>
        </div>
      )}

      {tab === 'Allotments' && (
        <table className="table table-hover bg-white">
          <thead><tr><th>Date</th><th>Type</th><th>Purchase Amount</th><th>Per-Month Sugar</th><th>Sugarcane</th></tr></thead>
          <tbody>
            {allotments.map((a) => (
              <tr key={a.id}>
                <td>{a.purchasingDate}</td>
                <td>{a.typeOfShare}</td>
                <td>{a.sharePurchaseAmount}</td>
                <td>{a.perMonthSugar}</td>
                <td>{a.typeOfSugarcane || '-'}</td>
              </tr>
            ))}
            {allotments.length === 0 && <tr><td colSpan={5} className="text-center text-secondary py-3">No allotments yet</td></tr>}
          </tbody>
        </table>
      )}

      {tab === 'Sugar History' && (
        <div>
          {shareholder.shareClass === 'A' && (
            <>
              <h6>Share Sugar (Class A)</h6>
              <table className="table table-sm bg-white mb-4">
                <thead><tr><th>Date</th><th>Entitlement</th><th>Supplied</th><th>Prev. Remaining</th><th>Remaining</th><th>Amount</th></tr></thead>
                <tbody>
                  {shareSugar.map((t) => (
                    <tr key={t.id}>
                      <td>{t.suppliedDate}</td><td>{t.entitlement}</td><td>{t.suppliedSugar}</td>
                      <td>{t.previousRemaining}</td><td>{t.remainingSugar}</td><td>₹{t.totalAmount}</td>
                    </tr>
                  ))}
                  {shareSugar.length === 0 && <tr><td colSpan={6} className="text-center text-secondary py-3">No transactions yet</td></tr>}
                </tbody>
              </table>
            </>
          )}
          {shareholder.shareClass === 'C' && (
            <>
              <h6>Tonnes Sugar (Class C)</h6>
              <table className="table table-sm bg-white">
                <thead><tr><th>Date</th><th>Total Tonnes</th><th>Entitlement</th><th>Supplied</th><th>Remaining</th><th>Amount</th></tr></thead>
                <tbody>
                  {tonnesSugar.map((t) => (
                    <tr key={t.id}>
                      <td>{t.suppliedDate}</td><td>{t.totalTonnes}</td><td>{t.entitlement}</td>
                      <td>{t.suppliedSugar}</td><td>{t.remainingSugar}</td><td>₹{t.amount}</td>
                    </tr>
                  ))}
                  {tonnesSugar.length === 0 && <tr><td colSpan={6} className="text-center text-secondary py-3">No transactions yet</td></tr>}
                </tbody>
              </table>
            </>
          )}
          {shareholder.shareClass === 'UNASSIGNED' && (
            <p className="text-secondary">No class assigned yet - allot shares first.</p>
          )}
        </div>
      )}

      {tab === 'Documents' && (
        <div>
          <form className="row g-2 mb-4 align-items-end" onSubmit={handleUpload}>
            <div className="col-md-3">
              <label className="form-label small mb-0">Document Type</label>
              <select className="form-select" value={docType} onChange={(e) => setDocType(e.target.value)}>
                {DOC_TYPES.map((t) => <option key={t} value={t}>{t.replaceAll('_', ' ')}</option>)}
              </select>
            </div>
            <div className="col-md-5">
              <label className="form-label small mb-0">File (PDF/JPEG/PNG, max 10MB)</label>
              <input type="file" className="form-control" accept=".pdf,.jpg,.jpeg,.png"
                onChange={(e) => setFile(e.target.files[0])} />
            </div>
            <div className="col-md-2">
              <button className="btn btn-primary" disabled={!file || uploading}>
                {uploading ? 'Uploading...' : 'Upload'}
              </button>
            </div>
          </form>

          <table className="table bg-white">
            <thead><tr><th>Type</th><th>File Name</th><th>Size</th><th></th></tr></thead>
            <tbody>
              {documents.map((d) => (
                <tr key={d.id}>
                  <td>{d.documentType.replaceAll('_', ' ')}</td>
                  <td>{d.fileName}</td>
                  <td>{(d.fileSize / 1024).toFixed(1)} KB</td>
                  <td>
                    <button className="btn btn-sm btn-outline-secondary me-2" onClick={() => downloadDocument(d.id, d.fileName)}>
                      Download
                    </button>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => handleDeleteDoc(d.id)}>Delete</button>
                  </td>
                </tr>
              ))}
              {documents.length === 0 && <tr><td colSpan={4} className="text-center text-secondary py-3">No documents uploaded</td></tr>}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
