package com.banksteel.bone.api.log.bin.util;

import com.alibaba.fastjson.JSON;
import com.banksteel.bone.api.log.bin.constant.SwitchEnum;
import com.banksteel.bone.api.log.bin.model.SwitchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 开关的配置项
 *
 * @author 杨新伦
 * @date 2018-11-08
 */
@Slf4j
@Component("boneApiLogSwitchHelper")
public class SwitchHelper {

    /**
     * 应用英文名
     */
    @Value("${app.name:app}")
    private String appName;

    private SwitchVo switchVo;

    public boolean checkEnv() {
        return switchVo != null && Objects.equals(SwitchEnum.ON.name(), switchVo.getEnvState());
    }

    public boolean checkApp() {
        return checkEnv() && (CollectionUtils.isEmpty(switchVo.getAppSet()) || !switchVo.getAppSet().contains(appName));
    }

    public boolean checkHttpClient() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getHttpClient());
    }

    public boolean checkHttpStackTrace() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getHttpStackTrace());
    }

    public boolean checkFilter() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getFilter());
    }

    public boolean checkFilterStackTrace() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getFilterStackTrace());
    }

    public boolean checkRpc() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getRpc());
    }

    public boolean checkRpcParam() {
        return checkRpc() && Objects.equals(SwitchEnum.ON.name(), switchVo.getRpcParam());
    }

    public boolean checkRpcResult() {
        return checkRpc() && Objects.equals(SwitchEnum.ON.name(), switchVo.getRpcResult());
    }

    public boolean checkRpcStackTrace() {
        return checkRpc() && Objects.equals(SwitchEnum.ON.name(), switchVo.getRpcStackTrace());
    }

    public boolean checkWeb() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getWeb());
    }

    public boolean checkWebParam() {
        return checkWeb() && Objects.equals(SwitchEnum.ON.name(), switchVo.getWebParam());
    }

    public boolean checkWebResult() {
        return checkWeb() && Objects.equals(SwitchEnum.ON.name(), switchVo.getWebResult());
    }

    public boolean checkWebStackTrace() {
        return checkWeb() && Objects.equals(SwitchEnum.ON.name(), switchVo.getWebStackTrace());
    }

    public boolean checkService() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getService());
    }

    public boolean checkServiceParam() {
        return checkService() && Objects.equals(SwitchEnum.ON.name(), switchVo.getServiceParam());
    }

    public boolean checkServiceResult() {
        return checkService() && Objects.equals(SwitchEnum.ON.name(), switchVo.getServiceResult());
    }

    public boolean checkServiceStackTrace() {
        return checkService() && Objects.equals(SwitchEnum.ON.name(), switchVo.getServiceStackTrace());
    }

    public boolean checkDao() {
        return checkApp() && Objects.equals(SwitchEnum.ON.name(), switchVo.getDao());
    }

    public boolean checkDaoParam() {
        return checkDao() && Objects.equals(SwitchEnum.ON.name(), switchVo.getDaoParam());
    }

    public boolean checkDaoResult() {
        return checkDao() && Objects.equals(SwitchEnum.ON.name(), switchVo.getDaoResult());
    }

    public boolean checkDaoStackTrace() {
        return checkDao() && Objects.equals(SwitchEnum.ON.name(), switchVo.getDaoStackTrace());
    }

    public void processData(byte[] data) {
        if (data != null && data.length > 0) {
            try {
                String dataStr = new String(data, "UTF-8");
                SwitchVo switchTmp = JSON.parseObject(dataStr, SwitchVo.class);
                Set<String> appSet = new HashSet<>();
                String[] appArr = StringUtils.hasText(switchTmp.getApps()) ? switchTmp.getApps().split(",") : new String[]{"none"};
                for (String app : appArr) {
                    appSet.add(app);
                }
                switchTmp.setAppSet(appSet);
                this.setSwitchVo(switchTmp);
            } catch (UnsupportedEncodingException e) {
                log.error("数据转码失败", e);
            }
        }
        if (switchVo == null) {
            switchVo = new SwitchVo();
        }
    }

    public SwitchVo getSwitchVo() {
        return switchVo;
    }

    public void setSwitchVo(SwitchVo switchVo) {
        this.switchVo = switchVo;
    }
}
