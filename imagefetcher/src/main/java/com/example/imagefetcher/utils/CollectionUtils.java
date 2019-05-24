package com.example.imagefetcher.utils;

import java.util.List;
import java.util.Set;

public class CollectionUtils {
    private CollectionUtils() {

    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isEmpty(Set<T> set) {
        return set == null || set.isEmpty();
    }

}
