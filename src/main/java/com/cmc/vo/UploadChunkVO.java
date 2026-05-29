package com.cmc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadChunkVO {

    private String eTag;
    private String uploadId;
    private String objectName;
    private Integer uploadChunks;
    private Integer totalChunks;

}
