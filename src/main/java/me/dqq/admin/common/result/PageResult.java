package me.dqq.admin.common.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页响应体
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 总条数 */
    private Long total;

    /** 当前页 */
    private Long current;

    /** 每页条数 */
    private Long size;

    /** 数据列表 */
    private List<T> records;

    /**
     * 从 MyBatis Plus Page 对象构建
     */
    public static <T> PageResult<T> of(Page<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setRecords(page.getRecords());
        return result;
    }

    /**
     * 自定义列表（转换后的VO列表）
     */
    public static <T, R> PageResult<R> of(Page<T> page, List<R> records) {
        PageResult<R> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setRecords(records);
        return result;
    }
}
