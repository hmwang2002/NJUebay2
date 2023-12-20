package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.entity.SaleState;
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
        queryWrapper.eq(Good::getOnSale, SaleState.ON_SALE);
        List<Good> list = goodMapper.selectList(queryWrapper);
        List<Commodity> commodities = new ArrayList<>();
        for (Good good : list) {
            User seller = userMapper.selectById(good.getSellerId());
            String[] imgList = getSplitUri(good.getImgList());
            Commodity commodity = Commodity.builder()
                    .goodsId(good.getId())
                    .goodsName(good.getName())
                    .imgList(imgList)
                    .description(good.getMainDesc())
                    .seller(seller.getUserName())
                    .sellerEmail(seller.getEmail())
                    .onSale(good.getOnSale())
                    .purchasePrice(good.getPurchasePrice())
                    .expectPrice(good.getExpectPrice())
                    .buyer("")
                    .buyerEmail("")
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }

    @Override
    public void addGood(GoodVO goodVO) {
        // 1220 dsy 修改了数据库表，需要修改这里
        Good good = new Good();
        good.setName(goodVO.getName());
        good.setMainDesc(goodVO.getMainDesc());
        good.setExpectPrice(goodVO.getExpectPrice());
        good.setPurchasePrice(goodVO.getPurchasePrice());

        // 将url list转为字符串，使用逗号分隔
        StringBuilder sb = new StringBuilder();
        for (String url : goodVO.getImgList()) {
            sb.append(url).append(",");
        }
        good.setImgList(sb.toString());
        good.setSellerId(StpUtil.getLoginIdAsLong());
        good.setNewnessDesc(goodVO.getNewnessDesc());
        good.setOnSale(SaleState.ON_SALE);
        goodMapper.insert(good);
    }

    @Override
    public void deleteGood(Long goodId) {
        goodMapper.deleteById(goodId);
    }

    // 意向购买商品
    @Override
    public String wannaBuyGood(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        Long id = StpUtil.getLoginIdAsLong();
        if (good == null) {
            return "商品不存在";
        }

        if(!good.getOnSale().equals(SaleState.ON_SALE)) {
            return "商品已被购买或正在交易";
        }

        good.setOnSale(SaleState.DEALING);
        good.setBuyerId(id);
        goodMapper.updateById(good);
        return "开始交易";
    }

    @Override
    public String cancelBuyGood(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) {
            return "商品不存在";
        }

        if(!good.getOnSale().equals(SaleState.DEALING)) {
            return "商品已经不在DEALING状态";
        }

        good.setOnSale(SaleState.ON_SALE);
        good.setBuyerId(null);
        goodMapper.updateById(good);
        return "交易关闭";
    }

    @Override
    public List<Commodity> getBoughtGoods() {
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = StpUtil.getLoginIdAsLong();
        queryWrapper.eq(Good::getBuyerId, userId);
        queryWrapper.eq(Good::getOnSale, SaleState.SOLD);
        List<Good> list = goodMapper.selectList(queryWrapper);

        return getCommoditiesFromGoods(list);
    }

    @Override
    public List<Commodity> getReadyToBuyGoods() {
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = StpUtil.getLoginIdAsLong();
        queryWrapper.eq(Good::getBuyerId, userId);
        queryWrapper.eq(Good::getOnSale, SaleState.DEALING);
        List<Good> list = goodMapper.selectList(queryWrapper);

        return getCommoditiesFromGoods(list);
    }

    @Override
    public List<Commodity> getSellGoods() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getSellerId, userId);
        queryWrapper.eq(Good::getOnSale, SaleState.ON_SALE);
        List<Good> list = goodMapper.selectList(queryWrapper);

        return getCommoditiesFromGoods(list);
    }

    @Override
    public List<Commodity> getDealingGoods() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getSellerId, userId);
        queryWrapper.eq(Good::getOnSale, SaleState.DEALING);
        List<Good> list = goodMapper.selectList(queryWrapper);

        return getCommoditiesFromGoods(list);
    }

    @Override
    public List<Commodity> getSoldGoods() {
        Long userId = StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getSellerId, userId);
        queryWrapper.eq(Good::getOnSale, SaleState.SOLD);
        List<Good> list = goodMapper.selectList(queryWrapper);

        return getCommoditiesFromGoods(list);
    }

    private List<Commodity> getCommoditiesFromGoods(List<Good> list){
        List<Commodity> commodities = new ArrayList<>();
        for (Good good : list) {
            User seller = User.builder().build();
            if (good.getSellerId() != null){
                seller = userMapper.selectById(good.getSellerId());
                if (seller == null) seller = User.builder().build();
            }

            User buyer = User.builder().build();
            if (good.getSellerId() != null){
                seller = userMapper.selectById(good.getSellerId());
                if (seller == null) seller = User.builder().build();
            }

            String[] imgList = getSplitUri(good.getImgList());
            Commodity commodity = Commodity.builder()
                    .goodsId(good.getId())
                    .goodsName(good.getName())
                    .imgList(imgList)
                    .description(good.getMainDesc())
                    .seller(seller.getUserName())
                    .sellerEmail(seller.getEmail())
                    .onSale(good.getOnSale())
                    .buyer(buyer.getUserName())
                    .buyerEmail(buyer.getEmail())
                    .newnessDesc(good.getNewnessDesc())
                    .expectPrice(good.getExpectPrice())
                    .purchasePrice(good.getPurchasePrice())
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }

    private String[] getSplitUri(String listStr){
        String[] ret = listStr.split(",");
        return ret;
    }
}
