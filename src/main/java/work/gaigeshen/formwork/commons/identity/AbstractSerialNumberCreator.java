package work.gaigeshen.formwork.commons.identity;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象序列号生成器
 *
 * @author gaigeshen
 */
public abstract class AbstractSerialNumberCreator implements SerialNumberCreator {

    private final Map<String, SerialNumber> serialNumbers = new ConcurrentHashMap<>();

    @Override
    public final String create(String prefix) {
        if (Objects.isNull(prefix)) {
            throw new IllegalArgumentException("prefix cannot be null");
        }
        String serialNumber = prefix + serialNumbers.computeIfAbsent(prefix, p -> {
            expiresSerialNumbers();
            long capacity = getCapacity(p);
            long newCapacity = getNewCapacity(p, capacity);
            return new SerialNumber(p, capacity + 1, newCapacity);
        }).getPositionValue();
        return wrapSerialNumber(prefix, serialNumber);
    }

    private void expiresSerialNumbers() {
        for (String prefix : new HashSet<>(serialNumbers.keySet())) {
            if (expireNecessary(prefix)) {
                serialNumbers.remove(prefix);
            }
        }
    }

    /**
     * 此方法用于包装生成的序列号
     *
     * @param prefix 序列号前缀
     * @param serialNumber 生成的序列号（包含序列号前缀）
     * @return 包装后的序列号
     */
    protected String wrapSerialNumber(String prefix, String serialNumber) {
        return serialNumber;
    }

    /**
     * 确认指定的序列号前缀是否需要执行过期操作
     *
     * @param prefix 序列号前缀
     * @return 是否需要执行过期操作
     */
    protected boolean expireNecessary(String prefix) {
        return false;
    }

    /**
     * 获取指定序列号的当前容量，对于相同的序列号前缀，此方法不会出现多线程同时访问的情况
     *
     * @param prefix 序列号前缀
     * @return 序列号的当前容量
     */
    protected long getCapacity(String prefix) {
        return 0;
    }

    /**
     * 获取指定序列号新的容量，当需要扩容的时候会被调用，对于相同的序列号前缀，此方法不会出现多线程同时访问的情况
     *
     * @param prefix 序列号前缀
     * @param oldCapacity 旧的容量
     * @return 新的容量
     */
    protected long getNewCapacity(String prefix, long oldCapacity) {
        return oldCapacity + 10000;
    }

    /**
     * 序列号抽象
     *
     * @author gaigeshen
     */
    private class SerialNumber {

        private final String prefix;

        private volatile long position;

        private volatile long capacity;

        public SerialNumber(String prefix, long position, long capacity) {
            this.prefix = prefix;
            this.position = position;
            this.capacity = capacity;
        }

        /**
         * 获取序列号当前的数值
         *
         * @return 序列号当前的数值
         */
        public long getPositionValue() {
            synchronized (this) {
                long positionValue = position++;
                while (positionValue >= capacity) {
                    capacity = getNewCapacity(prefix, capacity);
                }
                return positionValue;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SerialNumber that = (SerialNumber) o;
            return Objects.equals(prefix, that.prefix);
        }

        @Override
        public int hashCode() {
            return Objects.hash(prefix);
        }

        @Override
        public String toString() {
            return "SerialNumber{" +
                    "prefix='" + prefix + '\'' +
                    ", position=" + position +
                    ", capacity=" + capacity +
                    '}';
        }
    }

}
