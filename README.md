# MoodShare - 社交心情分享平台

<div align="center">
  
  ![MoodShare Logo](https://img.shields.io/badge/MoodShare-心情分享平台-blue?style=for-the-badge)
  
  ![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen?style=flat-square&logo=springboot)
  ![Vue.js](https://img.shields.io/badge/Vue.js-3.2.13-green?style=flat-square&logo=vue.js)
  ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=flat-square&logo=mysql)
  ![Redis](https://img.shields.io/badge/Redis-Latest-red?style=flat-square&logo=redis)

</div>

## 📖 项目简介

MoodShare 是一个现代化的社交心情分享平台，让用户可以轻松记录和分享自己的心情、情感与生活感悟。通过表情符号、标签、地理位置等多元化的表达方式，用户可以与朋友们建立更深层的情感连接，创建一个温馨友好的社交圈子。

### ✨ 核心特色

- 🎭 **多元化心情表达** - 支持表情符号、文字、图片、标签等多种表达方式
- 🌍 **地理位置分享** - 记录心情发生的地点，发现身边的精彩
- 👥 **智能隐私控制** - 公开、好友可见、私密三级隐私设置
- 💝 **社交互动** - 点赞、评论、好友系统，增进彼此了解
- 🔔 **实时通知** - 第一时间获知好友的互动反馈
- 📱 **响应式设计** - 完美适配各种设备，随时随地分享心情

## 🚀 主要功能

### 用户管理
- ✅ 用户注册与登录
- ✅ 个人资料管理
- ✅ 头像上传
- ✅ 用户搜索

### 心情分享
- ✅ 创建心情动态（文字、表情、图片）
- ✅ 添加标签和地理位置
- ✅ 三级隐私设置（公开/好友可见/私密）
- ✅ 心情编辑与删除
- ✅ 按位置搜索附近心情

### 社交功能
- ✅ 好友系统（添加/删除好友）
- ✅ 好友请求管理
- ✅ 心情点赞与取消点赞
- ✅ 评论系统（添加/删除评论）
- ✅ 分页评论加载

### 动态展示
- ✅ 个人主页心情展示
- ✅ 好友动态时间线
- ✅ 公开心情广场
- ✅ 智能推荐信息流

### 通知系统
- ✅ 实时通知推送
- ✅ 点赞通知
- ✅ 评论通知
- ✅ 好友请求通知

## 🛠 技术架构

### 后端技术栈
- **框架**: Spring Boot 3.4.4
- **语言**: Java 21
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0 + JPA/Hibernate
- **缓存**: Redis
- **构建工具**: Maven
- **API文档**: RESTful API

### 前端技术栈
- **框架**: Vue.js 3.2.13
- **状态管理**: Vuex 4.1.0
- **路由**: Vue Router 4.5.1
- **UI框架**: Bootstrap 5.3.6
- **HTTP客户端**: Axios 1.9.0
- **构建工具**: Vue CLI

### 核心依赖
```xml
<!-- 后端核心依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
</dependency>
```

```json
// 前端核心依赖
{
  "vue": "^3.2.13",
  "vue-router": "^4.5.1",
  "vuex": "^4.1.0",
  "axios": "^1.9.0",
  "bootstrap": "^5.3.6"
}
```

## 📋 API 接口文档

### 认证接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/auth/register` | 用户注册 |
| POST | `/auth/login` | 用户登录 |
| GET | `/auth/me` | 获取当前用户信息 |
| PUT | `/auth/profile` | 更新用户资料 |
| GET | `/auth/status` | 检查登录状态 |

### 心情接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/moods` | 获取所有心情 |
| POST | `/moods` | 创建心情 |
| GET | `/moods/{id}` | 获取单个心情 |
| PUT | `/moods/{id}` | 更新心情 |
| DELETE | `/moods/{id}` | 删除心情 |
| POST | `/moods/{id}/like` | 点赞/取消点赞 |
| GET | `/moods/feed` | 获取推荐动态 |
| GET | `/moods/friends` | 获取好友心情 |
| GET | `/moods/user/{userId}` | 获取用户心情 |
| GET | `/moods/nearby` | 获取附近心情 |

### 评论接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/moods/{moodId}/comments` | 获取心情评论 |
| POST | `/moods/{moodId}/comments` | 添加评论 |
| DELETE | `/moods/{moodId}/comments/{commentId}` | 删除评论 |

### 好友接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/friends` | 获取好友列表 |
| POST | `/friends/request/{userId}` | 发送好友请求 |
| GET | `/friends/requests` | 获取好友请求 |
| PUT | `/friends/requests/{requestId}/accept` | 接受好友请求 |
| DELETE | `/friends/{friendId}` | 删除好友 |

### 通知接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/notifications` | 获取所有通知 |
| GET | `/notifications/unread` | 获取未读通知 |
| GET | `/notifications/unread/count` | 获取未读通知数量 |
| PUT | `/notifications/{id}/read` | 标记通知为已读 |

### 文件接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/files/upload/avatar` | 上传头像 |
| POST | `/files/upload/mood` | 上传心情图片 |

## 🗄 数据模型

### 用户模型 (User)
```java
public class User {
    private Long id;              // 用户ID
    private String username;      // 用户名
    private String email;         // 邮箱
    private String password;      // 密码（加密）
    private String profilePicture; // 头像URL
    private boolean admin;        // 管理员标识
    private Set<User> friends;    // 好友列表
    private LocalDateTime createdAt; // 创建时间
}
```

### 心情模型 (Mood)
```java
public class Mood {
    private Long id;                    // 心情ID
    private String content;             // 内容
    private String emoji;               // 表情符号
    private Set<String> tags;           // 标签
    private PrivacyLevel privacyLevel;  // 隐私级别
    private String location;            // 位置描述
    private Double latitude;            // 纬度
    private Double longitude;           // 经度
    private MoodType moodType;          // 心情类型
    private String weather;             // 天气
    private User user;                  // 发布用户
    private Set<User> likedBy;          // 点赞用户
    private Set<Comment> comments;      // 评论列表
    private LocalDateTime createdAt;    // 创建时间
}
```

### 评论模型 (Comment)
```java
public class Comment {
    private Long id;                 // 评论ID
    private String content;          // 评论内容
    private User user;               // 评论用户
    private Mood mood;               // 所属心情
    private LocalDateTime createdAt; // 创建时间
}
```

### 通知模型 (Notification)
```java
public class Notification {
    private Long id;                     // 通知ID
    private User recipient;              // 接收用户
    private String message;              // 通知消息
    private NotificationType type;       // 通知类型
    private Long relatedEntityId;        // 相关实体ID
    private boolean read;                // 是否已读
    private LocalDateTime createdAt;     // 创建时间
}
```

## 🚀 快速开始

### 环境要求
- Java 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Node.js 16.0+
- npm 8.0+

### 后端启动

1. **克隆项目**
```bash
git clone https://github.com/your-username/moodshare.git
cd moodshare
```

2. **配置数据库**
```bash
# 在MySQL中创建数据库
mysql -u root -p
CREATE DATABASE moodshare CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **修改配置文件**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/moodshare
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT配置
jwt.expiration=86400000

# Redis配置（如需要）
spring.data.redis.host=localhost
spring.data.redis.port=6379

# 文件上传路径
file.upload.dir=./uploads
```

4. **构建并运行**
```bash
# 安装依赖并构建项目
mvn clean install

# 启动应用
mvn spring-boot:run
```

### 前端启动

1. **进入前端目录**
```bash
cd frontend
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run serve
```

4. **构建生产版本**
```bash
npm run build
```

### 访问应用

- **前端**: http://localhost:8080
- **后端API**: http://localhost:8080/api
- **API文档**: http://localhost:8080/swagger-ui.html

## 🔧 开发指南

### 项目结构
```
MoodShare/
├── src/main/java/org/example/moodshare/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器
│   ├── dto/            # 数据传输对象
│   ├── filter/         # 过滤器
│   ├── model/          # 实体模型
│   ├── repository/     # 数据访问层
│   ├── service/        # 业务逻辑层
│   └── util/           # 工具类
├── src/main/resources/
│   └── application.properties
├── frontend/
│   ├── src/
│   │   ├── api/        # API调用
│   │   ├── components/ # Vue组件
│   │   ├── router/     # 路由配置
│   │   ├── store/      # Vuex状态管理
│   │   └── views/      # 页面视图
│   └── public/
└── uploads/            # 文件上传目录
```

### 认证机制
- 使用JWT进行用户认证
- Token有效期：24小时
- 请求头格式：`Authorization: Bearer {token}`

### 权限控制
- **公开权限**: 注册、登录、查看公开心情
- **用户权限**: 创建心情、评论、点赞、管理好友
- **作者权限**: 编辑/删除自己的心情和评论
- **管理员权限**: 管理所有内容

### 隐私设置
- **PUBLIC**: 所有人可见
- **FRIENDS**: 仅好友可见  
- **PRIVATE**: 仅自己可见

## 🧪 测试

### 运行单元测试
```bash
# 后端测试
mvn test

# 前端测试
cd frontend
npm run test:unit
```

### 测试覆盖率
```bash
mvn test jacoco:report
```

## 📦 部署

### Docker部署

1. **构建Docker镜像**
```bash
# 后端
docker build -t moodshare-backend .

# 前端
cd frontend
docker build -t moodshare-frontend .
```

2. **使用Docker Compose**
```bash
docker-compose up -d
```

### 生产环境配置
```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
```

## 🛡️ 安全说明

- 密码使用BCrypt加密存储
- JWT Token过期时间配置合理
- SQL注入防护（JPA参数化查询）
- XSS防护（前端输入验证）
- CORS跨域配置
- 文件上传类型限制

## 🤝 贡献指南

1. Fork项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

### 代码规范
- 遵循Google Java Style Guide
- 使用ESLint和Prettier格式化前端代码
- 编写单元测试
- 添加必要的注释

## 📄 License

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 贡献者

感谢所有为这个项目做出贡献的开发者！

## 📞 联系我们

- 项目主页: [GitHub Repository](https://github.com/your-username/moodshare)
- 问题反馈: [Issues](https://github.com/your-username/moodshare/issues)
- 邮箱: your-email@example.com

## 🔮 未来规划

- [ ] 移动端APP开发
- [ ] 实时聊天功能
- [ ] 心情数据分析和可视化
- [ ] AI情感分析
- [ ] 多语言支持
- [ ] 主题定制
- [ ] 心情导出功能
- [ ] 社群/群组功能

---

<div align="center">
  <p>❤️ 用心记录，真情分享 ❤️</p>
  <p>让每一份心情都被温柔以待</p>
</div>
