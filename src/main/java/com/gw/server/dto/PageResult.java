package com.gw.server.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("分页结果")
public class PageResult<T> {

    @ApiModelProperty(value = "数据列表")
    private List<T> records;

    @ApiModelProperty(value = "总条数", example = "100")
    private long total;

    @ApiModelProperty(value = "当前页码", example = "1")
    private long page;

    @ApiModelProperty(value = "每页条数", example = "10")
    private long size;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(page.getRecords());
        result.setTotal(page.getTotal());
        result.setPage(page.getCurrent());
        result.setSize(page.getSize());
        return result;
    }
}
