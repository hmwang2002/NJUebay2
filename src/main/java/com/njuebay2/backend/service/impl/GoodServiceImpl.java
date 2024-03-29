package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.njuebay2.backend.domain.entity.Comment;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.entity.SaleState;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.CommentVO;
import com.njuebay2.backend.domain.vo.Commodity;
import com.njuebay2.backend.domain.vo.GoodEditInfoVO;
import com.njuebay2.backend.domain.vo.GoodVO;
import com.njuebay2.backend.mapper.CommentMapper;
import com.njuebay2.backend.mapper.GoodMapper;
import com.njuebay2.backend.mapper.UserMapper;
import com.njuebay2.backend.service.GoodService;
import com.njuebay2.backend.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private final CommentMapper commentMapper;

    private final MailService mailService;

    @Override
    public PageInfo<Commodity> getGoodsOnSaleByPage(int page, int size) {
        // 调用PageHelper的startPage方法来初始化分页参数
        PageHelper.startPage(page, size);

        // 紧接着的查询就是一个分页查询，PageHelper会自动对其进行分页处理
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getOnSale, SaleState.ON_SALE);
        List<Good> list = goodMapper.selectList(queryWrapper); // 这个查询会被分页

        // 将Good对象转换为Commodity对象
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
                    .isBuyerEval(good.isBuyerEval())
                    .isBuyerEval(good.isSellerEval())
                    .newnessDesc(good.getNewnessDesc())
                    .build();
            commodities.add(commodity);
        }

        // 使用PageInfo包装查询结果，这时PageInfo会包含分页信息，包括总记录数
        PageInfo<Good> goodPageInfo = new PageInfo<>(list);

        // 创建用于返回的PageInfo<Commodity>对象，并复制分页信息
        PageInfo<Commodity> commodityPageInfo = new PageInfo<>();
        BeanUtils.copyProperties(goodPageInfo, commodityPageInfo);
        commodityPageInfo.setList(commodities); // 设置转换后的列表

        return commodityPageInfo;
    }


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
                    .isBuyerEval(good.isBuyerEval())
                    .isBuyerEval(good.isSellerEval())
                    .newnessDesc(good.getNewnessDesc())
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
        // 在此之前要先查查有没有商品评论，由于设置了外键，所以要先删除评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getGoodId, goodId);
        commentMapper.delete(queryWrapper);
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
    public String confirmDeal(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) {
            return "商品不存在";
        }

        if(!good.getOnSale().equals(SaleState.DEALING)) {
            return "并非处于交易状态";
        }
        good.setOnSale(SaleState.SOLD);
        goodMapper.updateById(good);
        return "交易完成";
    }

    @Override
    public void addComment(Long userId, Long goodId, String content) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date();
        Comment comment = new Comment(userId, goodId, content, df.format(date));
        commentMapper.insert(comment);
    }

    @Override
    public List<CommentVO> getGoodComments(Long goodId) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getGoodId, goodId);
        List<Comment> comments = commentMapper.selectList(queryWrapper);

        List<CommentVO> res = new ArrayList<>();
        for (Comment comment : comments) {
            User user = userMapper.selectById(comment.getUserId());
            if (user == null) {
                // 头像还是先用默认的吧。。。
                user = User.builder().
                        userName("账号已注销").
                        photo("https://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg").
                        build();
            }
            CommentVO commentVO = new CommentVO();
            commentVO.setCommentId(comment.getCommentId());
            commentVO.setUserName(user.getUserName());
            commentVO.setAvatar(user.getPhoto());
            commentVO.setContent(comment.getContent());
            commentVO.setCreateTime(comment.getCreateTime());
            res.add(commentVO);
        }
        // 按时间排序，从最近的开始
        res.sort((o1, o2) -> {
            try {
                Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(o1.getCreateTime());
                Date date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(o2.getCreateTime());
                return date2.compareTo(date1);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return res;
    }

    @Override
    public boolean informSeller(Long goodId, boolean isPurchase) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) {
            return false;
        }
        User buyer = userMapper.selectById(good.getBuyerId());
        User seller = userMapper.selectById(good.getSellerId());


        String buyerName = buyer.getUserName();
        String buyerEmail = buyer.getEmail();
        String goodName = good.getName();
        String subject = "您在NJUebay的商品" + goodName + "被意向购买";
        String content = "您的商品" + goodName + "被用户" + buyerName + "意向购买，联系方式为" + buyerEmail;
        if (!isPurchase) {
            subject = "您在NJUebay的商品" + goodName + "被取消意向购买";
            content = "您的商品" + goodName + "被用户" + buyerName + "取消意向购买, 商品继续在商城出售";
        }
        return mailService.sendSimpleMail(seller.getEmail(), subject, content);
    }

    @Override
    public boolean informBuyer(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) {
            return false;
        }
        User buyer = userMapper.selectById(good.getBuyerId());
        User seller = userMapper.selectById(good.getSellerId());


        String sellerName = seller.getUserName();
        String sellerEmail = seller.getEmail();
        String goodName = good.getName();
        String subject = "您在NJUebay意向购买的商品" + goodName + "被卖家取消";
        String content = "您意向购买的商品" + goodName + "被卖家" + sellerName + "取消，卖家联系方式为" + sellerEmail + "， 您可以继续进入NJUebay选购";
        return mailService.sendSimpleMail(seller.getEmail(), subject, content);
    }

    @Override
    public boolean chat(Long userId, String sellerEmail, String goodName, String content) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        String buyerEmail = user.getEmail();
        String subject = "您在NJUebay的商品" + goodName + "收到一条私聊";
        content = content + "\n我的联系方式为" + buyerEmail;
        return mailService.sendSimpleMail(sellerEmail, subject, content);
    }

    @Override
    public List<Commodity> search(String queryStr) {
        List<Good> queryRes = goodMapper.queryFulltext(queryStr);
        return getCommoditiesFromGoods(queryRes);
    }

    @Override
    public boolean deleteComment(String userName, Long userId, Long commentId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !user.getUserId().equals(userId)) return false;
        commentMapper.deleteById(commentId);
        return true;
    }

    @Override
    public void informBuyerAndSellerEval(Long goodId) {
        Good good = goodMapper.selectById(goodId);
        if (good == null) return;
        Long buyerId = good.getBuyerId();
        Long sellerId = good.getSellerId();
        if (buyerId == null || sellerId == null) return;
        User buyer = userMapper.selectById(buyerId);
        User seller = userMapper.selectById(sellerId);
        if (buyer == null || seller == null) return;
        String buyerEmail = buyer.getEmail();
        String sellerEmail = seller.getEmail();
        String goodName = good.getName();
        String subject = "您在NJUebay购买的商品" + goodName + "交易完成";
        String content = "您的商品" + goodName + "交易完成，请对卖家" + seller.getUserName() + "进行评价";
        mailService.sendSimpleMail(buyerEmail, subject, content);

        subject = "您在NJUebay出售的商品" + goodName + "交易完成";
        content = "您在NJUebay的商品" + goodName + "交易完成，请对买家" + buyer.getUserName() + "进行评价";
        mailService.sendSimpleMail(sellerEmail, subject, content);
    }

    @Override
    public List<Commodity> getNotEvalGoods(Long userId) {
        LambdaQueryWrapper<Good> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getSellerId, userId);
        queryWrapper.eq(Good::isSellerEval, false);
        List<Good> list1 = goodMapper.selectList(queryWrapper);

        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Good::getBuyerId, userId);
        queryWrapper.eq(Good::isBuyerEval, false);
        List<Good> list2 = goodMapper.selectList(queryWrapper);

        list1.addAll(list2);
        return getCommoditiesFromGoods(list1);
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
            if (good.getBuyerId() != null){
                buyer = userMapper.selectById(good.getBuyerId());
                if (buyer == null) buyer = User.builder().build();
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
                    .isBuyerEval(good.isBuyerEval())
                    .isSellerEval(good.isSellerEval())
                    .build();
            commodities.add(commodity);
        }
        return commodities;
    }

    private String[] getSplitUri(String listStr){
        return listStr.split(",");
    }

    @Override
    public String editInfo(GoodEditInfoVO goodEditInfoVO) {
        Good good = goodMapper.selectById(goodEditInfoVO.getGoodId());
        if (good == null) return "商品不存在";
        if (!good.getSellerId().equals(StpUtil.getLoginIdAsLong())) return "无权限";
        good.setName(goodEditInfoVO.getGoodName());
        good.setMainDesc(goodEditInfoVO.getDescription());
        good.setExpectPrice(goodEditInfoVO.getExpectPrice());
        StringBuilder sb = new StringBuilder();
        for (String url : goodEditInfoVO.getImgList()) {
            sb.append(url).append(",");
        }
        good.setImgList(sb.toString());
        // 保存
        goodMapper.updateById(good);
        return "修改商品信息成功";
    }
}
