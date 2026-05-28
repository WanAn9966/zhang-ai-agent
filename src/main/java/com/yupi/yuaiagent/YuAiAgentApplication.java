package com.yupi.yuaiagent;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(excludeName = {
        "org.springframework.ai.vectorstore.pgvector.PgVectorStoreAutoConfiguration"
})
public class YuAiAgentApplication {

    public static void main(String[] args) {
        // 强制设置 JVM 默认编码为 UTF-8，解决 JDK21 在 Windows 下的中文乱码问题
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        
        SpringApplication.run(YuAiAgentApplication.class, args);
    }

}
