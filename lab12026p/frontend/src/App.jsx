import { useEffect, useState } from 'react'
import { api } from './api'

const emptyCustomer = { firstName: '', lastName: '', accountNumber: '', balance: '' }
const emptyTransfer = { senderAccountNumber: '', receiverAccountNumber: '', amount: '' }

function formatMoney(value) {
  return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }).format(value || 0)
}

function formatDate(value) {
  if (!value) return 'Sin fecha'
  return new Intl.DateTimeFormat('es-CO', { dateStyle: 'medium', timeStyle: 'short' }).format(new Date(value))
}

function App() {
  const [customers, setCustomers] = useState([])
  const [customer, setCustomer] = useState(emptyCustomer)
  const [transfer, setTransfer] = useState(emptyTransfer)
  const [lookupId, setLookupId] = useState('')
  const [lookupResult, setLookupResult] = useState(null)
  const [accountNumber, setAccountNumber] = useState('')
  const [transactions, setTransactions] = useState([])
  const [activeView, setActiveView] = useState('overview')
  const [notice, setNotice] = useState({ type: '', text: '' })
  const [loading, setLoading] = useState(false)

  async function loadCustomers() {
    try {
      setCustomers(await api.getCustomers())
    } catch (error) {
      setNotice({ type: 'error', text: error.message })
    }
  }

  useEffect(() => { loadCustomers() }, [])

  function update(setter, field, value) {
    setter((current) => ({ ...current, [field]: value }))
  }

  async function handleCreate(event) {
    event.preventDefault()
    setLoading(true)
    try {
      await api.createCustomer({ ...customer, balance: Number(customer.balance) })
      setCustomer(emptyCustomer)
      await loadCustomers()
      setNotice({ type: 'success', text: 'Cliente creado correctamente.' })
    } catch (error) {
      setNotice({ type: 'error', text: error.message })
    } finally { setLoading(false) }
  }

  async function handleLookup(event) {
    event.preventDefault()
    if (!lookupId) return
    setLoading(true)
    try {
      setLookupResult(await api.getCustomer(lookupId))
      setNotice({ type: 'success', text: 'Cliente encontrado.' })
    } catch (error) {
      setLookupResult(null)
      setNotice({ type: 'error', text: error.message })
    } finally { setLoading(false) }
  }

  async function handleTransfer(event) {
    event.preventDefault()
    setLoading(true)
    try {
      await api.transfer({ ...transfer, amount: Number(transfer.amount) })
      setTransfer(emptyTransfer)
      await loadCustomers()
      setNotice({ type: 'success', text: 'Transferencia registrada y saldos actualizados.' })
    } catch (error) {
      setNotice({ type: 'error', text: error.message })
    } finally { setLoading(false) }
  }

  async function handleHistory(event) {
    event.preventDefault()
    if (!accountNumber) return
    setLoading(true)
    try {
      setTransactions(await api.getTransactions(accountNumber))
      setNotice({ type: 'success', text: 'Historial actualizado.' })
    } catch (error) {
      setTransactions([])
      setNotice({ type: 'error', text: error.message })
    } finally { setLoading(false) }
  }

  const totalBalance = customers.reduce((total, item) => total + (item.balance || 0), 0)

  return (
    <main className="shell">
      <aside className="sidebar">
        <div className="brand"><span className="brand-mark">L</span><span>ledger<span className="brand-dot">.</span></span></div>
        <p className="eyebrow">LAB 12026P / CONTROL</p>
        <nav>
          <button className={activeView === 'overview' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveView('overview')}><span>01</span> Resumen</button>
          <button className={activeView === 'customers' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveView('customers')}><span>02</span> Clientes</button>
          <button className={activeView === 'transfer' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveView('transfer')}><span>03</span> Transferir</button>
          <button className={activeView === 'history' ? 'nav-item active' : 'nav-item'} onClick={() => setActiveView('history')}><span>04</span> Historial</button>
        </nav>
        <div className="sidebar-foot"><span className="status-dot" /> API conectada<div className="api-url">localhost:8080</div></div>
      </aside>

      <section className="content">
        <header className="topbar"><div><p className="eyebrow">SISTEMA DE CUENTAS</p><h1>{activeView === 'overview' ? 'Resumen operativo' : activeView === 'customers' ? 'Directorio de clientes' : activeView === 'transfer' ? 'Nueva transferencia' : 'Historial de movimientos'}</h1></div><div className="date-chip">12 SEP 2026 <span>•</span> EN LÍNEA</div></header>
        {notice.text && <div className={`notice ${notice.type}`} role="status">{notice.text}<button onClick={() => setNotice({ type: '', text: '' })}>×</button></div>}

        {activeView === 'overview' && <>
          <div className="hero-line"><div><p className="section-kicker">VISTA GENERAL</p><h2>El dinero en movimiento.</h2></div><button className="button dark" onClick={() => setActiveView('transfer')}>+ Nueva transferencia</button></div>
          <div className="stats"><article><span>Clientes registrados</span><strong>{customers.length}</strong><small>cuentas activas</small></article><article className="accent"><span>Saldo administrado</span><strong>{formatMoney(totalBalance)}</strong><small>saldo consolidado</small></article><article><span>Estado del servicio</span><strong className="healthy">Operativo</strong><small>API / Spring Boot</small></article></div>
          <section className="panel"><div className="panel-heading"><div><p className="section-kicker">CUENTAS</p><h3>Clientes recientes</h3></div><button className="text-button" onClick={() => setActiveView('customers')}>Ver directorio →</button></div><CustomerTable customers={customers.slice(0, 5)} /></section>
        </>}

        {activeView === 'customers' && <div className="two-columns"><section className="panel"><div className="panel-heading"><div><p className="section-kicker">GET /api/customers</p><h3>Directorio completo</h3></div><button className="icon-button" title="Actualizar clientes" onClick={loadCustomers}>↻</button></div><CustomerTable customers={customers} /></section><section className="panel form-panel"><p className="section-kicker">POST /api/customers</p><h3>Registrar cliente</h3><form onSubmit={handleCreate}><Field label="Nombre" value={customer.firstName} onChange={(value) => update(setCustomer, 'firstName', value)} required /><Field label="Apellido" value={customer.lastName} onChange={(value) => update(setCustomer, 'lastName', value)} required /><Field label="Número de cuenta" value={customer.accountNumber} onChange={(value) => update(setCustomer, 'accountNumber', value)} required /><Field label="Saldo inicial" type="number" min="0" value={customer.balance} onChange={(value) => update(setCustomer, 'balance', value)} required /><button className="button dark full" disabled={loading}>Crear cliente</button></form><hr /><p className="section-kicker">GET /api/customers/:id</p><h3>Consultar por ID</h3><form className="inline-form" onSubmit={handleLookup}><input type="number" min="1" placeholder="ID del cliente" value={lookupId} onChange={(event) => setLookupId(event.target.value)} /><button className="button outline" disabled={loading}>Consultar</button></form>{lookupResult && <div className="lookup-result"><strong>{lookupResult.firstName} {lookupResult.lastName}</strong><span>#{lookupResult.id} · {lookupResult.accountNumber}</span><b>{formatMoney(lookupResult.balance)}</b></div>}</section></div>}

        {activeView === 'transfer' && <div className="transfer-layout"><section className="transfer-intro"><p className="section-kicker">POST /api/transactions</p><h2>Mueve fondos entre cuentas.</h2><p>Registra una transferencia y el backend actualizará automáticamente los saldos de origen y destino.</p><div className="route-line"><span>Cuenta origen</span><i>→</i><span>Cuenta destino</span></div></section><section className="panel form-panel"><h3>Detalles de la operación</h3><form onSubmit={handleTransfer}><Field label="Cuenta remitente" value={transfer.senderAccountNumber} onChange={(value) => update(setTransfer, 'senderAccountNumber', value)} required /><Field label="Cuenta receptora" value={transfer.receiverAccountNumber} onChange={(value) => update(setTransfer, 'receiverAccountNumber', value)} required /><Field label="Monto a transferir" type="number" min="1" step="0.01" value={transfer.amount} onChange={(value) => update(setTransfer, 'amount', value)} required /><button className="button dark full" disabled={loading}>Confirmar transferencia <span>→</span></button></form></section></div>}

        {activeView === 'history' && <section className="panel history-panel"><div className="panel-heading"><div><p className="section-kicker">GET /api/transactions/:accountNumber</p><h3>Movimientos por cuenta</h3></div></div><form className="history-search" onSubmit={handleHistory}><input placeholder="Escribe un número de cuenta" value={accountNumber} onChange={(event) => setAccountNumber(event.target.value)} required /><button className="button dark" disabled={loading}>Buscar movimientos</button></form>{transactions.length ? <div className="transaction-list">{transactions.map((item) => <div className="transaction" key={item.id}><div className="transaction-icon">↗</div><div><strong>{item.senderAccountNumber} <span>→</span> {item.receiverAccountNumber}</strong><small>{formatDate(item.timestamp)} · operación #{item.id}</small></div><b>{formatMoney(item.amount)}</b></div>)}</div> : <div className="empty-state">Busca una cuenta para consultar sus movimientos.</div>}</section>}
      </section>
    </main>
  )
}

function Field({ label, value, onChange, type = 'text', ...props }) {
  return <label className="field"><span>{label}</span><input type={type} value={value} onChange={(event) => onChange(event.target.value)} {...props} /></label>
}

function CustomerTable({ customers }) {
  if (!customers.length) return <div className="empty-state">Aún no hay clientes registrados.</div>
  return <div className="table-wrap"><table><thead><tr><th>Cliente</th><th>Cuenta</th><th>Saldo</th></tr></thead><tbody>{customers.map((item) => <tr key={item.id}><td><strong>{item.firstName} {item.lastName}</strong><small>ID {item.id}</small></td><td><code>{item.accountNumber}</code></td><td className="amount">{formatMoney(item.balance)}</td></tr>)}</tbody></table></div>
}

export default App