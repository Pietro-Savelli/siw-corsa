import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"

export default defineConfig({
  plugins: [react()],
  // Path relativi negli asset generati: necessario perché in produzione
  // dist/ viene copiato dentro resources/static e servito da Spring Boot,
  // non da un dominio dedicato con root "/".
  base: "./",
  server: {
    proxy: {
      // Tutte le chiamate a /api/* durante "npm run dev" vengono
      // reindirizzate al backend Spring Boot su :8080. Evita CORS in
      // sviluppo: il browser vede solo richieste verso localhost:5173.
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },

  build: {
    rollupOptions: {
      output: {
        entryFileNames: 'assets/index-react.js',
        chunkFileNames: 'assets/[name].js',
        assetFileNames: 'assets/[name].[ext]'
      }
    }
  }
})