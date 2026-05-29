package com.cmc.mapper;

import com.cmc.entity.UploadTaskMultipart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 文件上传任务表（分片上传管理） Mapper 接口
 * </p>
 *
 * @author C
 * @since 2026-05-28
 */
@Repository
public interface UploadTaskMultipartMapper extends BaseMapper<UploadTaskMultipart> {

}
