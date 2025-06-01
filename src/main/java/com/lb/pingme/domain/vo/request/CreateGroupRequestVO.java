package com.lb.pingme.domain.vo.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
public class CreateGroupRequestVO {

    @ApiModelProperty("群组名称")
    private String groupName;

    @ApiModelProperty("群组用户")
    private Set<String> userIds;

    @ApiModelProperty("创建人id，无需前端传递")
    private String createUserId;
}
