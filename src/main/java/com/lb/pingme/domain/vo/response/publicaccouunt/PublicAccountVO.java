package com.lb.pingme.domain.vo.response.publicaccouunt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PublicAccountVO {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("公众号名称")
    private String name;

    @ApiModelProperty("公众号头像")
    private String photo;
}
