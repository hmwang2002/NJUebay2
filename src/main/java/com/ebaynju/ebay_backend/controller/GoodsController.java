package com.ebaynju.ebay_backend.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ebaynju.ebay_backend.pojo.Goods;
import com.ebaynju.ebay_backend.service.Impl.GoodsServiceImpl;
import com.ebaynju.ebay_backend.util.ImageUtil;
import com.ebaynju.ebay_backend.util.JsonUtil;

import io.jsonwebtoken.io.IOException;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsServiceImpl goodsService;

    @RequestMapping(value = "/uploadImg", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    @CrossOrigin
    public String uploadImg(MultipartFile multipartFile, HttpServletRequest httpServletRequest, int timeStamp)
            throws IOException, java.io.IOException {
        return ImageUtil.imgUpload(multipartFile, httpServletRequest, timeStamp);
    }

    @RequestMapping(value = "/deleteImg", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    @CrossOrigin
    public Map<String, Object> deleteImg(String img) {

        File f = new File(img);
        Map<String, Object> mymap = new HashMap<>();
        try {
            Files.delete(f.toPath());
        } catch (NoSuchFileException e) {
            mymap.put("msg", "fail deleted");
            return mymap;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        mymap.put("msg", "success deleted");
        return mymap;
    }

    @RequestMapping(value = "/addGoods", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    @CrossOrigin
    public String AddGoods(@RequestParam("goodsName") String goodsName, @RequestParam("description") String description,
            @RequestParam("seller") String seller,
            @RequestParam("sellerEmail") String sellerEmail, @RequestParam("img") String img,
            @RequestParam("price") float price) throws JSONException {
        Goods goods = new Goods();

        goods.setGoodsName(goodsName);
        goods.setDescription(description);
        goods.setSeller(seller);
        goods.setSellerEmail(sellerEmail);
        goods.setSellerEmail(sellerEmail);
        goods.setImg(img);
        goods.setPrice(price);
        goods.setOnSale(true);
        goods.setBuyer(null);

        Map<String, Object> map = goodsService.addGoods(goods);
        return JsonUtil.getJSONString(0, map);
    }

    @RequestMapping(value = "/buyGoods", method = { RequestMethod.GET, RequestMethod.POST })
    @ResponseBody
    @CrossOrigin
    public String buyGoods(@RequestParam("goodsId") int goodsId, @RequestParam("buyer") String buyer)
            throws JSONException {
        return JsonUtil.getJSONString(0, goodsService.buyGoods(goodsId, buyer));
    }

    @RequestMapping(value = "/updateGoodsOnSale", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @CrossOrigin
    public String updateGoodsOnSale() throws JSONException {
        List<Goods> goodsSet = goodsService.updateGoodsOnSale();
        List<Map<String, Object>> goodsStr = new LinkedList<Map<String, Object>>();
        for (int i = 0; i < goodsSet.size(); i++) {
            Map<String, Object> newMap = new HashMap<String, Object>();
            Goods tempGoods = goodsSet.get(i);

            if (tempGoods.isOnSale()) {
                newMap.put("goodsId", String.valueOf(tempGoods.getGoodsId()));
                newMap.put("goodsName", tempGoods.getGoodsName());
                newMap.put("description", tempGoods.getDescription());
                newMap.put("img", tempGoods.getImg());
                newMap.put("seller", tempGoods.getSeller());
                newMap.put("sellerEmail", tempGoods.getSellerEmail());
                newMap.put("onSale", String.valueOf(tempGoods.isOnSale()));
                newMap.put("buyer", tempGoods.getBuyer());
                newMap.put("price", String.valueOf(tempGoods.getPrice()));
            }
            goodsStr.add(newMap);
        }

        return JsonUtil.getJSONString(0, goodsStr);
    }


    @RequestMapping(value = "/getBoughtGoods", method = { RequestMethod.POST, RequestMethod.GET })
    @ResponseBody
    @CrossOrigin
    public String getBoughtGoods(String buyer) throws JSONException {
        List<Goods> goodsSet = goodsService.getBoughtGoods(buyer);
        List<Map<String, Object>> goodsStr = new LinkedList<Map<String, Object>>();
        for (int i = 0; i < goodsSet.size(); i++) {
            Map<String, Object> newMap = new HashMap<String, Object>();
            Goods tempGoods = goodsSet.get(i);
            newMap.put("goodsId", String.valueOf(tempGoods.getGoodsId()));
            newMap.put("goodsName", tempGoods.getGoodsName());
            newMap.put("description", tempGoods.getDescription());
            newMap.put("img", tempGoods.getImg());
            newMap.put("seller", tempGoods.getSeller());
            newMap.put("sellerEmail", tempGoods.getSellerEmail());
            newMap.put("onSale", String.valueOf(tempGoods.isOnSale()));
            newMap.put("buyer", tempGoods.getBuyer());
            newMap.put("price", String.valueOf(tempGoods.getPrice()));

            goodsStr.add(newMap);
        }
        return JsonUtil.getJSONString(0, goodsStr);
    }
}
