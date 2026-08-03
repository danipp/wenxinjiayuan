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
      <view class="form-title">提交帮扶申请</view>

      <scroll-view class="form-body" scroll-y>
        <view class="form-item">
          <text class="form-label required">申请人姓名</text>
          <input class="form-input" v-model="form.applicantName" placeholder="请输入姓名" maxlength="20" />
        </view>

        <view class="form-item">
          <text class="form-label required">联系电话</text>
          <input class="form-input" v-model="form.applicantPhone" placeholder="请输入联系电话" type="number" maxlength="11" />
        </view>

        <view class="form-item">
          <text class="form-label">身份证号</text>
          <input class="form-input" v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
        </view>

        <view class="form-item">
          <text class="form-label">居住地址</text>
          <input class="form-input" v-model="form.address" placeholder="请输入居住地址" maxlength="100" />
        </view>

        <view class="form-item">
          <text class="form-label">家庭情况</text>
          <textarea class="form-textarea" v-model="form.familySituation" placeholder="请描述家庭情况" maxlength="500" />
        </view>

        <view class="form-item">
          <text class="form-label required">帮扶类型</text>
          <view class="radio-row">
            <view
              v-for="opt in assistanceTypeOptions"
              :key="opt.value"
              class="radio-item"
              :class="{ active: form.assistanceType === opt.value }"
              @click="form.assistanceType = opt.value"
            >{{ opt.label }}</view>
          </view>
        </view>

        <view class="form-item">
          <text class="form-label required">困难描述</text>
          <textarea class="form-textarea" v-model="form.difficultyDesc" placeholder="请详细描述当前困难" maxlength="500" />
        </view>

        <view class="form-item">
          <text class="form-label required">期望帮扶</text>
          <textarea class="form-textarea" v-model="form.desiredHelp" placeholder="请描述期望的帮扶内容" maxlength="500" />
        </view>

        <view class="form-item">
          <text class="form-label">备注</text>
          <textarea class="form-textarea" v-model="form.remark" placeholder="选填，补充说明" maxlength="300" />
        </view>
      </scroll-view>

      <view class="form-footer">
        <button class="btn-cancel" @click="handleClose">取消</button>
        <button class="btn-submit" @click="handleSubmit" :disabled="submitting">
          {{ submitting ? '提交中...' : '提交申请' }}
        </button>
      </view>
    </view>
  </u-popup>
</template>

<script>
import { submit3 } from '@/spages/api/apply';

export default {
  name: 'ApplyForm',
  props: {
    show: { type: Boolean, default: false },
    communityId: { type: [Number, String], default: '' },
  },
  watch: {
    show(val) { if (val) this.initForm(); },
  },
  data() {
    return {
      submitting: false,
      assistanceTypeOptions: [
        { label: '生活', value: 'living' },
        { label: '医疗', value: 'medical' },
        { label: '教育', value: 'education' },
        { label: '就业', value: 'employment' },
      ],
      form: {
        applicantName: '',
        applicantPhone: '',
        idCard: '',
        address: '',
        familySituation: '',
        assistanceType: 'living',
        difficultyDesc: '',
        desiredHelp: '',
        remark: '',
        communityId: '',
      },
    };
  },
  methods: {
    initForm() {
      this.form = {
        applicantName: '',
        applicantPhone: '',
        idCard: '',
        address: '',
        familySituation: '',
        assistanceType: 'living',
        difficultyDesc: '',
        desiredHelp: '',
        remark: '',
        communityId: String(this.communityId || ''),
      };
    },

    validate() {
      if (!this.form.applicantName.trim()) return this.toast('请输入申请人姓名');
      if (!this.form.applicantPhone.trim()) return this.toast('请输入联系电话');
      if (!this.form.assistanceType) return this.toast('请选择帮扶类型');
      if (!this.form.difficultyDesc.trim()) return this.toast('请输入困难描述');
      if (!this.form.desiredHelp.trim()) return this.toast('请输入期望帮扶');
      return true;
    },
    toast(msg) {
      uni.showToast({ title: msg, icon: 'none' });
      return false;
    },

    async handleSubmit() {
      if (!this.validate()) return;
      this.submitting = true;
      try {
        const res = await submit3(this.form);
        if (res.code === '00000') {
          uni.showToast({ title: '提交成功', icon: 'none' });
          this.$emit('done');
          this.handleClose();
        } else {
          uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
        }
      } catch (e) {
        uni.showToast({ title: '提交失败', icon: 'none' });
      } finally {
        this.submitting = false;
      }
    },

    handleClose() {
      this.$emit('update:show', false);
    },
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
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    text-align: center;
    padding: 36rpx 0 24rpx;
    border-bottom: 1rpx solid #f0f0f0;
    flex-shrink: 0;
  }

  .form-body {
    flex: 1;
    overflow-y: auto;
    padding: 24rpx 40rpx 0;
    max-height: 60vh;

    .form-item {
      margin-bottom: 24rpx;

      .form-label {
        font-size: 26rpx;
        color: #333;
        margin-bottom: 10rpx;
        display: block;
        &.required::after { content: ' *'; color: #ef4444; }
      }

      .form-input {
        width: 100%;
        height: 72rpx;
        background: #f7f8fa;
        border-radius: 12rpx;
        padding: 0 20rpx;
        font-size: 26rpx;
        box-sizing: border-box;
      }

      .form-textarea {
        width: 100%;
        min-height: 120rpx;
        background: #f7f8fa;
        border-radius: 12rpx;
        padding: 16rpx 20rpx;
        font-size: 26rpx;
        box-sizing: border-box;
      }

      .radio-row {
        display: flex;
        gap: 16rpx;
        flex-wrap: wrap;

        .radio-item {
          height: 60rpx;
          line-height: 60rpx;
          padding: 0 28rpx;
          background: #f7f8fa;
          border-radius: 12rpx;
          font-size: 24rpx;
          color: #666;
          border: 2rpx solid transparent;

          &.active {
            background: #e6f7ed;
            color: #07c160;
            border-color: #07c160;
            font-weight: bold;
          }
        }
      }
    }
  }

  .form-footer {
    display: flex;
    gap: 20rpx;
    padding: 28rpx 40rpx calc(28rpx + env(safe-area-inset-bottom));

    button {
      flex: 1;
      height: 80rpx;
      line-height: 80rpx;
      font-size: 28rpx;
      font-weight: bold;
      border-radius: 40rpx;
      border: none;
      padding: 0;
      &::after { border: none; }
    }
    .btn-cancel { background: #f0f0f0; color: #666; }
    .btn-submit { background: #07c160; color: #fff;
      &[disabled] { opacity: 0.6; }
    }
  }
}
</style>
