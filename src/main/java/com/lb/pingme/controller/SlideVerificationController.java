package com.lb.pingme.controller;

import com.lb.pingme.common.bean.APIResponseBean;
import com.lb.pingme.common.bean.APIResponseBeanUtil;
import com.lb.pingme.config.annotation.ValidatePermission;
import com.lb.pingme.domain.vo.request.SlideVerificationVO;
import com.lb.pingme.service.SlideVerificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "【登录】滑块验证码")
@RestController
@RequestMapping("/api/slide/verification")
public class SlideVerificationController {

    @Autowired
    private SlideVerificationService slideVerificationService;

    @ApiOperation("配置滑块验证")
    @ValidatePermission
    @PostMapping("/save")
    public APIResponseBean<Long> save(@RequestBody SlideVerificationVO slideVerificationVO) {
        Long id = slideVerificationService.save(slideVerificationVO);
        return APIResponseBeanUtil.success(id);
    }

    @ApiOperation("修改滑块验证配置状态")
    @ValidatePermission
    @PostMapping("/status/{id}/{status}")
    public APIResponseBean<Long> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        slideVerificationService.updateStatus(id, status);
        return APIResponseBeanUtil.success();
    }

}
