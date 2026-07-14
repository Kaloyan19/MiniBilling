import './App.css';
import { useState } from "react";
import { loadOrGenerateInvoice } from "./api/invoiceApi";
import InvoiceForm from "./components/InvoiceForm";
import InvoiceTable from "./components/InvoiceTable";

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
    if (amount === undefined || amount === null) return "0.00 €";
    return amount.toFixed(2) + " €";
  };

  const handleSubmit = async () => {
    setError("");
    setInvoice(null);

    try {
      const response = await loadOrGenerateInvoice(reference, year, month);

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
      <InvoiceForm
        reference={reference}
        setReference={setReference}
        year={year}
        setYear={setYear}
        month={month}
        setMonth={setMonth}
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