package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRegistryInfoRequestVO {

    @ApiModelProperty("头像")
    private String photo;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("数字验证码")
    private String picCheckCode;
}
