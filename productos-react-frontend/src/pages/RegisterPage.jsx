import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { register } from '../services/authServices'

export default function RegisterPage() {
  const navigate = useNavigate()

  const [form, setForm] = useState({
    username: '',
    email: '',
    password: '',
  })

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }
  const handleSubmit = async (e) => {
    e.preventDefault()
    await register(form)
    navigate('/login')
  }

  return (
    <div className="container-sm mb-3 ">
      <form onSubmit={handleSubmit} className="p-4">
        <div className="form-group">
          <input
            name="username"
            type="text"
            className="form-control"
            onChange={handleChange}
            placeholder="Nombre de usuario"
          />
        </div>
        <div className="form-group">
          <input
            name="email"
            type="email"
            className="form-control"
            onChange={handleChange}
            placeholder="Correo electronico"
          />
        </div>

        <div className="form-group">
          <input
            name="password"
            className="form-control"
            type="password"
            onChange={handleChange}
            placeholder="Contraseña"
          />
        </div>

        <button className="btn btn-primary">Registrarse</button>
      </form>
    </div>
  )
}
