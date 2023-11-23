/*
 * @Author: didaxingshi 3037731915@qq.com
 * @Date: 2022-11-30 00:14:58
 * @LastEditors: didaxingshi 3037731915@qq.com
 * @LastEditTime: 2022-12-11 22:35:55
 * @FilePath: \ebay-nju\src\main\java\com\ebaynju\ebay_backend\pojo\Goods.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.ebaynju.ebay_backend.pojo;

import lombok.Data;

/**
 * @author cardigan
 * @version 1.0
 *          Create by 2022/11/26
 */

@Data
public class Goods {
    private int goodsId; // 商品编号
    private String goodsName; // 不超过20字符
    private String description; // 不超过120字符
    private String img;
    private String seller; // 拥有者
    private String sellerEmail; // 拥有者邮件
    private boolean onSale; // 是否仍在出售
    private String buyer; // 在出售后设定购买者
    private float price; // 价格
}
