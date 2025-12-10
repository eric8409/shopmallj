package com.eric.shopmall.controller;

import com.eric.shopmall.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/api/currency/rates")
    public ResponseEntity<String> getExchangeRates() {
        String rates = currencyService.getRates();

        if (rates != null) {
            // 回傳 Frankfurter API 的原始 JSON 格式
            return ResponseEntity.status(HttpStatus.OK).body(rates);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法取得匯率資訊");
        }
    }
}

