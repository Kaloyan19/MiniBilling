import './App.css';
import { useState } from "react";
import InvoiceForm from "./components/InvoiceForm";
import InvoiceTable from "./components/InvoiceTable";
import Login from "./components/Login";
import Register from "./components/Register";
import ImportPage from "./components/ImportPage";

function App() {
  const [token, setToken] = useState(localStorage.getItem("token"));
  const [role, setRole] = useState(localStorage.getItem("role"));
  const [page, setPage] = useState("login");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState("");

  const handleLogin = (token, userRole) => {
    setToken(token);
    setRole(userRole);
    setPage(userRole === "ADMIN" ? "import" : "invoice");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    setToken(null);
    setRole(null);
    setPage("login");
    setInvoice(null);
  };

  const formatDate = (isoString) => {
    const date = new Date(isoString);
    return date.toLocaleString("bg-BG", {
      day: "2-digit", month: "2-digit", year: "numeric",
      hour: "2-digit", minute: "2-digit"
    });
  };

  const formatAmount = (amount) => {
    if (amount === undefined || amount === null) return "0.00 €";
    return Number(amount).toFixed(2) + " €";
  };

  const handleSubmit = async () => {
    setError("");
    setInvoice(null);

    try {
      const response = await fetch(
        `http://localhost:8080/invoices/my?from=${from}&to=${to}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      if (response.status === 404) { setError("Потребителят не е намерен."); return; }
      if (response.status === 400) { const text = await response.text(); setError(text || "Невалидна заявка."); return; }
      if (response.status === 401) { setError("Сесията е изтекла."); handleLogout(); return; }
      if (response.status === 403) { setError("Нямате права."); return; }
      if (response.status === 500) { setError("Сървърна грешка."); return; }

      if (response.status === 204) {
        const postResponse = await fetch(
          `http://localhost:8080/invoices/my?from=${from}&to=${to}`,
          { method: "POST", headers: { Authorization: `Bearer ${token}` } }
        );
        if (postResponse.status === 204) { setError("Няма достатъчно отчети."); return; }
        if (postResponse.ok) { const data = await postResponse.json(); setInvoice(data); return; }
      }

      if (response.ok) { const data = await response.json(); setInvoice(data); }
    } catch (e) {
      setError("Грешка при свързване.");
    }
  };

  if (page === "login") return <Login onLogin={handleLogin} onSwitchToRegister={() => setPage("register")} />;
  if (page === "register") return <Register onSwitchToLogin={() => setPage("login")} />;

  return (
    <div className="layout">
      <aside className="sidebar">
        <h1>MiniBilling</h1>
        {role === "ADMIN" && (
          <>
            <button
              className={`nav-item ${page === "import" ? "active" : ""}`}
              onClick={() => setPage("import")}>
              📥 Импорт
            </button>
            <button
              className={`nav-item ${page === "invoices" ? "active" : ""}`}
              onClick={() => setPage("invoices")}>
              📄 Фактури
            </button>
            <button
              className={`nav-item ${page === "logs" ? "active" : ""}`}
              onClick={() => setPage("logs")}>
              ⚠️ Логове
            </button>
          </>
        )}
        {role === "USER" && (
          <button
            className={`nav-item ${page === "invoice" ? "active" : ""}`}
            onClick={() => setPage("invoice")}>
            📄 Моята фактура
          </button>
        )}
        <div className="sidebar-bottom">
          <button className="logout-btn" onClick={handleLogout}>Изход</button>
        </div>
      </aside>

      <main className="main-content">
        {page === "import" && <ImportPage token={token} />}
        {page === "invoices" && <div>Всички фактури - предстои</div>}
        {page === "logs" && <div>Логове - предстои</div>}
        {page === "invoice" && (
          <>
            <InvoiceForm from={from} setFrom={setFrom} to={to} setTo={setTo} onSubmit={handleSubmit} />
            {error && <p className="error">{error}</p>}
            {invoice && <InvoiceTable invoice={invoice} formatDate={formatDate} formatAmount={formatAmount} />}
          </>
        )}
      </main>
    </div>
  );
}

export default App;