const { request } = require('../../utils/request')

Page({
  data: {
    result: '（还没测试）'
  },
  onPing() {
    request({ url: '/api/ping' }).then(data => {
      this.setData({ result: '连通成功：' + JSON.stringify(data) })
    }).catch(() => {
      this.setData({ result: '连接失败，检查后端是否启动、baseUrl 是否填对' })
    })
  }
})
