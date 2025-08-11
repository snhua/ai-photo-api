package com.aiphone.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 文件上传记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file_uploads")
public class FileUpload {
    
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 上传用户ID
     */
    private Long userId;
    
    /**
     * 文件名
     */
    private String fileName;
    
    /**
     * 文件URL
     */
    private String fileUrl;
    
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    
    /**
     * 文件类型
     */
    private String fileType;
    
    /**
     * 上传类型：image-图片，file-文件
     */
    private String uploadType;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
} 