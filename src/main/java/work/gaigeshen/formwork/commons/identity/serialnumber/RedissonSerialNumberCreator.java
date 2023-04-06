package work.gaigeshen.formwork.commons.identity.serialnumber;

import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;

import java.util.Objects;

/**
 * 此序列号生成器利用外部缓存，不可以作为集群环境中使用
 *
 * @author gaigeshen
 */
public class RedissonSerialNumberCreator extends AbstractSerialNumberCreator {

    private final RedissonClient redisson;

    public RedissonSerialNumberCreator(RedissonClient redisson) {
        if (Objects.isNull(redisson)) {
            throw new IllegalArgumentException("redisson client cannot be null");
        }
        this.redisson = redisson;
    }

    @Override
    protected boolean expireNecessary(String prefix) {
        return false;
    }

    @Override
    protected long getCapacity(String prefix) {
        RMap<Object, Object> serialNumberState = redisson.getMap("SN:" + prefix, LongCodec.INSTANCE);
        if (serialNumberState.containsKey("capacity")) {
            Object capacity = serialNumberState.get("capacity");
            return (long) capacity;
        }
        serialNumberState.put("capacity", 0L);
        return 0L;
    }

    @Override
    protected long getNewCapacity(String prefix, long oldCapacity) {
        long newCapacity = 1000 + oldCapacity;
        RMap<Object, Object> serialNumberState = redisson.getMap("SN:" + prefix, LongCodec.INSTANCE);
        if (!serialNumberState.containsKey("capacity")) {
            throw new IllegalStateException("could not update capacity: " + prefix);
        }
        serialNumberState.put("capacity", newCapacity);
        return newCapacity;
    }
}
