package com.eric.shopmall.service;

public interface CurrencyService {
    /**
     * 取得最新的匯率資訊 (優先從 Redis 獲取，失效時才呼叫外部 API)
     * @return 匯率 JSON 字串
     */
    String getRates();
}