package com.eric.shopmall.service.impl;

import com.eric.shopmall.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.time.Duration;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final String OFFICIAL_CBC_URL = "https://cpx.cbc.gov.tw/API/DataAPI/Get?FileName=BP01D01";
    private final String REDIS_KEY = "currency:twd_to_usd_rates";

    @Override
    public String getRates() {
        // 1. 嘗試從 Redis 讀取快取
        String cachedData = redisTemplate.opsForValue().get(REDIS_KEY);

        if (cachedData != null) {
            System.out.println(">>> 成功！從 Redis DB1 取得央行匯率資料。");
            return cachedData;
        }

        System.out.println(">>> Redis 沒資料，正在連線至中央銀行 API...");
        RestTemplate restTemplate = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    OFFICIAL_CBC_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String apiResponse = response.getBody();

            // 3. 存入 Redis 並設定 1 小時過期
            // 修正條件：API 回傳的是 JSON 物件 (以 { 開頭)，不是陣列 (以 [ 開頭)
            if (apiResponse != null && !apiResponse.trim().isEmpty() && apiResponse.trim().startsWith("{")) {
                redisTemplate.opsForValue().set(REDIS_KEY, apiResponse, Duration.ofHours(1));
                System.out.println(">>> 資料已成功寫入 Redis DB1，並設定 1 小時過期。");
            } else {
                System.err.println("API 回應格式不正確 (非 JSON Object)，未寫入 Redis。");
            }
            return apiResponse;

        } catch (Exception e) {
            System.err.println("API 呼叫失敗或解析錯誤: " + e.getMessage());
            return null;
        }
    }
}
