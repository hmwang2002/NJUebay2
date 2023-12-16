package com.njuebay2.backend.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author whm
 * @date 2023/12/4 11:26
 */
@Data
@TableName("good")
public class Good implements Serializable {
    @Serial
    private static final long serialVersionUID = -5423459437718783021L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

    @TableField("img")
    private String img;

    @TableField("sellerId")
    private Long sellerId;

    @TableField("onSale")
    private SaleState onSale;

    @TableField("buyerId")
    private Long buyerId;

    @TableField("price")
    private double price;
}


