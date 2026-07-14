function InvoiceForm({ reference, setReference, year, setYear, month, setMonth, onSubmit }) {
    return (
      <div className="form">
        <input
          placeholder="Референтен номер"
          value={reference}
          onChange={(e) => setReference(e.target.value)}
        />
        <input
          placeholder="Година (2024)"
          value={year}
          onChange={(e) => setYear(e.target.value)}
        />
        <input
          placeholder="Месец (3)"
          value={month}
          onChange={(e) => setMonth(e.target.value)}
        />
        <button onClick={onSubmit}>Генерирай фактура</button>
      </div>
    );
  }
  
  export default InvoiceForm;