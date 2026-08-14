function InvoiceTable({ invoice, formatDate, formatAmount }) {
  return (
    <div className="invoice">
      <h2>Фактура №{invoice.documentNumber}</h2>
      <p>Клиент: {invoice.consumer}</p>
      <p>Референтен номер: {invoice.reference}</p>
      <p>Дата: {formatDate(invoice.documentDate)}</p>
      <p className="total">Обща сума: {formatAmount(invoice.totalAmount)}</p>
      <p className="total">Обща сума с ДДС: {formatAmount(invoice.totalAmountWithVat)}</p>

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
              <td>{line.name || line.product}</td>
              <td>{line.quantity}</td>
              <td>{line.price}</td>
              <td>{formatAmount(line.amount)}</td>
              <td>{formatDate(line.lineStart)}</td>
              <td>{formatDate(line.lineEnd)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {invoice.vat && invoice.vat.length > 0 && (
        <div>
          <h3>ДДС:</h3>
          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Линии</th>
                <th>Процент</th>
                <th>Сума</th>
              </tr>
            </thead>
            <tbody>
              {invoice.vat.map((vat) => (
                <tr key={vat.index}>
                  <td>{vat.index}</td>
                  <td>{vat.lines.join(", ")}</td>
                  <td>{vat.percentage}%</td>
                  <td>{formatAmount(vat.amount)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default InvoiceTable;