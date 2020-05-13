/*
 * Copyright 2016-2020 the original author
 *
 * @D8GER(https://github.com/caofanCPU).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xyz.caofancpu.mvc.init;

import com.google.common.collect.Lists;
import com.xyz.caofancpu.constant.SymbolConstantUtil;
import com.xyz.caofancpu.core.CollectionUtil;
import com.xyz.caofancpu.property.SpringDefaultProperties;
import com.xyz.caofancpu.property.SwaggerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.Optional;

/**
 * 初始化执行器, 打印项目环境信息
 *
 * @author D8GER
 */
@Component
@Order(value = 1)
@Slf4j
public class InitCompletedRunner implements CommandLineRunner, ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;
    private String serverIp;

    @Resource
    private Optional<SwaggerProperties> swaggerProperties;

    @Resource
    private SpringDefaultProperties springDefaultProperties;

    @Override
    public void run(String... strings) {
        log.info("项目启动成功, IP=" + this.serverIp + ", Port=" + serverPort);
        if (swaggerProperties.isPresent() && swaggerProperties.get().isShowApi()) {
            log.info("SwaggerApi文档参见: http://" + this.serverIp + ":" + serverPort + springDefaultProperties.contentPath + "/doc.html?plus=1&cache=1&filterApi=1&filterApiType=POST&lang=zh");
            log.warn("请确保以下SwaggerApi访问路径未被登录|权限拦截: {}", CollectionUtil.join(Lists.newArrayList("/doc.html", "/swagger*/**", "/webjars/**", "/v2/api-docs-ext"), SymbolConstantUtil.NORMAL_ENGLISH_COMMA_DELIMITER));
        }
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent webServerInitializedEvent) {
        this.serverPort = webServerInitializedEvent.getWebServer().getPort();
        try {
            this.serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (Objects.isNull(this.serverIp)) {
                this.serverIp = "UnknownHost";
            }
        }
    }
}
