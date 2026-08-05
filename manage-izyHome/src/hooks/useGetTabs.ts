import { computed } from 'vue'
import { useMediaTabsStore } from '@/stores/modules/mediaTabs'
const mediaTabsStore = useMediaTabsStore()

export function useGetTabs() {
    // 根据传参返回对应的tile
    const getTitle = computed(() => {
        return function (key, keyValue,text) {
            if (key === "enumPlatformId") {
                return mediaTabsStore.mediaPlatforms.find(item => item.enumPlatformId === keyValue)?.title;
            } else if (key === "enumDomainId") {
                if(text && keyValue){
                    return mediaTabsStore.fields.filter(item => keyValue.includes(item.enumDomainId)).map(item => item.title).join("、");
                }
                // keyValue是一个数组，也要返回数组出去
                return mediaTabsStore.fields.filter(item => keyValue && keyValue.includes(item.enumDomainId)).map(item => item.title);
            } else if (key === "enumProvinceId") {
                return mediaTabsStore.coverageAreas.find(item => item.enumProvinceId === keyValue)?.title;
            }
        };
    });
    return { getTitle };
}