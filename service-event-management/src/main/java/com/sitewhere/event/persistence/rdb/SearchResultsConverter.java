package com.sitewhere.event.persistence.rdb;

import com.sitewhere.spi.search.ISearchResults;

import java.util.List;

/**
 * @param <T>
 *
 * Simeon Chen
 */
public class SearchResultsConverter<T> {

    public ISearchResults<T> convert(List<T> list) {
        return new ISearchResults<T> () {

            @Override
            public long getNumResults() {
                return list.size();
            }

            @Override
            public List<T> getResults() {
                return list;
            }
        };
    }

}
