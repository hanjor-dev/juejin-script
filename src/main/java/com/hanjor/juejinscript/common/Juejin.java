package com.hanjor.juejinscript.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: junjie.han
 * @time: 2022/2/17 17:30
 * @description: 掘金相关配置值
 */
@Component
public class Juejin {

    @Value("${juejin.cookie}")
    public static String cookie;

    @Value("${juejin.aid}")
    public static String aid;

    @Value("${juejin.uuid}")
    public static String uuid;

    // response 返回消息-成功
    public static final String SUCCESS_MSG = "\"err_no\":0,\"err_msg\":\"success\"";
}
