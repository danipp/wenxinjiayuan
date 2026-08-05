package com.demo.weixin.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.demo.common.core.result.Result;
import com.demo.common.core.util.ValidatorUtils;
import com.demo.weixin.vo.OssPolicyVo;
import com.demo.weixin.vo.OssUploadPolicyVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 案例 服务实现类
 * </p>
 *
 * @author zane
 */
@Service
@Slf4j
public class OssService {

    @Value("${alibaba.cloud.oss.endpoint}")
    private String endpoint;

    @Value("${alibaba.cloud.oss.bucket}")
    private String bucket;

    @Value("${alibaba.cloud.oss.region}")
    private String region;


    @Value("${alibaba.cloud.accessKeyId}")
    private String accessKeyId;

    @Value("${alibaba.cloud.accessKeySecret}")
    private String accessKeySecret;

    @Resource
    private OSS ossClient;

    public Result<OssUploadPolicyVO> policyToken(OssPolicyVo ossPolicyVo, String dir) {
        ValidatorUtils.validateEntity(ossPolicyVo);
        //https://md-ossbucket.oss-cn-beijing.aliyuncs.com/QQ%E6%88%AA%E5%9B%BE20210609114525.png  host的格式为 bucketname.endpoint
        String host = "https://" + bucket + "." + endpoint;
        // 用户上传文件时指定的前缀,我们希望以日期作为一个目录
        OssUploadPolicyVO vo = new OssUploadPolicyVO();
        try {
            long expireTime = 60 * 5;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            vo.setAccessKeyId(accessKeyId);
            vo.setPolicy(encodedPolicy);
            vo.setSignature(postSignature);
            vo.setDir(dir);
            vo.setHost(host);
            vo.setExpire(String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
            log.error("oss获取临时凭证失败,errMsg:{}", e.getMessage());
        }
        return Result.success(vo);
    }

}

