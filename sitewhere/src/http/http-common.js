import axios from 'axios'

export const HTTP = axios.create({
  baseURL: `http://localhost:9090/sitewhere/api/`,
  headers: {
    Authorization: 'Basic YWRtaW46cGFzc3dvcmQ='
  }
})
