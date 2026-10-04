import { useEffect, useState } from "react";

function ErrorLogsPage({token}){
    const [logs, setLogs] = useState([]);
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        fetch("http://localhost:8080/logs", {
            headers: { Authorization: `Bearer ${token}`}
        })
        .then(resolve => {
            if(!resolve.ok) throw new Error("Грешка: " + resolve.status);
            return resolve.json()
        })
        .then(data => {
            setLogs(data);
            setLoading(false); 
        })
        .catch(err => {
            setError(err.message);
            setLoading(false); 
        });
    }, []);

    return (
        <div>
            <h2>Грешки при импорт</h2>

            {loading && <p>Зареждане...</p>}
            {error && <p className="error">{error}</p>}

            {!loading && !error && (
                <table>
                    <thead>
                        <tr>
                            <th>Тип</th>
                            <th>Съобщение</th>
                        </tr>
                    </thead>
                    <tbody>
                        {logs.map((log, index) => (
                            <tr key={index} style={{
                                color: log.level === "ERROR" ? "#e74c3c" : "#f39c12"
                            }}>
                                <td>{log.level}</td>
                                <td>{log.message}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </div>
    );
}

export default ErrorLogsPage;