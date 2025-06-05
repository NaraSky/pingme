package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserLoginInfoRequestVO {

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("滑块验证tokenId")
    private String solidTokenId;
}
