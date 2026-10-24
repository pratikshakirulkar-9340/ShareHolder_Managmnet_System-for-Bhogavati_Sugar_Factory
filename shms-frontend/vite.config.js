import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Proxies /api/* to the Spring Boot backend during local dev, so the browser
// never has to deal with CORS at all while you're running `npm run dev`.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
