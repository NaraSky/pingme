package com.lb.pingme.service;

import com.lb.pingme.common.exception.BusinessException;
import com.lb.pingme.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.util.MapUtils;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.util.SafeEncoder;

import java.util.concurrent.TimeUnit;
import java.util.*;

/**
 * Redis操作工具类
 * 提供字符串、集合、有序集合、哈希、列表等Redis数据结构的操作方法
 * 包含分布式锁、批量操作、管道操作等高级功能
 */
@Slf4j
@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // ==================== 字符串操作 ====================

    /**
     * 获取指定key的值
     * @param key 键名
     * @return 键对应的值，不存在则返回null
     */
    public String get(final String key) {
        String result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            result = operations.get(key);
        } catch (Exception e) {
            log.error("redis get error! key:{}", key, e);
        }
        return result;
    }

    /**
     * 批量获取多个key的值
     * @param keys 键名列表
     * @return 对应的值列表，顺序与keys一致
     */
    public List<String> mget(final List<String> keys) {
        List<String> result = new ArrayList<>();
        try {
            ValueOperations<String, String> operations = this.redisTemplate.opsForValue();
            return operations.multiGet(keys);
        } catch (Exception e) {
            log.error("redis get error! key:{}", JsonUtil.toJsonString(keys), e);
        }
        return result;
    }

    /**
     * 批量获取多个key的值并转换为Map
     * @param keys 键名列表
     * @return key-value对应的Map
     */
    public Map<String, String> mgetAndParseMap(final List<String> keys) {
        Map<String, String> resultMap = new HashMap<>(keys.size());
        try {
            List<String> result = this.mget(keys);
            for (int i = 0; i < keys.size(); i++) {
                resultMap.put(keys.get(i), result.get(i));
            }
            return resultMap;
        } catch (Exception e) {
            log.error("redis get error! key:{}", JsonUtil.toJsonString(keys), e);
        }
        return resultMap;
    }

    /**
     * 安装分布式锁（毫秒级过期时间）
     * @param key 锁的键名
     * @param requestId 请求标识ID，用于确保只能释放自己的锁
     * @param liveTime 锁存活时间（毫秒）
     * @return 成功获取锁返回true，否则返回false
     */
    public boolean installLockForMS(String key, String requestId, long liveTime) {
        if (org.springframework.util.StringUtils.isEmpty(key) || org.springframework.util.StringUtils.isEmpty(requestId)) {
            return false;
        }
        return setNxPx(key, requestId, liveTime);
    }

    /**
     * 设置键值，仅当键不存在时才设置（毫秒级过期）
     * @param key 键名
     * @param value 值
     * @param exptime 过期时间（毫秒）
     * @return 设置成功返回true，键已存在返回false
     */
    public boolean setNxPx(final String key, final String value, final long exptime) {
        Boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            Object obj = connection.execute("set", keySerializer.serialize(key),
                    valueSerializer.serialize(value),
                    SafeEncoder.encode("NX"),
                    SafeEncoder.encode("PX"),
                    Protocol.toByteArray(exptime));
            return obj != null && "OK".equals(new String((byte[]) obj));
        });
        return result;
    }

    /**
     * 设置键值，仅当键不存在时才设置（秒级过期）
     * @param key 键名
     * @param value 值
     * @param exptime 过期时间（秒）
     * @return 设置成功返回true，键已存在返回false
     */
    public boolean setNxEx(final String key, final String value, final long exptime) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
            return false;
        }
        Boolean result = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
            RedisSerializer keySerializer = redisTemplate.getKeySerializer();
            Object obj = connection.execute("set", keySerializer.serialize(key),
                    valueSerializer.serialize(value),
                    SafeEncoder.encode("NX"),
                    SafeEncoder.encode("EX"),
                    Protocol.toByteArray(exptime));
            return obj != null && "OK".equals(new String((byte[]) obj));
        });
        return result;
    }

    /**
     * 设置键值对，支持设置过期时间
     * @param key 键名
     * @param value 值
     * @param expireTime 过期时间（秒），小于等于0表示永不过期
     * @return 设置成功返回true，否则返回false
     */
    public boolean set(final String key, final String value, long expireTime) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            if (expireTime > 0) {
                operations.set(key, value, expireTime, TimeUnit.SECONDS);
            } else {
                operations.set(key, value);
            }
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, value, e);
        }
        return result;
    }

    /**
     * 设置键值对（永不过期）
     * @param key 键名
     * @param value 值
     * @return 设置成功返回true，否则返回false
     */
    public boolean set(final String key, String value) {
        boolean result = false;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            operations.set(key, value);
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, value);
        }
        return result;
    }

    // ==================== 集合(Set)操作 ====================

    /**
     * 向集合中添加元素
     * @param key 集合键名
     * @param value 要添加的元素（可变参数）
     * @return 添加成功返回true，否则返回false
     */
    public boolean sadd(String key, String... value) {
        boolean result = false;
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            set.add(key, value);
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, value, e);
        }
        return result;
    }

    /**
     * 向集合中添加多个元素
     * @param key 集合键名
     * @param setValue 要添加的元素集合
     */
    public void sadd(String key, Set<String> setValue) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            set.add(key, setValue.toArray(new String[0]));
        } catch (Exception e) {
            log.error("sadd error! key:{}", key, e);
        }
    }

    /**
     * 获取集合中的所有成员
     * @param key 集合键名
     * @return 集合中的所有元素
     */
    public Set<String> smembers(String key) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            return set.members(key);
        } catch (Exception e) {
            log.error("redis set error! key:{}", key, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取集合的大小
     * @param key 集合键名
     * @return 集合中元素的数量
     */
    public Long ssize(String key) {
        Long size = null;
        try {
            size = redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("redis ssize error! key:{}", key, e);
        }
        return size;
    }

    /**
     * 从集合中移除指定元素
     * @param key 集合键名
     * @param values 要移除的元素（可变参数）
     * @return 成功移除的元素数量
     */
    public Long sremove(String key, Object... values) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            return set.remove(key, values);
        } catch (Exception e) {
            log.error("redis set error! key:{}", key, e);
            return null;
        }
    }

    /**
     * 判断元素是否为集合的成员
     * @param key 集合键名
     * @param value 要检查的元素
     * @return 是成员返回true，否则返回false
     */
    public Boolean sIsMember(String key, String value) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            return set.isMember(key, value);
        } catch (Exception ex) {
            log.error("redis sIsMember error! key:{}", key, ex);
            return false;
        }
    }

    // ==================== 有序集合(ZSet)操作 ====================

    /**
     * 向有序集合中添加元素
     * @param key 有序集合键名
     * @param value 元素值
     * @param score 分数（用于排序）
     * @return 添加成功返回true，否则返回false
     */
    public boolean zadd(String key, String value, long score) {
        boolean result = false;
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            zset.add(key, value, score);
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, value, e);
        }
        return result;
    }

    /**
     * 检查有序集合中是否包含指定元素
     * @param key 有序集合键名
     * @param value 要检查的元素
     * @return 包含返回true，否则返回false
     */
    public boolean zContains(String key, String value) {
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            Long rank = zset.rank(key, value);
            return rank != null && rank >= 0;
        } catch (Exception e) {
            log.error("redis zContains error! key:{}, value:{}", key, value, e);
        }
        return false;
    }

    /**
     * 向有序集合中添加元素并设置过期时间
     * @param key 有序集合键名
     * @param value 元素值
     * @param score 分数
     * @param liveTime 过期时间（秒）
     * @return 添加成功返回true，否则返回false
     */
    public boolean zadd(String key, String value, long score, long liveTime) {
        boolean result = false;
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            zset.add(key, value, score);
            if (liveTime > 0) {
                expire(key, liveTime);
            }
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, value, e);
        }
        return result;
    }

    /**
     * 批量向有序集合中添加元素并设置过期时间
     * @param key 有序集合键名
     * @param tuples 元素-分数对集合
     * @param liveTime 过期时间（秒）
     * @return 添加成功返回true，否则返回false
     */
    public boolean zadd(String key, Set<ZSetOperations.TypedTuple<String>> tuples, long liveTime) {
        boolean result = false;
        try {
            ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
            zset.add(key, tuples);
            if (liveTime > 0) {
                expire(key, liveTime);
            }
            result = true;
        } catch (Exception e) {
            log.error("redis set error! key:{}, value:{}", key, tuples, e);
        }
        return result;
    }

    /**
     * 按排名范围获取有序集合中的元素（升序）
     * @param key 有序集合键名
     * @param start 开始位置
     * @param end 结束位置
     * @return 指定范围内的元素集合
     */
    public Set<String> zrange(String key, long start, long end) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Set<String> result = zset.range(key, start, end - 1L);
            return result;
        } catch (Exception e) {
            log.error("redis zrange error,key:{}", key, e);
        }
        return null;
    }

    /**
     * 获取元素在有序集合中的分数
     * @param key 有序集合键名
     * @param value 元素值
     * @return 元素的分数，不存在返回0
     */
    public Long zscore(String key, String value) {
        Double score = redisTemplate.opsForZSet().score(key, value);
        if (score == null) {
            return 0L;
        }
        return score.longValue();
    }

    /**
     * 按排名范围获取有序集合中的元素（倒序）
     * @param key 有序集合键名
     * @param start 开始位置
     * @param end 结束位置
     * @return 指定范围内的元素集合（分数从高到低）
     */
    public Set<String> zreverseRange(String key, long start, long end) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Set<String> result = zset.reverseRange(key, start, end - 1L);
            return result;
        } catch (Exception e) {
            log.error("redis zrange error,errorMessage:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 按分数范围获取有序集合中的元素（倒序，带分页）
     * @param key 有序集合键名
     * @param maxScore 最大分数
     * @param offset 偏移量
     * @param count 获取数量
     * @return 指定分数范围内的元素集合
     */
    public Set<String> zreverseRangeByScore(String key, double maxScore, long offset, long count) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Set<String> result = zset.reverseRangeByScore(key, 1 - Double.MAX_VALUE, maxScore, offset, count);
            return result;
        } catch (Exception e) {
            log.error("redis zrangeByScoreWithScores error,errorMessage:{}",
                    e.getMessage());
        }
        return null;
    }

    /**
     * 按分数范围获取有序集合中的元素（倒序，带分页）
     * @param key 有序集合键名
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @param offset 偏移量
     * @param count 获取数量
     * @return 指定分数范围内的元素集合
     */
    public Set<String> zreverseRangeByScore(String key, double minScore, double maxScore, long offset, long count) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Set<String> result = zset.reverseRangeByScore(key, minScore, maxScore, offset, count);
            return result;
        } catch (Exception e) {
            log.error("redis zrangeByScoreWithScores error,errorMessage:{}",
                    e.getMessage());
        }
        return null;
    }

    /**
     * 获取有序集合的大小
     * @param key 有序集合键名
     * @return 集合中元素的数量
     */
    public long zSetSize(String key) {
        if (StringUtils.isEmpty(key)) {
            return 0;
        }
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception ex) {
            log.error("zSetSize error", ex);
            return 0;
        }
    }

    /**
     * 统计指定分数范围内的元素数量
     * @param key 有序集合键名
     * @param minScore 最小分数
     * @param maxScore 最大分数
     * @return 分数范围内的元素数量
     */
    public long zSetCount(String key, double minScore, double maxScore) {
        if (StringUtils.isEmpty(key)) {
            return 0;
        }
        try {
            return redisTemplate.opsForZSet().count(key, minScore, maxScore);
        } catch (Exception ex) {
            log.error("zSetCount error", ex);
            return 0;
        }
    }

    /**
     * 获取有序集合中的所有元素（倒序）
     * @param key 有序集合键名
     * @return 所有元素的集合（分数从高到低）
     */
    public Set<String> zSetGetAll(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
        } catch (Exception ex) {
            log.error("zSetGetAll error", ex);
            return null;
        }
    }

    /**
     * 按分数范围获取有序集合中的元素
     * @param key 有序集合键名
     * @param start 最小分数
     * @param end 最大分数
     * @return 指定分数范围内的元素集合
     */
    public Set<String> zrangeByScore(String key, long start, long end) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Set<String> result = zset.rangeByScore(key, start, end);
            return result;
        } catch (Exception e) {
            log.error("redis zrangeByScore error,errorMessage:{}", e.getMessage());
        }
        return null;
    }

    /**
     * 移除指定分数范围内的元素
     * @param key 有序集合键名
     * @param start 最小分数
     * @param end 最大分数
     */
    public void zremoveRangeByScore(String key, long start, long end) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            zset.removeRangeByScore(key, start, end);
        } catch (Exception e) {
            log.error("redis zremoveRangeByScore error,errorMessage:{}", e.getMessage());
        }
    }

    /**
     * 从有序集合中移除指定元素
     * @param key 有序集合键名
     * @param value 要移除的元素
     */
    public void zremove(String key, String value) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        try {
            redisTemplate.opsForZSet().remove(key, value);
        } catch (Exception ex) {
            log.error("zSetAdd error", ex);
        }
    }

    /**
     * 计算两个有序集合的交集并存储到目标集合
     * @param key 第一个有序集合键名
     * @param otherKey 第二个有序集合键名
     * @param destKey 目标集合键名
     */
    public void zintersectAndStore(String key, String otherKey, String destKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(otherKey) || StringUtils.isEmpty(destKey)) {
            return;
        }
        try {
            redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
        } catch (Exception ex) {
            log.error("zSetAdd error", ex);
        }
    }

    /**
     * 计算两个有序集合的交集并存储（使用最小分数聚合）
     * @param key 第一个有序集合键名
     * @param otherKey 第二个有序集合键名
     * @param destKey 目标集合键名
     * @return 交集中元素的数量
     */
    public Long zinterstoreMin(String key, String otherKey, String destKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(otherKey) || StringUtils.isEmpty(destKey)) {
            return null;
        }
        List<String> keys = Arrays.asList(destKey, key, otherKey);
        String script = " return redis.call('zinterstore', KEYS[1], '2', KEYS[2], KEYS[3], 'aggregate', 'min') ";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        try {
            Object res = redisTemplate.execute(redisScript, keys);
            return (Long) res;
        } catch (Exception ex) {
            log.error("zinterstoreMin error, key:{}, otherKey:{}, destKey:{}", key, otherKey, destKey, ex);
        }
        return null;
    }

    /**
     * 计算有序集合的并集并存储
     * @param key 源集合键名
     * @param destKey 目标集合键名
     * @return 并集中元素的数量
     */
    public Long zunionstore(String key, String destKey) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(destKey)) {
            return null;
        }
        List<String> keys = Arrays.asList(destKey, key);
        String script = " return redis.call('zunionstore', KEYS[1], '1', KEYS[2]) ";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        try {
            Object res = redisTemplate.execute(redisScript, keys);
            return (Long) res;
        } catch (Exception ex) {
            log.error("zunionstore error, key:{}, destKey:{}", key, destKey, ex);
        }
        return null;
    }

    /**
     * 获取有序集合的大小
     * @param key 有序集合键名
     * @return 集合中元素的数量
     */
    public Long zsize(String key) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        try {
            Long result = zset.size(key);
            return result;
        } catch (Exception e) {
            log.error("redis zrange error,errorMessage:{}", e.getMessage());
        }
        return null;
    }

    // ==================== 哈希(Hash)操作 ====================

    /**
     * 设置哈希表中字段的值，支持设置过期时间
     * @param key 哈希表键名
     * @param field 字段名
     * @param value 字段值
     * @param expire 过期时间（秒，可选参数）
     * @return 设置成功返回true，否则返回false
     */
    public boolean hset(String key, String field, String value, long... expire) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field) || StringUtils.isEmpty(value)) {
            return false;
        }
        boolean result = false;
        try {
            final byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
            final byte[] rawField = redisTemplate.getStringSerializer().serialize(field);
            final byte[] rawValue = redisTemplate.getStringSerializer().serialize(value);
            result = redisTemplate.execute(connection -> {
                boolean ret = connection.hSet(rawKey, rawField, rawValue);
                if (expire.length > 0 && expire[0] > 0) {
                    connection.expire(rawKey, expire[0]);
                }
                return ret;
            }, true);
        } catch (Exception ex) {
            log.error("hset error", ex);
        }
        return result;
    }

    /**
     * 获取哈希表中指定字段的值
     * @param key 哈希表键名
     * @param field 字段名
     * @return 字段对应的值，不存在则返回null
     */
    public String hget(String key, String field) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return null;
        }
        final byte[] rawKey = redisTemplate.getStringSerializer().serialize(key);
        final byte[] rawField = redisTemplate.getStringSerializer().serialize(field);
        final byte[] rawValue = redisTemplate.execute(connection -> connection.hGet(rawKey, rawField), true);
        return redisTemplate.getStringSerializer().deserialize(rawValue);
    }

    /**
     * 删除哈希表中的指定字段
     * @param key 哈希表键名
     * @param field 字段名
     * @return 成功删除的字段数量
     */
    public Long hdel(String key, String field) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return null;
        }
        long result = redisTemplate.opsForHash().delete(key, field);
        return result;
    }

    /**
     * 批量获取哈希表中多个字段的值
     * @param key 哈希表键名
     * @param queryFields 字段名列表
     * @return 对应的值列表，顺序与queryFields一致
     */
    public List<String> hmget(String key, List<String> queryFields) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        return hashOperations.multiGet(key, queryFields);
    }

    /**
     * 批量设置哈希表的字段值（Long类型）
     * @param key 哈希表键名
     * @param map 字段-值映射
     */
    public void hmSet(String key, Map<Long, Long> map) {
        if (StringUtils.isEmpty(key) || MapUtils.isEmpty(map)) {
            return;
        }
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 批量设置哈希表的字段值，支持设置过期时间
     * @param key 哈希表键名
     * @param map 字段-值映射
     * @param expire 过期时间（秒，可选参数）
     */
    public void hmSet(String key, Map<String, String> map, long... expire) {
        if (StringUtils.isEmpty(key) || MapUtils.isEmpty(map)) {
            return;
        }
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 将哈希表中指定字段的值增加1
     * @param key 哈希表键名
     * @param field 字段名
     * @return 增加后的值
     */
    public Long hincrex(String key, String field) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return null;
        }
        Long result = redisTemplate.opsForHash().increment(key, field, 1);
        return result;
    }

    /**
     * 将哈希表中指定字段的值增加指定数值
     * @param key 哈希表键名
     * @param field 字段名
     * @param val 增加的数值
     * @return 增加后的值
     */
    public Long hIncrementVal(String key, String field, long val) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return null;
        }
        Long result = redisTemplate.opsForHash().increment(key, field, val);
        return result;
    }

    /**
     * 将哈希表中指定字段的值减少1
     * @param key 哈希表键名
     * @param field 字段名
     * @return 减少后的值
     */
    public Long hdecrex(String key, String field) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
            return null;
        }
        Long result = redisTemplate.opsForHash().increment(key, field, -1);
        return result;
    }

    // ==================== 列表(List)操作 ====================

    /**
     * 从列表左侧插入元素
     * @param key 列表键名
     * @param value 要插入的元素
     */
    public void lleftPush(String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从列表左侧批量插入元素
     * @param key 列表键名
     * @param values 要插入的元素集合
     */
    public void lleftPushAll(String key, Collection values) {
        if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(values)) {
            return;
        }
        redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 从列表右侧插入元素
     * @param key 列表键名
     * @param value 要插入的元素
     */
    public void lrightPush(String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return;
        }
        redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从列表右侧批量插入元素
     * @param key 列表键名
     * @param valueList 要插入的元素列表
     */
    public void lrightPushAll(String key, List<String> valueList) {
        if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(valueList)) {
            return;
        }
        redisTemplate.opsForList().rightPushAll(key, valueList);
    }

    /**
     * 从列表左侧批量插入元素
     * @param key 列表键名
     * @param valueList 要插入的元素列表
     */
    public void lleftPushAll(String key, List<String> valueList) {
        if (StringUtils.isEmpty(key) || CollectionUtils.isEmpty(valueList)) {
            return;
        }
        redisTemplate.opsForList().leftPushAll(key, valueList);
    }

    /**
     * 获取列表的长度
     * @param key 列表键名
     * @return 列表中元素的数量
     */
    public Long lsize(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 获取列表指定范围内的元素
     * @param key 列表键名
     * @param start 开始位置
     * @param end 结束位置
     * @return 指定范围内的元素列表
     */
    public List<String> lrange(String key, long start, long end) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 从列表左侧弹出一个元素
     * @param key 列表键名
     * @return 弹出的元素，列表为空则返回null
     */
    public String lleftPop(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 从列表左侧弹出一个元素（阻塞版本）
     * @param key 列表键名
     * @param time 阻塞等待时间
     * @param timeUnit 时间单位
     * @return 弹出的元素，超时则返回null
     */
    public String lleftPop(String key, long time, TimeUnit timeUnit) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForList().leftPop(key, time, timeUnit);
        } catch (Exception e) {
            log.error("redis lleftPop error! key:{}", key, e);
        }
        return null;
    }

    /**
     * 从列表右侧弹出一个元素（阻塞版本）
     * @param key 列表键名
     * @param time 阻塞等待时间
     * @param timeUnit 时间单位
     * @return 弹出的元素，超时则返回null
     */
    public String lrightPop(String key, long time, TimeUnit timeUnit) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            return redisTemplate.opsForList().rightPop(key, time, timeUnit);
        } catch (Exception e) {
            log.error("redis lrightPop error! key:{}", key, e);
        }
        return null;
    }

    // ==================== 计数器操作 ====================

    /**
     * 对键的值进行自增操作并设置过期时间
     * @param key 键名
     * @param sec 过期时间（秒）
     * @return 自增后的值
     */
    public Long increx(String key, long sec) {
        Long result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            result = operations.increment(key, 1);
            // 第一次设置，设置有效时间
            if (result == 1) {
                expire(key, sec);
            }
        } catch (Exception e) {
            log.error("redis get error! key:{}", key, e);
        }
        return result;
    }

    /**
     * 对键的值进行自减操作并设置过期时间
     * @param key 键名
     * @param sec 过期时间（秒）
     * @return 自减后的值
     */
    public Long decrex(String key, long sec) {
        Long result = null;
        try {
            ValueOperations<String, String> operations = redisTemplate.opsForValue();
            result = operations.increment(key, -1);
            // 第一次设置，设置有效时间
            if (result == -1) {
                expire(key, sec);
            }
        } catch (Exception e) {
            log.error("redis get error! key:{}", key, e);
        }
        return result;
    }

    // ==================== 分布式锁操作 ====================

    /**
     * 安装分布式锁（秒级过期时间）
     * @param key 锁的键名
     * @param requestId 请求标识ID，用于确保只能释放自己的锁
     * @param liveTime 锁存活时间（秒）
     * @return 成功获取锁返回true，否则返回false
     */
    public boolean installDistributedLock(String key, String requestId, long liveTime) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(requestId)) {
            return false;
        }
        return setNxEx(key, requestId, liveTime);
    }

    /**
     * 释放分布式锁
     * @param key 锁的键名
     * @param requestId 请求标识ID，确保只能释放自己的锁
     */
    public void releaseDistributedLock(String key, String requestId) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(requestId)) {
            return;
        }
        try {
            String redisId = redisTemplate.opsForValue().get(key);
            // 保证只解自己拿到的锁
            if (requestId.equals(redisId)) {
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error("release distributed lock error,requestId:{},errorMessage:{}",
                    requestId, e.getMessage());
            throw new BusinessException("release distributed lock error");
        }
    }

    // ==================== 通用操作 ====================

    /**
     * 删除指定的键
     * @param key 要删除的键名
     */
    public void remove(final String key) {
        redisTemplate.delete(key);
    }

    /**
     * 检查键是否存在
     * @param key 键名
     * @return 存在返回true，否则返回false
     */
    public Boolean exists(final String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 设置键的过期时间
     * @param key 键名
     * @param livetime 过期时间（秒）
     */
    public void expire(String key, long livetime) {
        redisTemplate.expire(key, livetime, TimeUnit.SECONDS);
    }

    // ==================== 集合间操作 ====================

    /**
     * 计算两个集合的交集
     * @param key1 第一个集合键名
     * @param key2 第二个集合键名
     * @return 两个集合的交集
     */
    public Set<String> intersect(String key1, String key2) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            return set.intersect(key1, key2);
        } catch (Exception e) {
            log.error("redis intersect error! key1:{}, key2:{}", key1, key2, e);
            return Collections.emptySet();
        }
    }

    /**
     * 计算两个集合的并集
     * @param key1 第一个集合键名
     * @param key2 第二个集合键名
     * @return 两个集合的并集
     */
    public Set<String> union(String key1, String key2) {
        try {
            SetOperations<String, String> set = redisTemplate.opsForSet();
            return set.union(key1, key2);
        } catch (Exception e) {
            log.error("redis union error! key1:{}, key2:{}", key1, key2, e);
            return Collections.emptySet();
        }
    }

    /**
     * 获取集合的大小
     * @param key 集合键名
     * @return 集合中元素的数量
     */
    public Long size(String key) {
        SetOperations<String, String> set = redisTemplate.opsForSet();
        try {
            Long result = set.size(key);
            return result;
        } catch (Exception e) {
            log.error("redis size error, key:{}, errorMessage:{}", key, e.getMessage());
        }
        return null;
    }

    // ==================== 键管理操作 ====================

    /**
     * 获取指定键的过期时间
     * @param key 键名
     * @return 过期时间（秒），-1表示永不过期，-2表示键不存在
     */
    public Long getKeysExpireTime(String key) {
        Set<String> keys = redisTemplate.keys(key);
        return redisTemplate.opsForValue().getOperations().getExpire(key);
    }

    /**
     * 批量获取匹配前缀的键的过期时间
     * @param keyPrefix 键名前缀
     * @return 键名-过期时间的映射
     */
    public Map<String, Long> batchGetKeysExpireTime(String keyPrefix) {
        Set<String> keys = redisTemplate.keys(keyPrefix + "*");
        Map<String, Long> keyExpireMap = new HashMap<>();
        for (String key : keys) {
            Long expireTime = redisTemplate.opsForValue().getOperations().getExpire(key);
            keyExpireMap.put(key, expireTime);
        }
        return keyExpireMap;
    }

    // ==================== 管道操作 ====================

    /**
     * 使用管道批量获取有序集合的倒序范围数据（带分数）
     * @param keys 有序集合键名列表
     * @param indexs 索引列表，用于结果映射
     * @param start 开始位置
     * @param end 结束位置
     * @return 索引-元素集合的映射
     */
    public Map<Long, Set<DefaultTypedTuple>> zreverseRangeWithScoreByPipelined(List<String> keys,
                                                                               List<Long> indexs,
                                                                               long start, long end) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(keys)) {
            return org.apache.commons.collections.MapUtils.EMPTY_MAP;
        }
        List<Object> redisResult = redisTemplate.executePipelined((RedisCallback<List<Object>>) connection -> {
            for (String key : keys) {
                byte[] rewKey = redisTemplate.getStringSerializer().serialize(key);
                connection.zRevRangeWithScores(rewKey, start, end - 1);
            }
            return null;
        });
        Map<Long, Set<DefaultTypedTuple>> resultMap = new HashMap<>();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(redisResult)) {
            for (int i = 0; i < indexs.size(); i++) {
                Set<DefaultTypedTuple> result = (Set<DefaultTypedTuple>) redisResult.get(i);
                resultMap.put(indexs.get(i), result);
            }
        }
        return resultMap;
    }
}