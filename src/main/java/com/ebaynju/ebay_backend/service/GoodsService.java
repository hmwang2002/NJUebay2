/*
 * @Author: didaxingshi 3037731915@qq.com
 * @Date: 2022-11-29 23:40:07
 * @LastEditors: didaxingshi 3037731915@qq.com
 * @LastEditTime: 2022-12-11 22:35:47
 * @FilePath: \ebay-nju\src\main\java\com\ebaynju\ebay_backend\service\GoodsService.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.ebaynju.ebay_backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ebaynju.ebay_backend.pojo.Goods;

@Service
public interface GoodsService {
    public List<Goods> selectByGoodsid(String goodsId);

    public List<Goods> selectByGoodsName(String goodsName);

    public Map<String, Object> addGoods(Goods goods);

    public void deleteGoods(int goodsId);

    public List<Goods> updateGoodsOnSale();

    public List<Goods> getBoughtGoods(String buyer);

    List<Goods> boughtGoods(String buyer);

    void changeInfo(int goodsId, String goodsName, String seller, String sellerEmail, String description, String img,
            float price);

    void changeBuyer(int goodsId, String buyer);

    public Map<String, Object> buyGoods(int goodsId, String buyer);
}
