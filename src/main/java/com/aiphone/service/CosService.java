package com.aiphone.service;

import com.aiphone.common.exception.CosResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 腾讯云COS服务接口
 */
public interface CosService {

    /**
     * 获取临时上传授权
     *
     * @param userId   用户ID
     * @param fileName 文件名
     * @param fileType 文件类型
     * @return 上传授权信息
     */
    CosResponse getUploadPolicy(Long userId, String fileName, String fileType);

    /**
     * 生成文件URL
     *
     * @param objectKey 对象键
     * @return 文件URL
     */
    String generateFileUrl(String objectKey);

    /**
     * 删除文件
     *
     * @param objectKey 对象键
     * @return 删除结果
     */
    boolean deleteFile(String objectKey);

    /**
     * 上传文件到COS
     *
     * @param file     文件
     * @param fileName 文件名
     * @return 文件URL
     */
    String uploadFileToCos(MultipartFile file, String fileName);
} 