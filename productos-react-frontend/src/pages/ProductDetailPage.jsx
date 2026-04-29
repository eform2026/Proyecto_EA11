import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getById } from '../services/productService'

const ProductDetailPage = () => {
  const { id } = useParams()
  const navigate = useNavigate()

  const [product, setProduct] = useState(null)

  useEffect(() => {
    getById(id).then(setProduct)
  }, [id])
  if (!product)
    return (
      <>
        <div class="d-flex justify-content-center">
          <div className="spinner-border " role="status">
            <span className="visually-hidden">Loading...</span>
          </div>

          <div class="alert alert-danger" role="alert">
            Producto No Encontrado.
          </div>
        </div>
      </>
    )

  return (
    <div className="container py-4">
      <div className="card p-4">
        <h2>{product.nombre}</h2>
        <p>{product.descripcion}</p>
        <h4>${product.precio}</h4>

        <button className="btn btn-secondary mt-3" onClick={() => navigate('/products')}>
          Volver
        </button>
      </div>
    </div>
  )
}

export default ProductDetailPage
