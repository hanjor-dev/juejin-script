package com.hanjor.juejinscript.task;

import cn.hutool.core.util.StrUtil;
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
    private static final Log log = LogFactory.get();

    //  签到
    private static final String CHECK_IN_API = "https://api.juejin.cn/growth_api/v1/check_in";
    // 抽奖沾喜气
    private static final String DIP_LUCKY_API = "https://api.juejin.cn/growth_api/v1/lottery_lucky/dip_lucky";
    // 发送沸点
    private static final String SEND_MSG_API = "https://api.juejin.cn/content_api/v1/short_msg/publish";
    // 获取推荐沸点列表
    private static final String RECOMMEND_MSG_API = "https://api.juejin.cn/recommend_api/v1/short_msg/recommend";
    // 点赞
    private static final String DIGG_API = "https://api.juejin.cn/interact_api/v1/digg/save";

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
     * 定时自动发送沸点
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
        String res = HttpRequest.post(SEND_MSG_API)
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

    /**
     * 自动点赞沸点 （增加活跃，防止脚本封禁）
     */
    @Scheduled(cron = "${cron.digg}")
    public void diggMessage() {
        String params = "{\"id_type\":4,\"sort_type\":300,\"cursor\":\"0\",\"limit\":1}";
        String res = HttpRequest.post(RECOMMEND_MSG_API)
                .header("cookie", Juejin.cookie)
                .form("aid",Juejin.aid)
                .form("uuid", Juejin.uuid)
                .body(params)
                .execute().body();

        if (res.contains(Juejin.SUCCESS_MSG)) {
            String msgId = StrUtil.subBetween(res, "\"msg_id\":\"", "\",\"msg_Info\"");

            String diggParam = "{\"item_id\":\"" + msgId + "\",\"item_type\":4,\"client_type\":" + Juejin.aid + "}";
            String diggRes = HttpRequest.post(DIGG_API)
                    .header("cookie", Juejin.cookie)
                    .form("aid",Juejin.aid)
                    .form("uuid", Juejin.uuid)
                    .body(diggParam)
                    .execute().body();
            if (diggRes.contains(Juejin.SUCCESS_MSG)) {
                log.info("沸点【{}】，点赞成功。", msgId);
            } else {
                log.error("沸点点赞失败。diggRes: {}", diggRes);
            }
        } else {
            log.error("获取沸点列表失败。res: {}", res);
        }
    }

}
