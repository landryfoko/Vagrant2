package com.libertas.vipaas.common.configs;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@EnableAutoConfiguration
public class CacheConfig {

	@Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
        		new ConcurrentMapCache("devices"), new ConcurrentMapCache("bookmarks"), new ConcurrentMapCache("entitlements"), new ConcurrentMapCache("playbacks"), new ConcurrentMapCache("promotions"), new ConcurrentMapCache("ratings"), new ConcurrentMapCache("watch"), new ConcurrentMapCache("recommendations"), new ConcurrentMapCache("genres"), new ConcurrentMapCache("reviews"), new ConcurrentMapCache("purchases"), new ConcurrentMapCache("offers"), new ConcurrentMapCache("products"), new ConcurrentMapCache("devices"), new ConcurrentMapCache("customers"), new ConcurrentMapCache("tenants"), new ConcurrentMapCache("creditcards"), new ConcurrentMapCache("subscriptions")
        	));
        return cacheManager;
    }
}
