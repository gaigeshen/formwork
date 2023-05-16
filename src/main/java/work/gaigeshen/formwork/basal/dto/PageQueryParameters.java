package work.gaigeshen.formwork.basal.dto;

import java.util.Objects;

/**
 * 分页查询参数类数据传输对象，最基本的需要传递分页页码和页容量
 *
 * @author gaigeshen
 */
public abstract class PageQueryParameters extends QueryParameters {

    private int current = 1;

    private int pageSize = 10;

    protected PageQueryParameters() {
    }

    protected PageQueryParameters(int current, int pageSize) {
        this.current = current;
        this.pageSize = pageSize;
    }

    public int getCurrent() {
        return current;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageQueryParameters pqps = (PageQueryParameters) o;
        return current == pqps.current && pageSize == pqps.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, pageSize);
    }
}
