/*
package com.yupi.yuaiagent.tool;

import org.springframework.ai.tool.ToolCallback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

*/
/**
 * 工具注册类（修正版）
 *//*

@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    // 关键1：让Spring注入所有工具实例（因为这些工具类都加了@Component）
    @Autowired
    private FileOperationTool fileOperationTool;
    @Autowired
    private WebScrapingTool webScrapingTool;
    @Autowired
    private ResourceDownloadTool resourceDownloadTool;
    @Autowired
    private TerminalOperationTool terminalOperationTool;
    @Autowired
    private PDFGenerationTool pdfGenerationTool;

    // 关键2：WebSearchTool需要传入searchApiKey，手动创建时要确保参数正确
    @Bean
    public WebSearchTool webSearchTool() {
        return new WebSearchTool(searchApiKey);
    }

    // 关键3：收集所有Spring管理的工具实例，封装成ToolCallback[]
    @Bean
    public ToolCallback[] allTools() {
        return ToolCallbacks.from(
                fileOperationTool,       // Spring注入的实例（带@Tool注解）
                webSearchTool(),         // 由@Bean管理的实例
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool
        );
    }
}*/

package com.yupi.yuaiagent.tool;

import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 集中的工具注册类
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        WebScrapingTool webScrapingTool = new WebScrapingTool();
       // ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        ResourceDownloadTool downloadResource = new ResourceDownloadTool();
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        TerminateTool terminateTool = new TerminateTool();

        //ImageSearchTool imageSearchTool = new ImageSearchTool();
        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                //resourceDownloadTool,
                downloadResource,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool

        );
    }
}

