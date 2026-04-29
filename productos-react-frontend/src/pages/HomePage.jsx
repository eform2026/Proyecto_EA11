import { useEffect, useState } from 'react'
import { getAll } from '../services/productService'
import ProductCard from '../components/products/ProductCard'

export default function HomePage() {
  const [products, setProducts] = useState([])
  const [filter, setFilter] = useState('')

  useEffect(() => {
    getAll().then(setProducts)
  }, [])

  const filteredProducts = products.filter((p) =>
    p.nombre.toLowerCase().includes(filter.toLowerCase())
  )

  return (
    <div className="container py-4">
      <input
        className="form-control mb-4"
        placeholder="Buscar..."
        value={filter}
        onChange={(e) => setFilter(e.target.value)}
      />

      <div className="row">
        {filteredProducts.map((products) => (
          <div key={products.id} className="col-md-4 mb-4">
            <ProductCard product={products} />
          </div>
        ))}
      </div>
    </div>
  )
}
