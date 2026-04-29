import { Link, useNavigate } from 'react-router-dom'

const ProductCard = ({ product, onDelete }) => {
  const navigate = useNavigate()
  return (
    <div className="card p-3 mb-3">
      <h5>{product.nombre}</h5>
      <p>{product.descripcion}</p>
      <p>
        <strong>${product.precio}</strong>
      </p>
      <p>stock:{product.stock}</p>

      <button onClick={() => navigate(`/products/${product.id}/edit`)}>Editar</button>
      <button onClick={() => onDelete && onDelete(product.id)} className="btn btn-danger">
        Eliminar
      </button>
    </div>
  )
}

export default ProductCard
