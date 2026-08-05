# 前端Vue项目

## 项目说明
这是一个基于Vue的前端项目，支持开发和生产环境的配置。

## 环境配置

### 开发环境配置
在项目根目录下的 `.env.development` 文件中配置环境变量：

```bash
# 开发环境接口地址
VITE_API_URL = /api

# 开发环境跨域代理，支持配置多个
VITE_PROXY = [["/api","开发环境接口地址"],["/oss-proxy","oss配置获取到的域名"]]
```

### 生产环境配置
在项目根目录下的 `.env.production` 文件中配置环境变量：

```bash
# 线上环境接口地址
VITE_API_URL = "线上环境接口地址"
# 线上环境无须配置跨域
```

## 项目运行

### 安装依赖
```bash
npm install
# 或者
yarn install
```

### 开发环境运行
```bash
npm run dev
# 或者
yarn dev
```

### 生产环境打包
```bash
npm run build:pro
# 或者
yarn build:pro
```

## 注意事项
- 开发环境需要配置跨域代理
- 生产环境不需要配置跨域代理
- 确保环境变量文件配置正确