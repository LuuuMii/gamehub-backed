package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cmc.common.R;
import com.cmc.entity.UploadTaskMultipart;
import com.cmc.enums.video.UploadTaskStatus;
import com.cmc.mapper.UploadTaskMultipartMapper;
import com.cmc.service.OssService;
import com.cmc.utils.OssUtil;
import com.cmc.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@Transactional
public class OssServiceImpl implements OssService {

    @Autowired
    private OssUtil ossUtil;

    @Autowired
    private UploadTaskMultipartMapper uploadTaskMultipartMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public R initUpload(String objectName) {
        R r = ossUtil.initUpload(objectName);

        // 注册上传信息
        Map<String,Object> dataMap = (Map<String, Object>) r.getData();
        String uploadId = (String) dataMap.get("uploadId");

        UploadTaskMultipart task = new UploadTaskMultipart();
        task.setUserId(UserContext.getUser().getId());
        task.setFileName(objectName);
        task.setObjectName(ossUtil.generateFileName("video",objectName));
        task.setFileMd5("");
        task.setUploadId(uploadId);
        task.setStatus(UploadTaskStatus.INIT.getCode());

        uploadTaskMultipartMapper.insert(task);

        return r;
    }

    @Override
    public R uploadChunk(MultipartFile file, String objectName, String uploadId, Integer partNumber, Integer totalChunks) {

        String partKey = "upload:" + uploadId + ":parts";

        R r = ossUtil.uploadChunk(file, objectName, uploadId, partNumber, totalChunks);

        LambdaQueryWrapper<UploadTaskMultipart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UploadTaskMultipart::getUploadId, uploadId);

        Long size = stringRedisTemplate.opsForSet().size(partKey);

        UploadTaskMultipart task = uploadTaskMultipartMapper.selectOne(queryWrapper);
        task.setTotalChunks(totalChunks);
        task.setStatus(UploadTaskStatus.UPLOADING.getCode());
        task.setUploadedChunks(size==null? 0 : Integer.parseInt(size+""));

        uploadTaskMultipartMapper.updateById(task);

        return r;
    }

    @Override
    public R completeUpload(String objectName, String uploadId) {
        R r = ossUtil.completeUpload(objectName, uploadId);

        LambdaQueryWrapper<UploadTaskMultipart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UploadTaskMultipart::getUploadId, uploadId);

        UploadTaskMultipart task = uploadTaskMultipartMapper.selectOne(queryWrapper);
        task.setStatus(UploadTaskStatus.SUCCESS.getCode());

        uploadTaskMultipartMapper.updateById(task);

        return r;
    }
}
