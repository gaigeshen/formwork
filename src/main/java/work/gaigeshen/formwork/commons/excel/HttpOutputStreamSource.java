package work.gaigeshen.formwork.commons.excel;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Objects;

/**
 * 用于获取导出输出流
 *
 * @author gaigeshen
 */
public class HttpOutputStreamSource implements ExcelExporter.OutputStreamSource {

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private static final String CHARACTER_ENCODING = "utf-8";

    private final HttpServletResponse response;

    private final String filename;

    /**
     * 创建此对象
     *
     * @param response 响应对象不能为空
     * @param filename 文件名称不能为空，不需要带文件后缀
     */
    public HttpOutputStreamSource(HttpServletResponse response, String filename) {
        if (Objects.isNull(response)) {
            throw new IllegalArgumentException("http servlet response cannot be null");
        }
        if (Objects.isNull(filename)) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        this.response = response;
        this.filename = filename;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        String filename = URLEncoder.encode(this.filename, "utf-8");

        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + filename + ".xlsx");

        return response.getOutputStream();
    }
}
