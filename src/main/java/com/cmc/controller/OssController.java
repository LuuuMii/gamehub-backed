package com.cmc.controller;

import com.cmc.utils.OssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/oss")
public class OssController {

    @Autowired
    private OssUtil ossUtil;

    @PostMapping("/uploadAvatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            ossUtil.uploadFile(file, ossUtil.generateFileName("avatar", file.getOriginalFilename()));
            return "上传成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        }
    }
}
