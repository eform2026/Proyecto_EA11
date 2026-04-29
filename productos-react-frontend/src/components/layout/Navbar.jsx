import { Link } from 'react-router-dom'
import { Routes, Route } from 'react-router-dom'

const Navbar = () => {
  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <Link className="navbar-brand" to="/">
        Productos SENA
      </Link>
      <button
        className="navbar-toggler"
        type="button"
        data-bs-toggle="collapse"
        data-bs-target="#navbarNav"
        aria-controls="navbarNav"
        aria-expanded="false"
        aria-label="Toggle navigation"
      >
        <span className="navbar-toggler-icon"></span>
      </button>
      <div className=" navbar-collapse" id="navbarNav">
        <ul className="navbar-nav">
          <li className="nav-item active">
            <Link className="nav-link" to="/">
              Home <span className="sr-only">(current)</span>
            </Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link" to="/products">
              Productos
            </Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link" to="/login">
              login
            </Link>
          </li>
          <li className="nav-item">
            <Link className="nav-link " to="/register">
              Registro de sesion
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  )
}

export default Navbar
