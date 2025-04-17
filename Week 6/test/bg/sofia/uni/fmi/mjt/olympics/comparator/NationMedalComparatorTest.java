package bg.sofia.uni.fmi.mjt.olympics.comparator;

import bg.sofia.uni.fmi.mjt.olympics.MJTOlympics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NationMedalComparatorTest {
    @Test
    void testCompareByMedalCountDescending() {
        MJTOlympics olympics = mock(MJTOlympics.class);
        when(olympics.getTotalMedals("USA")).thenReturn(9);
        when(olympics.getTotalMedals("Canada")).thenReturn(6);

        NationMedalComparator comparator = new NationMedalComparator(olympics);

        int result = comparator.compare("USA", "Canada");

        assertTrue(result < 0, "USA has more medals, should come before Canada");
    }

    @Test
    void testCompareByAlphabeticalOrderWhenMedalsAreEqual() {
        MJTOlympics olympics = mock(MJTOlympics.class);
        when(olympics.getTotalMedals("USA")).thenReturn(5);
        when(olympics.getTotalMedals("UK")).thenReturn(5);

        NationMedalComparator comparator = new NationMedalComparator(olympics);

        int result = comparator.compare("UK", "USA");

        assertTrue(result < 0, "When medals are equal, nations should be compared alphabetically");
    }
}
