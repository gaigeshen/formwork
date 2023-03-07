package work.gaigeshen.formwork.commons.excel.easyexcel.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;

import java.util.Objects;

/**
 * 布尔值转换为是或者否字符串
 *
 * @author gaigeshen
 */
public class BooleanConverter implements Converter<Boolean> {

    @Override
    public Boolean convertToJavaData(ReadConverterContext<?> context) {
        ReadCellData<?> readCellData = context.getReadCellData();
        String value = readCellData.getStringValue();
        return Objects.nonNull(value) && "是".equals(value);
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Boolean> context) {
        Boolean value = context.getValue();
        return new WriteCellData<>(Objects.nonNull(value) && value ? "是" : "否");
    }
}
