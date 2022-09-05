package com.example.demo.jwt;

import com.google.common.net.HttpHeaders;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "application.jwt")
public class JwtConfig {
    private String secretKey;
    private String tokenPrefix;
    private Integer tokenExpirationTokenAfterDays;

    public JwtConfig() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public Integer getTokenExpirationTokenAfterDays() {
        return tokenExpirationTokenAfterDays;
    }

    public void setTokenExpirationTokenAfterDays(Integer tokenExpirationTokenAfterDays) {
        this.tokenExpirationTokenAfterDays = tokenExpirationTokenAfterDays;
    }
    public String getAuthorizationHeader() {
        return HttpHeaders.AUTHORIZATION;
    }
}
