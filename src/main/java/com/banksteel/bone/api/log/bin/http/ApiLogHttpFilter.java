package com.banksteel.bone.api.log.bin.http;

import com.banksteel.bone.api.log.bin.constant.EndpointTypeEnum;
import com.banksteel.bone.api.log.bin.constant.InterfaceTypeEnum;
import com.banksteel.bone.api.log.bin.constant.RequestStateEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import com.banksteel.bone.api.log.bin.properties.ApiLogProperties;
import com.banksteel.bone.api.log.bin.util.InterfaceHelper;
import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Objects;

/**
 * HTTP请求的过滤器
 *
 * @author 杨新伦
 * @date 2018-12-11
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
@Component("boneApiLogHttpFilter")
@ConditionalOnWebApplication
public class ApiLogHttpFilter implements Filter {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Autowired
    private ApiLogProperties apiLogProperties;
    @Autowired
    private SwitchHelper switchHelper;
    @Autowired
    private InterfaceHelper interfaceHelper;

    @Override
    public void init(FilterConfig filterConfig) {
        appLog.info("-------init------ApiLogHttpFilter-------");
    }

    @SuppressWarnings("squid:S1181")
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String uri = null;
        InterfaceVo entity = null;
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            uri = req.getRequestURI();
            if (checkStaticResource(uri) && apiLogProperties != null && switchHelper != null && switchHelper.checkFilter()) {
                entity = new InterfaceVo(apiLogProperties);
                entity.setEndpointType(StringUtils.hasText(req.getHeader("X-Bone-traceId")) ? EndpointTypeEnum.SERVER.name() : EndpointTypeEnum.FILTER.name());
                entity.setInterfaceType(InterfaceTypeEnum.HTTP.name());
                entity.setInterfaceName(decodeWithUTF8(uri));
                entity.setParamValues(req.getQueryString());
                this.setUserInfos(req, entity);
            }
        } catch (Throwable e) {
            appLog.error("接口日志采集：Filter调用源头异常：" + uri, e);
        }

        try {
            chain.doFilter(request, response);
            this.postInterface(entity);
        } catch (IOException e1) {
            this.postInterfaceException(entity, e1);
            throw e1;
        } catch (ServletException e2) {
            this.postInterfaceException(entity, e2);
            throw e2;
        }
    }

    @Override
    public void destroy() {
        appLog.info("-------destroy------ApiLogHttpFilter-------");
    }

    @SuppressWarnings("squid:S1181")
    private void postInterface(InterfaceVo entity) {
        try {
            if (entity != null) {
                entity.setState(RequestStateEnum.OK.getStatusCode());
                log.info(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e) {
            appLog.warn("ApiLogHttpFilter.postInterface处理异常", e);
        }
    }

    @SuppressWarnings("squid:S1181")
    private void postInterfaceException(InterfaceVo entity, Exception e) {
        try {
            if (entity != null) {
                interfaceHelper.setInterfaceException(switchHelper.checkFilter(), entity, e);
                entity.setStackTrace(switchHelper.checkFilterStackTrace() ? ExceptionUtils.getFullStackTrace(e) : null);
                log.error(interfaceHelper.getLogString(entity));
            }
        } catch (Throwable e1) {
            appLog.warn("ApiLogHttpFilter.postInterfaceException处理异常", e1);
        }
    }

    private String decodeWithUTF8(String str) {
        try {
            if (str != null && str.contains("%")) {
                return URLDecoder.decode(str, "UTF-8");
            }
        } catch (Exception e) {
            appLog.warn("转码失败：" + str, e);
        }
        return str;
    }

    private void setUserInfos(HttpServletRequest request, InterfaceVo entity) {
        if (request == null) {
            return;
        }
        Cookie[] cookies = request.getCookies();
        if (isEmpty(cookies)) {
            return;
        }
        for (Cookie tmp : cookies) {
            this.setUserInfo(tmp, entity);
        }
    }

    private boolean isEmpty(Cookie[] cookies) {
        return (cookies == null || cookies.length == 0);
    }

    private void setUserInfo(Cookie tmp, InterfaceVo entity) {
        if (Objects.equals(apiLogProperties.getUserId(), tmp.getName())) {
            entity.setUserId(tmp.getValue());
        } else if (Objects.equals(apiLogProperties.getAccount(), tmp.getName())) {
            entity.setAccount(tmp.getValue());
        }
    }

    private boolean checkStaticResource(String uri) {
        return uri != null && (!(uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".png") || uri.endsWith(".jpg") || uri.endsWith(".gif") || uri.endsWith(".woff2") ||
                uri.endsWith(".svg") || uri.endsWith(".woff") || uri.endsWith(".tiff") || uri.endsWith(".ico") || uri.endsWith(".jsp") || uri.endsWith(".ftl") || uri.endsWith(".vm")));
    }

}
