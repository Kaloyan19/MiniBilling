import './App.css';
import { useState } from "react";
import InvoiceForm from "./components/InvoiceForm";
import InvoiceTable from "./components/InvoiceTable";
import Login from "./components/Login";
import Register from "./components/Register";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [page, setPage] = useState("login");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState("");

  const handleLogin = (token) => {
    setToken(token);
    setPage("app");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    setToken(null);
    setPage("login");
    setInvoice(null);
  };

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
      const response = await fetch(
        `http://localhost:8080/invoices/my?from=${from}&to=${to}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response.status === 404) {
        setError("Потребителят не е намерен.");
        return;
      }

      if (response.status === 400) {
        const text = await response.text();
        setError(text || "Невалидна заявка.");
        return;
      }

      if (response.status === 401) {
        setError("Сесията е изтекла. Моля влезте отново.");
        handleLogout();
        return;
      }

      if (response.status === 403) {
        setError("Нямате права за тази операция.");
        return;
      }

      if (response.status === 500) {
        setError("Сървърна грешка. Проверете данните.");
        return;
      }

      if (response.status === 204) {
        const postResponse = await fetch(
          `http://localhost:8080/invoices/my?from=${from}&to=${to}`,
          {
            method: "POST",
            headers: { Authorization: `Bearer ${token}` }
          }
        );

        if (postResponse.status === 204) {
          setError("Няма достатъчно отчети за този период.");
          return;
        }

        if (postResponse.status === 400) {
          const text = await postResponse.text();
          setError(text || "Невалидна заявка.");
          return;
        }

        if (postResponse.ok) {
          const data = await postResponse.json();
          setInvoice(data);
          return;
        }
      }

      if (response.ok) {
        const data = await response.json();
        setInvoice(data);
      }
    } catch (e) {
      setError("Грешка при свързване със сървъра.");
    }
  };

  if (page === "login") {
    return <Login onLogin={handleLogin} onSwitchToRegister={() => setPage("register")} />;
  }

  if (page === "register") {
    return <Register onSwitchToLogin={() => setPage("login")} />;
  }

  return (
    <div className="container">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
        <h1>MiniBilling</h1>
        <button onClick={handleLogout} style={{ background: "#e74c3c" }}>Изход</button>
      </div>
      <InvoiceForm
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