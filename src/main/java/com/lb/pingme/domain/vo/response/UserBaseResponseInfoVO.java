package com.lb.pingme.domain.vo.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserBaseResponseInfoVO {

    private Long id;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("头像")
    private String photo;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("签名")
    private String signature;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("角色code")
    private Integer roleCode;

    @ApiModelProperty("注册时间")
    private Long registryTime;

    private String registryTimeStr;
}
