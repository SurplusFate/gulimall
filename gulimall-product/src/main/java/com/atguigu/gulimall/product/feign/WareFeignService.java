package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("ware")
public interface WareFeignService {
    // 库存
    @PostMapping("/ware/waresku/hasstock")
    R getSkuHasStock(List<Long> skuIdList);
}
