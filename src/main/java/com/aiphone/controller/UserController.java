package com.aiphone.controller;

import com.aiphone.dto.UserLoginRequest;
import com.aiphone.dto.UserLoginResponse;
import com.aiphone.dto.UserUpdateRequest;
import com.aiphone.entity.FileUpload;
import com.aiphone.entity.User;
import com.aiphone.service.CosService;
import com.aiphone.service.FileUploadService;
import com.aiphone.service.UserService;
import com.aiphone.util.UserContext;
import com.aiphone.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CosService cosService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/login")
    @ApiOperation("微信小程序登录")
    public Result<UserLoginResponse> login(
            @ApiParam("登录请求参数") @Valid @RequestBody UserLoginRequest request) {
        log.info("用户登录请求: {}", request);
        return userService.login(request);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result<User> getUserInfo() {
        // 从SecurityContext获取当前用户ID
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getUserById(userId);
        if (user == null) {
            return Result.error(1004, "用户不存在");
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    @ApiOperation("更新用户信息")
    public Result<User> updateUserInfo(
            @ApiParam("用户信息更新请求") @Valid @RequestBody UserUpdateRequest request) {
        Long userId = UserContext.getCurrentUserId();
        return userService.updateUserInfo(userId, request);
    }

    /**
     * 获取头像上传授权
     */
//    @PostMapping("/avatar/upload-policy")
//    @ApiOperation("获取头像上传授权")
//    public Result<Map<String, Object>> getAvatarUploadPolicy(
//            @ApiParam("文件名") @RequestParam("fileName") String fileName) {
//        try {
//            Long userId = UserContext.getCurrentUserId();
//            Map<String, Object> policy = cosService.getUploadPolicy(userId, fileName, "avatar");
//            return Result.success(policy);
//        } catch (Exception e) {
//            log.error("获取上传授权失败", e);
//            return Result.error(1005, "获取上传授权失败：" + e.getMessage());
//        }
//    }

    /**
     * 更新用户头像
     */
    @PostMapping("/avatar/update")
    @ApiOperation("更新用户头像")
    public Result<User> updateAvatar(
            @ApiParam("文件对象键") @RequestParam("objectKey") String objectKey) {
        try {
            Long userId = UserContext.getCurrentUserId();
            
            // 生成文件URL
            String fileUrl = cosService.generateFileUrl(objectKey);
            
            // 更新用户头像
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setAvatar(fileUrl);
                userService.updateUser(user);
                
                // 保存文件上传记录
                FileUpload fileUpload = new FileUpload();
                fileUpload.setUserId(userId);
                fileUpload.setFileName(objectKey.substring(objectKey.lastIndexOf("/") + 1));
                fileUpload.setFileUrl(fileUrl);
                fileUpload.setFileSize(0L); // COS上传无法获取文件大小
                fileUpload.setFileType("image/*");
                fileUpload.setUploadType("avatar");
                fileUploadService.save(fileUpload);
                
                return Result.success(user);
            } else {
                return Result.error(1004, "用户不存在");
            }
        } catch (Exception e) {
            log.error("更新头像失败", e);
            return Result.error(1005, "更新头像失败：" + e.getMessage());
        }
    }

    /**
     * 上传头像（兼容旧版本）
     */
    @PostMapping("/avatar")
    @ApiOperation("上传头像")
    public Result<FileUpload> uploadAvatar(
            @ApiParam("头像文件") @RequestParam("file") MultipartFile file,
            @ApiParam("文件类型") @RequestParam(value = "type", defaultValue = "avatar") String type) {
        try {
            Long userId = UserContext.getCurrentUserId();
            FileUpload fileUpload = fileUploadService.uploadAvatar(file, userId);
            
            // 更新用户头像
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setAvatar(fileUpload.getFileUrl());
                userService.updateUser(user);
            }
            
            return Result.success(fileUpload);
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return Result.error(1005, "头像上传失败：" + e.getMessage());
        }
    }



    /**
     * 用户提现
     */
    @PostMapping("/withdraw")
    @ApiOperation("用户提现")
    public Result<User> withdraw(
            @ApiParam("提现金额") @RequestParam("amount") java.math.BigDecimal amount) {
        Long userId = UserContext.getCurrentUserId();
        return userService.withdraw(userId, amount);
    }
} 