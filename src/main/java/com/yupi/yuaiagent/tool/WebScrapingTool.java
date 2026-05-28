package com.yupi.yuaiagent.tool;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 抓取网页
 */

@Component
public class WebScrapingTool {

    @Tool(description= "Scrape the content of a web page and extract text content and image URLs")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document doc = Jsoup.connect(url).get();

            // 提取页面文本内容
            String textContent = doc.body().text();

            // 提取所有图片链接
            Elements images = doc.select("img[src]");
            String imageUrls = images.stream()
                    .map(img -> img.absUrl("src"))
                    .filter(src -> !src.isEmpty())
                    .collect(Collectors.joining(", "));

            StringBuilder result = new StringBuilder();
            result.append("=== 页面文本内容 ===\n");
            result.append(textContent.substring(0, Math.min(textContent.length(), 2000))); // 限制长度
            result.append("\n\n=== 页面中的图片链接 ===\n");
            result.append(imageUrls.isEmpty() ? "未找到图片" : imageUrls);

            return result.toString();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
