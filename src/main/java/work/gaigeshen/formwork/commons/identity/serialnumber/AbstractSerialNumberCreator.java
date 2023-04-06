package work.gaigeshen.formwork.commons.identity.serialnumber;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象序列号生成器，此类维护当前有效的序列号状态，凡是直接继承此类的，都不能作为集群环境使用
 *
 * @author gaigeshen
 */
public abstract class AbstractSerialNumberCreator implements SerialNumberCreator {

    private final Map<String, SerialNumber> serialNumbers = new ConcurrentHashMap<>();

    @Override
    public final String create(String prefix) {
        String checkedPrefix = Objects.isNull(prefix) ? "SN" : prefix;
        return wrapSerialNumber(checkedPrefix, serialNumbers.computeIfAbsent(checkedPrefix, p -> {
            for (String current : new HashSet<>(serialNumbers.keySet())) {
                if (expireNecessary(current)) {
                    serialNumbers.remove(current);
                }
            }
            long capacity = getCapacity(p);
            long newCapacity = getNewCapacity(p, capacity);
            return new SerialNumber(p, capacity + 1, newCapacity);
        }).getPositionValue());
    }

    /**
     * 此方法用于包装生成的序列号
     *
     * @param prefix 序列号前缀不会为空
     * @param serialNumber 生成的序列号数值
     * @return 包装后的序列号
     */
    protected String wrapSerialNumber(String prefix, long serialNumber) {
        String serialNumberText = Long.toString(serialNumber);
        return prefix + StringUtils.leftPad(serialNumberText, 7, "0");
    }

    /**
     * 确认指定的序列号前缀是否需要执行过期操作，每当新的序列号前缀被维护的时候，才会调用此方法
     *
     * @param prefix 序列号前缀不会为空
     * @return 是否需要执行过期操作
     */
    protected boolean expireNecessary(String prefix) {
        return false;
    }

    /**
     * 获取指定序列号的当前容量，对于相同的序列号前缀，此方法不会出现多线程同时访问的情况
     *
     * @param prefix 序列号前缀不会为空
     * @return 序列号的当前容量
     */
    protected long getCapacity(String prefix) {
        return 0;
    }

    /**
     * 获取指定序列号新的容量，当需要扩容的时候会被调用，对于相同的序列号前缀，此方法不会出现多线程同时访问的情况
     *
     * @param prefix 序列号前缀不会为空
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
            if (this == o) {
                return true;
            }
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
