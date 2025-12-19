package com.eric.shopmall.controller;

import com.eric.shopmall.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    // 單一美金匯率路徑
    @GetMapping("/api/currency/rates")
    public ResponseEntity<String> getExchangeRates() {
        String rates = currencyService.getRates();
        if (rates != null) {
            return ResponseEntity.status(HttpStatus.OK).body(rates);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法取得 TWD-USD 資訊");
        }
    }

    // 各國匯率完整列表路徑
    @GetMapping("/api/currency/all-list")
    public ResponseEntity<String> getAllExchangeRatesList() {
        String fullList = currencyService.getAllCurrenciesFullList();
        if (fullList != null) {
            return ResponseEntity.status(HttpStatus.OK).body(fullList);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("無法取得完整的各國匯率列表");
        }
    }
}
