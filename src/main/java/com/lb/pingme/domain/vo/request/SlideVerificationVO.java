package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SlideVerificationVO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("x坐标")
    private Integer x;

    @ApiModelProperty("y坐标")
    private Integer y;
}
