package com.hmall.search.controller;

import com.hmall.search.service.DataImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据导入控制器
 */
@Api(tags = "数据导入管理")
@RestController
@RequestMapping("/admin/import")
@RequiredArgsConstructor
public class DataImportController {

    private final DataImportService dataImportService;

    /**
     * 从MySQL导入商品数据到Elasticsearch
     */
    @ApiOperation("导入商品数据")
    @PostMapping("/items")
    public String importItems() {
        try {
            dataImportService.importItemsFromDatabase();
            return "数据导入成功";
        } catch (Exception e) {
            return "数据导入失败: " + e.getMessage();
        }
    }

    /**
     * 清空Elasticsearch数据
     */
    @ApiOperation("清空ES数据")
    @PostMapping("/clear")
    public String clearData() {
        try {
            dataImportService.clearElasticsearchData();
            return "数据清空成功";
        } catch (Exception e) {
            return "数据清空失败: " + e.getMessage();
        }
    }

    /**
     * 重新导入数据（先清空再导入）
     */
    @ApiOperation("重新导入数据")
    @PostMapping("/reload")
    public String reloadData() {
        try {
            dataImportService.clearElasticsearchData();
            Thread.sleep(2000); // 等待清空完成
            dataImportService.importItemsFromDatabase();
            return "数据重新导入成功";
        } catch (Exception e) {
            return "数据重新导入失败: " + e.getMessage();
        }
    }
}