import { useState } from "react";

function ImportPage({ token }) {
  const [file, setFile] = useState(null);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [fileName, setFileName] = useState("");

  const handleImport = async () => {
    if (!file) {
      setError("Моля изберете файл!");
      return;
    }

    setError("");
    setResult(null);
    setLoading(true);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const response = await fetch("http://localhost:8080/import", {
        method: "POST",
        headers: { Authorization: `Bearer ${token}` },
        body: formData
      });

      if (response.ok) {
        const data = await response.json();
        setResult(data);
      } else {
        const text = await response.text();
        setError(text || "Грешка при импорт.");
      }
    } catch (e) {
      setError("Грешка при свързване със сървъра.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
        <h2>Импорт на CSV файл</h2>
        <div className="form">
            <label className="file-label">
                <input
                    type="file"
                    accept=".csv"
                    style={{ display: "none" }}
                    onChange={(e) => {
                        setFile(e.target.files[0]);
                        setFileName(e.target.files[0]?.name || "");
                    }}
                />
                📁 {fileName || "Изберете CSV файл"}
            </label>
            <button onClick={handleImport} disabled={loading}>
                {loading ? "Импортиране..." : "Импортирай"}
            </button>
        </div>

      {error && <p className="error">{error}</p>}

      {result && (
        <div className="invoice">
          <p>✅ Успешни: {result.success}</p>
          <p>❌ Неуспешни: {result.failed}</p>

          {result.errors.length > 0 && (
            <div>
              <h3>Грешки:</h3>
              <table>
                <thead>
                  <tr>
                    <th>Ред</th>
                    <th>Данни</th>
                    <th>Грешка</th>
                    <th>За поправяне</th>
                  </tr>
                </thead>
                <tbody>
                  {result.errors.map((err, i) => (
                    <tr key={i}>
                      <td>{err.line}</td>
                      <td>{err.data}</td>
                      <td>{err.error}</td>
                      <td>{err.canFix ? "✏️ Да" : "❌ Не"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default ImportPage;