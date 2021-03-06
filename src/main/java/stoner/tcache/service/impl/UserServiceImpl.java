package stoner.tcache.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import stoner.tcache.Application;
import stoner.tcache.bean.User;
import stoner.tcache.config.CacheConfig;
import stoner.tcache.service.UserService;
import stoner.tcache.utils.ApplicationContextUtil;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserServiceImpl implements UserService {

    private static final AtomicInteger num = new AtomicInteger(1);

//    @Autowired
//    private ApplicationContextUtil applicationContextUtil;

    @Override
    public User getUser() {
        return null;
    }

    @Override
    @Cacheable(value = {CacheConfig.CacheEntryTtl.CACHE_TEN_SECONDS,"redis_users"}, cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, key = "#name")
    public User getUser(String name) {
//        ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
//        Object redisTemplate = applicationContext.getBeansOfType(RedisTemplate.class);
//        RequestContextHolder.currentRequestAttributes()

        return new User(String.valueOf(num.addAndGet(1)), name, "PROTECT", null);
    }

    @Override
    @Cacheable(value = {"users"},cacheManager = CacheConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER, key = "#id")
    public User getUser(int id) {
        return new User(String.valueOf(num.addAndGet(1)), "id:[" + id + "]", "PROTECT", null);
    }

    @Override
    @CachePut(value = {"redis_users"},cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, key = "#user.getUserName()")
    public User saveUser(User user) {
        return user;
    }

    @Override
    @CacheEvict(value = {"redis_users"}, cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, key = "#name")
    public boolean cleanUser(String name) {
        return true;
    }
}
