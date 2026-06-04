package com.cmc.controller;

import com.cmc.common.R;
import com.cmc.dto.ImageDTO;
import com.cmc.enums.file.FileCategory;
import com.cmc.service.OssService;
import com.cmc.service.UploadTaskMultipartService;
import com.cmc.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.List;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private OssService ossService;

    @PostMapping("/uploadAvatar")
    public R uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            String url = ossUtil.uploadFile(file, ossUtil.generateFileName("avatar", file.getOriginalFilename()));

            return R.ok("上传成功", url);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传文章封面图
     * @param file
     * @return
     */
    @PostMapping("/uploadCoverImg")
    public R uploadCoverImg(@RequestParam("file") MultipartFile file){
        try {
            String url = ossUtil.uploadFile(file, ossUtil.generateFileName("coverImg", file.getOriginalFilename()));

            return R.ok("上传成功", url);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 上传文章图片
     * @param file
     * @return
     */
    @PostMapping("/uploadArticleImg")
    public R uploadArticleImg(@RequestParam("file") MultipartFile file){
        try {
            String url = ossUtil.uploadFile(file, ossUtil.generateFileName("articleImg", file.getOriginalFilename()));

            return R.ok("上传成功", url);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/uploadImgByUrl")
    public R uploadImgByUrl(@RequestBody ImageDTO imageDTO){
        MultipartFile file = null;
        try {
            file = ossUtil.urlToMultipartFile(imageDTO.getUrl());
            String urlVO = ossUtil.uploadFile(file, ossUtil.generateFileName("articleImg", file.getOriginalFilename()));
            return R.ok("上传成功", urlVO);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }

    @PostMapping("/deleteFiles")
    public R deleteFiles(@RequestBody List<String> urlList){
        R r = ossUtil.deleteFiles(urlList);
        return r;
    }


    /**
     * 分片上传初始化   获取uploadId + 放重复名 ObjectName
     * @param objectName 传入的文件名称
     * @return R   uploadId objectName
     */
    @PostMapping("/upload/init")
    public R initUpload(@RequestParam("objectName") String objectName){
//        return ossUtil.initUpload(objectName);
        return ossService.initUpload(objectName);
    }

    /**
     * 传入分片上传到OSS
     * @param file 分片
     * @param objectName 名称
     * @param uploadId id
     * @param partNumber 当前位置
     * @param totalChunks 总数量
     * @return R
     */
    @PostMapping("/upload/chunk")
    public R uploadChunk(@RequestParam("file") MultipartFile file,
                         @RequestParam("objectName") String objectName,
                         @RequestParam("uploadId") String uploadId,
                         @RequestParam("partNumber") Integer partNumber,
                         @RequestParam("totalChunks") Integer totalChunks){
//        return ossUtil.uploadChunk(file,objectName,uploadId,partNumber,totalChunks);
        return ossService.uploadChunk(file,objectName,uploadId,partNumber,totalChunks);
    }

    /**
     * 合并分片
     * @param objectName 文件地址
     * @param uploadId 分片任务ID
     * @return R
     */
    @PostMapping("/upload/complete")
    public R completeUpload(@RequestParam("objectName") String objectName,
                            @RequestParam("uploadId") String uploadId){
//        return ossUtil.completeUpload(objectName,uploadId);
        return ossService.completeUpload(objectName,uploadId);
    }

    /**
     * 查询分片
     * @param objectName  OSS文件名称
     * @param uploadId 上传ID
     * @return R
     */
    @PostMapping("/upload/listParts")
    public R listParts(@RequestParam("objectName") String objectName,
                       @RequestParam("uploadId") String uploadId){
        return ossService.listParts(objectName,uploadId);
    }


    /**
     *
     * @param file 上传文件
     * @param fileCategory  文件分类(区分OSS文件夹)
     * @return
     */
    @PostMapping("/upload/file")
    public R uploadFile(@RequestParam("file") MultipartFile file,
                        @RequestParam("fileCategory") FileCategory fileCategory){
        return ossService.uploadFile(file,fileCategory);
    }

}
