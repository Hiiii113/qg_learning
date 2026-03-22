# 封装 axios

axios 的请求已经很完善了，但是我们还可以根据项目的实际情况进行个人的封装
封装主要需要实现的功能如下：

1. 实现请求拦截
2. 实现响应拦截
3. 常见错误信息处理
4. 请求头设置
5. api 集中式管理

## 1. Axios 中文官网

有任何疑问可以参考该文档：[Axios中文文档 | Axios中文网](https://www.axios-http.cn/)

## 2. 安装axios

在项目文件夹下使用终端运行`npm install axios`以安装
## 3. 封装

**在适合的位置新建一个 js 文件，然后在里面封装 axios 请求**

首先，axios请求可以使用`axios({})`或者`axios.post()`的方式发送，但是，axios还存在其他的方法可以发送请求

我们可以创建一个 axios 的实例，并且使用相关的方法

### 3.1 导入

首先，使用import语句导入axios
```javascript
import axios from 'axios';
```

### 3.2 创建应用实例

使用 `axios.create({})` 方法创建一个实例
其中两个参数的解释：

- `baseURL`：请求的基础 url，会和具体的方法路径拼接而成具体的地址
- `timeout`：超时时间，设定成10000就是10秒钟未成功视为超时

```javascript
const serviceAxios = axios.create({
	baseURL: 'http://localhost:8080/pay_system_war_exploded',
	timeout: 10000,
});
```

### 3.3 设置请求拦截

请求拦截的意思就是：在请求发出之前我们可能需要对请求头进行处理，例如附加一些 token 之类的，这个时候就需要使用请求拦截器把请求拦截下来，处理业务逻辑

```javascript
// 创建请求拦截
serviceAxios.interceptors.request.use(
    (config) => {
        // 如果开启 token 认证
        if (serverConfig.useTokenAuthorization) {
            // 请求头携带 token
            config.headers["Authorization"] = localStorage.getItem("token");
        }
        // 设置请求头
        if (!config.headers["content-type"]) { // 如果没有设置请求头
            if (config.method === 'post') {
                // post 请求
                config.headers["content-type"] = "application/x-www-form-urlencoded";
                config.data = qs.stringify(config.data); // 序列化,比如表单数据
            } else {
                config.headers["content-type"] = "application/json"; // 默认类型
            }
        }
        console.log("请求配置", config);
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);
```

### 3.4 设置响应拦截

axios的返回结果默认携带很多东西，所以设置一个响应拦截可以先对结果进行处理，再将结果返回给调用者

```javascript
// 创建响应拦截
serviceAxios.interceptors.response.use(
  (res) => {
    let data = res.data;
    // 处理自己的业务逻辑，比如判断 token 是否过期等等
    // 代码块
    return data;
  },
  (error) => {
    let message = "";
    if (error && error.response) {
      switch (error.response.status) {
        case 302:
          message = "接口重定向了！";
          break;
        case 400:
          message = "参数不正确！";
          break;
        case 401:
          message = "您未登录，或者登录已经超时，请先登录！";
          break;
        case 403:
          message = "您没有权限操作！";
          break;
        case 404:
          message = `请求地址出错: ${error.response.config.url}`;
          break;
        case 408:
          message = "请求超时！";
          break;
        case 409:
          message = "系统已存在相同数据！";
          break;
        case 500:
          message = "服务器内部错误！";
          break;
        case 501:
          message = "服务未实现！";
          break;
        case 502:
          message = "网关错误！";
          break;
        case 503:
          message = "服务不可用！";
          break;
        case 504:
          message = "服务暂时无法访问，请稍后再试！";
          break;
        case 505:
          message = "HTTP 版本不受支持！";
          break;
        default:
          message = "异常问题，请联系管理员！";
          break;
      }
    }
    return Promise.reject(message);
  }
);

```

## 4. 使用

现在，就成功的封装了axios，只需要在其他JS文件或者`<script>`块使用import语句导入所需要的post或者get即可