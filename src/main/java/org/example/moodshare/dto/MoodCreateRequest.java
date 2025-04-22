// 心情创建请求
package org.example.moodshare.dto;

import lombok.Data;
import java.util.Set;

@Data
public class MoodCreateRequest {
    private String content;
    private String emoji;
    private Set<String> tags;
}