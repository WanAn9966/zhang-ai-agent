package com.yupi.yuaiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;
    @Test
    void doChat() {
       String chatId = UUID.randomUUID().toString();
       //
        String message = "你好,我是一名大学生";
        String answer = loveApp.doChat(message,chatId);
        System.out.println(answer);

        //�ڶ���
        message = "我有一个女朋友,她叫晴晴";
        answer = loveApp.doChat(message,chatId);
        System.out.println(answer);
        Assertions.assertNotNull(answer);

        //������
        message = "我女朋友叫什么名字?";
        answer = loveApp.doChat(message,chatId);
        System.out.println(answer);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好,我是小明,我想让我的女朋友更爱我一点,但是我不知道应该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message,chatId);
        Assertions.assertNotNull(loveReport);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了,但是婚后不亲密";
        String  loveReport = loveApp.doChatWithRag(message,chatId);
        Assertions.assertNotNull(loveReport);
    }


    @Test
    void doChatWithTools() {

       //testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");


        testMessage("最近和对象吵架了，看看百度(https://www.baidu.com)其他情侣是怎么解决矛盾的？");
      //  testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");



      /*  testMessage("执行 Python3 脚本来生成数据分析报告");


       testMessage("保存我的恋爱档案为文件");


       testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");*/
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {

        String chatId = UUID.randomUUID().toString();

       /* String message = "我的另一半居住在上海静安区，请帮我找到 5 公里内合适的约会地点";
        String answer =  loveApp.doChatWithMcp(message, chatId);*/
        String message = "帮我找一些玫瑰花的图片";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }
}