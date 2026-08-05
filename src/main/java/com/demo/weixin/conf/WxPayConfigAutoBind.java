package com.demo.weixin.conf;

import cn.hutool.json.JSONUtil;
import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.api.*;
import com.alipay.v3.model.*;
import com.alipay.v3.util.model.AlipayConfig;
import com.github.binarywang.wxpay.config.WxPayConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Configuration
@EnableConfigurationProperties
public class WxPayConfigAutoBind {

    @Bean
    @ConfigurationProperties(prefix = "wx.service-mch")
    public WxPayConfig wxServiceMchConfig() {
        return new WxPayConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "wx.merchant")
    public WxPayConfig wxMerchantConfig() {
        return new WxPayConfig();
    }


//202512100633300563960000000019864344
    public static void main(String[] args) throws Exception {
        ApiClient defaultClient = com.alipay.v3.Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://openapi.alipay.com");
        // 设置alipayConfig参数（全局设置一次）
        AlipayConfig config = new AlipayConfig();
        config.setAppId("2021006112603769");
        config.setPrivateKey("MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC7vRiipy9gOLHuQ87hj28MKLPz3KRX8Mp8z+W4kkSLkqJcO1dTwLvSQXSSSL6Xvqzag3/S8WTyMXUuGwpgoVqY+ghjZkpGZEQvfaN8Kpr800ckTpgjgnyhcJnQBQvSqZ4ipaLqq0Fgf1n2t8UybS6AnNg+8WuWopxTsIz+BPBtWKJ3KoXOKMTlX49kdFFWjHuCqAzNFrigj+HNrQAc9j0zkj5wQ9Ad3XoSAKC1jSPYLLNqnhjZ03lpwlh2L8AsPS0ddD99gxsl4P0EozTbUGKT5ZGtdQQ/mvlTT6ekSESxcIKJll4kcHX88WAMQRwwRBbOBPrTyC4gP+rQ2zHRbkf9AgMBAAECggEAY0yZgy66uEJEmqny+wtKyTueMxY5Nup9r5anAwEakFh22CHrBrminotQhS9E4C5SbyGusnxL0G6odEKJMqMlsFRPq4OnNaRDDHt/xWUjpSZ7MpD/l4ilMx7DvGtu3XYp1lmV2lnKsqephf2dqBZnyk8rcd9v8LUByqJPcOxb3vlgcuyJE/sqvgh835ldCnJBBp0ZnnF1syjTp+QD66TwnMdGWD4dyW5c329WfUgOtICzqMAcwXxL+PgPFctGj/EUPuRmv+/ZrdexCYhc/gOvf1BepAfIFYZw+2svnM7oKlYAzvjJcp92EubXfMwGFt7J9ciVdG/Y+Kq5as7GeQBzAQKBgQD8tnAhAQLcoQvq9huHL+a6+ZxLD1PDieQpBETw2CLMSDKzuOAar0AKHwE52zpoV6UR1FcNzQumuXDF9WfxEYqJKg0lk5V5DSl9x5jBVQ9AaPR/q0fj/4JTnnbD/5xBp266cYEyvyBJNzmjtSw4WnFJPHMHgE5vah09QQ8T2uWyZQKBgQC+LkmT4q50ONdQPUNeLlVRCs17tBvJCeuJ3uog/u0cLxTW/n2Z2Cm1kD3On/YfUOoealolF9IuWCOqY0gK7hgNaVMXC7uyicnhL7y2AMjNino2qmSStgilsVSkfDZI7FyRDW+t4axHGu+b78gA8Ofjkn8jf92cnh7/4WMtzGqZuQKBgQCB0S40phJUSB6ZKqflgEPklMkm/c6nyjP8pgbMOhtWGViCXlwxGQmqqVa149pTM4LGb+/wVP7BPM2jw3cLrVkfMQzj2raUIqDHzjq4Rfu5uLCwzMn3Y5ANaH/WG36nc108RYZ7DReGxOnFIRKAOGWtm3HzzoN8VGSEzODz8CTCyQKBgQCEv+aYFdjUQ4D3/2tw6EKhaTr5qETk1cnV2QXwtkFoNO0FDWs64gprNB4QhtPUFDT3uiDmV3Rc6SUsIFT/XCbp5aUcJxKDk26ZgyrkmDOSVaDtW48MYYgzsE526kUyLdqHXaug4i/RClpKrNsc3QvgbDfW3cAhPLZCwl7qbw/R8QKBgAQKD2UKNCc7fd5JVQVeJbpX0ePx/UwJYbyvwEdKbdkKsC/7JvrXPcoo47HuEsoUo8buFl2uSilPi/bgrBjBxEfRgnHSDmjOeeqfpF2FDNEkH3F/3t+/xCGNxA0KhQoalR6GK0UQpSKvvFFS1swlrxHxf+vHpkpbp7UVsOJthvWm");
        // 密钥模式
//        config.setAlipayPublicKey("alipay_public_key");
        // 证书模式
        config.setAppCertPath("D:\\develop\\alipay\\coupon\\appCertPublicKey_2021006112603769.crt");
        config.setAlipayPublicCertPath("D:\\develop\\alipay\\coupon\\alipayCertPublicKey_RSA2.crt");
        config.setRootCertPath("D:\\develop\\alipay\\coupon\\alipayRootCert.crt");
        config.setEncryptKey("tjGTnVnOwQoQ7zP5ZhvqOQ==");
        defaultClient.setAlipayConfig(config);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        AlipayMarketingActivityVoucherPublishModel voucherPublishModel = new AlipayMarketingActivityVoucherPublishModel();
//        voucherPublishModel.setActivityId("2025120900826004174541536529");
//        voucherPublishModel.setMerchantAccessMode("SELF_MODE");
//        voucherPublishModel.setOutBizNo("asdh12394nb23941234xasd2025120901");
//        AlipayMarketingActivityVoucherPublishResponseModel publish = new AlipayMarketingActivityVoucherApi(defaultClient).publish(voucherPublishModel);
//        System.out.println(publish);+
//        AlipayMarketingCampaignCashCreateModel campaignCashCreateModel = new AlipayMarketingCampaignCashCreateModel();
//        campaignCashCreateModel.setCouponName("XXX周年庆红包");
//        campaignCashCreateModel.setPrizeType("fixed");
//        campaignCashCreateModel.setTotalMoney("1");
//        campaignCashCreateModel.setTotalNum("5");
//        campaignCashCreateModel.setPrizeMsg("XXX送您大红包");
//        campaignCashCreateModel.setStartTime("2025-12-09 17:58:30");
//        campaignCashCreateModel.setEndTime("2025-12-09 22:50:30");
//        AlipayMarketingCampaignCashApi alipayMarketingCampaignCashApi = new AlipayMarketingCampaignCashApi(defaultClient);
//        AlipayMarketingCampaignCashCreateResponseModel alipayMarketingCampaignCashCreateResponseModel = alipayMarketingCampaignCashApi.create(campaignCashCreateModel);
//        System.out.println(alipayMarketingCampaignCashCreateResponseModel);
//        AlipayFundTransUniTransferModel   fundTransUniTransferModel = new AlipayFundTransUniTransferModel  ();
//        fundTransUniTransferModel.setTransAmount("0.1");
//        fundTransUniTransferModel.setOutBizNo("asdh12394nb23941234xasd2025121001");
//        fundTransUniTransferModel.setProductCode("STD_RED_PACKET");
//        Participant participant = new Participant();
//        participant.setIdentityType("ALIPAY_OPEN_ID");
//        participant.setIdentity("046dD5JdnZJ7n7yV_ZgGEvWP6uHVlqAxWQwLsIxmJj1TLIc");
//        fundTransUniTransferModel.setPayeeInfo(participant);
//        fundTransUniTransferModel.setBizScene("DIRECT_TRANSFER");
//        fundTransUniTransferModel.setOrderTitle("转账标题");
//        fundTransUniTransferModel.setRemark("单笔转账备注");
//        fundTransUniTransferModel.setBusinessParams("{\"sub_biz_scene\":\"REDPACKET\"}");
//        AlipayFundTransUniTransferResponseModel transfer = new AlipayFundTransUniApi(defaultClient).transfer(fundTransUniTransferModel);
//        System.out.println(transfer);
//if(true){
//    return;
//}
        AlipayMarketingActivityVoucherApi apiInstance = new AlipayMarketingActivityVoucherApi(defaultClient);
        AlipayMarketingActivityVoucherCreateModel model = new AlipayMarketingActivityVoucherCreateModel(); // AlipayMarketingActivityVoucherCreateModel |
        try {
// 设置外部业务单号
            model.setOutBizNo("4e5650bda49642b9b55c4955237d1881_49218390253568");

            // 设置商户接入模式
            model.setMerchantAccessMode("SELF_MODE");

            // 设置活动基础信息
            ActivityBaseInfo activityBaseInfo = new ActivityBaseInfo();
            activityBaseInfo.setActivityName("测试活动");
            model.setActivityBaseInfo(activityBaseInfo);

            // 设置券发放信息
            VoucherSendModeInfo voucherSendModeInfo = new VoucherSendModeInfo();
            voucherSendModeInfo.setVoucherSendMode("DIRECT_SEND_MODE");
            VoucherSendRuleInfo voucherSendRuleInfo = new VoucherSendRuleInfo();
            voucherSendRuleInfo.setQuantity(5);
            voucherSendRuleInfo.setQuantityLimitPerUser(1);
            voucherSendRuleInfo.setNaturalPersonLimit(true);
            voucherSendRuleInfo.setPhoneNumberLimit(true);
            voucherSendRuleInfo.setRealNameLimit(true);
            voucherSendRuleInfo.setPublishStartTime("2025-12-10 17:50:01");
            voucherSendRuleInfo.setPublishEndTime("2026-01-25 23:59:59");
            voucherSendModeInfo.setVoucherSendRuleInfo(voucherSendRuleInfo);
            model.setVoucherSendModeInfo(voucherSendModeInfo);

            // 设置券优惠抵扣信息
            VoucherDeductInfo voucherDeductInfo = new VoucherDeductInfo();
            voucherDeductInfo.setVoucherType("FIX_VOUCHER");
            FixVoucherInfo fixVoucherInfo = new FixVoucherInfo();
            fixVoucherInfo.setAmount("0.10");
            fixVoucherInfo.setFloorAmount("0.11");
            voucherDeductInfo.setFixVoucherInfo(fixVoucherInfo);
            model.setVoucherDeductInfo(voucherDeductInfo);

            // 设置券可用范围
            VoucherAvailableScopeInfo voucherAvailableScopeInfo = new VoucherAvailableScopeInfo();
            VoucherAvailableAccountInfo voucherAvailableAccountInfo = new VoucherAvailableAccountInfo();
            List<String> availablePids = new ArrayList<String>();
            availablePids.add("2088251371945175");
            voucherAvailableAccountInfo.setAvailablePids(availablePids);
            voucherAvailableScopeInfo.setVoucherAvailableAccountInfo(voucherAvailableAccountInfo);
//            VoucherAvailableGoodsInfo voucherAvailableGoodsInfo = new VoucherAvailableGoodsInfo();
//            voucherAvailableGoodsInfo.setGoodsName("美味甜甜圈");
//            voucherAvailableGoodsInfo.setGoodsDescription("美味甜甜圈很美味");
//            List<String> availableGoodsSkuIds = new ArrayList<String>();
//            availableGoodsSkuIds.add("apple-01");
//            availableGoodsSkuIds.add("apple-02");
//            voucherAvailableGoodsInfo.setAvailableGoodsSkuIds(availableGoodsSkuIds);
//            voucherAvailableScopeInfo.setVoucherAvailableGoodsInfo(voucherAvailableGoodsInfo);
            model.setVoucherAvailableScopeInfo(voucherAvailableScopeInfo);

            // 设置券核销限制
            VoucherUseRuleInfo voucherUseRuleInfo = new VoucherUseRuleInfo();
            VoucherUseTimeInfo voucherUseTimeInfo = new VoucherUseTimeInfo();
            voucherUseTimeInfo.setPeriodType("ABSOLUTE");
            VoucherAbsolutePeriodInfo absolutePeriodInfo = new VoucherAbsolutePeriodInfo();
            absolutePeriodInfo.setValidBeginTime("2025-12-10 17:50:01");
            absolutePeriodInfo.setValidEndTime("2026-01-25 23:59:59");
            voucherUseTimeInfo.setAbsolutePeriodInfo(absolutePeriodInfo);
            voucherUseRuleInfo.setVoucherUseTimeInfo(voucherUseTimeInfo);
            model.setVoucherUseRuleInfo(voucherUseRuleInfo);

            // 设置券引导信息
            VoucherCustomerGuideInfo voucherCustomerGuideInfo = new VoucherCustomerGuideInfo();
            VoucherUseGuideInfo voucherUseGuideInfo = new VoucherUseGuideInfo();
            List<String> useGuideMode = new ArrayList<String>();
            useGuideMode.add("CAN_USE");
            voucherUseGuideInfo.setUseGuideMode(useGuideMode);
//            VoucherMiniAppUseGuideInfo miniAppUseGuideInfo = new VoucherMiniAppUseGuideInfo();
//            miniAppUseGuideInfo.setMiniAppUrl("alipays://platformapi/startapp?appId=xxxx");
//            voucherUseGuideInfo.setMiniAppUseGuideInfo(miniAppUseGuideInfo);
            voucherCustomerGuideInfo.setVoucherUseGuideInfo(voucherUseGuideInfo);
            model.setVoucherCustomerGuideInfo(voucherCustomerGuideInfo);

            // 设置券展示信息
            VoucherDisplayPatternInfo voucherDisplayPatternInfo = new VoucherDisplayPatternInfo();
            voucherDisplayPatternInfo.setBrandName("圣达碰一碰");
            voucherDisplayPatternInfo.setVoucherDescription("1、本券不可兑换现金，不可找零。2、每个用户最多可以领取1张。3、如果订单发生退款，优惠券无法退还。");
//            voucherDisplayPatternInfo.setVoucherImage("adeDSktiQO-u5vJUqVbcQwAAACMAAQED");
            model.setVoucherDisplayPatternInfo(voucherDisplayPatternInfo);

            // 设置资金信息
            VoucherBudgetSupplyInfo voucherBudgetSupplyInfo = new VoucherBudgetSupplyInfo();
            voucherBudgetSupplyInfo.setBudgetType("NO_CASH");
//            VoucherRechargeInfo voucherRechargeInfo = new VoucherRechargeInfo();
//            voucherRechargeInfo.setRechargeType("ALIPAY_BALANCE");
//            VoucherBalanceRechargeInfo voucherBalanceRechargeInfo = new VoucherBalanceRechargeInfo();
//            voucherBalanceRechargeInfo.setAmount("0.5");
//            voucherBalanceRechargeInfo.setPartnerId("2088251371945175");
//            voucherRechargeInfo.setVoucherBalanceRechargeInfo(voucherBalanceRechargeInfo);
//            voucherBudgetSupplyInfo.setVoucherRechargeInfo(voucherRechargeInfo);
            model.setVoucherBudgetSupplyInfo(voucherBudgetSupplyInfo);

            // 第三方代调用模式下请设置app_auth_token
            // request.putOtherTextParam("app_auth_token", "<-- 请填写应用授权令牌 -->");
            System.out.println(JSONUtil.toJsonStr(model));
//            if(false){
                AlipayMarketingActivityVoucherCreateResponseModel result = apiInstance.create(model);
//            }
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling AlipayMarketingActivityVoucherApi#create");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}