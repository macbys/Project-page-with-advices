package com.maxbys.strona_z_poradami_projekt.paginagtion;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class PaginationGenerator{

    public static List<Integer> createPaginationList(int currentPage, int totalPages) {
        List<Integer> paginationNumbers;
        if(currentPage <= 3) {
            paginationNumbers = getPaginationNumbersForCurrentPageLowerOrEqual3(totalPages);
        } else if(currentPage >= totalPages - 2) {
            paginationNumbers = getPaginationNumbersForLastOrSecondToLastPage(totalPages);
        } else {
            paginationNumbers = getPaginationNumbers(currentPage - 2, currentPage + 2);
        }
        return paginationNumbers;
    }

    private static List<Integer> getPaginationNumbers(int firstPaginationNumber, int lastPaginationNumber) {
        List<Integer> paginationNumbers;
        paginationNumbers = IntStream
                .rangeClosed(firstPaginationNumber, lastPaginationNumber)
                .boxed()
                .collect(Collectors.toList());
        return paginationNumbers;
    }

    private static List<Integer> getPaginationNumbersForLastOrSecondToLastPage(int totalPages) {
        int firstShownPaginationNumber = totalPages <= 5? 1 : totalPages - 4;
        List<Integer> paginationNumbers = getPaginationNumbers(firstShownPaginationNumber, totalPages);
        return paginationNumbers;
    }

    private static List<Integer> getPaginationNumbersForCurrentPageLowerOrEqual3(int totalPages) {
        int lastShownPaginationNumber = totalPages >= 5? 5 : totalPages;
        List<Integer> paginationNumbers = getPaginationNumbers(1, lastShownPaginationNumber);
        return paginationNumbers;
    }
}
