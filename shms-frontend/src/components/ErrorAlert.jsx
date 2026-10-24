export default function ErrorAlert({ error }) {
  if (!error) return null;
  const message = error?.response?.data?.message || error?.message || String(error);
  return <div className="alert alert-danger">{message}</div>;
}
