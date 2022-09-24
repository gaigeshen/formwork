package work.gaigeshen.formwork.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import work.gaigeshen.formwork.dto.parameters.PageQueryParameters;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 分页查询结果
 *
 * @author gaigeshen
 */
@Schema(description = "分页查询结果")
public class PageQueryResponse<C> implements QueryResponse {

    @Schema(description = "分页页码")
    private int current;

    @Schema(description = "分页大小")
    private int pageSize;

    @Schema(description = "查询结果数据")
    private Collection<C> content;

    @Schema(description = "总数据条数")
    private long total;

    protected PageQueryResponse() {

    }

    protected PageQueryResponse(PageQueryParameters parameters, Collection<C> content, long total) {
        this.current = parameters.getCurrent();
        this.pageSize = parameters.getPageSize();
        this.content = content;
        this.total = total;
    }

    public static <C> PageQueryResponse<C> create(PageQueryParameters parameters) {
        return new PageQueryResponse<>(parameters, Collections.emptyList(), 0);
    }

    public static <C> PageQueryResponse<C> create(PageQueryParameters parameters, Collection<C> content, long totalCount) {
        return new PageQueryResponse<>(parameters, content, totalCount);
    }

    public int getCurrent() {
        return current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Collection<C> getContent() {
        return content;
    }

    public long getTotal() {
        return total;
    }

    protected void setCurrent(int current) {
        this.current = current;
    }

    protected void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    protected void setContent(Collection<C> content) {
        this.content = content;
    }

    protected void setTotal(int total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageQueryResponse<?> that = (PageQueryResponse<?>) o;
        if (current != that.current) {
            return false;
        }
        if (pageSize != that.pageSize) {
            return false;
        }
        if (total != that.total) {
            return false;
        }
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, pageSize, content, total);
    }
}
