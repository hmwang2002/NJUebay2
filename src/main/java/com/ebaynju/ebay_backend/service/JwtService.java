package com.ebaynju.ebay_backend.service;

import java.util.Map;

public interface JwtService {
    String generateToken(Map<String, Object> map);
    String getInfo(String jwt);
}
