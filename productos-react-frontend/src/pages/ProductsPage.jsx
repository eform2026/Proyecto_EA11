import { useEffect, useState } from 'react'
import { getAll, remove } from '../services/productService'
import ProductCard from '../components/products/ProductCard'
const ProductPage = () => {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  useEffect(() => {
    getAll()
      .then(setProducts)
      .catch(() => setError('error al cargar'))
      .finally(() => setLoading(false))
  }, [])

  const handleDelete = async (id) => {
    if (!confirm('¿eliminar producto?')) return
    await remove(id)
    setProducts(products.filter((p) => p.id !== id))
  }
  if (loading) return <p> cargando... </p>
  if (error) return <p> {error} </p>
  return (
    <div className="row">
      {products.map((p) => (
        <div key={p.id} className="col-md-4">
          <ProductCard product={p} onDelete={handleDelete} />
        </div>
      ))}
    </div>
  )
}
export default ProductPage
