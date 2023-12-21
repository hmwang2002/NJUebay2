package com.njuebay2.backend.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.njuebay2.backend.domain.entity.Comment;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.entity.SaleState;
import com.njuebay2.backend.domain.entity.User;
import com.njuebay2.backend.domain.vo.CommentVO;
import com.njuebay2.backend.domain.vo.Commodity;
import com.njuebay2.backend.domain.vo.GoodVO;
import com.njuebay2.backend.mapper.CommentMapper;
import com.njuebay2.backend.mapper.GoodMapper;
import com.njuebay2.backend.mapper.UserMapper;
import com.njuebay2.backend.service.GoodService;
import com.njuebay2.backend.service.MailService;
import lombok.RequiredArgsConstructor;
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
    public boolean informSeller(Long userId, String sellerEmail, String goodName) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        String buyerName = user.getUserName();
        String buyerEmail = user.getEmail();
        String subject = "您在NJUebay的商品" + goodName + "被意向购买";
        String content = "您的商品" + goodName + "被用户" + buyerName + "意向购买，联系方式为" + buyerEmail;
        boolean res = mailService.sendSimpleMail(sellerEmail, subject, content);
        return res;
    }

    @Override
    public boolean chat(Long userId, String sellerEmail, String goodName, String content) {
        User user = userMapper.selectById(userId);
        if (user == null) return false;
        String buyerEmail = user.getEmail();
        String subject = "您在NJUebay的商品" + goodName + "收到一条私聊";
        content = content + "\n我的联系方式为" + buyerEmail;
        boolean res = mailService.sendSimpleMail(sellerEmail, subject, content);
        return res;
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
