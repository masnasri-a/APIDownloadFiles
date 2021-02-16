package com.index.isa.dependency.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("es")
public class ElasticConfig {
	private String host;
	private int port;
	private String index_news;
}
