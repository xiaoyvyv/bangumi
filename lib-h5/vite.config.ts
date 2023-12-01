import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    base: "./",
    server: {
        port: 5173
    },
    plugins: [vue()],
    build: {
        chunkSizeWarningLimit: 3000,
        outDir: "../app/src/main/assets/h5/",
        emptyOutDir: true
    }
})
