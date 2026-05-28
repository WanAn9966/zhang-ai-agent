package com.yupi.yuaiagent.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;

import java.util.function.Function;

public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {
    private static final Logger logger = LoggerFactory.getLogger(MyLoggerAdvisor.class);
    // 移除默认的JSON格式化，改为图片指定的文本提取逻辑
    private final int order;

    // 简化构造器：无需自定义toString函数，直接固定提取文本
    public MyLoggerAdvisor() {
        this(0);
    }

    public MyLoggerAdvisor(int order) {
        this.order = order;
    }

    // 核心：匹配图片中的 before 方法（原 logRequest）
    private ChatClientRequest before(ChatClientRequest request) {
        logger.info("AI 请求参数：{}", request.context());
        return request;
    }
    
    // 核心：匹配图片中的 observeAfter 方法（原 logResponse）
    private void observeAfter(ChatClientResponse advisedResponse) {
        // 只打印简化的响应信息，避免过长的 JSON 输出
        String responseText = advisedResponse.chatResponse()
                .getResult()
                .getOutput()
                .getText();
        logger.info("AI 响应结果：{}", responseText);
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        // 调用改造后的before方法
        ChatClientRequest processedRequest = before(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(processedRequest);
        // 调用改造后的observeAfter方法
        observeAfter(chatClientResponse);
        return chatClientResponse;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 调用改造后的before方法
        before(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        // 流式响应聚合后，调用observeAfter打印最终结果
        return new ChatClientMessageAggregator()
                .aggregateChatClientResponse(chatClientResponses, this::observeAfter);
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    // 修复原代码：返回实例的order，而非固定0
    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public String toString() {
        return MyLoggerAdvisor.class.getSimpleName();
    }

    // 简化Builder：移除无用的toString函数配置
    public static MyLoggerAdvisor.Builder builder() {
        return new MyLoggerAdvisor.Builder();
    }

    public static final class Builder {
        private int order = 0;

        private Builder() {}

        public MyLoggerAdvisor.Builder order(int order) {
            this.order = order;
            return this;
        }

        public MyLoggerAdvisor build() {
            return new MyLoggerAdvisor(this.order);
        }
    }
}