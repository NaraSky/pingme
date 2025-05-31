package com.lb.pingme.common.util;

import java.util.Random;

public class AvatarUtil {


    private static final String PHOTO_PREFIX = "/image/photo/%s.png";

    /**
     * 随机获取一个头像
     *
     * @return
     */
    public static String getRandomAvatar() {
        int name = new Random().nextInt(9) + 1;
        return String.format(PHOTO_PREFIX, name);
    }
}
