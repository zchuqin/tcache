package tcache.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tcache.bean.User;
import tcache.config.CacheConfig;
import tcache.service.UserService;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserServiceImpl implements UserService {

    private static final AtomicInteger num = new AtomicInteger(1);

    @Override
    public User getUser() {
        return null;
    }

    @Override
    @Cacheable(value = {CacheConfig.CacheEntryTtl.CACHE_TEN_SECONDS,"redis_users"}, cacheManager = CacheConfig.CacheManagerNames.REDIS_CACHE_MANAGER, key = "#name")
    public User getUser(String name) {
        return new User(String.valueOf(num.addAndGet(1)), name, "PROTECT", null);
    }

    @Override
    @Cacheable(value = {"users"},cacheManager = CacheConfig.CacheManagerNames.EHCACHE_CACHE_MANAGER,key = "#id")
    public User getUser(int id) {
        return new User(String.valueOf(num.addAndGet(1)), "id:[" + id + "]", "PROTECT", null);
    }
}
