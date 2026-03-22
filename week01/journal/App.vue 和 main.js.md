# App.vue 和 main.js

main.js 里面的代码其实就是 `<script>` 标签里面的代码

```javascript
// 导入
import { createApp } from 'vue'  
import { createPinia } from 'pinia'  
  
import App from './App.vue'  
import router from './router'  

// 创建app
const app = createApp(App)  

app.use(createPinia())  
app.use(router)  

// 挂载  
app.mount('#app')
```

app.vue 里面的则是 `<div id="app">` 中的内容

```javascript
<template>  
    <router-view/>  // 根据路由跳转展示页面
</template>  
  
<script setup>  
import {RouterView} from 'vue-router'  
</script>  
  
// 全局css样式
<style>
* {  
    margin: 0;  
    padding: 0;  
    box-sizing: border-box;  
}  
</style>
