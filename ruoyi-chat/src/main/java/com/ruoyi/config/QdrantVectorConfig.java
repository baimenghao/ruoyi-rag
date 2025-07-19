package com.ruoyi.config;

import com.ruoyi.enums.SystemConstant;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.qdrant.autoconfigure.QdrantVectorStoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutionException;

@Configuration
public class QdrantVectorConfig {

    @Autowired
    private QdrantVectorStoreProperties properties;

    @Bean
    public QdrantVectorStore openAiQdrantVectorStore(
            QdrantClient qdrantClient, OpenAiEmbeddingModel openAiEmbeddingModel
    ) throws ExecutionException, InterruptedException {
        String collectionName = SystemConstant.OPENAI_QDRANT;

        try {
            // 如果不存在，则创建集合
            qdrantClient.createCollectionAsync(
                    collectionName,
                    Collections.VectorParams.newBuilder()
                            .setDistance(Collections.Distance.Cosine)
                            .setSize(1536)
                            .build()
            ).get();
        }catch (Exception e){

        }

        return QdrantVectorStore.builder(qdrantClient, openAiEmbeddingModel)
                .collectionName(collectionName)
                .initializeSchema(properties.isInitializeSchema())
                .build();
    }

    @Bean
    public QdrantVectorStore ollamaQdrantVectorStore(
            QdrantClient qdrantClient, OllamaEmbeddingModel ollamaEmbeddingModel
    ) throws ExecutionException, InterruptedException {

        return QdrantVectorStore.builder(qdrantClient, ollamaEmbeddingModel)
                .collectionName(SystemConstant.OLLAMA_QDRANT)
                .initializeSchema(properties.isInitializeSchema())
                .build();
    }
}
