package me.dqq.admin.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.dqq.admin.common.annotation.Log;
import me.dqq.admin.common.constant.SystemConst;
import me.dqq.admin.common.utils.IpUtil;
import me.dqq.admin.module.log.entity.SysLogOperate;
import me.dqq.admin.module.log.service.LogOperateService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志 AOP 切面
 * 拦截带有 @Log 注解的方法，异步记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final LogOperateService logOperateService;

    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, Log logAnnotation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            // 异步保存日志
            saveLog(joinPoint, logAnnotation, result, exception, duration);
        }
    }

    /**
     * 异步保存操作日志
     */
    @Async
    public void saveLog(ProceedingJoinPoint joinPoint, Log logAnnotation,
                        Object result, Exception exception, long duration) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;

            HttpServletRequest request = attributes.getRequest();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();

            SysLogOperate logOperate = new SysLogOperate();
            logOperate.setModule(logAnnotation.module());
            logOperate.setAction(logAnnotation.action());
            logOperate.setMethod(signature.getDeclaringTypeName() + "." + signature.getName());
            logOperate.setRequestUrl(request.getRequestURI());
            logOperate.setRequestMethod(request.getMethod());

            // 序列化请求参数（脱敏处理 password 字段）
            String params = serializeParams(joinPoint.getArgs());
            logOperate.setRequestParams(params);

            // 序列化响应结果（限制长度）
            if (result != null && exception == null) {
                String responseStr = JSONUtil.toJsonStr(result);
                if (responseStr.length() > SystemConst.MAX_RESPONSE_LENGTH) {
                    responseStr = responseStr.substring(0, SystemConst.MAX_RESPONSE_LENGTH) + "...";
                }
                logOperate.setResponseResult(responseStr);
                logOperate.setStatus(1);
            } else if (exception != null) {
                logOperate.setStatus(0);
                String errMsg = exception.getMessage();
                if (errMsg != null && errMsg.length() > 500) {
                    errMsg = errMsg.substring(0, 500);
                }
                logOperate.setErrorMsg(errMsg);
            }

            logOperate.setDuration(duration);
            logOperate.setCreateTime(LocalDateTime.now());

            // 获取操作人信息
            try {
                if (StpUtil.isLogin()) {
                    logOperate.setOperatorId(StpUtil.getLoginIdAsLong());
                    // 尝试从 session 获取用户名
                    Object username = StpUtil.getSession().get("username");
                    logOperate.setOperatorName(username != null ? username.toString() : String.valueOf(StpUtil.getLoginId()));
                }
            } catch (Exception e) {
                log.debug("获取操作人信息失败: {}", e.getMessage());
            }

            // 获取 IP 和归属地
            String ip = IpUtil.getRealIp(request);
            logOperate.setIp(ip);
            logOperate.setIpLocation(IpUtil.getIpLocation(ip));

            logOperateService.save(logOperate);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    /**
     * 序列化参数（脱敏 password 字段）
     */
    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "{}";
        }
        try {
            String json = JSONUtil.toJsonStr(args.length == 1 ? args[0] : Arrays.asList(args));
            // 脱敏密码字段
            return json.replaceAll("(?i)(\"password\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"")
                       .replaceAll("(?i)(\"oldPassword\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"")
                       .replaceAll("(?i)(\"newPassword\"\\s*:\\s*)\"[^\"]*\"", "$1\"***\"");
        } catch (Exception e) {
            return "{}";
        }
    }
}
