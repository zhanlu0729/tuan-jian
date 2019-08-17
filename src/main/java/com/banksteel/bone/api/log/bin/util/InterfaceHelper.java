package com.banksteel.bone.api.log.bin.util;

import com.alibaba.fastjson.JSON;
import com.banksteel.bone.api.log.bin.constant.RequestStateEnum;
import com.banksteel.bone.api.log.bin.model.InterfaceVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Closeable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 接口的工具类
 *
 * @author 杨新伦
 * @date 2018-11-05
 */
@Component("boneApiLogInterfaceHelper")
public class InterfaceHelper {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        }
    };

    /**
     * 设置接口名
     *
     * @param entity        接口值对象
     * @param interfaceName 接口名
     */
    public void setInterfaceName(InterfaceVo entity, String interfaceName) {
        if (entity == null || !StringUtils.hasText(interfaceName)) {
            return;
        }
        if (interfaceName.contains(" ")) {
            entity.setInterfaceName(interfaceName.split(" ")[1]);
        } else if (interfaceName.contains("(")) {
            entity.setInterfaceName(interfaceName);
        } else {
            entity.setInterfaceName(interfaceName + "()");
        }
    }

    /**
     * 设置接口参数
     *
     * @param entity 接口值对象
     * @param args   参数列表
     */
    public void setInterfaceParams(boolean paramSwitch, InterfaceVo entity, Object[] args) {
        if (entity == null || !paramSwitch) {
            return;
        }
        entity.setParamValues(this.getArgsValueAsString(args));
    }

    /**
     * 设置接口结果信息
     *
     * @param entity 接口值对象
     * @param result 结果信息
     */
    public void setInterfaceResult(boolean resultSwitch, ProceedingJoinPoint joinPoint, InterfaceVo entity, Object result) {
        if (entity == null) {
            return;
        }
        entity.setState(RequestStateEnum.OK.getStatusCode());
        if (!resultSwitch) {
            return;
        }
        if (!checkParam(result)) {
            entity.setReturnValue("-");
            return;
        }
        try {
            String[] methodArr = joinPoint.getSignature().toString().split(" ");
            if (methodArr[1].equals("void")) {
                entity.setReturnValue("void");
            } else if (result == null) {
                entity.setReturnValue("null");
            } else if (result instanceof String) {
                entity.setReturnValue((String) result);
            } else {
                entity.setReturnValue(JSON.toJSONString(result));
            }
        } catch (Exception e) {
            appLog.warn("返回值异常", e);
            entity.setRemark(e.getMessage());
        }
    }

    /**
     * 设置接口结果异常信息
     *
     * @param entity    接口值对象
     * @param throwable 结果异常信息
     */
    public void setInterfaceException(boolean resultSwitch, InterfaceVo entity, Throwable throwable) {
        if (entity == null) {
            return;
        }
        entity.setState(RequestStateEnum.ERROR.getStatusCode());
        if (!resultSwitch) {
            return;
        }
        entity.setRemark(throwable.getMessage());
    }

    /**
     * 获取参数类型列表
     *
     * @param args
     * @return
     */
    public String getArgTypesAsString(Class[] args) {
        if (args == null || args.length == 0) {
            return "()";
        }
        StringBuilder argBuf = new StringBuilder("(");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                argBuf.append(",");
            }
            argBuf.append(args[i] == null ? "null" : args[i].getSimpleName());
        }
        argBuf.append(")");
        return argBuf.toString();
    }

    /**
     * 获取参数值列表
     *
     * @param args
     * @return
     */
    public String getArgsValueAsString(Object[] args) {
        if (args == null || args.length == 0) {
            return "()";
        }
        StringBuilder paramValues = new StringBuilder("(");
        String tmpVal = null;
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                paramValues.append(",");
            }
            if (!checkParam(args[i])) {
                paramValues.append("-");
                continue;
            }
            if (args[i] == null) {
                tmpVal = "null";
            } else if (args[i] instanceof String) {
                tmpVal = args[i].toString().trim();
            } else {
                tmpVal = JSON.toJSONString(args[i]);
            }
            paramValues.append(tmpVal.isEmpty() ? "--" : tmpVal);
        }
        paramValues.append(")");
        return paramValues.toString();
    }

    /**
     * 格式化接口值对象
     *
     * @param entity 接口值对象
     * @return
     */
    public String getLogString(InterfaceVo entity) {
        entity.setEndTime(new Date());
        entity.setCost(entity.getEndTime().getTime() - entity.getStartTime().getTime());
        StringBuilder buf = new StringBuilder("apiLog|" + entity.getClusterCode() + "|" + entity.getDeptCode() + "|" + entity.getEndpointType() + "|");
        buf.append(entity.getInterfaceType() + "|" + entity.getInterfaceName() + "|" + DATE_FORMAT.get().format(entity.getStartTime()).replace(" ", "T") + "|");
        buf.append(DATE_FORMAT.get().format(entity.getEndTime()).replace(" ", "T") + "|" + entity.getCost() + "|" + entity.getState() + "|" + getAsString(entity.getUserId()) + "|");
        buf.append(getAsString(entity.getAccount()) + "|" + JSON.toJSONString(entity));
        return buf.toString();
    }

    private String getAsString(String str) {
        return StringUtils.hasText(str) ? str : "-";
    }

    private boolean checkParam(Object arg) {
        if (arg != null && !(arg instanceof String)) {
            String clazzName = arg.getClass().getCanonicalName();
            if ((arg instanceof Closeable) || clazzName.startsWith("org.") || clazzName.startsWith("javax.")) {
                return false;
            }
        }
        return true;
    }
}
