package com.aiphone.service;

import com.aiphone.entity.FileUpload;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @param userId 用户ID
     * @return 上传结果
     */
    FileUpload uploadAvatar(MultipartFile file, Long userId);

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @param userId 用户ID
     * @param uploadType 上传类型
     * @return 上传结果
     */
    FileUpload uploadImage(MultipartFile file, Long userId, String uploadType);

    /**
     * 上传文件
     *
     * @param file 文件
     * @param userId 用户ID
     * @param uploadType 上传类型
     * @return 上传结果
     */
    FileUpload uploadFile(MultipartFile file, Long userId, String uploadType);

    /**
     * 删除文件
     *
     * @param fileId 文件ID
     * @param userId 用户ID
     * @return 删除结果
     */
    boolean deleteFile(Long fileId, Long userId);

    /**
     * 获取用户文件列表
     *
     * @param userId 用户ID
     * @param uploadType 上传类型
     * @return 文件列表
     */
    java.util.List<FileUpload> getUserFiles(Long userId, String uploadType);

    /**
     * 保存文件上传记录
     *
     * @param fileUpload 文件上传记录
     * @return 保存结果
     */
    boolean save(FileUpload fileUpload);
} 