const BASE_URL = "http://localhost:8080/invoices";

export async function loadOrGenerateInvoice(reference, year, month) {
    let response = await fetch(
        `${BASE_URL}/${reference}?year=${year}&month=${month}`
    );

    if (response.status === 204) {
        
        response = await fetch(
            `${BASE_URL}/${reference}?year=${year}&month=${month}`,
            { method: "POST" }
        );
    }

    return response;
}