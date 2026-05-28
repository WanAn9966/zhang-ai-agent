# Yu AI Agent

Yu AI Agent 是一个基于 Spring AI 的 AI 智能体应用平台，包含后端 Agent 服务、Vue 前端页面，以及一个可独立运行的图片搜索 MCP Server。项目内置「AI 恋爱大师」对话应用和「YuManus 超级智能体」，支持流式对话、工具调用、文件会话记忆、RAG 知识库问答和 MCP 工具扩展。

## 功能特性

- AI 恋爱大师：面向情感、恋爱、婚姻等场景的智能问答应用。
- YuManus 超级智能体：基于 ReAct 思路自主规划步骤，并按需调用工具完成复杂任务。
- SSE 流式响应：后端通过 Server-Sent Events 持续返回模型输出，前端实时展示。
- 工具调用能力：支持网页搜索、网页抓取、文件读写、资源下载、终端命令执行、PDF 生成和任务终止。
- 会话记忆：使用本地文件持久化聊天上下文，支持多轮对话。
- RAG 知识库：加载 `src/main/resources/document` 下的 Markdown 文档，构建恋爱问答知识库。
- MCP 扩展：集成本地 MCP Client 配置，并提供 `yu-image-search-mcp` 图片搜索服务。
- 前后端分离：后端基于 Spring Boot，前端基于 Vue 3 + Vite。

## 技术栈

后端：

- Java 21
- Spring Boot 3.4.4
- Spring AI 1.0.0
- Spring AI Alibaba DashScope
- LangChain4J DashScope
- PGVector / SimpleVectorStore
- Knife4j / OpenAPI
- Hutool、Jsoup、iText、Kryo

前端：

- Vue 3
- Vue Router
- Axios
- Vite
- SSE / EventSource

MCP Server：

- Java 17
- Spring Boot 3.5.11
- Spring AI MCP Server
- Pexels API

## 项目结构

```text
yu-ai-agent
├── src/main/java/com/yupi/yuaiagent
│   ├── agent          # BaseAgent、ReActAgent、ToolCallAgent、YuManus
│   ├── app            # LoveApp 恋爱大师应用
│   ├── controller     # AI 对话接口
│   ├── rag            # 文档加载、向量库、查询改写、RAG Advisor
│   ├── tool           # 智能体可调用工具
│   └── chatmemory     # 文件持久化会话记忆
├── src/main/resources
│   ├── document       # 恋爱知识库 Markdown 文档
│   ├── application.yml
│   └── mcp-servers.example.json
├── yu-ai-agent-frontend   # Vue 3 前端
└── yu-image-search-mcp    # 图片搜索 MCP Server
```

## 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/your-username/yu-ai-agent.git
cd yu-ai-agent
```

### 2. 配置后端

在 `src/main/resources` 下创建 `application-local.yml`，填写自己的密钥：

```yaml
spring:
  ai:
    dashscope:
      api-key: your_dashscope_api_key

search-api:
  api-key: your_searchapi_key
```

如需启用 MCP Client，请根据示例复制配置：

```bash
cp src/main/resources/mcp-servers.example.json src/main/resources/mcp-servers.json
```

然后按需修改 `mcp-servers.json` 中的命令、Jar 路径和环境变量。

### 3. 启动后端

Windows 可以直接运行：

```bash
start.bat
```

也可以使用 Maven Wrapper：

```bash
./mvnw spring-boot:run
```

后端默认地址：

```text
http://localhost:8123/api
```

接口文档地址：

```text
http://localhost:8123/api/doc.html
http://localhost:8123/api/swagger-ui.html
```

### 4. 启动前端

```bash
cd yu-ai-agent-frontend
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:3000
```

开发环境下，Vite 会将 `/api` 请求代理到 `http://localhost:8123`。

### 5. 启动图片搜索 MCP Server

图片搜索 MCP Server 使用 Pexels API，需要先配置环境变量：

```bash
set PEXELS_API_KEY=your_pexels_api_key
```

构建子项目：

```bash
cd yu-image-search-mcp
./mvnw clean package -DskipTests
```

默认使用 `stdio` 模式，适合被 MCP Client 拉起：

```bash
java -jar target/yu-image-search-mcp-0.0.1-SNAPSHOT.jar
```

如需通过 SSE/Web 模式运行，可以切换 Spring Profile：

```bash
java -jar target/yu-image-search-mcp-0.0.1-SNAPSHOT.jar --spring.profiles.active=sse
```

## 核心接口

后端统一前缀为 `/api`。

| 接口 | 方法 | 说明 |
| --- | --- | --- |
| `/api/ai/love_app/chat/sync` | GET | 恋爱大师同步对话 |
| `/api/ai/love_app/chat/sse` | GET | 恋爱大师 SSE 流式对话 |
| `/api/ai/love_app/chat/server_sent_event` | GET | 基于 `ServerSentEvent` 的流式对话 |
| `/api/ai/love_app/chat/sse_emitter` | GET | 基于 `SseEmitter` 的流式对话 |
| `/api/ai/manus/chat` | GET | YuManus 超级智能体流式对话 |

示例：

```bash
curl "http://localhost:8123/api/ai/love_app/chat/sync?message=如何经营异地恋&chatId=demo"
```

## 工具能力

YuManus 会根据任务自动选择工具，当前注册的工具包括：

- `WebSearchTool`：通过 SearchApi 调用百度搜索。
- `WebScrapingTool`：抓取网页正文和图片链接。
- `FileOperationTool`：读写本地文件，默认保存到 `tmp/file`。
- `ResourceDownloadTool`：下载网络资源，默认保存到 `tmp/download`。
- `TerminalOperationTool`：执行本地终端命令。
- `PDFGenerationTool`：根据文本内容生成 PDF，默认保存到 `tmp/pdf`。
- `TerminateTool`：在任务完成后终止智能体循环。

注意：终端命令、文件读写、资源下载等工具具有本地执行能力，建议仅在可信环境中运行，不要直接暴露到公网。

## RAG 知识库

项目会读取 `src/main/resources/document/*.md` 中的 Markdown 文档，并使用 Spring AI 向量能力构建知识库。当前内置资料包括：

- 恋爱常见问题和回答 - 单身篇
- 恋爱常见问题和回答 - 恋爱篇
- 恋爱常见问题和回答 - 已婚篇

你可以继续向该目录添加 Markdown 文档，扩展恋爱大师的知识范围。

## 常用命令

后端：

```bash
./mvnw spring-boot:run
./mvnw test
./mvnw clean package -DskipTests
```

前端：

```bash
cd yu-ai-agent-frontend
npm run dev
npm run build
npm run preview
```

MCP Server：

```bash
cd yu-image-search-mcp
./mvnw clean package -DskipTests
java -jar target/yu-image-search-mcp-0.0.1-SNAPSHOT.jar
```

## 配置说明

请不要将真实密钥提交到 GitHub。建议把本地配置放在 `application-local.yml`、环境变量或部署平台的密钥管理中。

| 配置项 | 说明 |
| --- | --- |
| `spring.ai.dashscope.api-key` | 阿里云百炼 / DashScope API Key |
| `search-api.api-key` | SearchApi API Key，用于网页搜索 |
| `PEXELS_API_KEY` | Pexels API Key，用于图片搜索 MCP |
| `AMAP_MAPS_API_KEY` | 高德地图 MCP API Key，可选 |

## 部署说明

后端可以打包为 Jar：

```bash
./mvnw clean package -DskipTests
java -jar target/yu-ai-agent-0.0.1-SNAPSHOT.jar
```

前端可以构建静态资源：

```bash
cd yu-ai-agent-frontend
npm run build
```

前端目录内提供了 `Dockerfile` 和 `nginx.conf`，可用于将构建产物部署到 Nginx。部署时请根据实际后端地址调整 Nginx 的 `/api/` 反向代理配置。

## 许可证

本项目暂未声明开源许可证。发布到 GitHub 前，建议根据你的使用意图补充 `LICENSE` 文件。
