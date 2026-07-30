// oss-upload.js
import { getSts } from '@/api/oss'

export default {
  data() {
    return {
      ossConfig: {
        accessKeyId: '',
        signature: '',
        expire: '',
        host: '',
        dir: '',
        policy: ''
      },
      isGettingSts: false,
      stsPromise: null
    }
  },
  methods: {
    /**
     * 检查STS Token是否有效
     */
    isStsTokenValid() {
      if (!this.ossConfig.expire) return false
      const now = Math.floor(Date.now() / 1000)
      const expiration = Number.parseInt(this.ossConfig.expire)
      // 提前5分钟刷新
      return now < expiration - 5 * 60
    },

    /**
     * 获取OSS配置
     */
    async getOssConfig(Osspath) {
      console.log(Osspath,'Osspath');
      
      if (this.isStsTokenValid()) {
        return Promise.resolve(this.ossConfig)
      }

      if (this.isGettingSts && this.stsPromise) {
        return this.stsPromise
      }

      this.isGettingSts = true

      try {
        // 根据用户角色获取不同的STS
        let res
        res = await getSts(Osspath)

        const data = res.data
        this.ossConfig = {
          accessKeyId: data.accessKeyId,
          signature: data.signature,
          expire: data.expire,
          host: data.host,
          dir: data.dir,
          policy: data.policy
        }

        return this.ossConfig
      } catch (err) {
        console.error('获取OSS配置失败:', err)
        throw err
      } finally {
        this.isGettingSts = false
        this.stsPromise = null
      }
    },

    /**
     * 上传单个文件到OSS
     * @param {Object} file 从uni.chooseImage等API获取的文件对象
     * @param {Function} progressCallback 进度回调函数
     */
    uploadFileToOss(file, progressCallback,Osspath) {
      return new Promise(async (resolve, reject) => {
        try {
          // 获取OSS配置
          await this.getOssConfig(Osspath)

          // 生成文件名
          const name = file.name || 'unknown-file'
          const index = name.lastIndexOf('.')
          const suffix = index > -1 ? name.substring(index) : ''
          const uid = Math.random().toString(36).substr(2, 15)
          const fileName = uid + suffix
          const fullFileName = this.ossConfig.dir + fileName

          // 准备上传参数
          const formData = {
            key: fullFileName,
            policy: this.ossConfig.policy,
            OSSAccessKeyId: this.ossConfig.accessKeyId,
            signature: this.ossConfig.signature,
            success_action_status: '200'
          }

          // 确定上传地址（处理开发环境和生产环境）
          let uploadUrl = this.ossConfig.host
          // if (process.env.NODE_ENV !== 'production') {
          //   uploadUrl = '/oss-proxy'
          // }

          // 执行上传
          uni.uploadFile({
            url: uploadUrl,
            filePath: file.path,
            name: 'file',
            formData: formData,
            timeout: 60000, // 60秒超时
            header: {
              'Content-Type': 'multipart/form-data'
            },
            // 进度回调
            onProgressUpdate: (res) => {
              if (progressCallback && typeof progressCallback === 'function') {
                progressCallback(res.progress, file.index)
              }
            },
            success: (uploadRes) => {
              if (uploadRes.statusCode >= 200 && uploadRes.statusCode < 300) {
                const result = {
                  url: `${this.ossConfig.host}/${fullFileName}`,
                  fileName: fileName,
                  fullFileName: fullFileName,
                  status: uploadRes.statusCode,
                  index: file.index // 保留索引，方便对应原始文件
                }
                resolve(result)
              } else {
                reject(new Error(`上传失败，状态码: ${uploadRes.statusCode}`))
              }
            },
            fail: (err) => {
              console.error(`OSS上传失败 (${file.index}):`, err)
              reject({ err, index: file.index })
            }
          })
        } catch (err) {
          reject({ err, index: file.index })
        }
      })
    },

    /**
     * 选择图片并上传（支持多张）
     * @param {Object} options 选择图片的配置
     * @param {Function} progressCallback 进度回调 (progress, index) => {}
     * @param {Boolean} concurrent 是否并发上传，默认true
     */
    chooseAndUploadImage(options = {}, progressCallback, concurrent = true,Osspath) {
      return new Promise((resolve, reject) => {
        // 默认配置，支持多张
        const chooseOptions = {
          count: 9, // 默认可选9张
          sizeType: ['original', 'compressed'],
          sourceType: ['album', 'camera'],
          ...options
        }

        // 选择图片
        uni.chooseImage({
          ...chooseOptions,
          success: (res) => {
            const tempFilePaths = res.tempFilePaths
            const tempFiles = res.tempFiles || []

            if (tempFilePaths.length === 0) {
              // reject(new Error('未选择图片'))
              return
            }

            // 包装文件信息，添加索引
            const files = tempFilePaths.map((path, index) => ({
              path,
              name: tempFiles[index]?.name || `image-${Date.now()}-${index}.png`,
              index // 记录原始索引，方便对应
            }))

            // 上传所有文件
            if (concurrent) {
              // 并发上传
              Promise.all(
                files.map(file => this.uploadFileToOss(file, progressCallback,Osspath))
              ).then(results => {
                // 按原始顺序排序
                resolve(results.sort((a, b) => a.index - b.index))
              }).catch(err => {
                reject(err)
              })
            } else {
              // 串行上传
              const results = []
              const uploadNext = (index) => {
                if (index >= files.length) {
                  resolve(results)
                  return
                }

                this.uploadFileToOss(files[index], progressCallback,Osspath)
                  .then(result => {
                    results.push(result)
                    uploadNext(index + 1)
                  })
                  .catch(err => {
                    reject(err)
                  })
              }

              uploadNext(0)
            }
          },
          fail: reject
        })
      })
    }
  }
}
