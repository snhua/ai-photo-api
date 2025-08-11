package com.aiphone.security;

import com.aiphone.entity.User;
import com.aiphone.service.UserService;
import com.aiphone.util.JwtUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序认证服务
 */
@Slf4j
@Service
public class WechatAuthenticationService {

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

//    @Autowired
//    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 通过code获取openid
     */
    public String getOpenidByCode(String code) {
        try {
            String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appid, secret, code
            );

            log.info("调用微信API获取openid: url={}", url);
            String res = restTemplate.getForObject(url, String.class);
            JSONObject response = JSON.parseObject(res);
            if (response != null && response.containsKey("openid")) {
                String openid = (String) response.get("openid");
                log.info("获取openid成功: openid={}", openid);
                return openid;
            } else {
                log.error("获取openid失败: response={}", response);
                return null;
            }
        } catch (Exception e) {
            log.error("调用微信API失败", e);
            return null;
        }
    }

    /**
     * 微信小程序登录认证
     */
//    public String authenticate(String code, String nickName, String avatarUrl) {
//        try {
//            // 1. 通过code获取openid
//            String openid = getOpenidByCode(code);
//            if (openid == null) {
//                throw new RuntimeException("获取openid失败");
//            }
//
//            // 2. 查找或创建用户
//            User user = userService.getUserByOpenid(openid);
//            if (user == null) {
//                // 创建新用户
//                user = new User();
//                user.setOpenid(openid);
//                user.setNickname(nickName);
//                user.setAvatar(avatarUrl);
//                user.setUserType("user");
//                user.setStatus(1);
//                userService.createUser(user);
//            } else {
//                // 更新用户信息
//                user.setNickname(nickName);
//                user.setAvatar(avatarUrl);
//                userService.updateUser(user);
//            }
//
//            // 3. 生成JWT token
//            return generateToken(openid);
//        } catch (Exception e) {
//            log.error("微信登录认证失败", e);
//            throw new RuntimeException("登录失败");
//        }
//    }

    /**
     * 生成JWT token
     */
    public String generateToken(String openid) {
        return jwtUtil.generateToken(openid);
    }

    /**
     * 验证JWT token
     */
    public boolean validateToken(String token, String openid) {
        return jwtUtil.validateToken(token, openid);
    }

    /**
     * 从token中获取openid
     */
    public String getOpenidFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
} 