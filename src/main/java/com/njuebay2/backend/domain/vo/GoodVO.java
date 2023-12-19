package com.njuebay2.backend.domain.vo;

import lombok.Data;

/**
 * @author whm
 * @date 2023/12/4 17:08
 */
@Data
public class GoodVO {
    private String name;

    private String mainDesc;

    private double expectPrice;

    private double purchasePrice;

    private String[] imgList;

    private Long sellerId;

    private String newnessDesc;
}
