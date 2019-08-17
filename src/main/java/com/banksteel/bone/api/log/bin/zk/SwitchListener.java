package com.banksteel.bone.api.log.bin.zk;

import com.banksteel.bone.api.log.bin.util.SwitchHelper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * 全链路跟踪的开关监听器
 *
 * @author 杨新伦
 * @date 2018-04-17
 */
@Component("boneApiLogSwitchListener")
@ConditionalOnProperty(name = "zk.url")
public class SwitchListener implements FactoryBean<NodeCache> {

    private static final Logger appLog = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    @Resource(name = "curatorFramework")
    private CuratorFramework curatorClient;
    @Autowired
    private SwitchHelper switchHelper;
    /**
     * 应用所在环境
     */
    @Value("${env:dev}")
    private String env;

    private NodeCache nodeCache;

    @PostConstruct
    public void postConstruct() {
        String path = "/config/apilog/" + env + "/switches";
        try {
            Stat stat = curatorClient.checkExists().forPath(path);
            if (stat != null) {
                switchHelper.processData(curatorClient.getData().forPath(path));
            }
        } catch (Exception e) {
            appLog.error("ZK初始化数据失败", e);
        }
        try {
            nodeCache = new NodeCache(curatorClient, path, false);
            nodeCache.start(true);
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    switchHelper.processData(nodeCache.getCurrentData().getData());
                }
            });
        } catch (Exception e) {
            appLog.error("ZK注册节点失败", e);
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        nodeCache.close();
    }

    @Override
    public NodeCache getObject() throws Exception {
        return nodeCache;
    }

    @Override
    public Class<?> getObjectType() {
        return NodeCache.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
