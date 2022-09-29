package work.gaigeshen.formwork.persistence;

import java.util.*;

/**
 *
 * @author gaigeshen
 */
public class Pagination<E> extends ArrayList<E> implements List<E> {

    private final int index;

    private final int size;

    private long total = 0;

    private Pagination(int index, int size) {
        this.index = index;
        this.size = size;
    }

    public static <E> Pagination<E> create(int index, int size) {
        return new Pagination<>(index, size);
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
