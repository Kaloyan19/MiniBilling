import './App.css';
import { useState } from "react";

function App() {
  const [reference, setReference] = useState("");
  const [year, setYear] = useState("");
  const [month, setMonth] = useState("");
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState("");

  const formatDate = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleString("bg-BG", {
      day: "2-digit",
      month: "2-digit", 
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit"
    });
  };

  const formatAmount = (amount) => {
    if (amount === undefined || amount === null) return "0.00 лв.";
    return amount.toFixed(2) + "€";
  };

  const handleSubmit = async () => {
    setError("");
    setInvoice(null);

    try {
      const response = await fetch(
        `http://localhost:8080/invoices/${reference}?year=${year}&month=${month}`,
        { method: "POST" }  
      );

      if (response.status === 404) {
        setError("Потребителят не е намерен.");
        return;
      }

      if (response.status === 204) {
        setError("Няма фактура за този период.");
        return;
      }

      const data = await response.json();
      setInvoice(data);
    } catch (e) {
      setError("Грешка при свързване със сървъра.");
    }
  };

  return (
    <div className="container">
      <h1>MiniBilling</h1>

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
        <button onClick={handleSubmit}>Генерирай фактура</button>
      </div>

      {error && <p className="error">{error}</p>}

      {invoice && (
        <div className="invoice">
          <h2>Фактура №{invoice.documentNumber}</h2>
          <p>Клиент: {invoice.consumer}</p>
          <p>Референтен номер: {invoice.reference}</p>
          <p>Дата: {formatDate(invoice.documentDate)}</p>
          <p className="total">Обща сума: {formatAmount(invoice.totalAmount)}</p>

          <h3>Линии:</h3>
          <table border="1">
            <thead>
              <tr>
                <th>#</th>
                <th>Продукт</th>
                <th>Количество</th>
                <th>Цена</th>
                <th>Сума</th>
                <th>От</th>
                <th>До</th>
              </tr>
            </thead>
            <tbody>
              {invoice.lines.map((line) => (
                <tr key={line.index}>
                  <td>{line.index}</td>
                  <td>{line.product}</td>
                  <td>{line.quantity}</td>
                  <td>{line.price}</td>
                  <td>{formatAmount(line.amount)}</td>
                  <td>{formatDate(line.lineStart)}</td>
                  <td>{formatDate(line.lineEnd)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;