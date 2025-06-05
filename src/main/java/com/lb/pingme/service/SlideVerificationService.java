package com.lb.pingme.service;

import com.lb.pingme.common.enums.CommonStatusEnum;
import com.lb.pingme.common.enums.RedisKeyEnum;
import com.lb.pingme.common.util.JsonUtil;
import com.lb.pingme.domain.vo.request.SlideVerificationVO;
import com.lb.pingme.repository.dao.ISlideVerificationEntityDAO;
import com.lb.pingme.repository.entity.SlideVerificationEntity;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SlideVerificationService {

    @Autowired
    private ISlideVerificationEntityDAO slideVerificationEntityDAO;

    @Autowired
    private RedisService redisService;

    public void validateTokenResult(String id) {
        String tokenKey = RedisKeyEnum.SLIDE_VERIFICATION_TOKEN_VALIDATE_RESULT_CACHE.getKey(id);
        String cache = redisService.get(tokenKey);
        Assert.isTrue(ObjectUtils.equals(cache, "1"), "滑块验证失败");
    }

    // 保存滑块验证码配置（图片及初始状态）
    public Long save(SlideVerificationVO slideVerificationVO) {
        SlideVerificationEntity slideVerificationEntity = convert(slideVerificationVO);
        slideVerificationEntity = slideVerificationEntityDAO.save(slideVerificationEntity);
        return slideVerificationEntity.getId();
    }

    // 更新滑块配置的状态（比如启用/禁用）
    public void updateStatus(Long id, Integer status) {
        SlideVerificationEntity slideVerificationEntity = slideVerificationEntityDAO.findById(id).orElse(null);
        Assert.isTrue(slideVerificationEntity != null, "滑块验证信息编辑失败：未查找到！");
        slideVerificationEntity.setStatus(status);
        slideVerificationEntityDAO.save(slideVerificationEntity);
        this.refreshSlideVerificationCache();
    }

    // VO对象转Entity对象
    private SlideVerificationEntity convert(SlideVerificationVO slideVerificationVO) {
        SlideVerificationEntity slideVerificationEntity = new SlideVerificationEntity();
        slideVerificationEntity.setStatus(CommonStatusEnum.NEW.getStatusCode());
        slideVerificationEntity.setImage(slideVerificationVO.getImage());
        return slideVerificationEntity;
    }

    // 更新Redis缓存：获取已发布的配置，刷新缓存中的滑块图片列表
    private void refreshSlideVerificationCache() {
        List<SlideVerificationEntity> list = slideVerificationEntityDAO.findAllByStatus(CommonStatusEnum.PUBLISHED.getStatusCode());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<String> voList = list.stream().map(SlideVerificationEntity::getImage).collect(Collectors.toList());
        String key = RedisKeyEnum.SLIDE_VERIFICATION_CACHE.getKey();
        redisService.remove(key);
        redisService.set(key, JsonUtil.toJsonString(voList), RedisKeyEnum.SLIDE_VERIFICATION_CACHE.getExpireTime());
    }

}
