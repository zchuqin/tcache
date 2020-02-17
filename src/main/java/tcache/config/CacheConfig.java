package tcache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Configuration
public class CacheConfig extends CachingConfigurerSupport {
    public interface CacheManagerNames {
        String REDIS_CACHE_MANAGER = "redisCacheManager";
        String EHCACHE_CACHE_MANAGER = "ehCacheManager";
    }

    public interface CacheEntryTtl {
        String CACHE_ONE_MINUTE = "CACHE_ONE_MINUTE";
        String CACHE_TEN_SECONDS = "CACHE_TEN_SECONDS";
        String CACHE_ONE_HOUR = "CACHE_ONE_HOUR";
    }

//    @Bean
//    public KeyGenerator keyGenerator() {
//        return (target, method, params) -> {
//            StringBuilder sb = new StringBuilder();
//            sb.append(target.getClass().getName());
//            sb.append(method.getName());
//            for (Object obj : params) {
//                sb.append(obj.toString());
//            }
//            return sb.toString();
//        };
//    }

//    @Bean
//    public FastJsonRedisSerializer fastJsonRedisSerializer() {
//        return new FastJsonRedisSerializer<>(Object.class);
//    }


    @Bean(name = CacheManagerNames.REDIS_CACHE_MANAGER)
    @Primary
    //在这里配置缓存reids配置
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(30)); // 设置缓存默认有效期一小时
        System.out.println("======== redis配置启动 ========");
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(CacheEntryTtl.CACHE_ONE_MINUTE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(1)));
        configurationMap.put(CacheEntryTtl.CACHE_TEN_SECONDS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(10)));
        configurationMap.put(CacheEntryTtl.CACHE_ONE_HOUR, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1)));
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration).withInitialCacheConfigurations(configurationMap).build();
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public EhCacheManagerFactoryBean cacheManagerFactoryBean(){
        EhCacheManagerFactoryBean bean = new EhCacheManagerFactoryBean();
        bean.setConfigLocation(new ClassPathResource("ehcache.xml"));
        bean.setShared(true);
        return bean;
    }

    @Bean(name = CacheManagerNames.EHCACHE_CACHE_MANAGER)
    public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean bean){
        return new EhCacheCacheManager(Objects.requireNonNull(bean.getObject()));
    }

}
