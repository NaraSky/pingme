package com.lb.pingme.controller.client;

import com.lb.pingme.common.bean.APIResponseBean;
import com.lb.pingme.common.bean.APIResponseBeanUtil;
import com.lb.pingme.common.util.AvatarUtil;
import com.lb.pingme.domain.vo.request.UserRegistryInfoRequestVO;
import com.lb.pingme.service.UserService;
import com.lb.pingme.service.ValidCodeService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/registry")
    public APIResponseBean registry(@RequestBody UserRegistryInfoRequestVO requestVO) {
        validCodeService.validCode(requestVO.getPicCheckCode());
        String photo = AvatarUtil.getRandomAvatar();
        userService.registry(photo,requestVO.getUserName(), requestVO.getMobile(), requestVO.getPassword());
        return APIResponseBeanUtil.success("注册成功");
    }
}
