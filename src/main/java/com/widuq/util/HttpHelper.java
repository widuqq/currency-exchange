package com.widuq.util;

import lombok.experimental.UtilityClass;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@UtilityClass
public class HttpHelper {
    public static Map<String, String> parseFormData(String formData) {
        Map<String, String> result = new LinkedHashMap<>();
        if (formData == null || formData.isEmpty()) {
            return result;
        }

        String decoded = URLDecoder.decode(formData, StandardCharsets.UTF_8);

        String[] pairs = decoded.split("&");

        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                result.put(keyValue[0], "");
            }
        }

        return result;
    }
}
