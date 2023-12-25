package com.njuebay2.backend.domain.vo;

import lombok.Data;

/**
 * @author SYuan03
 * @date 2023/12/25
 */
@Data
public class GoodEditInfoVO {
    private Long goodId;
    private String goodName;
    private String description;
    private String[] imgList;
    private Double expectPrice;
}
