import { useEffect, useState } from 'react';
import { getDashboardSummary } from '../api/api';
import Loading from '../components/Loading';
import ErrorAlert from '../components/ErrorAlert';
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend } from 'recharts';

const COLORS = ['#0d6efd', '#20c997', '#ffc107', '#dc3545'];

function StatCard({ label, value, variant = 'primary' }) {
  return (
    <div className="col-md-3 mb-3">
      <div className={`card border-${variant} h-100`}>
        <div className="card-body">
          <div className="text-secondary small">{label}</div>
          <div className={`fs-3 fw-bold text-${variant}`}>{value}</div>
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    getDashboardSummary()
      .then((res) => setSummary(res.data.data))
      .catch(setError)
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Loading label="Loading dashboard..." />;
  if (error) return <ErrorAlert error={error} />;

  const classData = [
    { name: 'Class A', value: summary.classAShareholders },
    { name: 'Class C', value: summary.classCShareholders },
  ];

  const transferData = [
    { name: 'Pending', value: summary.pendingTransfers },
    { name: 'Approved', value: summary.approvedTransfers },
    { name: 'Rejected', value: summary.rejectedTransfers },
  ];

  return (
    <div>
      <h3 className="mb-4">Dashboard</h3>

      <div className="row">
        <StatCard label="Total Shareholders" value={summary.totalShareholders} variant="primary" />
        <StatCard label="Class A" value={summary.classAShareholders} variant="success" />
        <StatCard label="Class C" value={summary.classCShareholders} variant="warning" />
        <StatCard label="Pending Transfers" value={summary.pendingTransfers} variant="danger" />
      </div>

      <div className="row mt-3">
        <StatCard label="Total Share Sugar Supplied (kg)" value={summary.totalShareSugarSupplied} variant="success" />
        <StatCard label="Total Tonnes Sugar Supplied (kg)" value={summary.totalTonnesSugarSupplied} variant="success" />
        <StatCard label="Approved Transfers" value={summary.approvedTransfers} variant="primary" />
        <StatCard label="Rejected Transfers" value={summary.rejectedTransfers} variant="secondary" />
      </div>

      <div className="row mt-4">
        <div className="col-md-6">
          <div className="card">
            <div className="card-header">Shareholders by Class</div>
            <div className="card-body" style={{ height: 280 }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={classData} dataKey="value" nameKey="name" outerRadius={90} label>
                    {classData.map((_, i) => (
                      <Cell key={i} fill={COLORS[i % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="card">
            <div className="card-header">Transfer Status</div>
            <div className="card-body" style={{ height: 280 }}>
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie data={transferData} dataKey="value" nameKey="name" outerRadius={90} label>
                    {transferData.map((_, i) => (
                      <Cell key={i} fill={COLORS[i % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
