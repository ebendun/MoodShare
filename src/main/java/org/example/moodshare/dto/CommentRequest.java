// 评论请求和响应
package org.example.moodshare.dto;

import lombok.Data;


@Data
public class CommentRequest {
    private String content;//接收客户端的评论内容
    private String imageUrl; // 评论图片URL
}