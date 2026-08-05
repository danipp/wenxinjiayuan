import { defineStore } from "pinia";

export const useCartsStore = defineStore({
    id: "carts",
    state: () => ({
        // 判断当前是否是在路由/release页面
        isReleasePage: false,
    }),
    getters: {},
    actions: {
        setIsReleasePage(isReleasePage: boolean) {
            this.isReleasePage = isReleasePage;
        }
    },
})