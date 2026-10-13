const API_BASE_URL = (import.meta.env.VITE_API_URL || 'https://arquisoft-uidq.onrender.com/api').replace(/\/+$/, '')

async function request(path, options = {}) {
  const cleanPath = path.startsWith('/') ? path : `/${path}`
  const endpoint = cleanPath.startsWith('/api')
    ? `${API_BASE_URL}${cleanPath.slice(4)}`
    : `${API_BASE_URL}${cleanPath}`

  const response = await fetch(endpoint, {
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