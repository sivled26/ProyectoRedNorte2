import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      "/bff": {
        target: "http://localhost:8080", // tu BFF
        changeOrigin: true,
        secure: false
      }
    }
  }
})
