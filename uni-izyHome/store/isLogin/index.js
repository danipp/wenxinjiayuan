
const state = {
    hasRedirected: false,
};

const mutations = {
    SET_REDIRECTED(state, payload) {
        console.log(payload,'销毁');
        state.hasRedirected = payload;
    }
};

const actions = {

};
// 详情页
// this.$store.dispatch('listAsync/notifyItemUpdate', {
//     id: itemId,
//     newStatus: '已购买'
//   })

//   // 列表页
// onShow() {
//   const updateMap = this.$store.state.listAsync.updateMap
//   Object.keys(updateMap).forEach(id => {
//     // 找到本地列表数据中对应的item，更新其状态
//     const item = this.list.find(i => i.id == id)
//     if (item) {
//       item.status = updateMap[id]
//       // 更新完后清除
//       this.$store.dispatch('listAsync/clearItemUpdate', id)
//     }
//   })
// }
export default {
    namespaced: true,
    state,
    mutations,
    actions
};