package net.messagehandler.utility;

import java.util.List;

public class PaginatedList {


    List<?> list;
    int perPage;
    int maxPage;

    public PaginatedList(List<?> list, int perPage) {
        this.list = list;
        this.perPage = perPage;
        this.maxPage = (int) Math.ceil(list.size()/(double)perPage);
    }

    public <T> List<T> getListOfPage(int page) {
        if(page > maxPage) {
            page = maxPage;
        }
        return (List<T>) list.subList((page - 1) * perPage, Math.min(list.size(), page * perPage));
    }

    public int getMaxPage() {
        return maxPage;
    }


}
