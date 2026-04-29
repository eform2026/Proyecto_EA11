import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import ProductForm from '../components/products/ProductForm'
import { create, update, getById } from '../services/productService'

function productFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [initialData, setInitialData] = useState(null)
  useEffect(() => {
    if (id) {
      getById(id).then(setInitialData)
    }
  }, [id])

  const handleSubmit = async (data) => {
    try {
      if (id) {
        await update(id, data)
      } else {
        console.log(data)
        await create(data)
      }
      navigate('/products')
    } catch (error) {
      console.error(error)
    }
  }

  if (id && !initialData) return <p> Cargando...</p>

  return (
    <div>
      <h1>{id ? 'Editar Producto' : 'Crear Producto'}</h1>
      <ProductForm
        initialData={initialData}
        onSubmit={handleSubmit}
        submitLabel={id ? 'Actualizar' : 'Crear'}
      />
    </div>
  )
}

export default productFormPage
