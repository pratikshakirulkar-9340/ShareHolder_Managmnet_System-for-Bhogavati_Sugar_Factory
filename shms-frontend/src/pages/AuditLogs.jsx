import { useEffect, useState } from 'react';
import { getAuditLogs } from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';

export default function AuditLogs() {
  const [page, setPage] = useState({ content: [], totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const load = (pageNumber = 0) => {
    setLoading(true);
    getAuditLogs({ page: pageNumber, size: 20, sort: 'createdAt,desc' })
      .then((res) => setPage(res.data.data))
      .catch(setError)
      .finally(() => setLoading(false));
  };

  useEffect(() => load(0), []);

  return (
    <div>
      <h3 className="mb-4">Audit Log</h3>
      <ErrorAlert error={error} />
      {loading ? <Loading /> : (
        <>
          <table className="table table-sm bg-white">
            <thead><tr><th>Time</th><th>User</th><th>Action</th><th>Entity</th><th>Description</th></tr></thead>
            <tbody>
              {page.content.map((log) => (
                <tr key={log.id}>
                  <td>{new Date(log.timestamp).toLocaleString()}</td>
                  <td>{log.username}</td>
                  <td><span className="badge bg-secondary">{log.action}</span></td>
                  <td>{log.entityName} #{log.entityId}</td>
                  <td>{log.description}</td>
                </tr>
              ))}
              {page.content.length === 0 && <tr><td colSpan={5} className="text-center text-secondary py-4">No audit entries yet</td></tr>}
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
