package work.gaigeshen.formwork.basal.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 分页查询响应类数据传输对象
 *
 * @author gaigeshen
 */
public class PageQueryResponse<C> extends QueryResponse {

    private int current;

    private int pageSize;

    private Collection<C> content;

    private long total;

    protected PageQueryResponse() {

    }

    protected PageQueryResponse(PageQueryParameters pageQuery, Collection<C> content, long total) {
        this.current = pageQuery.getCurrent();
        this.pageSize = pageQuery.getPageSize();
        this.content = content;
        this.total = total;
    }

    public static <C> PageQueryResponse<C> create(PageQueryParameters pageQuery) {
        return new PageQueryResponse<>(pageQuery, Collections.emptyList(), 0);
    }

    public static <C> PageQueryResponse<C> create(PageQueryParameters pageQuery, Collection<C> content, long totalCount) {
        return new PageQueryResponse<>(pageQuery, content, totalCount);
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
