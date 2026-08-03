<template>
  <u-popup
    :show="show"
    mode="bottom"
    round="20"
    @close="handleClose"
    :safeAreaInsetBottom="true"
    @touchmove.stop.prevent
  >
    <view class="form-popup">
      <view class="form-title">提交物资申领</view>

      <scroll-view class="form-body" scroll-y>
        <view class="form-item">
          <text class="form-label required">选择商品</text>
          <view class="goods-selector" @click="openGoodsPicker">
            <template v-if="selectedGoods">
              <image class="selected-img" :src="selectedGoods.coverImage || '/static/default-goods.png'" mode="aspectFill" />
              <view class="selected-info">
                <text class="selected-title">{{ selectedGoods.title }}</text>
                <text class="selected-price" v-if="selectedGoods.cashPrice">¥{{ selectedGoods.cashPrice }}</text>
              </view>
            </template>
            <text v-else class="placeholder">点击选择商品</text>
            <u-icon name="arrow-right" color="#cbd5e1" size="14" />
          </view>
        </view>

        <view class="form-item">
          <text class="form-label required">申领数量</text>
          <input class="form-input" v-model.number="form.claimCount" placeholder="请输入申领数量" type="number" />
        </view>

        <view class="form-item">
          <text class="form-label required">申领原因</text>
          <textarea class="form-textarea" v-model="form.claimReason" placeholder="请说明申领原因" maxlength="300" />
        </view>

        <view class="form-item">
          <text class="form-label required">联系人</text>
          <input class="form-input" v-model="form.contactName" placeholder="请输入联系人姓名" maxlength="20" />
        </view>

        <view class="form-item">
          <text class="form-label required">联系电话</text>
          <input class="form-input" v-model="form.contactPhone" placeholder="请输入联系电话" type="number" maxlength="11" />
        </view>

        <view class="form-item">
          <text class="form-label">收货地址</text>
          <input class="form-input" v-model="form.address" placeholder="请输入收货地址" maxlength="200" />
        </view>
      </scroll-view>

      <view class="form-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交申领' }}
        </button>
      </view>
    </view>

    <!-- 商品选择弹窗 -->
    <GoodsPicker
      :show.sync="showGoodsPicker"
      @confirm="onGoodsConfirm"
    />
  </u-popup>
</template>

<script>
import { submit2 } from '@/spages/api/material';
import GoodsPicker from './GoodsPicker.vue';

export default {
  name: 'ClaimForm',
  components: { GoodsPicker },
  props: {
    show: { type: Boolean, default: false },
  },
  watch: {
    show(val) { if (val) this.initForm(); },
  },
  data() {
    return {
      submitting: false,
      showGoodsPicker: false,
      selectedGoods: null,
      form: { goodsId: '', claimCount: '', claimReason: '', contactName: '', contactPhone: '', address: '' },
    };
  },
  methods: {
    initForm() {
      this.selectedGoods = null;
      this.form = { goodsId: '', claimCount: '', claimReason: '', contactName: '', contactPhone: '', address: '' };
    },

    openGoodsPicker() {
      this.showGoodsPicker = true;
    },

    onGoodsConfirm(goods) {
      this.selectedGoods = goods;
      this.form.goodsId = goods.goodsId;
    },

    validate() {
      if (!this.form.goodsId) return this.toast('请选择商品');
      if (!this.form.claimCount || this.form.claimCount <= 0) return this.toast('请输入有效的申领数量');
      if (!this.form.claimReason.trim()) return this.toast('请输入申领原因');
      if (!this.form.contactName.trim()) return this.toast('请输入联系人');
      if (!this.form.contactPhone.trim()) return this.toast('请输入联系电话');
      return true;
    },
    toast(msg) { uni.showToast({ title: msg, icon: 'none' }); return false; },

    async handleSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        const res = await submit2(this.form);
        if (res.code === '00000') {
          uni.showToast({ title: '提交成功', icon: 'none' });
          this.$emit('done');
          this.handleClose();
        } else {
          uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '提交失败', icon: 'none' });
      } finally { this.submitting = false; }
    },

    handleClose() { this.$emit('update:show', false); },
  },
};
</script>

<style lang="scss" scoped>
.form-popup {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  display: flex; flex-direction: column;
  max-height: 85vh; overflow: hidden;

  .form-title {
    font-size: 32rpx; font-weight: bold; color: #333;
    text-align: center; padding: 36rpx 0 24rpx;
    border-bottom: 1rpx solid #f0f0f0; flex-shrink: 0;
  }

  .form-body {
    flex: 1; overflow-y: auto;
    padding: 24rpx 40rpx 0; max-height: 60vh;

    .form-item {
      margin-bottom: 24rpx;
      .form-label {
        font-size: 26rpx; color: #333; margin-bottom: 10rpx; display: block;
        &.required::after { content: ' *'; color: #ef4444; }
      }
      .form-input {
        width: 100%; height: 72rpx; background: #f7f8fa;
        border-radius: 12rpx; padding: 0 20rpx; font-size: 26rpx; box-sizing: border-box;
      }
      .form-textarea {
        width: 100%; min-height: 120rpx; background: #f7f8fa;
        border-radius: 12rpx; padding: 16rpx 20rpx; font-size: 26rpx; box-sizing: border-box;
      }

      .goods-selector {
        display: flex; align-items: center;
        background: #f7f8fa; border-radius: 12rpx;
        padding: 16rpx 20rpx; min-height: 100rpx;

        .selected-img {
          width: 72rpx; height: 72rpx;
          border-radius: 10rpx; margin-right: 16rpx; flex-shrink: 0;
        }

        .selected-info {
          flex: 1; min-width: 0;
          .selected-title {
            font-size: 26rpx; color: #333; font-weight: 500;
            display: block; margin-bottom: 4rpx;
          }
          .selected-price {
            font-size: 24rpx; color: #f59e0b; font-weight: bold;
          }
        }

        .placeholder {
          flex: 1; font-size: 26rpx; color: #c0c4cc;
        }
      }
    }
  }

  .form-footer {
    display: flex; gap: 20rpx;
    padding: 28rpx 40rpx calc(28rpx + env(safe-area-inset-bottom));
    button {
      flex: 1; height: 80rpx; line-height: 80rpx;
      font-size: 28rpx; font-weight: bold; border-radius: 40rpx; border: none; padding: 0;
      &::after { border: none; }
    }
    .btn-cancel { background: #f0f0f0; color: #666; }
    .btn-submit { background: #07c160; color: #fff;
      &[disabled] { opacity: 0.6; }
    }
  }
}
</style>
