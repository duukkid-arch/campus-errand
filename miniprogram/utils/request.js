// 全项目只有这一个地方发网络请求。
// 统一拼 baseUrl、统一带 token（放请求头，不拼在 URL 里）、统一处理错误。
const app = getApp()

function request(options) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: app.globalData.baseUrl + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': app.globalData.token || ''
      },
      success(res) {
        const body = res.data
        if (res.statusCode === 200 && body.code === 0) {
          resolve(body.data)
        } else if (res.statusCode === 401) {
          wx.showToast({ title: '请先登录', icon: 'none' })
          reject(body)
        } else {
          wx.showToast({ title: (body && body.msg) || '请求失败', icon: 'none' })
          reject(body)
        }
      },
      fail(err) {
        wx.showToast({ title: '网络异常，检查后端是否启动', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = { request }
