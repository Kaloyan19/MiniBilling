const BASE_URL = "http://localhost:8080/invoices";

export async function loadOrGenerateInvoice(reference, from, to) {
    let response = await fetch(
        `${BASE_URL}/${reference}?from=${from}&to=${to}`
    );

    if (response.status === 204) {
        response = await fetch(
            `${BASE_URL}/${reference}?from=${from}&to=${to}`,
            { method: "POST" }
        );
    }

    return response;
}