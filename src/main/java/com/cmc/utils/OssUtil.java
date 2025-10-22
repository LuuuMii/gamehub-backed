package com.cmc.utils;

import com.aliyun.oss.*;
import com.aliyun.oss.common.auth.*;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.DeleteObjectsResult;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.cmc.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
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
     * @return 文件的名字(带有目录)
     */
    public String generateFileName(String prefix,String fileName){
        return prefix + "/" + UUID.randomUUID() + "_" + fileName;
    }

    /**
     * 获取文件的URL
     * @param objectName 文件名(有目录要带有目录)
     * @return 文件的URL地址
     */
    public String getOssUrl(String objectName){
        try {
            // 找到最后一个 / 的位置
            int lastSlashIndex = objectName.lastIndexOf('/');
            if (lastSlashIndex >= 0) {
                // 路径部分保留
                String path = objectName.substring(0, lastSlashIndex + 1); // 包含最后的 '/'
                // 文件名部分进行编码
                String filename = objectName.substring(lastSlashIndex + 1);
                return "https://" + bucketName + "." + endpoint + "/" + path + URLEncoder.encode(filename, "UTF-8");
            } else {
                // 没有路径，直接编码整个名字
                return "https://" + bucketName + "." + endpoint + "/" + URLEncoder.encode(objectName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     * @param file 文件
     * @param objectName 文件名（可带有目录）
     * @return 文件的URL
     * @throws IOException
     */
    public String uploadFile(MultipartFile file, String objectName) throws IOException {

        PutObjectResult result;

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

            result = ossClient.putObject(putObjectRequest);
            return getOssUrl(objectName);


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

    /**
     * 将OSS的URL拆分成ObjectName 便于删除
     * @param urlList URL 数组
     * @return 返回 ObjectName 数组
     */
    private List<String> getObjectNameList(List<String> urlList){
        List<String> objectNameList = new ArrayList<>();
        String urlPrefix = "https://" + bucketName + "." + endpoint + "/";
        for (String url : urlList) {
            //截取掉前面的
            if (url.startsWith(urlPrefix)) {
                String objectName = url.substring(urlPrefix.length());
                objectNameList.add(objectName);
            }
        }
        return objectNameList;
    }


    public R deleteFiles(List<String> urlList){

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
            // 删除文件。
            // 填写需要删除的多个文件完整路径。文件完整路径中不能包含Bucket名称。
            List<String> objectNameList = getObjectNameList(urlList);

            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(objectNameList).withEncodingType("url"));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            try {
                for(String obj : deletedObjects) {
                    String deleteObj =  URLDecoder.decode(obj, "UTF-8");
                    System.out.println(deleteObj);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return R.ok("删除成功");
    }

    public MultipartFile urlToMultipartFile(String url) throws Exception {
        //ignoreSsl();
        String decodedUrl = URLDecoder.decode(url, "utf-8");
        File tempFile = null;
        try {
            // 1️⃣ 创建临时文件
            tempFile = File.createTempFile("img_", ".jpg");

            // 2️⃣ 下载图片到临时文件
            try (InputStream in = new URL(decodedUrl).openStream();
                 FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            // 3️⃣ 转 MultipartFile
            try (InputStream inputStream = new FileInputStream(tempFile)) {
                String filename = UUID.randomUUID().toString() + ".jpg"; // 可自定义扩展名
                MultipartFile multipartFile = new MockMultipartFile(
                        "file",          // name
                        filename,        // original filename
                        "image/jpeg",    // content type
                        inputStream      // 输入流
                );
                return multipartFile;
            }

        } finally {
            // 删除临时文件
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
    private void ignoreSsl() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }

}
