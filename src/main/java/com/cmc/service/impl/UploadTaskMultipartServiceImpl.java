package com.cmc.service.impl;

import com.cmc.entity.UploadTaskMultipart;
import com.cmc.mapper.UploadTaskMultipartMapper;
import com.cmc.service.UploadTaskMultipartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 文件上传任务表（分片上传管理） 服务实现类
 * </p>
 *
 * @author C
 * @since 2026-05-28
 */
@Service
public class UploadTaskMultipartServiceImpl extends ServiceImpl<UploadTaskMultipartMapper, UploadTaskMultipart> implements UploadTaskMultipartService {

}
