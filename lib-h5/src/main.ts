import {createApp} from 'vue';
import App from './App.vue';
import router from "./router";
import './style.scss';

// PC端调试时加载 light.css
if (window.android == null) {
    import("./assets/css/light.css");
}

createApp(App)
    .use(router)
    .mount('#app');
