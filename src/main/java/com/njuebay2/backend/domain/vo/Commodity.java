package com.njuebay2.backend.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author whm
 * @date 2023/12/15 20:56
 */
@Data
@Builder
public class Commodity {
    private Long goodsId;

    private String goodsName;

    private String description;

    private String img;

    private String seller;

    private String sellerEmail;

    private boolean onSale;

    private String buyer;

    private Double price;
}