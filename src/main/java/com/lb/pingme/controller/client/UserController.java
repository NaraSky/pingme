package com.lb.pingme.controller.client;

import com.lb.pingme.common.bean.APIResponseBean;
import com.lb.pingme.common.bean.APIResponseBeanUtil;
import com.lb.pingme.common.enums.ClickEvent;
import com.lb.pingme.common.enums.RoleCodeEnum;
import com.lb.pingme.common.helper.SessionHelper;
import com.lb.pingme.common.util.AvatarUtil;
import com.lb.pingme.config.annotation.SafeClick;
import com.lb.pingme.config.annotation.ValidateLogin;
import com.lb.pingme.config.annotation.ValidatePermission;
import com.lb.pingme.domain.vo.request.*;
import com.lb.pingme.service.SlideVerificationService;
import com.lb.pingme.service.UserService;
import com.lb.pingme.service.ValidCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ValidCodeService validCodeService;

    @Autowired
    private SlideVerificationService slideVerificationService;

    @PostMapping("/registry")
    public APIResponseBean registry(@RequestBody UserRegistryInfoRequestVO requestVO) {
        validCodeService.validCode(requestVO.getPicCheckCode());
        String photo = AvatarUtil.getRandomAvatar();
        userService.registry(photo,requestVO.getUserName(), requestVO.getMobile(), requestVO.getPassword());
        return APIResponseBeanUtil.success("注册成功");
    }

    /**
     * 创建机器人接口
     * 机器人复用用户表，通过角色字段区分，这种设计简化了表结构
     */
    @PostMapping("/createRobot")
    public APIResponseBean createRobot(@RequestBody CreateRobotRequestVO requestVO) {
        String currentUserId = SessionHelper.getCurrentUserId();
        requestVO.setCreateUserId(currentUserId);
        userService.createRobot(requestVO);
        return APIResponseBeanUtil.success("机器人创建成功");
    }

    /**
     * 创建公众号接口
     * 使用@ValidatePermission注解进行权限校验，只有管理员才能创建公众号
     * 这种声明式的权限控制方式使代码更加清晰
     */
    @ValidatePermission(role = RoleCodeEnum.ADMIN)
    @PostMapping("/createPublicAccount")
    public APIResponseBean createPublicAccount(@RequestBody CreatePublicAccountRequestVO requestVO) {
        String userId = SessionHelper.getCurrentUserId();
        requestVO.setCreateUserId(userId);
        userService.createPublicAccount(requestVO);
        return APIResponseBeanUtil.success("公众号创建成功");
    }

    /**
     * 创建聊天群组接口
     * 组合使用多个注解：登录校验 + 防重复点击，展示了AOP的强大功能
     */
    @ValidateLogin
    @SafeClick(event = ClickEvent.CREATE_GROUP, time = 2000, message = "群聊创建中")
    @PostMapping("/createChatGroup")
    public APIResponseBean createChatGroup(@RequestBody CreateGroupRequestVO requestVO) {
        String userId = SessionHelper.getCurrentUserId();
        requestVO.setCreateUserId(userId);
        userService.createChatGroup(requestVO);
        return APIResponseBeanUtil.success("群组创建成功");
    }

    @PostMapping("/login")
    public APIResponseBean login(@RequestBody UserLoginInfoRequestVO request) {
        Assert.isTrue(request != null, "登录参数为空！");
        String mobile = request.getMobile();
        String password = request.getPassword();
        String solidTokenId = request.getSolidTokenId();
        Assert.isTrue(StringUtils.isNotBlank(mobile), "手机号为空！");
        Assert.isTrue(StringUtils.isNotBlank(password), "密码为空！");
        Assert.isTrue(StringUtils.isNotBlank(solidTokenId), "滑块验证失败！");
        // 滑块验证
        slideVerificationService.validateTokenResult(solidTokenId);
        return APIResponseBeanUtil.success(userService.login(mobile, password));
    }
}
