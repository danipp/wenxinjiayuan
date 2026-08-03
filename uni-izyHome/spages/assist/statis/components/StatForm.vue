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
      <view class="form-title">{{ isEdit ? '编辑统计项' : '新增统计项' }}</view>

      <scroll-view class="form-body" scroll-y>
        <view class="form-item">
          <text class="form-label required">标签</text>
          <input class="form-input" v-model="form.statLabel" placeholder="如：帮扶人数、累计捐赠" maxlength="30" />
        </view>

        <view class="form-item">
          <text class="form-label">标识 Key</text>
          <input class="form-input" v-model="form.statKey" placeholder="英文标识，如 helpCount" maxlength="50" />
        </view>

        <view class="form-item">
          <text class="form-label">统计值</text>
          <input class="form-input" v-model.number="form.statValue" placeholder="请输入数值" type="number" />
        </view>

        <view class="form-item">
          <text class="form-label">自定义值</text>
          <view class="switch-row" @click="form.isCustom = !form.isCustom">
            <text class="switch-label">{{ form.isCustom ? '开启（手动输入）' : '关闭（自动统计）' }}</text>
            <view class="switch-track" :class="{ on: form.isCustom }">
              <view class="switch-thumb"></view>
            </view>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label">排序</text>
          <input class="form-input" v-model.number="form.displayOrder" placeholder="数字越大越靠前" type="number" />
        </view>
      </scroll-view>

      <view class="form-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交' }}
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { save3 } from '@/spages/api/statis';

export default {
  name: 'StatForm',
  props: {
    show: { type: Boolean, default: false },
    editData: { type: Object, default: null },
    communityId: { type: [Number, String], default: '' },
  },
  computed: {
    isEdit() { return this.editData && this.editData.statId; },
  },
  watch: {
    show(val) { if (val) this.initForm(); },
  },
  data() {
    return {
      submitting: false,
      form: { statId: '', statKey: '', statLabel: '', statValue: 0, isCustom: false, displayOrder: 0, communityId: '' },
    };
  },
  methods: {
    initForm() {
      if (this.editData && this.editData.statId) {
        this.form = {
          statId: this.editData.statId || '',
          statKey: this.editData.statKey || '',
          statLabel: this.editData.statLabel || '',
          statValue: this.editData.statValue || 0,
          isCustom: this.editData.isCustom || false,
          displayOrder: this.editData.displayOrder || 0,
          communityId: String(this.editData.communityId || this.communityId || ''),
        };
      } else {
        this.form = { statId: '', statKey: '', statLabel: '', statValue: 0, isCustom: false, displayOrder: 0, communityId: String(this.communityId || '') };
      }
    },
    validate() {
      if (!this.form.statLabel.trim()) return this.toast('请输入标签');
      return true;
    },
    toast(msg) { uni.showToast({ title: msg, icon: 'none' }); return false; },

    async handleSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        const payload = { ...this.form };
        if (!payload.statId) delete payload.statId;
        const res = await save3(payload);
        if (res.code === '00000') {
          uni.showToast({ title: this.isEdit ? '编辑成功' : '新增成功', icon: 'none' });
          this.$emit('done');
          this.handleClose();
        } else {
          uni.showToast({ title: res.msg || '操作失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '操作失败', icon: 'none' });
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
  display: flex;
  flex-direction: column;
  max-height: 85vh;
  overflow: hidden;

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
      .switch-row {
        display: flex; align-items: center; justify-content: space-between;
        padding: 16rpx 0;
        .switch-label { font-size: 26rpx; color: #333; }
        .switch-track {
          width: 88rpx; height: 46rpx; border-radius: 23rpx;
          background: #ddd; position: relative; transition: background 0.2s;
          &.on { background: #07c160; }
          .switch-thumb {
            position: absolute; top: 4rpx; left: 4rpx;
            width: 38rpx; height: 38rpx; border-radius: 19rpx; background: #fff;
            transition: left 0.2s;
          }
          &.on .switch-thumb { left: 46rpx; }
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
