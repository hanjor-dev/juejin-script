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
 * @description: 掘金脚本
 */
@Component
@EnableScheduling
public class JuejinService {
    private final Log log = LogFactory.get();

    //  签到地址
    private static final String CHECK_IN_API = "https://api.juejin.cn/growth_api/v1/check_in";
    // 沾喜气地址
    private static final String DIP_LUCKY_API = "https://api.juejin.cn/growth_api/v1/lottery_lucky/dip_lucky";
    // 发送沸点地址
    private static final String SEND_PIN_API = "https://api.juejin.cn/content_api/v1/short_msg/publish";

    /**
     * 签到领矿石
     */
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

    /**
     * 沾抽奖福气值
     */
    @Scheduled(cron = "${cron.dip-lucky}")
    public void dipLucky() {
        String res = HttpRequest.post(DIP_LUCKY_API)
                .header("cookie", Juejin.cookie)
                .form("aid",Juejin.aid)
                .form("uuid", Juejin.uuid)
                .body("{\"lottery_history_id\":\"7065132644912070686\"}")
                .execute().body();
        if (res.contains(Juejin.SUCCESS_MSG)) {
            log.info("粘福气成功！res: {}", res);
        } else {
            log.error("粘福气失败。res: {}", res);
        }
    }

    /**
     * 定时发送沸点
     */
    @Scheduled(cron = "${cron.send-pin}")
    public void send() {
        sendPin("jym好，又是摸鱼的一天。");
    }

    /**
     * 发送沸点
     */
    private void sendPin(String content) {
        String contentJson = "{\"content\":" + content + ",\"sync_to_org\":false}";
        String res = HttpRequest.post(SEND_PIN_API)
                .header("cookie", Juejin.cookie)
                .form("aid",Juejin.aid)
                .form("uuid", Juejin.uuid)
                .body(contentJson)
                .execute().body();
        if (res.contains(Juejin.SUCCESS_MSG)) {
            log.info("掘金沸点发送成功。【{}】", contentJson);
        } else {
            log.error("掘金沸点发送失败。res: {}", res);
        }
    }

}
