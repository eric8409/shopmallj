package com.eric.shopmall.service;

public interface CurrencyService {
    /**
     * 取得最新的匯率資訊 (優先從 Redis 獲取，失效時才呼叫外部 API)
     * @return 匯率 JSON 字串
     */
    String getRates();

    /**
     * 新增：取得中央銀行 BP01 統計代碼下所有國家、所有歷史的完整匯率資料
     * @return 完整的匯率 JSON 字串
     */
    String getAllCurrenciesFullList(); // <-- 新增此方法
}
