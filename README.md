# My-Agent - 基于Java和Spring AI的智能体开发框架

一个基于Java、Spring AI的轻量级智能体开发框架，提供完整的AI智能体管理和工作流执行能力。

## 🚀 项目概述

My-Agent是一个现代化的AI智能体开发平台，集成了多种AI模型和工具，支持智能体配置、工作流编排、知识库检索等功能。项目采用前后端分离架构，提供完整的智能体生命周期管理。

## ✨ 核心特性

### 智能体管理
- **多模型支持**: 集成OpenAI、Ollama等多种AI模型
- **智能体配置**: 支持系统提示词、工具配置、顾问配置
- **对话记忆**: 支持会话级别的对话记忆管理
- **流式对话**: 提供实时流式对话体验

### 工作流引擎
- **可视化编排**: 基于Flowgram.ai的工作流可视化编辑器
- **节点类型**: 支持HTTP调用、批量处理、循环控制等多种节点
- **DSL支持**: 基于YAML的工作流定义语言
- **执行监控**: 实时工作流执行状态监控

### 知识库检索 (RAG)
- **向量存储**: 基于PgVector的向量数据库
- **文档检索**: 支持语义检索和相似度匹配
- **文档处理**: 自动文档分块和向量化
- **多知识库**: 支持多个独立知识库管理

### 工具集成
- **MCP工具**: 支持Model Context Protocol工具集成
- **自定义工具**: 可扩展的自定义工具开发框架
- **工具编排**: 智能体与工具的协同工作

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.x、Spring AI
- **数据库**: MySQL 8.0、PostgreSQL (PgVector)
- **ORM**: MyBatis Plus 3.5.5
- **缓存**: Redis 6.2
- **安全**: JWT认证
- **构建工具**: Maven
- **Java版本**: JDK 21

### 前端技术
- **框架**: React 18、TypeScript
- **UI组件**: Semi UI (@douyinfe/semi-ui)
- **路由**: React Router 6
- **构建工具**: Rsbuild 1.2.16
- **工作流编辑器**: Flowgram.ai组件

### 部署与运维
- **容器化**: Docker、Docker Compose
- **版本控制**: Git

## 📁 项目结构

My-Agent项目采用清晰的前后端分离架构，整体由两大部分组成：后端服务（agent-station）和前端应用（my-agent-front）。后端服务采用模块化设计，遵循分层架构原则，确保代码的可维护性和扩展性。

### 整体结构
```text
my-agent/
├── agent-station/            # 后端服务目录
│   ├── agent-station-api/    # API接口模块
│   ├── agent-station-app/    # 应用主模块
│   ├── agent-station-domain/ # 领域模型模块
│   ├── agent-station-infrastructure/ # 基础设施模块
│   ├── agent-station-trigger/ # 触发器模块
│   ├── docs/                 # 文档目录
│   ├── Dockerfile            # 后端Docker构建文件
│   ├── docker-compose.yml    # 后端服务编排文件
│   └── pom.xml               # Maven项目配置
├── my-agent-front/           # 前端应用目录
│   ├── src/                  # 前端源码
│   ├── Dockerfile            # 前端Docker构建文件
│   └── docker-compose.yml    # 前端服务编排文件
├── .gitignore                # Git忽略配置
├── LICENSE-APACHE.txt        # Apache许可证
└── README.md                 # 项目说明文档
```

### 后端模块职责
- **agent-station-domain**: 核心领域模型和业务逻辑，包含实体、仓储接口、领域服务等
- **agent-station-app**: 应用服务层，协调领域对象完成业务流程，包含应用服务和控制器
- **agent-station-infrastructure**: 基础设施层，实现领域层定义的接口，包含数据访问、外部系统集成等
- **agent-station-api**: API接口定义，包含DTO、VO等数据传输对象
- **agent-station-trigger**: 触发器模块，处理定时任务、事件触发等功能

### 技术架构
项目采用经典的三层架构设计：
- **表示层**: 处理HTTP请求，返回响应结果，由Spring MVC控制器实现
- **业务层**: 实现核心业务逻辑，由领域服务和应用服务组成
- **数据访问层**: 负责与数据库交互，由MyBatis Mapper实现

## 🚀 快速开始

### 环境准备
- **JDK**: 21或更高版本
- **Maven**: 3.8.0或更高版本
- **MySQL**: 8.0或更高版本
- **PostgreSQL**: 13或更高版本（带PgVector扩展）
- **Redis**: 6.2或更高版本
- **Node.js**: 18.0或更高版本
- **Docker** (可选): 用于容器化部署

### 后端启动步骤

#### 本地开发启动
1. **克隆项目**
```bash
git clone git@github.com:szhuima/my-agent.git
cd my-agent
```

2. **配置数据库**
   - 创建MySQL数据库: `agent-station`
   - 创建PostgreSQL数据库: `springai` (并启用PgVector扩展)
   - 修改`agent-station-app/src/main/resources/application.yml`中的数据库连接信息

3. **构建并运行后端服务**
```bash
cd agent-station
mvn clean package -DskipTests
java -jar agent-station-app/target/agent-station-app-1.0-SNAPSHOT.jar
```

#### Docker容器化启动
项目提供了完整的Docker自动化部署脚本，只需执行一条命令即可完成从代码构建到容器启动的全流程：

1. **使用pipeline.sh一键部署**
```bash
cd agent-station
sh pipeline.sh
```

pipeline.sh脚本将自动执行以下操作：
- 使用Maven构建后端项目
- 调用build.sh脚本构建Docker镜像
- 使用docker-compose.yml启动所有必要的容器服务

### 前端启动步骤

#### 本地开发启动
1. **安装依赖**
```bash
cd ../my-agent-front
npm install
```

2. **启动前端开发服务器**
```bash
npm run dev
```

3. **访问应用**
   打开浏览器，访问 `http://localhost:3000`

#### Docker容器化启动
1. **构建并启动前端Docker容器**
```bash
cd ../my-agent-front
sh build.sh
```

## ⚙️ 配置说明

### 后端主要配置文件
- **application.yml**: 主配置文件，包含数据库连接、服务端口等配置
- **jwt.properties**: JWT认证配置
- **docker-compose-environment.yml**: 环境服务编排配置

### 关键配置项

#### 数据库配置
```yaml
spring:
  datasource:
    primary:
      url: jdbc:mysql://localhost:3306/agent-station?useSSL=false&serverTimezone=UTC
      username: root
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
    vector:
      url: jdbc:postgresql://localhost:5432/springai
      username: postgres
      password: password
      driver-class-name: org.postgresql.Driver
```

#### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 16379
      database: 0
```

#### JWT配置
```properties
jwt.secret=your-secret-key
jwt.expire=86400000 # 24小时，单位毫秒
```

## 🔧 功能模块详情

### 1. 智能体管理模块
提供完整的智能体生命周期管理，包括智能体创建、配置、部署和监控。

**主要功能**: 
- 智能体创建与配置
- 系统提示词配置
- 智能体对话调试
- 对话历史配置
- 接入MCP服务

### 2. 工作流引擎
基于Flowgram.ai的可视化工作流引擎，支持复杂业务流程的编排和执行。

**主要功能**: 
- 可视化工作流编辑器
- 工作流版本管理
- 工作流执行监控
- 任务调度与触发
- 异常处理与重试机制

### 3. 知识库检索 (RAG)
基于PostgreSQL和PgVector的向量知识库，支持高效的语义检索。

**主要功能**: 
- 多格式文档导入
- 自动文档分块
- 向量嵌入生成
- 语义相似度检索

### 4. 工具集成框架
灵活的工具集成机制，支持MCP协议和自定义工具开发。

**主要功能**: 
- 工具注册与管理
- 工具参数自动填充
- 工具结果处理

## 🔒 安全特性

- **JWT认证**: 基于JSON Web Token的用户认证机制
- **细粒度权限控制**: 基于角色的访问控制（RBAC）
- **接口安全防护**: XSS、CSRF防护
- **数据加密**: 敏感数据加密存储
- **请求限流**: 防止恶意请求攻击

## 📊 监控与日志

- **SLF4J日志框架**: 统一的日志管理
- **Lombok @Slf4j**: 简化日志记录代码
- **MyBatis日志**: SQL执行日志配置
- **Spring Boot Actuator**: 应用监控端点

## 🐳 Docker部署

项目支持完整的Docker容器化部署，包括所有依赖服务。项目提供了两种Docker部署方式：全自动部署（使用pipeline.sh）和手动部署。

### 全自动部署（推荐）
项目提供了完整的自动化部署脚本pipeline.sh，只需执行一条命令即可完成从代码构建到容器启动的全流程：

```bash
cd agent-station
sh pipeline.sh
```

### pipeline.sh脚本详情
pipeline.sh脚本实现了以下功能：
1. 使用Maven构建Java项目（跳过测试）
2. 调用build.sh构建Docker镜像
3. 使用docker-compose.yml启动所有服务容器

```bash
# pipeline.sh脚本内容摘要
export VERSION=1.0-SNAPSHOT
mvn clean package -DskipTests
echo "Maven 打包完成"

sh ./build.sh
echo "Docker 镜像构建完成"

docker compose -f ./docker-compose.yml up -d
echo "Docker 容器已创建成功并启动"
```

### build.sh脚本详情
build.sh脚本负责构建Docker镜像：

```bash
# build.sh脚本内容摘要
docker build -t agent-station-app:1.0-SNAPSHOT -f ./Dockerfile .
```

### 手动Docker部署

1. **启动所有服务**
```bash
cd agent-station/docs/dev-ops
docker-compose -f docker-compose-environment.yml up -d
```

2. **构建并运行应用服务**
```bash
# 构建并启动后端服务
cd agent-station
sh pipeline.sh

# 构建并启动前端服务
cd my-agent-front
sh pipeline.sh

```

## 🛠️ 开发指南

### 代码规范
- 遵循Java编码规范
- 使用Lombok简化代码
- 接口与实现分离
- 合理使用设计模式

### 开发流程
1. Fork项目仓库
2. 创建特性分支
3. 提交代码
4. 创建Pull Request
5. 代码审查
6. 合并到主分支

## 📝 版本信息

- **当前版本**: 1.0.0-SNAPSHOT
- **主要技术栈版本**: 
  - Spring Boot: 3.4.3
  - Spring AI: 1.0.2
  - MyBatis Plus: 3.5.5
  - JDK: 21
  - React: 18
  - TypeScript: 5.x

## 🤝 贡献指南

我们欢迎社区贡献，如果你有任何想法或建议，请提交Issue或Pull Request。

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 📄 许可证

本项目采用Apache License 2.0许可证。

## 📧 联系我们

如有任何问题，请随时联系项目维护者。作者邮箱: szhuima@gmail.com