function InvoiceForm({ from, setFrom, to, setTo, onSubmit }) {
  return (
    <div className="form">
      <input
        type="date"
        value={from}
        onChange={(e) => setFrom(e.target.value)}
      />
      <input
        type="date"
        value={to}
        onChange={(e) => setTo(e.target.value)}
      />
      <button onClick={onSubmit}>Генерирай фактура</button>
    </div>
  );
}

export default InvoiceForm;