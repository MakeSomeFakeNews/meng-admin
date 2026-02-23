package me.dqq.admin.common.utils;

import cn.hutool.core.net.NetUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

/**
 * IP 工具类（获取真实IP + 离线解析归属地）
 */
@Slf4j
public class IpUtil {

    private static Searcher searcher;

    static {
        try {
            // 从 classpath 加载 ip2region.xdb
            ClassPathResource resource = new ClassPathResource("ip2region/ip2region.xdb");
            InputStream inputStream = resource.getInputStream();
            byte[] cBuff = inputStream.readAllBytes();
            inputStream.close();
            searcher = Searcher.newWithBuffer(cBuff);
            log.info("ip2region 离线库加载成功");
        } catch (Exception e) {
            log.warn("ip2region 离线库加载失败，IP归属地解析不可用: {}", e.getMessage());
        }
    }

    /**
     * 获取客户端真实 IP（处理代理场景）
     */
    public static String getRealIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            // X-Forwarded-For 可能包含多个IP，取第一个有效的
            String[] ips = ip.split(",");
            for (String part : ips) {
                String trimmed = part.trim();
                if (isValidIp(trimmed)) {
                    return trimmed;
                }
            }
        }

        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) return ip;

        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) return ip;

        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) return ip;

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (isValidIp(ip)) return ip;

        ip = request.getRemoteAddr();
        // 本地回环地址转换
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }
        return ip;
    }

    /**
     * 根据 IP 解析归属地（离线）
     */
    public static String getIpLocation(String ip) {
        if (ip == null || ip.isBlank()) {
            return "未知";
        }
        // 本地回环地址
        if ("127.0.0.1".equals(ip) || "::1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "本地访问";
        }
        // 内网IP
        if (NetUtil.isInnerIP(ip)) {
            return "内网IP";
        }
        // 使用 ip2region 离线解析
        if (searcher == null) {
            return "未知";
        }
        try {
            String region = searcher.search(ip);
            // 格式：国家|区域|省份|城市|ISP，如 "中国|0|广东省|深圳市|电信"
            // 去掉 0 占位符，拼接有意义的字段
            if (region == null || region.isBlank()) {
                return "未知";
            }
            String[] parts = region.split("\\|");
            StringBuilder location = new StringBuilder();
            for (String part : parts) {
                if (!"0".equals(part) && !part.isBlank()) {
                    if (!location.isEmpty()) location.append(" ");
                    location.append(part);
                }
            }
            return !location.isEmpty() ? location.toString() : "未知";
        } catch (Exception e) {
            log.error("IP归属地解析失败，ip={}: {}", ip, e.getMessage());
            return "未知";
        }
    }

    /**
     * 判断IP是否有效（非空、非unknown）
     */
    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isBlank() && !"unknown".equalsIgnoreCase(ip);
    }
}
