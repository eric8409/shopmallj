package com.eric.shopmall.service.impl;

import com.eric.shopmall.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final RestTemplate restTemplate;

    // 特定幣別 (如 TWD-USD) 與 完整列表 API 網址
    // 根據 2025 中央銀行規範，BP01D01 包含美元、歐元、日圓、人民幣等主要貨幣
    private final String OFFICIAL_CBC_URL = "https://cpx.cbc.gov.tw/API/DataAPI/Get?FileName=BP01D01";

    private final String REDIS_KEY = "currency:twd_to_usd_rates";
    private final String REDIS_KEY_FULL_LIST = "currency:full_list_all_countries";

    // 建構子：設定 10 秒連線與讀取逾時
    public CurrencyServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * 原有的 getRates 方法 (此處延用您的實作邏輯)
     */
    @Override
    public String getRates() {
        String cachedData = redisTemplate.opsForValue().get(REDIS_KEY);
        if (cachedData != null) {
            return cachedData;
        }

        // 呼叫 API 並存入 Redis 的邏輯 (與 getAllCurrenciesFullList 類似)
        return getAllCurrenciesFullList();
    }

    /**
     * 2025 完整更新版：取得所有國家匯率列表
     */
    @Override
    public String getAllCurrenciesFullList() {
        // 1. 先嘗試從 Redis 快取取得
        String cachedData = redisTemplate.opsForValue().get(REDIS_KEY_FULL_LIST);
        if (cachedData != null) {
            System.out.println(">>> 成功從 Redis 取得 2025 匯率完整快取。");
            return cachedData;
        }

        System.out.println(">>> Redis 無資料，發送請求至中央銀行 API...");

        try {
            // 2. 設定 Header (模擬瀏覽器避免被阻擋)
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 3. 發送請求
            ResponseEntity<String> response = restTemplate.exchange(
                    OFFICIAL_CBC_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String apiResponse = response.getBody();

            // 4. 驗證回應格式
            // 央行 API-JSON 標準格式是以 "{" 開頭的 JSON Object (內含 Header 與 DataSet)
            if (apiResponse != null && apiResponse.trim().startsWith("{")) {

                // 5. 存入 Redis，設定 1 小時過期 (符合央行每日更新頻率)
                redisTemplate.opsForValue().set(REDIS_KEY_FULL_LIST, apiResponse, Duration.ofHours(1));

                System.out.println(">>> API 資料獲取成功，已更新 Redis 快取。");
                return apiResponse;
            } else {
                System.err.println(">>> 錯誤：API 回傳內容非預期的 JSON 格式");
                return null;
            }

        } catch (Exception e) {
            System.err.println(">>> getAllCurrenciesFullList 呼叫失敗: " + e.getMessage());
            return null;
        }
    }
}
