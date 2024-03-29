package com.njuebay2.backend.service;

import com.github.pagehelper.PageInfo;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.vo.CommentVO;
import com.njuebay2.backend.domain.vo.Commodity;
import com.njuebay2.backend.domain.vo.GoodEditInfoVO;
import com.njuebay2.backend.domain.vo.GoodVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author whm
 * @date 2023/12/4 16:41
 */
public interface GoodService {
    List<Commodity> getGoodsOnSale();

    PageInfo<Commodity> getGoodsOnSaleByPage(int page, int size);

    void addGood(GoodVO goodVO);

    void deleteGood(Long goodId);

    String wannaBuyGood(Long goodId);

    String cancelBuyGood(Long goodId);

    List<Commodity> getBoughtGoods();

    List<Commodity> getReadyToBuyGoods();

    List<Commodity> getSellGoods();

    List<Commodity> getDealingGoods();

    List<Commodity> getSoldGoods();

    String confirmDeal(Long goodId);

    void addComment(Long userId, Long goodId, String content);

    List<CommentVO> getGoodComments(Long goodId);

    boolean informSeller(Long goodId, boolean isPurchase);

    boolean informBuyer(Long goodId);

    boolean chat(Long userId, String sellerEmail, String goodName, String content);

    List<Commodity> search(String queryStr);

    boolean deleteComment(String userName, Long userId, Long commentId);

    void informBuyerAndSellerEval(Long goodId);

    List<Commodity> getNotEvalGoods(Long userId);

    String editInfo(GoodEditInfoVO goodEditInfoVO);
}
