package cn.jiangzeyin.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static cn.jiangzeyin.cache.ObjectCache.DEFAULT_CACHE_TIME;

/**
 * 缓存信息实体
 *
 * @author jiangzeyin
 * @see 2017/12/12.
 */
class CacheInfo {
    private final String key;
    /**
     * 缓存时间 单位秒
     */
    private long cacheTime;

    CacheInfo(String key, long cacheTime) {
        this.key = key;
        this.cacheTime = cacheTime;
    }

    void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    String getKey() {
        return key;
    }

    long getCacheTime() {
        return cacheTime;
    }


    static Map<String, CacheInfo> loadClass(Class cls) throws IllegalAccessException {
        if (cls == null) {
            throw new NullPointerException();
        }
        Map<String, CacheInfo> map = new HashMap<>();
        CacheConfig cacheConfig = (CacheConfig) cls.getAnnotation(CacheConfig.class);
        Field[] fields = cls.getFields();
        for (Field field : fields) {
            if (field.getType() != String.class) {
                continue;
            }
            if (!Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            if (!Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            CacheConfigField cacheConfigField = field.getAnnotation(CacheConfigField.class);
            String key = (String) field.get(null);
            if (cacheConfigField == null) {
                // 秒
                long cacheTime = cacheConfig != null ? cacheConfig.UNIT().toSeconds(cacheConfig.value()) : DEFAULT_CACHE_TIME;
                CacheInfo cacheInfo = new CacheInfo(key, cacheTime);
                map.put(key, cacheInfo);
            } else {
                // 秒
                CacheInfo cacheInfo = new CacheInfo(key, cacheConfigField.UNIT().toSeconds(cacheConfigField.value()));
                map.put(key, cacheInfo);
            }
        }
        return map;
    }
}
