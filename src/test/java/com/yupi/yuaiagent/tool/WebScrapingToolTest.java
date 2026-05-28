package com.yupi.yuaiagent.tool;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {

        WebScrapingTool tool = new WebScrapingTool();
        String url = "https://www.doubao.com";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}