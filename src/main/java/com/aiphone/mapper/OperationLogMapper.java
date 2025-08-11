package com.aiphone.mapper;

import com.aiphone.entity.OperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    
    /**
     * 根据用户ID获取操作日志
     */
    @Select("SELECT * FROM operation_logs WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<OperationLog> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据操作类型获取日志
     */
    @Select("SELECT * FROM operation_logs WHERE operation = #{operation} ORDER BY created_at DESC")
    List<OperationLog> getByOperation(@Param("operation") String operation);
    
    /**
     * 获取操作统计
     */
    @Select("SELECT operation, COUNT(*) as count FROM operation_logs GROUP BY operation")
    List<OperationStatistics> getOperationStatistics();
    
    /**
     * 操作统计信息
     */
    class OperationStatistics {
        private String operation;
        private Long count;
        
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
        
        public Long getCount() { return count; }
        public void setCount(Long count) { this.count = count; }
    }
} 