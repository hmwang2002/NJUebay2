package com.njuebay2.backend.service;

import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.vo.Commodity;
import com.njuebay2.backend.domain.vo.GoodVO;

import java.util.List;

/**
 * @author whm
 * @date 2023/12/4 16:41
 */
public interface GoodService {
    List<Commodity> getGoodsOnSale();

    void addGood(GoodVO goodVO);

    void deleteGood(Long goodId);

    String buyGood(Long goodId);

    List<Commodity> getBoughtGoods();

    List<Commodity> getSellGoods();
}
