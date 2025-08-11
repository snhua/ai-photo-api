package com.aiphone.service.impl;

import com.aiphone.entity.FileUpload;
import com.aiphone.mapper.FileUploadMapper;
import com.aiphone.service.FileUploadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现类
 */
@Slf4j
@Service
public class FileUploadServiceImpl extends ServiceImpl<FileUploadMapper, FileUpload> implements FileUploadService {

    @Value("${file.upload.path:/uploads}")
    private String uploadPath;

    @Value("${file.upload.url-prefix:/uploads}")
    private String urlPrefix;

    @Override
    public FileUpload uploadAvatar(MultipartFile file, Long userId) {
        return uploadImage(file, userId, "avatar");
    }

    @Override
    public FileUpload uploadImage(MultipartFile file, Long userId, String uploadType) {
        return uploadFile(file, userId, uploadType);
    }

    @Override
    public FileUpload uploadFile(MultipartFile file, Long userId, String uploadType) {
        try {
            // 验证文件
            validateFile(file);

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String fileName = generateFileName(extension);

            // 创建目录
            String relativePath = getRelativePath(uploadType);
            Path uploadDir = Paths.get(uploadPath, relativePath);
            Files.createDirectories(uploadDir);

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // 保存到数据库
            FileUpload fileUpload = new FileUpload();
            fileUpload.setUserId(userId);
            fileUpload.setFileName(originalFilename);
            fileUpload.setFileUrl(urlPrefix + "/" + relativePath + "/" + fileName);
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFileType(file.getContentType());
            fileUpload.setUploadType(uploadType);

            this.save(fileUpload);

            return fileUpload;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(Long fileId, Long userId) {
        FileUpload fileUpload = this.getById(fileId);
        if (fileUpload == null || !fileUpload.getUserId().equals(userId)) {
            return false;
        }

        // 删除物理文件
        try {
            String fileUrl = fileUpload.getFileUrl();
            String relativePath = fileUrl.replace(urlPrefix, "");
            Path filePath = Paths.get(uploadPath, relativePath);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 记录日志但不抛出异常
            log.warn("删除物理文件失败: {}", e.getMessage());
        }

        // 删除数据库记录
        return this.removeById(fileId);
    }

    @Override
    public List<FileUpload> getUserFiles(Long userId, String uploadType) {
        if (StringUtils.hasText(uploadType)) {
            return baseMapper.getByUploadType(userId, uploadType);
        } else {
            return baseMapper.getByUserId(userId);
        }
    }

    @Override
    public boolean save(FileUpload fileUpload) {
        return super.save(fileUpload);
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 检查文件大小（10MB）
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只支持图片文件");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * 获取相对路径
     */
    private String getRelativePath(String uploadType) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return uploadType + "/" + datePath;
    }
} 