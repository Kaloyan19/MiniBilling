import { useState } from "react";

function Login({ onLogin, onSwitchToRegister }) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  const handleLogin = async () => {
    setError("");

    try {
      const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });

      if (response.ok) {
        const token = await response.text();
        const payload = JSON.parse(atob(token.split('.')[1]));
        const role = payload.role;
        
        localStorage.setItem("token", token);
        localStorage.setItem("username", username);
        localStorage.setItem("role", role);
        onLogin(token, role);
    } else {
        const text = await response.text();
        setError(text || "Невалидно потребителско име или парола.");
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
        <button onClick={handleLogin}>Вход</button>
      </div>
      {error && <p className="error">{error}</p>}
      <p style={{ marginTop: "1rem" }}>
        Нямате акаунт?{" "}
        <span
          style={{ color: "#3498db", cursor: "pointer" }}
          onClick={onSwitchToRegister}
        >
          Регистрирайте се
        </span>
      </p>
    </div>
  );
}

export default Login;