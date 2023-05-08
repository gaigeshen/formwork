package work.gaigeshen.formwork.basal.identity.serialnumber;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;

import java.util.Date;
import java.util.Objects;

/**
 * 序列号生成器实现，数据放在外部缓存中，所以可以用在集群环境中
 *
 * @author gaigeshen
 */
public class RedissonSerialNumberGenerator implements SerialNumberGenerator {

    private final RedissonClient redisson;

    public RedissonSerialNumberGenerator(RedissonClient redisson) {
        if (Objects.isNull(redisson)) {
            throw new IllegalArgumentException("redisson client cannot be null");
        }
        this.redisson = redisson;
    }

    /**
     * 关联当前年月日来生成序列号，区分不同的前缀用不同的键来存储
     *
     * @param prefix 序列号前缀如果传递空的，就采用默认的前缀
     * @return 生成的序列号包含当前年月日以及序列号数字，前面拼接前缀
     */
    @Override
    public String generate(String prefix) {

        String date = DateFormatUtils.format(new Date(), "yyMMdd");

        String checkedPrefix = StringUtils.defaultIfBlank(prefix, "SN");

        Integer number = (Integer) redisson.getMap("SN:" + checkedPrefix, IntegerCodec.INSTANCE).addAndGet(date, 1);

        return checkedPrefix + date + StringUtils.leftPad(number.toString(), 6, "0");
    }
}
