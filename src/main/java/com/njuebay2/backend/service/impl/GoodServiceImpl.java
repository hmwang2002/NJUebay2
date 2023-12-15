package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.Commodity;
import com.njuebay2.backend.domain.vo.GoodVO;
import com.njuebay2.backend.mapper.GoodMapper;
import com.njuebay2.backend.mapper.UserMapper;
import com.njuebay2.backend.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whm
 * @date 2023/12/4 16:41
 */
@Service
@RequiredArgsConstructor
public class GoodServiceImpl implements GoodService {
    private final GoodMapper goodMapper;

    private final UserMapper userMapper;


    @Override
    public List<Commodity> getGoodsOnSale() {
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::isOnSale, true);
        List<Good> list = goodMapper.selectList(queryWrapper);
        List<Commodity> commodities = new ArrayList<>();
        for (Good good : list) {
            User seller = userMapper.selectById(good.getSellerId());

            Commodity commodity = Commodity.builder()
                    .goodsId(good.getId())
                    .goodsName(good.getName())
                    .img(good.getImg())
                    .description(good.getDescription())
                    .seller(seller.getUserName())
                    .sellerEmail(seller.getEmail())
                    .onSale(good.isOnSale())
                    .buyer("")
                    .price(good.getPrice())
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }

    @Override
    public void addGood(GoodVO goodVO) {
        Good good = new Good();
        good.setName(goodVO.getName());
        good.setDescription(goodVO.getDescription());
        good.setImg(goodVO.getImg());
        good.setSellerId(goodVO.getSellerId());
        good.setOnSale(true);
        good.setPrice(goodVO.getPrice());
        goodMapper.insert(good);
    }

    @Override
    public void deleteGood(Long goodId) {
        goodMapper.deleteById(goodId);
    }

    @Override
    public String buyGood(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) {
            return "商品已下架";
        }

        if(!good.isOnSale()) {
            return "商品已被购买";
        }

        good.setOnSale(false);
        good.setBuyerId(StpUtil.getLoginIdAsLong());
        goodMapper.updateById(good);
        return "购买成功";
    }

    @Override
    public List<Commodity> getBoughtGoods() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getBuyerId, userId);
        List<Good> list = goodMapper.selectList(queryWrapper);

        List<Commodity> commodities = new ArrayList<>();
        for (Good good : list) {
            User seller = userMapper.selectById(good.getSellerId());
            User buyer = userMapper.selectById(good.getBuyerId());

            Commodity commodity = Commodity.builder()
                    .goodsId(good.getId())
                    .goodsName(good.getName())
                    .img(good.getImg())
                    .description(good.getDescription())
                    .seller(seller.getUserName())
                    .sellerEmail(seller.getEmail())
                    .onSale(good.isOnSale())
                    .buyer(buyer.getUserName())
                    .price(good.getPrice())
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }

    @Override
    public List<Commodity> getSellGoods() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getSellerId, userId);
        List<Good> list = goodMapper.selectList(queryWrapper);

        List<Commodity> commodities = new ArrayList<>();
        for (Good good : list) {
            User seller = userMapper.selectById(good.getSellerId());
            User buyer = userMapper.selectById(good.getBuyerId());
            if (buyer == null) buyer = User.builder().build();

            Commodity commodity = Commodity.builder()
                    .goodsId(good.getId())
                    .goodsName(good.getName())
                    .img(good.getImg())
                    .description(good.getDescription())
                    .seller(seller.getUserName())
                    .sellerEmail(seller.getEmail())
                    .onSale(good.isOnSale())
                    .buyer(buyer.getUserName())
                    .price(good.getPrice())
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }
}
