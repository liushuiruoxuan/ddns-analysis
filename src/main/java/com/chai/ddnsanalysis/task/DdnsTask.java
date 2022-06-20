package com.chai.ddnsanalysis.task;

import com.chai.ddnsanalysis.client.DdnsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author yangxiaohui
 * @since 2022/4/15 10:28
 */
@Component
@EnableScheduling
public class DdnsTask {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final DdnsClient ddnsClient;

    public DdnsTask(DdnsClient ddnsClient) {
        this.ddnsClient = ddnsClient;
    }

    /**
     * 刷新阿里巴巴DNS解析记录
     */
    @Scheduled(fixedRate=300000)
    public void refreshDdns() throws Exception {
        ddnsClient.refreshDdns();
    }
}


