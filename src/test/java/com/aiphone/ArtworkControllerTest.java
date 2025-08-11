package com.aiphone;

import com.aiphone.common.Result;
import com.aiphone.controller.ArtworkController;
import com.aiphone.dto.ArtworkDTO;
import com.aiphone.entity.Artwork;
import com.aiphone.service.ArtworkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 作品控制器测试类
 */
@SpringBootTest
public class ArtworkControllerTest {

    @Autowired
    private ArtworkController artworkController;

    @MockBean
    private ArtworkService artworkService;

    @Test
    public void testGetHotArtworks() {
        // 准备测试数据
        ArtworkDTO artwork1 = new ArtworkDTO();
        artwork1.setId(1L);
        artwork1.setArtistId(2L);
        artwork1.setTitle("梦幻森林");
        artwork1.setDescription("美丽的森林风景画");
        artwork1.setImageUrl("https://example.com/artwork1.jpg");
        artwork1.setCategory("风景画");
        artwork1.setTags(Arrays.asList("森林", "梦幻", "自然"));
        artwork1.setPrice(new BigDecimal("299.0"));
        artwork1.setStatus(1);
        artwork1.setCreatedAt(LocalDateTime.now());

        ArtworkDTO artwork2 = new ArtworkDTO();
        artwork2.setId(2L);
        artwork2.setArtistId(3L);
        artwork2.setTitle("山水画");
        artwork2.setDescription("传统山水画风格");
        artwork2.setImageUrl("https://example.com/artwork2.jpg");
        artwork2.setCategory("风景画");
        artwork2.setTags(Arrays.asList("山水", "传统", "意境"));
        artwork2.setPrice(new BigDecimal("399.0"));
        artwork2.setStatus(1);
        artwork2.setCreatedAt(LocalDateTime.now());

        List<ArtworkDTO> mockArtworks = Arrays.asList(artwork1, artwork2);

        // 模拟服务层方法
        when(artworkService.getHotArtworks(10, null)).thenReturn(mockArtworks);
        when(artworkService.getHotArtworks(5, "风景画")).thenReturn(Arrays.asList(artwork1));

        // 测试获取热门作品（默认参数）
        Result<List<ArtworkDTO>> result1 = artworkController.getHotArtworks(10, null);
        assertNotNull(result1);
        assertEquals(0, result1.getCode());
        assertEquals("success", result1.getMessage());
        assertNotNull(result1.getData());
        assertEquals(2, result1.getData().size());

        // 测试获取热门作品（指定分类）
        Result<List<ArtworkDTO>> result2 = artworkController.getHotArtworks(5, "风景画");
        assertNotNull(result2);
        assertEquals(0, result2.getCode());
        assertEquals("success", result2.getMessage());
        assertNotNull(result2.getData());
        assertEquals(1, result2.getData().size());

        // 验证服务方法被调用
        verify(artworkService, times(1)).getHotArtworks(10, null);
        verify(artworkService, times(1)).getHotArtworks(5, "风景画");
    }

    @Test
    public void testGetHotArtworksWithException() {
        // 模拟服务层抛出异常
        when(artworkService.getHotArtworks(anyInt(), anyString()))
            .thenThrow(new RuntimeException("数据库连接失败"));

        // 测试异常处理
        Result<List<ArtworkDTO>> result = artworkController.getHotArtworks(10, null);
        assertNotNull(result);
        assertEquals(1005, result.getCode());
        assertTrue(result.getMessage().contains("获取热门作品失败"));
        assertNull(result.getData());
    }
} 