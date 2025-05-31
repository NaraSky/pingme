package com.lb.pingme.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class IDGenerateUtil {

    public static String createId(String prefix) {
        if (StringUtils.isNotBlank(prefix)) {
            return prefix.concat("_").concat(uuid());
        }
        return uuid();
    }

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        String uuid = uuid();
        System.out.println("uuid = " + uuid);

        String id = createId("666");
        System.out.println("id = " + id);
    }
}
