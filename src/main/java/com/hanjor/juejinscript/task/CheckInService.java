package com.hanjor.juejinscript.task;

import cn.hutool.http.HttpRequest;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.hanjor.juejinscript.common.Juejin;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: junjie.han
 * @time: 2022/2/17 17:26
 * @description: 掘金每日签到
 */
@Component
@EnableScheduling
public class CheckInService {
    private final Log log = LogFactory.get();

    private static final String CHECK_IN_API = "https://api.juejin.cn/growth_api/v1/check_in";

    @Scheduled(cron = "${cron.check-in}")
    public void checkIn() {
        String res = HttpRequest.post(CHECK_IN_API)
                .header("cookie", Juejin.cookie)
                .form("aid",Juejin.aid)
                .form("uuid", Juejin.uuid)
                .execute().body();

        if (res.contains(Juejin.SUCCESS_MSG)) {
            log.info("签到成功！");
        } else {
            log.error("签到失败。res: {}", res);
        }
    }

}
