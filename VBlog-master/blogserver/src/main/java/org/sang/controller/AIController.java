package org.sang.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.sang.bean.RespBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI助手Controller
 * 用于调用本地部署的Ollama大语言模型
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    // Ollama API地址，默认本地11434端口
    private static final String OLLAMA_API_URL = "http://localhost:11434/api/chat";
    
    // 使用的模型名称
    private static final String MODEL_NAME = "deepseek-r1:7b";
    
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送消息到AI助手
     * @param message 用户消息
     * @return AI回复
     */
    @RequestMapping(value = "/chat", method = RequestMethod.POST)
    public RespBean chat(@RequestParam("message") String message) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL_NAME);
            requestBody.put("stream", false);
            
            // 构建消息数组
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(userMessage);
            requestBody.put("messages", messages);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 创建请求实体
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(requestBody), headers);
            
            // 发送请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    OLLAMA_API_URL, 
                    entity, 
                    String.class
            );
            
            // 解析响应
            JSONObject responseJson = JSON.parseObject(response.getBody());
            
            // 检查响应格式
            if (responseJson.containsKey("message")) {
                JSONObject messageObj = responseJson.getJSONObject("message");
                if (messageObj != null && messageObj.containsKey("content")) {
                    String aiResponse = messageObj.getString("content");
                    return new RespBean("success", aiResponse);
                }
            }
            
            // 如果响应格式不符合预期，返回错误
            return new RespBean("error", "AI服务返回格式异常");
            
        } catch (org.springframework.web.client.ResourceAccessException e) {
            e.printStackTrace();
            return new RespBean("error", "无法连接到Ollama服务，请确保Ollama正在运行（默认端口11434）");
        } catch (Exception e) {
            e.printStackTrace();
            return new RespBean("error", "AI服务调用失败: " + e.getMessage());
        }
    }
}

