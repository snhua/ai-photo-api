package com.aiphone.mapper;

import com.aiphone.entity.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 消息通知Mapper接口
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 根据用户ID获取消息列表
     */
    @Select("SELECT * FROM messages WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Message> getByUserId(@Param("userId") Long userId);
    
    /**
     * 根据类型获取消息列表
     */
    @Select("SELECT * FROM messages WHERE user_id = #{userId} AND type = #{type} ORDER BY created_at DESC")
    List<Message> getByType(@Param("userId") Long userId, @Param("type") String type);
    
    /**
     * 获取未读消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE user_id = #{userId} AND is_read = 0")
    Long getUnreadCount(@Param("userId") Long userId);
    
    /**
     * 标记消息为已读
     */
    @Update("UPDATE messages SET is_read = 1 WHERE id = #{messageId}")
    int markAsRead(@Param("messageId") Long messageId);
    
    /**
     * 标记用户所有消息为已读
     */
    @Update("UPDATE messages SET is_read = 1 WHERE user_id = #{userId}")
    int markAllAsRead(@Param("userId") Long userId);
} 