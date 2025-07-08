package jpabook.javaspring.util;

public class PaginationUtil {

    public static int toZeroBasedPage(int page) {
        int zeroBased = page - 1;
        return Math.max(zeroBased, 0);
    }
}
