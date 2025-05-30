package com.lb.pingme.common.constants;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP内容类型(ContentType)常量工具类
 * 提供常用的UTF-8编码的ContentType定义和处理方法
 */
public class HttpContentType {

    // 文本纯内容类型，UTF-8编码
    public static final ContentType UTF8_TEXT_PLAIN = ContentType.TEXT_PLAIN.withCharset(Consts.UTF_8);

    // 表单提交内容类型，UTF-8编码
    public static final ContentType UTF8_APPLICATION_FORM_URLENCODED =
            ContentType.APPLICATION_FORM_URLENCODED.withCharset(Consts.UTF_8);

    // JSON内容类型，UTF-8编码
    public static final ContentType UTF8_APPLICATION_JSON = ContentType.APPLICATION_JSON.withCharset(Consts.UTF_8);

    // XML内容类型，UTF-8编码
    public static final ContentType UTF8_APPLICATION_XML = ContentType.APPLICATION_XML.withCharset(Consts.UTF_8);

    // 表单文件上传内容类型，UTF-8编码
    public static final ContentType UTF8_MULTIPART_FORM_DATA =
            ContentType.MULTIPART_FORM_DATA.withCharset(Consts.UTF_8);

    // HTML内容类型，UTF-8编码
    public static final ContentType UTF8_TEXT_HTML = ContentType.TEXT_HTML.withCharset(Consts.UTF_8);

    // XML文本内容类型，UTF-8编码
    public static final ContentType UTF8_TEXT_XML = ContentType.TEXT_XML.withCharset(Consts.UTF_8);

    /**
     * 判断两个ContentType是否完全相等（包括MIME类型和字符集）
     * @param type1 第一个ContentType
     * @param type2 第二个ContentType
     * @return 如果两个ContentType完全相等返回true，否则返回false
     */
    public static boolean equals(ContentType type1, ContentType type2) {
        return equalsMimeType(type1, type2) && type1.getCharset().equals(type2.getCharset());
    }

    /**
     * 判断两个ContentType的MIME类型是否相等
     * @param type1 第一个ContentType
     * @param type2 第二个ContentType
     * @return 如果两个ContentType的MIME类型相等返回true，否则返回false
     */
    public static boolean equalsMimeType(ContentType type1, ContentType type2) {
        if (null == type1 || null == type2) {
            return false;
        }
        return type1.getMimeType().equals(type2.getMimeType());
    }

    /**
     * 获取支持的媒体类型列表
     * @return 包含所有支持的MediaType的列表
     */
    public static List<MediaType> getSupportedMediaTypes() {
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.APPLICATION_ATOM_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
        supportedMediaTypes.add(MediaType.APPLICATION_PDF);
        supportedMediaTypes.add(MediaType.APPLICATION_RSS_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XHTML_XML);
        supportedMediaTypes.add(MediaType.APPLICATION_XML);
        supportedMediaTypes.add(MediaType.IMAGE_GIF);
        supportedMediaTypes.add(MediaType.IMAGE_JPEG);
        supportedMediaTypes.add(MediaType.IMAGE_PNG);
        supportedMediaTypes.add(MediaType.TEXT_EVENT_STREAM);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(MediaType.TEXT_MARKDOWN);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_XML);
        return supportedMediaTypes;
    }
}