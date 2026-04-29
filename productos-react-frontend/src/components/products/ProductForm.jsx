import { useState } from 'react'

export default function ProductForm({ initialData = {}, onSubmit, submitLabel = 'Guardar' }) {
  const [form, setForm] = useState({
    nombre: initialData?.nombre || '',
    precio: initialData?.precio || '',
    descripcion: initialData?.descripcion || '',
    stock: initialData?.stock || '',
  })

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    })
  }
  const handleSubmit = (e) => {
    e.preventDefault()
    onSubmit(form)
  }

  return (
    <form onSubmit={handleSubmit}>
      <input
        className="form-control mb-3"
        name="nombre"
        placeholder="Nombre"
        value={form.nombre}
        onChange={handleChange}
      />
      <input
        className="form-control mb-3"
        name="precio"
        placeholder="Precio"
        value={form.precio}
        onChange={handleChange}
      />
      <textarea
        className="form-control mb-3"
        name="descripcion"
        placeholder="Descripcion"
        value={form.descripcion}
        onChange={handleChange}
      />
      <input
        className="form-control mb-3"
        name="stock"
        placeholder="Stock"
        value={form.stock}
        onChange={handleChange}
      />

      <button className="btn btn-primary">{submitLabel}</button>
    </form>
  )
}
