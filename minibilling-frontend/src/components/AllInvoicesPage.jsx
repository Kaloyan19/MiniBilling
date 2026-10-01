import { useState, useEffect } from "react";

function AllInvoicesPage({ token }){
    const [invoices, setInvoices] = useState([]);
    const [error, setError] = useState("");
    const [reference, setReference] = useState("");

    useEffect(() => {
        fetch("http://localhost:8080/invoices", {
            headers: { Authorization: `Bearer ${token}`}
        })
        .then(resolve => {
            if(!resolve.ok) throw new Error("Грешка: " + resolve.status);
            return resolve.json()
        })
        .then(data => setInvoices(data))
        .catch(err => setError(err.message));
    }, []);

    const handleFilter = async () => {
        const url = reference
        ? `http://localhost:8080/invoices/by-reference?reference=${reference}`
        : `http://localhost:8080/invoices`;

        const res = await fetch(url, {
            headers: { Authorization: `Bearer ${token}`}
        });
        const data = await res.json();
        setInvoices(data);
    };

    const handleClear = async () => {
        setReference("");
        const res = await fetch("http://localhost:8080/invoices", {
            headers: { Authorization: `Bearer ${token}` }
        });
        const data = await res.json();
        setInvoices(data);
    };

    return (
        <div>
            <h2>Всички фактури</h2>

            <div className="form">
                <input
                placeholder="Reference номер"
                value={reference}
                onChange={(e) => setReference(e.target.value)}Ъ
                onKeyDown={(e) => e.key === "Enter" && handleFilter()}
                />
                <button onClick={handleFilter}>Търси</button>
                <button onClick={handleClear}>Покажи всички</button>
            </div>
            {error && <p className="error">{error}</p>}
            <table>
                <thead>
                    <tr>
                        <th>Номер</th>
                        <th>Клиент</th>
                        <th>Refference</th>
                        <th>Сума</th>
                        <th>Дати</th>
                    </tr>
                </thead>
                <tbody>
                    {invoices.map((invoice, index) => (
                            <tr key={index}>
                            <td>{invoice.documentNumber}</td>
                            <td>{invoice.consumer}</td>
                            <td>{invoice.reference}</td>
                            <td>{invoice.totalAmountWithVat}</td>
                            <td>{new Date(invoice.documentDate).toLocaleDateString("bg-BG")}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default AllInvoicesPage;