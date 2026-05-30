package com.cmc.service;

import com.cmc.common.R;
import org.springframework.web.multipart.MultipartFile;

public interface OssService {

    public R initUpload(String objectName);

    public R uploadChunk(MultipartFile file, String objectName, String uploadId, Integer partNumber, Integer totalChunks);

    public R completeUpload(String objectName, String uploadId);

    R listParts(String objectName, String uploadId);
}
