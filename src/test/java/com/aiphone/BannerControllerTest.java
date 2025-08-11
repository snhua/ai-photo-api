package com.aiphone;

import com.aiphone.common.Result;
import com.aiphone.controller.BannerController;
import com.aiphone.dto.BannerDTO;
import com.aiphone.service.BannerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 横幅控制器测试类
 */
@SpringBootTest
public class BannerControllerTest {

    @Autowired
    private BannerController bannerController;

    @MockBean
    private BannerService bannerService;

    @Test
    public void testGetHomeBanners() {
        // 准备测试数据
        BannerDTO banner1 = new BannerDTO();
        banner1.setId(1L);
        banner1.setTitle("首页横幅1");
        banner1.setDescription("首页横幅描述1");
        banner1.setImageUrl("https://example.com/banner1.jpg");
        banner1.setLinkUrl("https://example.com/link1");
        banner1.setType("home");
        banner1.setSortWeight(100);
        banner1.setStatus(1);
        banner1.setStartTime(LocalDateTime.now().minusDays(1));
        banner1.setEndTime(LocalDateTime.now().plusDays(30));
        banner1.setCreatedAt(LocalDateTime.now());

        BannerDTO banner2 = new BannerDTO();
        banner2.setId(2L);
        banner2.setTitle("首页横幅2");
        banner2.setDescription("首页横幅描述2");
        banner2.setImageUrl("https://example.com/banner2.jpg");
        banner2.setLinkUrl("https://example.com/link2");
        banner2.setType("home");
        banner2.setSortWeight(90);
        banner2.setStatus(1);
        banner2.setStartTime(LocalDateTime.now().minusDays(1));
        banner2.setEndTime(LocalDateTime.now().plusDays(30));
        banner2.setCreatedAt(LocalDateTime.now());

        List<BannerDTO> mockBanners = Arrays.asList(banner1, banner2);

        // 模拟服务层方法
        when(bannerService.getHomeBanners(5)).thenReturn(mockBanners);

        // 测试获取首页横幅
        Result<List<BannerDTO>> result = bannerController.getHomeBanners(5);
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(2, result.getData().size());

        // 验证服务方法被调用
        verify(bannerService, times(1)).getHomeBanners(5);
    }

    @Test
    public void testGetBannersByType() {
        // 准备测试数据
        BannerDTO banner = new BannerDTO();
        banner.setId(1L);
        banner.setTitle("分类横幅");
        banner.setDescription("分类横幅描述");
        banner.setImageUrl("https://example.com/category-banner.jpg");
        banner.setLinkUrl("https://example.com/category-link");
        banner.setType("category");
        banner.setSortWeight(80);
        banner.setStatus(1);
        banner.setCreatedAt(LocalDateTime.now());

        List<BannerDTO> mockBanners = Arrays.asList(banner);

        // 模拟服务层方法
        when(bannerService.getBannersByType("category")).thenReturn(mockBanners);

        // 测试根据类型获取横幅
        Result<List<BannerDTO>> result = bannerController.getBannersByType("category");
        assertNotNull(result);
        assertEquals(0, result.getCode());
        assertEquals("success", result.getMessage());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());

        // 验证服务方法被调用
        verify(bannerService, times(1)).getBannersByType("category");
    }

    @Test
    public void testGetHomeBannersWithException() {
        // 模拟服务层抛出异常
        when(bannerService.getHomeBanners(anyInt()))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 测试异常处理
        Result<List<BannerDTO>> result = bannerController.getHomeBanners(5);
        assertNotNull(result);
        assertEquals(1005, result.getCode());
        assertTrue(result.getMessage().contains("获取首页横幅失败"));
        assertNull(result.getData());
    }
} 