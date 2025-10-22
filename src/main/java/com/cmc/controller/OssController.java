package com.cmc.controller;

import com.cmc.common.R;
import com.cmc.dto.ImageDTO;
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

}
