package com.aiphone.mapper;

import com.aiphone.entity.FileUpload;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件上传记录Mapper接口
 */
@Mapper
public interface FileUploadMapper extends BaseMapper<FileUpload> {
    
    /**
     * 根据用户ID获取文件列表
     */
    @Select("SELECT * FROM file_uploads WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<FileUpload> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据上传类型获取文件列表
     */
    @Select("SELECT * FROM file_uploads WHERE user_id = #{userId} AND upload_type = #{uploadType} ORDER BY created_at DESC")
    List<FileUpload> getByUploadType(@Param("userId") Long userId, @Param("uploadType") String uploadType);
    
    /**
     * 获取用户文件统计
     */
    @Select("SELECT COUNT(*) as total, SUM(file_size) as totalSize FROM file_uploads WHERE user_id = #{userId}")
    FileStatistics getUserFileStatistics(@Param("userId") Long userId);
    
    /**
     * 文件统计信息
     */
    class FileStatistics {
        private Long total;
        private Long totalSize;
        
        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }
        
        public Long getTotalSize() { return totalSize; }
        public void setTotalSize(Long totalSize) { this.totalSize = totalSize; }
    }
}