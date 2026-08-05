// oss配置跟获取oss配置接口
import { getSts } from "@/api/modules/oss"
import { useUserStore } from "@/stores/modules/user"
const userStore = useUserStore()
import axios from "axios"
import { reactive, ref } from "vue"

export const useOssConfig = () => {
  const ossConfig = reactive({
    accessKeyId: "",
    signature: "",
    expire: "",
    host: "",
    dir: "",
    policy: "",
  })

  const isGettingSts = ref(false)
  const stsPromise = ref<Promise<any> | null>(null)

  const isStsTokenValid = () => {
    if (!ossConfig.expire) return false
    const now = Math.floor(Date.now() / 1000)
    const expiration = Number.parseInt(ossConfig.expire)
    return now < expiration - 5 * 60
  }

  const getOssConfig = (file: File, ossPath: string) =>
    new Promise(async (resolve, reject) => {
      try {
        // console.log('开始上传文件:', file.name)

        if (!isStsTokenValid()) {
          if (isGettingSts.value && stsPromise.value) {
            await stsPromise.value
          } else {
            isGettingSts.value = true
            stsPromise.value = getSts(ossPath)
            const res = await stsPromise.value

            ossConfig.accessKeyId = res.data.accessKeyId
            ossConfig.signature = res.data.signature
            ossConfig.expire = res.data.expire
            ossConfig.host = res.data.host
            ossConfig.dir = res.data.dir
            ossConfig.policy = res.data.policy

            isGettingSts.value = false
            stsPromise.value = null
          }
        }

        // 生成文件名
        const name = file.name
        const index = name.lastIndexOf(".")
        const suffix = name.substring(index, name.length)
        const uid = Math.random().toString(36).substr(2, 15)
        const fileName = uid + suffix
        const fullFileName = ossConfig.dir + fileName

        // 创建FormData - 注意字段顺序很重要
        const formData = new FormData()
        formData.append("key", fullFileName)
        formData.append("policy", ossConfig.policy)
        formData.append("OSSAccessKeyId", ossConfig.accessKeyId)
        formData.append("signature", ossConfig.signature)
        // success_action_status 设置为200，这样成功时返回200而不是204
        formData.append("success_action_status", "200")
        formData.append("file", file)

        // console.log('上传参数:', {
        //   key: fullFileName,
        //   policy: ossConfig.policy,
        //   OSSAccessKeyId: ossConfig.accessKeyId,
        //   signature: ossConfig.signature,
        //   originalHost: ossConfig.host,
        //   proxyUrl: '/oss-proxy' // 使用代理地址
        // })
        // 🔥 关键修改：使用代理地址而不是原始OSS地址
        // 判断是开发环境还是线上环境
        let roomUrl = ''
        if (import.meta.env.VITE_USER_NODE_ENV === 'production') {
          roomUrl = ossConfig.host
        } else {
          roomUrl = '/oss-proxy'
        }
        console.log(roomUrl, 'roomUrl');

        const uploadResponse = await axios.post(roomUrl, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
          // 允许200和204状态码
          validateStatus: (status) => status >= 200 && status < 300,
          timeout: 60000, // 60秒超时
          // 添加withCredentials: false 避免一些CORS问题
          withCredentials: false,
        })

        // 🔥 注意：返回的URL仍然使用原始OSS地址，因为这是文件的实际访问地址
        const response = {
          url: `${ossConfig.host}/${fullFileName}`,
          fileName: fileName,
          fullFileName: fullFileName,
          status: uploadResponse.status,
        }

        console.log('上传成功:', response)
        resolve(response)

      } catch (err) {
        console.error("OSS上传失败:", err)

        if (axios.isAxiosError(err)) {
          if (err.code === 'ERR_NETWORK') {
            console.error('网络错误，可能是CORS问题，请检查代理配置')
          }
          console.error("详细错误:", {
            message: err.message,
            code: err.code,
            status: err.response?.status,
            data: err.response?.data
          })
        }

        isGettingSts.value = false
        stsPromise.value = null
        reject(err)
      }
    })

  return { ossConfig, getOssConfig }
}