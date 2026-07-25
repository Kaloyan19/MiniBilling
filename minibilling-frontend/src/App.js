import './App.css';
import { useState } from "react";
import { loadOrGenerateInvoice } from "./api/invoiceApi";
import InvoiceForm from "./components/InvoiceForm";
import InvoiceTable from "./components/InvoiceTable";

function App() {
  const [reference, setReference] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
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
    if (amount === undefined || amount === null) return "0.00 €";
    return amount.toFixed(2) + " €";
  };

  const handleSubmit = async () => {
    setError("");
    setInvoice(null);

    try {
      const response = await loadOrGenerateInvoice(reference, from, to);

      if (response.status === 404) {
        setError("Потребителят не е намерен.");
        return;
      }

      if (response.status === 204) {
        setError("Няма фактура за този период.");
        return;
      }

      if (response.status === 400) {
        const text = await response.text();
        setError(text || "Невалидна заявка.");
        return;
    }
      if (response.status === 500) {
          setError("Сървърна грешка. Проверете данните.");
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
      <InvoiceForm
        reference={reference}
        setReference={setReference}
        from={from}
        setFrom={setFrom}
        to={to}
        setTo={setTo}
        onSubmit={handleSubmit}
      />
      {error && <p className="error">{error}</p>}
      {invoice && (
        <InvoiceTable
          invoice={invoice}
          formatDate={formatDate}
          formatAmount={formatAmount}
        />
      )}
    </div>
  );
}

export default App;