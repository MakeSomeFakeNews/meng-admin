package me.dqq.admin.common.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器
 */
@Slf4j
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间
        this.strictInsertFill(metaObject, "createTime", LocalDateTime::now, LocalDateTime.class);
        // 更新时间
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 创建人（未登录时不填充）
        try {
            if (StpUtil.isLogin()) {
                long loginId = StpUtil.getLoginIdAsLong();
                this.strictInsertFill(metaObject, "createBy", () -> loginId, Long.class);
                this.strictInsertFill(metaObject, "updateBy", () -> loginId, Long.class);
            }
        } catch (Exception e) {
            log.debug("自动填充创建人失败（未登录场景）: {}", e.getMessage());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime::now, LocalDateTime.class);
        // 更新人
        try {
            if (StpUtil.isLogin()) {
                long loginId = StpUtil.getLoginIdAsLong();
                this.strictUpdateFill(metaObject, "updateBy", () -> loginId, Long.class);
            }
        } catch (Exception e) {
            log.debug("自动填充更新人失败: {}", e.getMessage());
        }
    }
}
