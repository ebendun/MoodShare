// 心情创建请求
package org.example.moodshare.dto;

import lombok.Data;
import org.example.moodshare.model.Mood;

import java.util.Set;
//客户端发送给服务器的心情创建请求
@Data
public class MoodCreateRequest {
    private String content;
    private String emoji;
    private Set<String> tags;
    
    // 新增隐私设置
    private Mood.PrivacyLevel privacyLevel = Mood.PrivacyLevel.PUBLIC;
    
    // 新增位置信息
    private String location;
    private Double latitude;
    private Double longitude;
    
    // 新增心情类型
    private Mood.MoodType moodType = Mood.MoodType.NEUTRAL;
    
    // 天气信息
    private String weather;
}