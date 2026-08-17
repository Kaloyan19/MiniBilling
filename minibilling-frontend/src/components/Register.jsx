import { useState } from "react";

function Register({ onSwitchToLogin }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [reference, setReference] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleRegister = async () => {
    setError("");
    setSuccess("");

    try {
      const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password, customerReference: reference })
      });

      if (response.ok) {
        setSuccess("Регистрацията е успешна!");
      } else {
        const text = await response.text();
        setError(text || "Грешка при регистрация.");
      }
    } catch (e) {
      setError("Грешка при свързване със сървъра.");
    }
  };

  return (
    <div className="auth-container">
      <h1>MiniBilling</h1>
      <div className="form">
        <input
          placeholder="Потребителско име"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Парола"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <input
          placeholder="Клиентски номер"
          value={reference}
          onChange={(e) => setReference(e.target.value)}
        />
        <button onClick={handleRegister}>Регистрация</button>
      </div>
      {error && <p className="error">{error}</p>}
      {success && <p className="success">{success}</p>}
      <p style={{ marginTop: "1rem" }}>
        Вече имате акаунт?{" "}
        <span
          style={{ color: "#3498db", cursor: "pointer" }}
          onClick={onSwitchToLogin}
        >
          Влезте тук
        </span>
      </p>
    </div>
  );
}

export default Register;