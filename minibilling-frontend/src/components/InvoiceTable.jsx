function InvoiceTable({ invoice, formatDate, formatAmount }) {
    return (
      <div className="invoice">
        <h2>Фактура №{invoice.documentNumber}</h2>
        <p>Клиент: {invoice.consumer}</p>
        <p>Референтен номер: {invoice.reference}</p>
        <p>Дата: {formatDate(invoice.documentDate)}</p>
        <p className="total">Обща сума: {formatAmount(invoice.totalAmount)}</p>
  
        <h3>Компоненти на фактурата:</h3>
        <table>
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
    );
  }
  
  export default InvoiceTable;