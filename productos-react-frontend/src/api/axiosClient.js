import axios from 'axios'

const axiosClient = axios.create({
  baseURL: 'https://products-react-api-hzc6gzege9gtd8dq.centralus-01.azurewebsites.net',
})
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}` // TODO: Implementar template literals
  }
  return config
})

export default axiosClient
