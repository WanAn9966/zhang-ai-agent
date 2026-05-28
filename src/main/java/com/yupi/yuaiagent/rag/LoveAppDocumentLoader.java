/*
package com.yupi.yuaiagent.rag;

import org.springframework.core.io.Resource;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;

import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets; // 导入标准字符集
import org.springframework.ai.document.Document; // 正确的AI文档类
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

*/
/**
 * 文章加载器,一次读取多个
 *//*

@Component
@Slf4j
public class LoveAppDocumentLoader {

    // 1 一次读取多个文章
    private final ResourcePatternResolver resourcePatternResolver;
    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }
    //2 加载多篇markdown文档
      //将返回结果转化成Springai的形式
     public List<Document> loadMarkdowns(){
        List<Document> allDocument = new ArrayList<>();
         try {
             Resource [] resources =  resourcePatternResolver.getResources("classpath:document/*.md");
             for (Resource resource : resources) {
                 String fileName = resource.getFilename();
                 MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                         .withHorizontalRuleCreateDocument(true)
                         .withIncludeCodeBlock(false)
                         .withIncludeBlockquote(false)
                         //便于后续搜索

                         .withCharset(StandardCharsets.UTF_8) // 关键：指定UTF-8编码
                         .withAdditionalMetadata("filename", fileName)
                         .build();
                 MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                 List<Document> docs = reader.get();
                 log.info("文件 {} 加载了 {} 个文档", fileName, docs.size());
                 allDocument.addAll(docs);
               */
/* resource = resources[0]; // 取第一个资源
                 String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                 System.out.println("=== 原始文件内容 ===");
                 System.out.println(content);*//*



             }
         } catch (IOException e) {
             log.error("加载markdown失败",e);
         }

         return allDocument;

     }

}
*/
package com.yupi.yuaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 文章加载器（适配Spring AI 1.0.x，彻底解决UTF-8乱码）
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {

    private final ResourcePatternResolver resourcePatternResolver;

    // 构造器注入（Spring自动装配，无报错）
    public LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdowns() {
        List<Document> allDocument = new ArrayList<>();
        try {
            // 1. 获取 classpath 下所有 md 文件
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");
            log.info("✅ 找到 Markdown 文件总数：{}", resources.length);

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                if (fileName == null) {
                    log.warn("⚠️ 跳过名称为空的文件：{}", resource);
                    continue;
                }
                log.info("🔍 开始处理文件：{}", fileName);

                // 2. 从文件名提取 status 标签（文件名倒数第 6 到倒数第 4 个字符）
                String status = "";
                if (fileName.length() >= 6) {
                    status = fileName.substring(fileName.length() - 6, fileName.length() - 4);
                    log.info("📊 从文件 {} 提取到 status: {}", fileName, status);
                }

                // 3. 核心：手动以 UTF-8 读取文件内容（杜绝乱码）
                String fileContent = readFileByUtf8(resource);
                if (fileContent.trim().isEmpty()) {
                    log.warn("⚠️ 文件 {} 内容为空，跳过", fileName);
                    continue;
                }

                // 4. 按水平线---拆分（还原 withHorizontalRuleCreateDocument(true) 逻辑）
                List<String> contentFragments = splitByHorizontalRule(fileContent);

                // 5. 封装为 Spring AI 的 Document 对象（适配 1.0.x 版本，添加 status 元数据）
                for (int i = 0; i < contentFragments.size(); i++) {
                    String fragment = contentFragments.get(i).trim();
                    if (fragment.isEmpty()) {
                        continue;
                    }
                    // 附加文件名和 status 元数据
                    Document doc = new Document(fragment, Map.of(
                            "filename", fileName,
                            "status", status
                    ));
                    allDocument.add(doc);
                }
                log.info("📄 文件 {} 拆分出 {} 个有效文档片段", fileName, contentFragments.size());
            }
        } catch (IOException e) {
            log.error("❌ 加载 Markdown 文件失败", e);
        }
        log.info("🎯 最终加载的文档片段总数：{}", allDocument.size());
        return allDocument;
    }
// ... existing code ...


    /**
     * 手动以UTF-8编码读取文件内容（核心解决乱码）
     */
    private String readFileByUtf8(Resource resource) throws IOException {
        StringBuilder content = new StringBuilder();
        // 显式指定UTF-8，和你的文件编码匹配
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // 保留换行符，保证Markdown格式
            }
        }
        return content.toString();
    }

    /**
     * 按Markdown水平线---拆分内容（兼容各种写法：---、 --- 、----等）
     */
    private List<String> splitByHorizontalRule(String content) {
        // 正则匹配：至少3个连字符，前后可加空格/制表符
        String[] fragments = content.split("\\s*-{3,}\\s*");
        return Arrays.asList(fragments);
    }
}