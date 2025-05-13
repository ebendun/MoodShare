MoodShare：社交心情分享平台
项目介绍
MoodShare 是一个简单而强大的心情分享社交平台，允许用户分享他们的心情、情绪和想法。用户可以使用表情符号表达情绪，添加标签，对他人的心情进行点赞和评论，从而建立一个积极的社区氛围。


主要功能
用户管理：注册、登录、个人信息更新
心情分享：创建、查看、更新和删除心情
社交互动：
点赞/取消点赞
添加评论
查看评论列表
删除评论
权限控制：基于角色的权限管理（普通用户和管理员）
技术栈
后端：Spring Boot
数据库：JPA/Hibernate
安全：Spring Security 与 JWT 认证
构建工具：Maven
API 端点
用户 API
POST /api/auth/register：注册新用户
POST /api/auth/login：用户登录
GET /api/users/profile：获取用户资料
PUT /api/users/profile：更新用户资料
心情 API
POST /api/moods：创建心情
GET /api/moods：获取所有心情
GET /api/moods/{id}：获取单个心情详情
PUT /api/moods/{id}：更新心情
DELETE /api/moods/{id}：删除心情
POST /api/moods/{id}/like：点赞/取消点赞
评论 API
POST /api/moods/{moodId}/comments：添加评论
GET /api/moods/{moodId}/comments：获取心情的所有评论
DELETE /api/moods/{moodId}/comments/{commentId}：删除评论
数据模型
用户 (User)
id：用户唯一标识
username：用户名
password：密码（加密存储）
email：电子邮件
admin：管理员标识
createdAt：创建时间
心情 (Mood)
id：心情唯一标识
content：内容
emoji：表情符号
tags：标签列表
user：发布用户
likedBy：点赞用户集合
comments：评论集合
createdAt：创建时间
评论 (Comment)
id：评论唯一标识
content：内容
user：评论用户
mood：所属心情
createdAt：创建时间
安装与运行
先决条件
Java 11+
Maven 3.6+
MySQL/PostgreSQL (或其他 Spring Data JPA 支持的数据库)
步骤
克隆仓库


git clone https://github.com/ebendun/moodshare.git
cd moodshare
配置数据库


在 src/main/resources/application.properties 中修改数据库配置
构建项目


mvn clean install
运行应用


mvn spring-boot:run
访问 API


API 默认运行在 http://localhost:8080
开发注意事项
所有 API 请求（除了注册和登录）都需要在头部包含有效的 JWT 令牌：


Authorization: Bearer {token}
评论和点赞操作中注意避免并发修改异常，尤其在处理集合关系时


权限控制：


用户只能删除自己的心情和评论
管理员可以删除任何心情和评论
心情发布者可以删除自己心情下的任何评论
贡献指南
欢迎提交 Pull Requests！请确保你的代码符合项目的编码风格和测试要求。
