package com.njuebay2.backend.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.njuebay2.backend.domain.entity.Good;
import com.njuebay2.backend.domain.vo.GoodVO;
import com.njuebay2.backend.domain.vo.Response;
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

    @RequestMapping("/getOnSale")
    public Response<List<Good>> getOnSale() {
        List<Good> goods = goodService.getGoodsOnSale();
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

    @DeleteMapping("/delete")
    public Response<?> deleteGood(@RequestParam("goodId") Long goodId) {
        if (StpUtil.isLogin()) {
            goodService.deleteGood(goodId);
            return Response.success(200, "删除商品成功");
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    @RequestMapping("/buy")
    public Response<?> buyGood(@RequestParam("goodId") Long goodId) {
        if (StpUtil.isLogin()) {
            String res = goodService.buyGood(goodId);
            if (res.equals("购买成功")) {
                return Response.success(200, res);
            } else {
                return Response.failed(999, res);
            }
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    //获取正在卖的商品
    @RequestMapping("/getSellGoods")
    public Response<List<Good>> getSellGoods() {
        if (StpUtil.isLogin()) {
            List<Good> sellGoods = goodService.getSellGoods();
            return Response.success(200, "获取出售商品成功", sellGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }

    //获取我已经购买的（购物车）
    @RequestMapping("/getBoughtGoods")
    public Response<List<Good>> getBoughtGoods() {
        if (StpUtil.isLogin()) {
            List<Good> boughtGoods = goodService.getBoughtGoods();
            return Response.success(200, "获取已购商品成功", boughtGoods);
        } else {
            return Response.failed(999, "用户未登录");
        }
    }
}
