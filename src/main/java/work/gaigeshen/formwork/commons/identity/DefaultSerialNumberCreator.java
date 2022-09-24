package work.gaigeshen.formwork.commons.identity;

import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 序列号生成器，可用于各种低调用频率情况下单号的生成
 *
 * @author gaigeshen
 */
public class DefaultSerialNumberCreator implements SerialNumberCreator {

    public static final DefaultSerialNumberCreator INSTANCE = new DefaultSerialNumberCreator();

    // 缓存时间字符串和对应该时间内的序列计数
    // 将当前的时间精确到秒作为时间字符串也就是每秒都有对应的序列计数
    // 两秒之后该缓存项被清除，因为两秒过后肯定该缓存项永远用不上
    private final static LoadingCache<String, AtomicInteger> SERIAL_NUMBER_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(2, TimeUnit.SECONDS)
            .build(CacheLoader.from((Supplier<AtomicInteger>) AtomicInteger::new));

    // 每秒序列计数的字符串长度，设为四位（0001 ~ 9999），如果每秒需要产生达到上万个序列计数，则将此数字加大
    private final static int SERIAL_NUMBER_LEN = 4;

    private DefaultSerialNumberCreator() { }

    @Override
    public String create(String prefix) {
        // 没有传递前缀那么取空字符串
        String _prefix = StringUtils.defaultString(prefix);
        // 中间的内容取当前的时间，需要精确到秒
        String _center = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        // 通过当前的时间获取序列号
        int serialNumber;
        try {
            serialNumber = SERIAL_NUMBER_CACHE.get(_center).incrementAndGet();
        } catch (ExecutionException e) {
            throw new IllegalStateException("could not get serial number", e);
        }
        // 后缀取自增的序列号
        String _suffix = StringUtils.leftPad(serialNumber + "", SERIAL_NUMBER_LEN, "0");

        return _prefix + _center + _suffix;
    }
}
