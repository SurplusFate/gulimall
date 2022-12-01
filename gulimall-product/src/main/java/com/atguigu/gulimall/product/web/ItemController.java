package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.SkuInfoService;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;

@Controller
public class ItemController {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情
     *
     * @param skuId
     * @return
     */
    // @GetMapping("/{skuId}.html")
    // public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
    //     System.out.println("准备查询" + skuId + "详情");
    //     SkuItemVo vos = skuInfoService.item(skuId);
    //     model.addAttribute("item", vos);
    //     return "item";
    // }
}
