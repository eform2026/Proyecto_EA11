import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '../services/authServices'

export default function LoginPage() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    email: '',
    password: '',
  })
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }
  const handleSubmit = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      const res = await login(form)
      localStorage.setItem('token', res.token)
      navigate('/products')
    } catch {
      setError('Credenciales invalidas')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="P-4">
      <form onSubmit={handleSubmit}>
        <input name="email" className="form-control" onChange={handleChange} />
        <input name="password" className="form-control" type="password" onChange={handleChange} />
        <button className="btn btn-primary" disabled={loading}>
          {loading ? 'Cargando...' : 'Login'}
        </button>
        {error && <p>{error}</p>}
      </form>
    </div>
  )
}
