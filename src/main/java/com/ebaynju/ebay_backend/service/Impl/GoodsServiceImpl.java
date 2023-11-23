/*
 * @Author: didaxingshi 3037731915@qq.com
 * @Date: 2022-11-29 23:40:07
 * @LastEditors: didaxingshi 3037731915@qq.com
 * @LastEditTime: 2022-12-11 22:35:27
 * @FilePath: \ebay-nju\src\main\java\com\ebaynju\ebay_backend\service\Impl\GoodsServiceImpl.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.ebaynju.ebay_backend.service.Impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ebaynju.ebay_backend.mapper.GoodsMapper;

import com.ebaynju.ebay_backend.pojo.Goods;

/**
 * @author cardigan
 * @version 1.0
 *          Create by 2022/11/26 18:39
 */
@Service
public class GoodsServiceImpl {

    @Autowired
    private GoodsMapper goodsMapper;

    public Goods selectByGoodsId(int goodsId) {
        return goodsMapper.queryGoodsBygoodsId(goodsId);
    }

    public Goods selectByGoodsName(String goodsName) {
        return goodsMapper.queryGoodsBygoodsName(goodsName);
    }

    public Map<String, Object> addGoods(Goods goods) {
        goodsMapper.addGoods(goods);
        Map<String, Object> map = new HashMap<>();

        String goodsName = goods.getGoodsName();
        String seller = goods.getSeller();
        String sellerEmail = goods.getSellerEmail();
        float price = goods.getPrice();

        map.put("goodsName", goodsName);
        map.put("seller", seller);
        map.put("sellerEmail", sellerEmail);
        map.put("price", price);

        return map;
    }

    public void deleteGoods(int goodsId) {
        goodsMapper.deleteGoods(goodsId);
    }

    public List<Goods> updateGoodsOnSale() {
        return goodsMapper.updateGoodsOnSale();
    }

    public List<Goods> getBoughtGoods(String buyer) {
        return goodsMapper.getBoughtGoods(buyer);
    }

    List<Goods> boughtGoods(String buyer) {
        return goodsMapper.getBoughtGoods(buyer);
    }

    public void changeInfo(int goodsId, String goodsName, String seller, String sellerEmail, String description,
            String img, float price) {
        goodsMapper.changeInfo(goodsId, goodsName, seller, sellerEmail, description, img, price);
    }

    public void changeBuyer(int goodsId, String buyer) {
        goodsMapper.changeBuyer(goodsId, buyer);
    }

    public Map<String, Object> buyGoods(int goodsId, String buyer) {
        Map<String, Object> map = new HashMap<>();
        Goods target = goodsMapper.queryGoodsBygoodsId(goodsId);

        if (target == null || !target.isOnSale()) {
            map.put("msg", "商品已下架");
            return map;
        } else {
            changeBuyer(goodsId, buyer);

            map.put("goodsId", goodsId);
            map.put("goodsName", target.getGoodsName());
            map.put("buyer", buyer);
            return map;
        }
    }

}
