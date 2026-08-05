
import type { Directive, DirectiveBinding } from "vue"

interface ElType extends HTMLElement {
  __handleClick__: (e: Event) => void
  __debounce_timer__: NodeJS.Timeout | null
}

const debounce: Directive = {
  mounted(el: ElType, binding: DirectiveBinding) {
    if (typeof binding.value !== "function") {
      console.error("[v-debounce] 必须传入函数")
      return
    }

    el.__debounce_timer__ = null
    const delay = binding.arg ? parseInt(binding.arg) : 500

    el.__handleClick__ = function (e: Event) {
      e.stopPropagation()
      if (el.__debounce_timer__) clearTimeout(el.__debounce_timer__)

      el.__debounce_timer__ = setTimeout(() => {
        binding.value(e)
      }, delay)
    }

    el.addEventListener("click", el.__handleClick__)
  },
  beforeUnmount(el: ElType) {
    if (el.__debounce_timer__) clearTimeout(el.__debounce_timer__)
    el.removeEventListener("click", el.__handleClick__)
  }
}

export default debounce
