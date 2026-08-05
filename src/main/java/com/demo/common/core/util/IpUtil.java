package com.demo.common.core.util;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class IpUtil {

    static final String DOCKER_IP_PREFIX = "172.17";
    static final String VIRTUAL_IP_ENDING = ".1";
    static final String VIRTUAL_IP_START = "10.244";

    // 标准IPv4地址的正则表达式：
    private static final Pattern IPV4_REGEX = Pattern
            .compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    // 无全0块，标准IPv6地址的正则表达式
    private static final Pattern IPV6_STD_REGEX = Pattern.compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    // 非边界压缩正则表达式
    private static final Pattern IPV6_COMPRESS_REGEX = Pattern
            .compile("^((?:[0-9A-Fa-f]{1,4}(:[0-9A-Fa-f]{1,4})*)?)::((?:([0-9A-Fa-f]{1,4}:)*[0-9A-Fa-f]{1,4})?)$");

    // 边界压缩情况正则表达式
    private static final Pattern IPV6_COMPRESS_REGEX_BORDER = Pattern.compile(
            "^(::(?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5})|((?:[0-9A-Fa-f]{1,4})(?::[0-9A-Fa-f]{1,4}){5}::)$");

    public static boolean isIP(final String input) {
        return isIPv4Address(input) || isIPv6Address(input);
    }

    // 判断是否为合法IPv4地址
    public static boolean isIPv4Address(final String input) {
        return IPV4_REGEX.matcher(input).matches();
    }

    // 判断是否为合法IPv6地址
    public static boolean isIPv6Address(final String input) {
        int NUM = 0;
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == ':')
                NUM++;
        }
        // 合法IPv6地址中不可能有多余7个的冒号(:)
        if (NUM > 7)
            return false;
        if (IPV6_STD_REGEX.matcher(input).matches()) {
            return true;
        }
        // 冒号(:)数量等于7有两种情况：无压缩、边界压缩，所以需要特别进行判断
        if (NUM == 7) {
            return IPV6_COMPRESS_REGEX_BORDER.matcher(input).matches();
        }
        // 冒号(:)数量小于七，使用于飞边界压缩的情况
        else {
            return IPV6_COMPRESS_REGEX.matcher(input).matches();
        }
    }

    /**
     * ip 格式化， 将 . 点 替换为 _ 下划线
     *
     * @param ip
     * @return ip 格式化， 将 . 点 替换为 _ 下划线
     */
    public static String formatedIp(String ip) {
        return ip.replaceAll("/.", "-");
    }


    public static String getRequestIp(HttpServletRequest request) {
        // 专门针对 Node 服务端渲染，处理请求从node服务器端传递过来的请求 与前端约定了访客真实ip的请求头
		/* String nodeIp = request.getHeader("Node-Real-IP");
		if (!isInnerIp(nodeIp) && StringUtils.hasText(nodeIp) && !"unknown".equalsIgnoreCase(nodeIp)) {
			return nodeIp;
		}*/
        // 一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是
        // CF-Connecting-IP
        String cfIp = request.getHeader("cf-connecting-ip");
        if (!isInnerIp(cfIp) && hasText(cfIp) && !"unknown".equalsIgnoreCase(cfIp)) {
            return cfIp;
        }
        // 一些 CDN 会在 CDN 回源请求加 HTTP 头信息，其中可能包含访客的“真实 IP ”。具体要看是哪家 CDN ，比如 Cloudflare 是
        // CF-Connecting-IP

        String srcIp = request.getHeader("X-Real-IP");
        if (!isInnerIp(srcIp) && hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("x-forwarded-for");
        if (hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            String ips[] = srcIp.split(",");
            for (String ip : ips) {
                if (!isInnerIp(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }

        }
        srcIp = request.getHeader("Proxy-Client-IP");
        if (!isInnerIp(srcIp) && hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("WL-Proxy-Client-IP");
        if (!isInnerIp(srcIp) && hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("HTTP_CLIENT_IP");
        if (!isInnerIp(srcIp) && hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }

        srcIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (!isInnerIp(srcIp) && hasText(srcIp) && !"unknown".equalsIgnoreCase(srcIp)) {
            return srcIp;
        }
        srcIp = request.getRemoteAddr();
        if ("127.0.0.1".equals(srcIp) || "0:0:0:0:0:0:0:1".equals(srcIp)) {
            // 根据网卡取本机配置的IP
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getLocalHost();
                srcIp = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                System.err.println("读取本机IP地址失败。");
            }
        }
        return srcIp;

    }


    private static boolean hasText(CharSequence str) {
        return (str != null && containsText(str));
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0,
     * and it contains at least one non-whitespace character.
     *
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its
     * length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     */
    private static boolean hasText(String str) {
        return (str != null && containsText(str));
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }


    public static boolean isInnerIp(String ip) {
        return "127.0.0.1".equals(ip);
    }

    private static String hostAddress = null;

    /**
     * @return
     * @description 获取本地主机地址 add by luoshan
     */
    public static String getHostAddress() {
        if (hostAddress != null) {
            return hostAddress;
        }

        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces
                    .hasMoreElements(); ) {
                NetworkInterface iface = ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.getHostAddress().startsWith(DOCKER_IP_PREFIX)) {// 排除docker 的地址
                            //logger.warn("IGNORE :{}", inetAddr);
                            continue;
                        }
                        if (inetAddr.getHostAddress().endsWith(VIRTUAL_IP_ENDING)) {// 排除 虚拟地址
                            //logger.warn("IGNORE :{}", inetAddr);
                            continue;
                        }
                        if (inetAddr.getHostAddress().startsWith(VIRTUAL_IP_START)) {// 排除 虚拟地址
                            //logger.warn("IGNORE :{}", inetAddr);
                            continue;
                        }
                        if (inetAddr.isSiteLocalAddress()) {
                            // logger.debug("IpUtil SUCCESS find SiteLocalAddress："
                            // +inetAddr.getHostAddress());
                            return inetAddr.getHostAddress();
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            // logger.debug("IpUtil find candidateAddress：" +inetAddr.getHostAddress());
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null && !candidateAddress.getHostAddress().contains(":")) {
                hostAddress = candidateAddress.getHostAddress();
                return hostAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            hostAddress = jdkSuppliedAddress.getHostAddress();
            return hostAddress;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cloudflare ：根据http请求获取来源国家代码简码： 例如中国CN
     *
     * @param request
     * @return 如果不是Cloudflare的环境，返回null
     */
    public static String getCloudFlareCountry(HttpServletRequest request) {
        return request.getHeader("cf-ipcountry");
    }

    //注意哪个域名开启了，就有时区。值参考:  Asia/Hong_Kong
    private String getTimezoneFromHead(HttpServletRequest request) {
        return request.getHeader("cf-timezone");
    }


    /**
     * 从ip的字符串形式得到字节数组形式
     *
     * @param ip 字符串形式的ip
     * @return 字节数组形式的ip
     */
    public static byte[] getIpByteArrayFromString(String ip) {
        byte[] ret = new byte[4];
        StringTokenizer st = new StringTokenizer(ip, ".");
        try {
            ret[0] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[1] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[2] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
            ret[3] = (byte) (Integer.parseInt(st.nextToken()) & 0xFF);
        } catch (Exception e) {
            e.printStackTrace();
            // LogFactory.log("从ip的字符串形式得到字节数组形式报错", Level.ERROR, e);
        }
        return ret;
    }

    /**
     * @param ip ip的字节数组形式
     * @return 字符串形式的ip
     */
    public static String getIpStringFromBytes(byte[] ip) {
        StringBuilder sb = new StringBuilder();
        sb.append(ip[0] & 0xFF);
        sb.append('.');
        sb.append(ip[1] & 0xFF);
        sb.append('.');
        sb.append(ip[2] & 0xFF);
        sb.append('.');
        sb.append(ip[3] & 0xFF);
        return sb.toString();
    }

    /**
     * 根据某种编码方式将字节数组转换成字符串
     *
     * @param b        字节数组
     * @param offset   要转换的起始位置
     * @param len      要转换的长度
     * @param encoding 编码方式
     * @return 如果encoding不支持，返回一个缺省编码的字符串
     */
    public static String getString(byte[] b, int offset, int len, String encoding) {
        try {
            return new String(b, offset, len, encoding);
        } catch (UnsupportedEncodingException e) {
            return new String(b, offset, len);
        }
    }
}
