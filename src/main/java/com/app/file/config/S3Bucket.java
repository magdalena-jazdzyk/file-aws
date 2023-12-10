package com.app.file.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3.buckets")
public class S3Bucket {


//    Pole customer w klasie S3Bucket służy do określenia, jaki klient (lub jakie konto) jest powiązane z danym kubełkiem (bucket) w AWS S3
    private String customer;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }
}
