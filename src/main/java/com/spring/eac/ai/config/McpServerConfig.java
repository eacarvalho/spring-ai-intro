package com.spring.eac.ai.config;

import com.spring.eac.ai.tool.CustomerScoreTool;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.StaticToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class McpServerConfig {

    @Bean
    @Qualifier("customerScoreAndAllTools")
    ToolCallbackProvider customerScoreAndAllTools(SyncMcpToolCallbackProvider toolCallbackProvider,
                                            CustomerScoreTool customerScoreTool) {

        // Get MCP tool callbacks
        ToolCallback[] mcpCallbacks = toolCallbackProvider.getToolCallbacks();

        // Create method-based tool callbacks
        ToolCallbackProvider methodProvider = MethodToolCallbackProvider
                .builder()
                .toolObjects(customerScoreTool)
                .build();

        // Then combine them in your service where you use them
        List<ToolCallback> allCallbacks = new ArrayList<>();
        allCallbacks.addAll(Arrays.asList(mcpCallbacks));
        allCallbacks.addAll(Arrays.asList(methodProvider.getToolCallbacks()));

        return new StaticToolCallbackProvider(allCallbacks);
    }

    @Bean
    @Qualifier("customerScoreTools")
    ToolCallbackProvider customerScoreTools(CustomerScoreTool customerScoreTool) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(customerScoreTool)
                .build();
    }
}