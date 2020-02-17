package stoner.tcache.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.sun.org.glassfish.gmbal.Description;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import stoner.tcache.bean.User;
import stoner.tcache.common.Fast2JsonRedisSerializer;

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

    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append('.');
            sb.append(method.getName());
            sb.append('(');
            for (Object obj : params) {
                sb.append(obj.toString()).append(',');
            }
            if (params.length > 0) {
                sb.setCharAt(sb.length() - 1, ')');
            } else {
                sb.append(')');
            }
            return sb.toString();
        };
    }

    @Bean(name = CacheManagerNames.REDIS_CACHE_MANAGER)
    @Primary
    //在这里配置缓存reids配置
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, GenericFastJsonRedisSerializer genericFastJsonRedisSerializer) {
        RedisSerializationContext.SerializationPair<Object> objectSerializationPair =
                RedisSerializationContext.SerializationPair.fromSerializer(genericFastJsonRedisSerializer);
        RedisCacheConfiguration redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeValuesWith(objectSerializationPair)
                        .entryTtl(Duration.ofMinutes(30));
        System.out.println("======== redis配置启动 ========");
        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
        configurationMap.put(CacheEntryTtl.CACHE_ONE_MINUTE, RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(objectSerializationPair).entryTtl(Duration.ofMinutes(1)));
        configurationMap.put(CacheEntryTtl.CACHE_TEN_SECONDS, RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(objectSerializationPair).entryTtl(Duration.ofSeconds(10)));
        configurationMap.put(CacheEntryTtl.CACHE_ONE_HOUR, RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(objectSerializationPair).entryTtl(Duration.ofHours(1)));
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration).withInitialCacheConfigurations(configurationMap).build();
    }

//    @Bean
//    @SuppressWarnings("unchecked")
//    public Fast2JsonRedisSerializer<Object> fast2JsonRedisSerializer() {
//        //fastjson反序列化白名单
//        ParserConfig.getGlobalInstance().addAccept("stoner.tcache");
//        return new Fast2JsonRedisSerializer(Object.class);
//    }

    @Bean
    @SuppressWarnings("unchecked")
    public FastJsonRedisSerializer fastJsonRedisSerializer() {
        //这里必须写要反序列化的对象类型，否则反序列化会抛异常
        return new FastJsonRedisSerializer(User.class);
    }

    @Bean
    public GenericFastJsonRedisSerializer genericFastJsonRedisSerializer() {
        //不用写明类型，泛型
        return new GenericFastJsonRedisSerializer();
    }

//    @Bean
//    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory, Fast2JsonRedisSerializer fast2JsonRedisSerializer) {
//        StringRedisTemplate template = new StringRedisTemplate(redisConnectionFactory);
////        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
////        ObjectMapper om = new ObjectMapper();
////        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
////        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
////        jackson2JsonRedisSerializer.setObjectMapper(om);
////        ParserConfig.getGlobalInstance().addAccept("stoner.tcache");
//        template.setValueSerializer(fast2JsonRedisSerializer);
//        template.setHashValueSerializer(fast2JsonRedisSerializer);
//        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
//        template.setKeySerializer(stringRedisSerializer);
//        template.setHashKeySerializer(stringRedisSerializer);
//        template.afterPropertiesSet();
//        return template;
//    }

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
