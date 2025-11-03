package dev.szhuima.agent.trigger.http.admin;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.szhuima.agent.api.dto.PageDTO;

import java.util.List;

/**
 * * @Author: szhuima
 * * @Date    2025/10/12 15:34
 * * @Description
 **/
public class BaseController {

    public <T> PageDTO<T> convertPage(IPage<T> page) {
        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setSize(page.getSize());
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(page.getRecords());
        return pageDTO;
    }

    public <T> PageDTO<T> copyPage(IPage page, Class<T> targetType) {
        List<Object> records = page.getRecords();
        List<T> targetList = BeanUtil.copyToList(records, targetType);

        return PageDTO.<T>builder()
                .total(page.getTotal())
                .size(page.getSize())
                .current(page.getCurrent())
                .records(targetList)
                .build();
    }

}
