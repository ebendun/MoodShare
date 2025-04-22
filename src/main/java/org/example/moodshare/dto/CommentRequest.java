// 评论请求和响应
package org.example.moodshare.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentRequest {
    private String content;
}