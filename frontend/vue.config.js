module.exports = {
  transpileDependencies: [],
  
  // 开发服务器配置
  devServer: {
    port: 8082, // 强制使用固定端口8082
    host: 'localhost',
    proxy: {
      // 代理所有API请求到后端
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/moods': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/friends': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/notifications': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/files': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },
  
  // 构建配置
  outputDir: '../src/main/resources/static', // 打包后的文件存放位置
  
  // 部署在子路径下，如果不部署在根路径下需要配置
  // publicPath: process.env.NODE_ENV === 'production' ? '/app/' : '/'
}
