package com.aiphone.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 作品交付DTO
 */
@Data
@ApiModel(description = "作品交付信息")
public class DeliveryDTO {

    @ApiModelProperty(value = "作品文件URL列表", required = true)
    @NotNull(message = "作品文件不能为空")
    private List<String> artworkUrls;

    @ApiModelProperty(value = "作品标题", required = true)
//    @NotBlank(message = "作品标题不能为空")
    private String title;

    @ApiModelProperty(value = "作品描述")
    private String description;

    @ApiModelProperty(value = "作品说明")
    private String notes;

    @ApiModelProperty(value = "是否包含源文件")
    private Boolean includeSourceFiles = false;

    @ApiModelProperty(value = "源文件URL列表")
    private List<String> sourceFileUrls;

    @ApiModelProperty(value = "作品标签")
    private List<String> tags;

    @ApiModelProperty(value = "作品分类")
    private String category;

    @ApiModelProperty(value = "作品风格")
    private String style;

    @ApiModelProperty(value = "作品尺寸")
    private String dimensions;

    @ApiModelProperty(value = "作品格式")
    private String format;

    @ApiModelProperty(value = "作品分辨率")
    private String resolution;

    @ApiModelProperty(value = "制作时间（小时）")
    private Integer workHours;

    @ApiModelProperty(value = "技术说明")
    private String technicalNotes;

    @ApiModelProperty(value = "客户反馈处理说明")
    private String feedbackHandling;
} 