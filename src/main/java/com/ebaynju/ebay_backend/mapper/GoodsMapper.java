/*
 * @Author: didaxingshi 3037731915@qq.com
 * @Date: 2022-11-30 00:18:12
 * @LastEditors: didaxingshi 3037731915@qq.com
 * @LastEditTime: 2022-12-11 22:34:36
 * @FilePath: \ebay-nju\src\main\java\com\ebaynju\ebay_backend\mapper\GoodsMapper.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.ebaynju.ebay_backend.mapper;

import com.ebaynju.ebay_backend.pojo.Goods;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface GoodsMapper {
    void addGoods(Goods goods);

    void deleteGoods(int goodsId);

    Goods queryGoodsBygoodsId(int goodsId);

    Goods queryGoodsBygoodsName(String goodsName);

    List<Goods> updateGoodsOnSale();

    List<Goods> getBoughtGoods(String buyer);

    void changeInfo(int goodsId, String goodsName, String seller, String sellerEmail, String description, String img,
            float price);

    void changeBuyer(int goodsId, String buyer);

}