package com.ebaynju.ebay_backend;

import com.ebaynju.ebay_backend.controller.GoodsController;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
public class EbayBackendApplication {

    public static void main(String[] args) throws JSONException {
        SpringApplication.run(EbayBackendApplication.class, args);
    }

}
