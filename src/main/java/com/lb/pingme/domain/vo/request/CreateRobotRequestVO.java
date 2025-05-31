package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateRobotRequestVO {

    private Long id;

    @ApiModelProperty("机器人账号，类似 robot1、gpt等自定义英文账号")
    private String robotNumber;

    @ApiModelProperty("机器人名称")
    private String robotName;

    @ApiModelProperty("头像")
    private String robotPhoto;

    @ApiModelProperty("机器人角色")
    private String robotRole;

    @ApiModelProperty("创建人Id")
    private String createUserId;
}
