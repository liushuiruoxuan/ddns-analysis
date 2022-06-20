package com.chai.ddnsanalysis.client;

import com.alibaba.fastjson.JSON;
import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.chai.ddnsanalysis.util.WebToolUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yangxiaohui
 * @since 2022/4/15 10:10
 */
@Component
public class DdnsClient {
    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Value("${ali.domain:cloudn}")
    private String domain = "cloudx";
    @Value("${ali.keyword:gitlab.cloudxn}")
    private String keyWord = "gitlab.cloudxn";
    @Value("${ali.access-key:LTAI5tAYRK6Y57B}")
    private String accessKey = "LTAI5tAYRK6f57B";
    @Value("${ali.access-key-secret:6lIJLuFnmP5c5693i}")
    private String accessSecret = "6lIJLuFnmP5Wc5693i";
    @Value("${ali.endpoint:dns.aliym}")
    private String endpoint = "dns.alim";

    private String currentDdnsIp="";

    private final Client client;

    public DdnsClient() throws Exception {
        this.client = new Client(new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKey)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessSecret)
                .setEndpoint(endpoint)
        );
        this.refreshDdns();
    }

    public void refreshDdns() throws Exception {
        String currentIp = this.getCurrentIp();
        if (!this.currentDdnsIp.equals(currentIp)){
            // 获取解析记录
            DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord domainDnsRecord = this.getDomainDnsRecord();
            // 刷新解析记录
            this.updateDomainDnsRecord(domainDnsRecord,currentIp);
        }
        logger.info("无需修改解析记录，当前IP：{}",currentIp);
    }

    private String getCurrentIp(){
        return WebToolUtils.getPublicIP();
    }

    private DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord getDomainDnsRecord() throws Exception {
        DescribeDomainRecordsRequest describeDomainRecordsRequest = new DescribeDomainRecordsRequest()
                .setDomainName(domain)
                .setKeyWord(keyWord);
        // 获取记录详情
        DescribeDomainRecordsResponse domainRecordsResponse = client.describeDomainRecords(describeDomainRecordsRequest);
        List<DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord> record = domainRecordsResponse.getBody().getDomainRecords().getRecord();
        DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord describeDomainRecordsResponseBodyDomainRecordsRecord = record.get(0);
        logger.info("获取解析记录：{}", JSON.toJSONString(describeDomainRecordsResponseBodyDomainRecordsRecord));
        return describeDomainRecordsResponseBodyDomainRecordsRecord;
    }
    private void updateDomainDnsRecord(@NotNull DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord domainDnsRecord, String newIp) throws Exception {
        // 修改记录
        UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                .setRecordId(domainDnsRecord.getRecordId())
                .setRR(domainDnsRecord.getRR())
                .setType(domainDnsRecord.getType())
                .setValue(newIp);
        // 修改记录
        UpdateDomainRecordResponse updateDomainRecordResponse = client.updateDomainRecord(updateDomainRecordRequest);
        logger.info("修改解析结果：{}",JSON.toJSONString(updateDomainRecordResponse));
    }
}

