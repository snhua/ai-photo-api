package com.aiphone.controller;

import com.aiphone.common.exception.CosResponse;
import com.aiphone.service.CosService;
import com.aiphone.util.FileUtil;
import com.aiphone.util.UserContext;
import com.aiphone.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 腾讯云COS控制器
 */
@Slf4j
@RestController
@RequestMapping("/cos")
@Api(tags = "腾讯云COS相关接口")
public class CosController {

    @Autowired
    private CosService cosService;

    /**
     * 获取临时上传授权
     */
    @GetMapping("/temp-auth")
    @ApiOperation("获取临时上传授权")
    public Result<CosResponse> getTempAuth(
            @ApiParam("文件名") @RequestParam("fileName") String fileName,
            @ApiParam("文件类型") @RequestParam(value = "fileType", defaultValue = "avatar") String fileType) {
        try {
            // 检查用户是否已认证
            if (!UserContext.isAuthenticated()) {
                return Result.error(401, "用户未认证");
            }
            
            Long userId = UserContext.getCurrentUserId();
            if (userId == null) {
                return Result.error(401, "无法获取用户信息");
            }
            
            log.info("userId: {}", userId);

            CosResponse authInfo = cosService.getUploadPolicy(userId, fileName,fileType);
            return Result.success(authInfo);
        } catch (Exception e) {
            log.error("获取临时授权失败", e);
            return Result.error(1005, "获取临时授权失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件访问URL
     */
    @PostMapping("/file-url")
    @ApiOperation("获取文件访问URL")
    public Result<String> getFileUrl(
            @ApiParam("文件对象键") @RequestParam("objectKey") String objectKey) {
        try {
            String fileUrl = cosService.generateFileUrl(objectKey);
            return Result.success(fileUrl);
        } catch (Exception e) {
            log.error("获取文件URL失败", e);
            return Result.error(1005, "获取文件URL失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/file")
    @ApiOperation("删除文件")
    public Result<Boolean> deleteFile(
            @ApiParam("文件对象键") @RequestParam("objectKey") String objectKey) {
        try {
            boolean success = cosService.deleteFile(objectKey);
            if (success) {
                return Result.success(true);
            } else {
                return Result.error(1005, "删除文件失败");
            }
        } catch (Exception e) {
            log.error("删除文件失败", e);
            return Result.error(1005, "删除文件失败：" + e.getMessage());
        }
    }
} 