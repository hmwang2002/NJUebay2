package com.njuebay2.backend.domain.vo;

import com.njuebay2.backend.domain.entity.SaleState;
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

    private String[] imgList;

    private String seller;

    private String sellerEmail;

    private SaleState onSale;

    private String buyer;

    private String buyerEmail;

    private Double expectPrice;

    private Double purchasePrice;

    private String newnessDesc;

    private boolean isSellerEval;

    private boolean isBuyerEval;
}
