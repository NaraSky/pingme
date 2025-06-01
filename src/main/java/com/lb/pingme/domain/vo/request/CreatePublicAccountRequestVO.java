package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreatePublicAccountRequestVO {

    private Long id;

    @ApiModelProperty("公众号账号")
    private String account;

    @ApiModelProperty("账号名称")
    private String accountName;

    @ApiModelProperty("头像")
    private String accountPhoto;

    @ApiModelProperty("公众号简介")
    private String signature;

    private String createUserId;
}
