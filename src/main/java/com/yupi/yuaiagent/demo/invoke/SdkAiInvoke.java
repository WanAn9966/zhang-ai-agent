package com.yupi.yuaiagent.demo.invoke;

import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.Arrays;

/**
 * DashScope SDK invocation demo.
 */
public class SdkAiInvoke {

    private static final String DASHSCOPE_API_KEY_ENV = "DASHSCOPE_API_KEY";

    public static GenerationResult callWithMessage()
            throws ApiException, NoApiKeyException, InputRequiredException {
        String apiKey = System.getenv(DASHSCOPE_API_KEY_ENV);
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + DASHSCOPE_API_KEY_ENV);
        }

        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("You are a helpful assistant.")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("Hello, I am building a project.")
                .build();
        GenerationParam param = GenerationParam.builder()
                .apiKey(apiKey)
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        return gen.call(param);
    }

    public static void main(String[] args) {
        try {
            GenerationResult result = callWithMessage();
            String content = result.getOutput().getChoices().get(0).getMessage().getContent();
            System.out.println("=== AI response ===");
            System.out.println(content);
            System.out.println(JSONUtil.toJsonStr(result));
        } catch (ApiException | NoApiKeyException | InputRequiredException | IllegalStateException e) {
            System.err.println("An error occurred while calling the generation service: " + e.getMessage());
        }
    }
}
