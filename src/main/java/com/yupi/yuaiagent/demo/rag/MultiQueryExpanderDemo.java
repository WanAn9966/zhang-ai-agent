package com.yupi.yuaiagent.demo.rag;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 扩展查询
 */

@Component
public class MultiQueryExpanderDemo {


   /* DocumentRetriever retriever = VectorStoreDocumentRetriever.builder()
            .vectorStore(vectorStore)
            .similarityThreshold(0.7)
            .topK(5)
            .filterExpression(new FilterExpressionBuilder()
                    .eq("type", "web")
                    .build())
            .build();
    List<Document> documents = retriever.retrieve(new Query("谁是程序员鱼皮"));*/

    private final ChatClient.Builder chatClientBuilder;;
    public  MultiQueryExpanderDemo(ChatModel dashscopeChatModel){
        this.chatClientBuilder = ChatClient.builder(dashscopeChatModel);
    }
    public List<Query> expand(String query){
        MultiQueryExpander queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(chatClientBuilder)
                .numberOfQueries(3)
                .build();
        List<Query> queries = queryExpander.expand(new Query("谁是鱼皮"));
        return queries;
    }
}
