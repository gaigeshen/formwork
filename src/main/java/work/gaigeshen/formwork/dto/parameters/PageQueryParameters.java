package work.gaigeshen.formwork.dto.parameters;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import java.util.Objects;

/**
 * 分页查询的参数包含页码和分页大小
 *
 * @author gaigeshen
 */
public abstract class PageQueryParameters implements QueryParameters {

    @Schema(description = "分页页码")
    @Min(1)
    private int current;

    @Schema(description = "分页大小")
    @Min(1)
    private int pageSize;

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

    protected void setCurrent(int current) {
        this.current = current;
    }

    protected void setPageSize(int pageSize) {
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
        PageQueryParameters query = (PageQueryParameters) o;
        return current == query.current && pageSize == query.pageSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(current, pageSize);
    }
}
