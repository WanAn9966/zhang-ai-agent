package com.yupi.yuaiagent.demo.invoke;

/**
 * Demo API key holder.
 *
 * <p>Keep real API keys outside source control and provide them through
 * environment variables or local configuration files.</p>
 */
public class TestApikey {

    private final String apiKey = System.getenv("DASHSCOPE_API_KEY");

    public String getApiKey() {
        return apiKey;
    }
}
