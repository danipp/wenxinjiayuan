/*
 * @Date: 2021-07-13 11:19:38
 * @LastEditors: xuxiaojian
 * @LastEditTime: 2021-07-20 12:35:46
 * @使用方法 在组件页面
    import mixinTab from "@/common/mixin.js";
    mixins:[mixinTab],
    data() {
      return {
        json: [{ name: "nomal",keys:'',api: FeedBackList, list: [] ,param:{},pageNo:1,pageSize:10,total:0,is_end:false}],  // param指的是额外的查询条件，如搜索词，切换id
      };
    },
 */
    import { debounce } from '@/common/syy_tools'
    export default {
      data() {
        return {
          // 当前tab索引
          current: 0,
          loadStatus: 'loadmore',
          loadingStatus: true,
        }
      },
      // 触底加载下一页
      onReachBottom() {
        const handle = debounce(this.addRandomData, 1000)
        handle()
      },
      computed: {
        // tabs的名字列表
        tabsList() {
          return this.json.map(({
            name
          }) => ({
            name
          }))
        }
      },
      // onShow的时候刷新
      async onShow() {
        this.loadingStatus = true;
        this.json.forEach(item => {
          item.list = []
          item.pageNo = 1
          item.is_end = false;
          item.total = 0
        })
        await this.init();
      },
      methods: {
        // 回到第一页, 在切换tab或者删除等操作后调用刷新
        async init() {
          if (!this.json[this.current]) return
          this.json[this.current].getPageList = this.getPageList(this.json[this.current])
          await this.addRandomData()
        },
        // 闭包: 获取当前页面的触底加载方法, 刷新时重新调用
        getPageList(item) {
          return async () => {
            const res = await item.api({
              pageNo: item.pageNo,
              pageSize: item.pageSize,
              param: item.param,
              ...item.search
            })
            setTimeout(() => {
              let { data } = res
              let newData = []
              if (item.pageNo === 1) {
                if (Object.prototype.toString.call(data) === '[object Object]') {
                  item.list = data?.records ?? data
                } else {
                  item.list = data?.records ?? data
                }
              } else {
                item.list.push(...(data?.records ?? data))
              }
              newData = data?.records ?? data
              if (newData && newData.length < 10) {
                item.is_end = true
              }
              setTimeout(() => {
                this.loadingStatus = false
              }, 500);
              console.log(newData, 'newData');
              // item.total = data?.total ?? data.meta?.total ?? 0
              item.pageNo++
              if (item.is_end) {
                this.loadStatus = 'nomore'
                return
              }
              this.loadStatus = 'loadmore'
            }, 500);
          }
        },
        // 切换tab
        changeTabs(current) {
          if (this.json[current].list.length) {
            console.log('有数据的');
            this.current = current
            return
          }
          console.log('无数据的');
    
          this.current = current
          this.loadingStatus = true;
          this.$nextTick(async () => {
            await this.init();
          })
        },
        // 回退
        rollback() {
          uni.navigateBack()
        },
        // 加载更多
        async addRandomData() {
          this.loadStatus = 'loading'
          if (this.json[this.current].is_end) {
            uni.showToast({
              title: "没有更多数据了哦",
              icon: "none"
            })
            setTimeout(() => {
              this.loadingStatus = false
            }, 500);
            this.loadStatus = 'nomore'
            return
          }
          this.json[this.current].getPageList()
        },
        async onSearch(param) {
          this.loadingStatus = true;
          this.json[this.current].search = param
          await this.init();
        }
      }
    }