package com.yupi.jieOj;

import cn.hutool.json.JSONUtil;
import com.yupi.jieOj.config.WxOpenConfig;
import com.yupi.jieOj.judge.codesandbox.model.ExecuteCodeResponse;
import com.yupi.jieOj.model.dto.quiestionsubmit.JudgeInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Resource
//    private WxOpenConfig wxOpenConfig;
//
//    @Test
//    void contextLoads() {
//        System.out.println(wxOpenConfig);
//    }
//
//    @Test
//    void redisTest() {
//        String key = "name";
//        String s = stringRedisTemplate.opsForValue().get(key);
//        System.out.println(s);
//    }

    @Test
    void jsonTest() {
        String s = "{\"message\":null,\"status\":1,\"judgeinfo\":{\"message\":\"运行成功\",\"memory\":3371008,\"time\":508},\"outputList\":[\"3\",\"7\"]}";
        System.out.println(JSONUtil.toBean(s, ExecuteCodeResponse.class));
    }

}
