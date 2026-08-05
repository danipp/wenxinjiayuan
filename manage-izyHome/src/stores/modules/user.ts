import { defineStore } from "pinia";
import { UserState } from "@/stores/interface";
import piniaPersistConfig from "@/stores/helper/persist";
import { basicInfo1 } from "@/api/modules/user"
import JSEncrypt from 'jsencrypt'
export const useUserStore = defineStore({
  id: "geeker-user",
  state: () => ({
    token: "",
    userInfo: {
      name: "",
      avatar: "",
      nickName: "",
    },
    publicKey: ``,
    basic: {},//判断是公司还是个人
    is_agent: false,
    // 判断是否是管理员
    is_admin: false,
  }),
  getters: {},
  actions: {
    setAdmin(admin) {
      this.is_admin = admin
    },
    setAgent(agent) {
      this.is_agent = agent
    },
    setBasic(basic) {
      this.basic = basic
    },
    // Set Token
    setToken(token: string) {
      this.token = token;
    },
    // Set setUserInfo
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
    },
    // RSA加密
    encryptPassword(password) {
      const encrypt = new JSEncrypt()
      encrypt.setPublicKey(`-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCvusFFU4Yiwi/8AY9bIgC38dVRMdffbi70yxz0SpuKlm6+sJ8qia8kt7B2IQ1BQmfrTM3XOmbdtOZFmCvTGAXDkrWrFCDrcAgGNjvIdIppZp99Tq8I6z8VbyBbOlwt53nEWA1Z5AS4yIbDTTYPlQsM2xkuhF4tjbvLABssvEk62QIDAQAB
-----END PUBLIC KEY-----`)
      return encrypt.encrypt(password)
    },
    getUserInfo() {
      return new Promise((resolve, reject) => {
        basicInfo1().then(res => {
          this.userInfo = res.data || {}
          this.setUserInfo(res.data)
          resolve(res.data)
        }).catch(err => {
        })
      })
    }
  },
  persist: piniaPersistConfig("geeker-user")
});
