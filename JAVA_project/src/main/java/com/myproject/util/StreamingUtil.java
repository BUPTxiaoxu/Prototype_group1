package main.java.com.myproject.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * 用于处理流式API请求的工具类
 */
public class StreamingUtil {

    /**
     * 流式输出的回调接口
     */
    public interface StreamCallback {
        /**
         * 当收到新的token时调用
         * @param token 收到的文本片段
         */
        void onToken(String token);

        /**
         * 当流式输出完成时调用
         */
        default void onComplete() {}

        /**
         * 当发生错误时调用
         * @param errorMessage 错误信息
         */
        default void onError(String errorMessage) {}
    }

    // HTTP客户端，用于发送API请求
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(120))
            .build();

    /**
     * 发送流式HTTP请求
     * @param endpoint API端点
     * @param apiKey API密钥
     * @param requestBody 请求体（JSON格式）
     * @param callback 流式回调接口
     */
    public static void sendStreamingRequest(
            String endpoint,
            String apiKey,
            String requestBody,
            StreamCallback callback) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // 使用异步处理流式响应
            CompletableFuture<HttpResponse<InputStream>> responseFuture =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream());

            responseFuture.thenAccept(response -> {
                if (response.statusCode() == 200) {
                    try (InputStream inputStream = response.body();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (line.startsWith("data: ") && !line.contains("[DONE]")) {
                                String jsonData = line.substring(6); // 移除 "data: " 前缀

                                // 解析流式JSON响应
                                String token = parseStreamToken(jsonData);
                                if (token != null && !token.isEmpty()) {
                                    callback.onToken(token);
                                }
                            }
                        }

                        // 流处理完成
                        callback.onComplete();
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError("Error processing streaming response: " + e.getMessage());
                    }
                } else {
                    try (InputStream errorStream = response.body();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {

                        StringBuilder errorMsg = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            errorMsg.append(line);
                        }

                        System.err.println("API错误响应: " + response.statusCode() + " - " + errorMsg);
                        callback.onError("API request failed: " + response.statusCode());
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onError("Error reading error response: " + e.getMessage());
                    }
                }
            }).exceptionally(e -> {
                e.printStackTrace();
                callback.onError("Error sending API request: " + e.getMessage());
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Error initiating API request: " + e.getMessage());
        }
    }

    /**
     * 发送非流式HTTP请求
     * @param endpoint API端点
     * @param apiKey API密钥
     * @param requestBody 请求体（JSON格式）
     * @return 响应字符串
     * @throws IOException 如果请求失败
     * @throws InterruptedException 如果请求被中断
     */
    public static String sendRequest(String endpoint, String apiKey, String requestBody)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("API request failed with status code: " + response.statusCode() +
                    " and message: " + response.body());
        }
    }

    /**
     * 为JSON字符串中的特殊字符进行转义
     * @param str 原始字符串
     * @return 转义后的字符串
     */
    public static String escapeJsonString(String str) {
        if (str == null) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            switch (ch) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 解析DeepSeek流式API返回的JSON数据，提取token
     * @param jsonData JSON响应数据
     * @return 解析出的token字符串
     */
    private static String parseStreamToken(String jsonData) {
        try {
            // 简单解析token (生产环境应使用JSON库)
            if (jsonData.contains("\"content\"")) {
                int contentStart = jsonData.indexOf("\"content\":");
                if (contentStart != -1) {
                    contentStart = jsonData.indexOf("\"", contentStart + 10);
                    int contentEnd = jsonData.indexOf("\"", contentStart + 1);

                    while (contentEnd > 0 && jsonData.charAt(contentEnd - 1) == '\\') {
                        contentEnd = jsonData.indexOf("\"", contentEnd + 1);
                    }

                    if (contentStart != -1 && contentEnd != -1) {
                        String content = jsonData.substring(contentStart + 1, contentEnd);
                        // 反转义
                        return content.replace("\\n", "\n")
                                .replace("\\\"", "\"")
                                .replace("\\\\", "\\");
                    }
                }
            }
            return "";
        } catch (Exception e) {
            System.err.println("Error parsing stream token: " + e.getMessage());
            return "";
        }
    }

    /**
     * 解析非流式响应中的内容字段
     * @param jsonResponse 完整的JSON响应
     * @return 解析出的内容
     */
    public static String parseResponseContent(String jsonResponse) {
        try {
            // 简化的解析逻辑 - 生产环境建议使用JSON库
            if (jsonResponse.contains("\"choices\"") && jsonResponse.contains("\"content\"")) {
                int contentStart = jsonResponse.indexOf("\"content\":");
                if (contentStart != -1) {
                    contentStart = jsonResponse.indexOf("\"", contentStart + 10);
                    int contentEnd = jsonResponse.indexOf("\"", contentStart + 1);

                    while (contentEnd > 0 && jsonResponse.charAt(contentEnd - 1) == '\\') {
                        contentEnd = jsonResponse.indexOf("\"", contentEnd + 1);
                    }

                    if (contentStart != -1 && contentEnd != -1) {
                        String content = jsonResponse.substring(contentStart + 1, contentEnd);
                        // 反转义
                        return content.replace("\\n", "\n")
                                .replace("\\\"", "\"")
                                .replace("\\\\", "\\");
                    }
                }
            }

            System.err.println("Failed to parse API response: " + jsonResponse);
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing response: " + e.getMessage());
            return null;
        }
    }
}