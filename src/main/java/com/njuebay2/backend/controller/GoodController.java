package com.njuebay2.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.njuebay2.backend.domain.vo.*;
import com.njuebay2.backend.service.GoodService;
import com.njuebay2.backend.service.OssService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whm
 * @date 2023/12/4 16:53
 */
@RestController
@RequestMapping("/good")
@RequiredArgsConstructor
@CrossOrigin()
public class GoodController {
    private final GoodService goodService;

    private final OssService ossService;

    /**
     * 上传商品图片
     * @param file
     * @return
     */
    @PostMapping("/uploadPhoto")
    public Response<String> uploadPhoto(@RequestParam("photo") MultipartFile file) {
        String url = ossService.uploadFile(file);
        if (url == null) {
            return Response.failed(999, "图片上传失败");
        }
        return Response.success(200, "图片上传成功", url);
    }

    //获取在售商品
    @RequestMapping("/getOnSale")
    public Response<List<Commodity>> getOnSale() {
        List<Commodity> goods = goodService.getGoodsOnSale();
        if (goods == null) goods = new ArrayList<>();
        return Response.success(200, "获取在售商品成功", goods);
    }

    @PostMapping("/add")
    public Response<?> addGood(@RequestBody GoodVO goodVO) {
        if (StpUtil.isLogin()) {
            goodService.addGood(goodVO);
            return Response.success(200, "添加商品成功");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @DeleteMapping("/delete/{goodId}")
    public Response<?> deleteGood(@PathVariable("goodId") Long goodId) {
        if (StpUtil.isLogin()) {
            goodService.deleteGood(goodId);
            return Response.success(200, "删除商品成功");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/wannaBuy")
    public Response<?> wannaBuyGood(@RequestParam("goodId") Long goodId) {
        if (StpUtil.isLogin()) {
            String res = goodService.wannaBuyGood(goodId);
            if (res.equals("开始交易")) {
                goodService.informSeller(goodId, true);
                return Response.success(200, res);
            } else {
                return Response.failed(999, res);
            }
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/cancelBuy")
    public Response<?> CancelBuyGood(@RequestParam("goodId") Long goodId, @RequestParam("isBuyer") boolean isBuyer) {
        if (StpUtil.isLogin()) {
            if (isBuyer) {
                goodService.informSeller(goodId, false);
            } else {
                goodService.informBuyer(goodId);
            }

            String res = goodService.cancelBuyGood(goodId);
            return Response.success(200, res);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    //获取用户相关的出售商品
    @RequestMapping("/getSellGoods")
    public Response<List<Commodity>> getSellGoods() {
        if (StpUtil.isLogin()) {
            List<Commodity> sellGoods = goodService.getSellGoods();
            return Response.success(200, "获取出售商品成功", sellGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    //获取我已经购买的（购物车）
    @RequestMapping("/getBoughtGoods")
    public Response<List<Commodity>> getBoughtGoods() {
        if (StpUtil.isLogin()) {
            List<Commodity> boughtGoods = goodService.getBoughtGoods();
            return Response.success(200, "获取已购商品成功", boughtGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/getReadyToBuyGoods")
    public Response<List<Commodity>> getReadyToBuyGoods() {
        if (StpUtil.isLogin()) {
            List<Commodity> ReadyToBuyGoods = goodService.getReadyToBuyGoods();
            return Response.success(200, "获取已购商品成功", ReadyToBuyGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/getDealingGoods")
    public Response<List<Commodity>> getDealingGoods(){
        if (StpUtil.isLogin()){
            List<Commodity> dealingGoods = goodService.getDealingGoods();
            return Response.success(200, "获取正在交易商品成功", dealingGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    //获取用户相关的出售商品
    @RequestMapping("/getSoldGoods")
    public Response<List<Commodity>> getSoldGoods() {
        if (StpUtil.isLogin()) {
            List<Commodity> soldGoods = goodService.getSoldGoods();
            return Response.success(200, "获取出售商品成功", soldGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @PostMapping("/confirmDeal")
    public Response<?> confirmDeal(@RequestParam("goodId") Long goodId){
        if (StpUtil.isLogin()) {
            String res = goodService.confirmDeal(goodId);
            if (res.equals("交易完成")){
                goodService.informBuyerAndSellerEval(goodId);
                return Response.success(200, "确认交易完成");
            }

            if (res.equals("商品不存在")){
                return Response.failed(MyErrorCode.GOOD_NULL.status, res);
            }

            return Response.failed(MyErrorCode.STATUS_NOT_DEALING.status, "商品并非处于交易状态，请检查交易状况");

        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/addComment")
    public Response<?> addComment(@RequestParam("goodId") Long goodId, @RequestParam("content") String content) {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            goodService.addComment(userId, goodId, content);
            return Response.success(200, "添加评论成功");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/getCommentByGoodId")
    public Response<List<CommentVO>> getCommentByGoodId(@RequestParam("goodId") Long goodId) {
        List<CommentVO> commentVOList = goodService.getGoodComments(goodId);
        return Response.success(200, "获取评论成功", commentVOList);
    }

    @RequestMapping("/deleteComment")
    public Response<?> deleteComment(@RequestParam("userName") String userName, @RequestParam("commentId") Long commentId) {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean res = goodService.deleteComment(userName, userId, commentId);
            if (res) return Response.success(200, "删除评论成功");
            else return Response.failed(999, "删除评论失败，无权限");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/informSeller")
    public Response<?> informSeller(@RequestParam("goodId") Long goodId, @RequestParam("isPurchase") boolean isPurchase) {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean res = goodService.informSeller(goodId, isPurchase);
            if (res) return Response.success(200, "通知卖家成功");
            else return Response.failed(999, "通知卖家失败");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/chat")
    public Response<?> chat(@RequestParam("sellerEmail") String sellerEmail, @RequestParam("goodName") String goodName, @RequestParam("content") String content) {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            boolean res = goodService.chat(userId, sellerEmail, goodName, content);
            if (res) return Response.success(200, "发送消息成功");
            else return Response.failed(999, "发送消息失败");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/search")
    public Response<List<Commodity>> search(@RequestParam("query") String query) {
        List<Commodity> goods = goodService.search(query);
        if (goods == null) goods = new ArrayList<>();
        return Response.success(200, "搜索成功", goods);
    }

    @RequestMapping("/getNotEval")
    public Response<List<Commodity>> getNotEvalGoods() {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            List<Commodity> goods = goodService.getNotEvalGoods(userId);
            if (goods == null) goods = new ArrayList<>();
            return Response.success(200, "获取未评价商品成功", goods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @PostMapping("/editInfo")
    public Response<?> editInfo(@RequestBody GoodEditInfoVO goodEditInfoVO) {
        if (StpUtil.isLogin()) {
            String res = goodService.editInfo(goodEditInfoVO);
            if (res.equals("修改商品信息成功")) {
                return Response.success(200, "修改商品信息成功！");
            } else {
                return Response.failed(999, res);
            }
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

}
