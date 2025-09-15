package com.cmc.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class OssUtil {

    @Value("${aliyun.oss.file.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.file.region}")
    private String region;

    @Value("${aliyun.oss.file.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.file.keyId}")
    private String keyId;

    @Value("${aliyun.oss.file.keySecret}")
    private String keySecret;

    /**
     * 生成文件名称
     * @param prefix 文件夹名称
     * @param fileName 文件名
     * @return
     */
    public String generateFileName(String prefix,String fileName){
        return prefix + "/" + UUID.randomUUID() + "_" + fileName;
    }

    public void uploadFile(MultipartFile file, String objectName) throws IOException {

        // 从环境变量中获取访问凭证。运行本代码示例之前，请确保已设置环境变量OSS_ACCESS_KEY_ID和OSS_ACCESS_KEY_SECRET。

        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(keyId, keySecret);

        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);

        OSS ossClient = OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .clientConfiguration(clientBuilderConfiguration)
                .region(region)
                .build();


        try {
            // 上传 MultipartFile 流
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    objectName,
                    file.getInputStream()  // 改为流上传
            );

            PutObjectResult result = ossClient.putObject(putObjectRequest);

        } catch (OSSException oe) {
            System.out.println("OSSException: " + oe.getErrorMessage());
            throw oe;
        } catch (ClientException ce) {
            System.out.println("ClientException: " + ce.getMessage());
            throw ce;
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }



}
