async function request(path, options = {}) {
  const response = await fetch(path, {
    headers: { 'Content-Type': 'application/json', ...options.headers },
    ...options,
  })

  const text = await response.text()
  let body = null
  try {
    body = text ? JSON.parse(text) : null
  } catch {
    body = text
  }

  if (!response.ok) {
    throw new Error(typeof body === 'string' ? body : body?.message || `Error ${response.status}`)
  }

  return body
}

export const api = {
  getCustomers: () => request('/api/customers'),
  getCustomer: (id) => request(`/api/customers/${encodeURIComponent(id)}`),
  createCustomer: (customer) => request('/api/customers', {
    method: 'POST',
    body: JSON.stringify(customer),
  }),
  transfer: (transaction) => request('/api/transactions', {
    method: 'POST',
    body: JSON.stringify(transaction),
  }),
  getTransactions: (accountNumber) => request(`/api/transactions/${encodeURIComponent(accountNumber)}`),
}