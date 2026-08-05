import { defineStore } from "pinia";
import { getOrderStatusList } from '@/api/modules/order'
export const useMediaTabsStore = defineStore({
    id: "mediaTabs",
    state: () => ({
        mediaPlatforms: [],//媒体平台
        fields: [],//领域
        coverageAreas: [],//覆盖区域
        collegesList: [],//高校类型
        statusList: [],//订单状态列表
        advancedList:[],//高级搜索条件
    }),
    getters: {},
    actions: {
        setAdvancedList(advancedList) {
            this.advancedList = advancedList;
        },
        setPlatForm(platform) {
            this.mediaPlatforms = platform;
        },
        setField(field) {
            this.fields = field;
        },
        setCoverageArea(coverageArea) {
            this.coverageAreas = coverageArea;
        },
        setCollegesList(colleges) {
            this.collegesList = colleges;
        },
        reset() {
            this.mediaPlatforms = [];
            this.fields = [];
            this.coverageAreas = [];
            this.collegesList = [];
        },
        getStatusList() {
            getOrderStatusList().then(res=>{
                this.statusList = res.data
            })
        }
    },
    // 永久存储
    persist: true,
})