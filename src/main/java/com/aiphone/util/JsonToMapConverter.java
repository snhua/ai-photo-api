package com.aiphone.util;
import com.fasterxml.jackson.core.type.TypeReference; import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
public class JsonToMapConverter {
    /**
     将 JSON 字符串转换为 Map 对象
     @param jsonStr 输入的 JSON 字符串
     @return 转换后的 Map<String, Object>
     @throws Exception 转换过程中发生的异常
     */
    public static Map<String, Object> jsonToMap (String jsonStr) throws Exception {
// 校验输入参数
        if (jsonStr == null || jsonStr.trim ().isEmpty ()) {
            throw new IllegalArgumentException ("JSON 字符串不能为空");
        }
// 创建 Jackson 的 ObjectMapper 实例
        ObjectMapper objectMapper = new ObjectMapper ();
// 使用 TypeReference 指定 Map 的泛型类型（String 为 key，Object 为 value）
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
// 转换 JSON 字符串为 Map
        return objectMapper.readValue (jsonStr, typeReference);
    }
    // 测试方法
    public static void main (String [] args) {
// 待转换的 JSON 字符串
        String jsonStr = "{\"id\":\"deb26a13-b50b-5c97-a579-28b24ae51aad\",\"create_time\":\"2025-08-12T13:48:22+08:00\",\"resource_type\":\"encrypt-resource\",\"event_type\":\"TRANSACTION.SUCCESS\",\"summary\":\"支付成功\",\"resource\":{\"original_type\":\"transaction\",\"algorithm\":\"AEAD_AES_256_GCM\",\"ciphertext\":\"9km4P49y/PhydXXj/QYEIolNw/wqlbZM+UrnnMZAvsrxJ0upSL87/mTTzqWHudB4+UXnR+OKFMjeW+1NJB2MtTlkKAlQw5KtFzfIduQPrN9fwuHkONERnUXnHiNYl8Hk0c82oBx6JlND5OTJCo6IL+cIAXPLAUFKw42ZI+sVs7wDh8UvFTMSJvXP3JT3Vcai2+W74WORC05Mu2/37phJDsMoqElkYLeuOdJUXGn9jeV/jsHnkD7BT70F6dCcQdRl/Pl7aZ+qjqySlbZ7EN0fdVagQ3zuqAGUSw8xV0xEcprLLQJiBKIxufl+ofpy+YNWAe0tq2CefYxTta46a09S/ShiXu565UGFBgP+7qCIIHvmrJA8B8ILA1pFKLTZAR8IUS61PsFwG/oSNB6HzK8ohN35ZXa91BVGTQG/7qh5ccpdShFP4NXlG8vgh81SXRhg4ncXhnCXebiYnLaT5ib9Ujm5nfKQ0UVWc2f9x4PnsNQ79ZKLqWotSNnvKvjkr1pJ8f9qK3Wp6ksuYg5BZKK+TbeOFpPgW5SuCwneK7j5T+G9yij8PIouoEavc3HEVp5OzK39IfwqgwvYcA==\",\"associated_data\":\"transaction\",\"nonce\":\"7ZydP0dERhED\"}}";
        try {
// 转换 JSON 为 Map
            Map<String, Object> resultMap = jsonToMap (jsonStr);
// 打印转换结果
            System.out.println ("转换成功，Map 大小：" + resultMap.size ());
            System.out.println ("id 字段值：" + resultMap.get ("id"));
            System.out.println ("event_type 字段值：" + resultMap.get ("event_type"));
// 获取嵌套的 resource 对象（也是一个 Map）
            Map<String, Object> resourceMap = (Map<String, Object>) resultMap.get ("resource");
            System.out.println ("resource 中的 algorithm 字段值：" + resourceMap.get ("algorithm"));
        } catch (Exception e) {
            System.err.println ("转换失败：" + e.getMessage ());
            e.printStackTrace ();
        }
    }
}
