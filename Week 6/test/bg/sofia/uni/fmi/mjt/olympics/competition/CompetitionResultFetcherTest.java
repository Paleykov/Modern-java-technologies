package bg.sofia.uni.fmi.mjt.olympics.competition;

import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompetitionResultFetcherTest {
    @Test
    void testConstructorThrowsWhenNameIsNull() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "John", "USA"));
        assertThrows(IllegalArgumentException.class,
                () -> new Competition(null, "Running", competitors));
    }

    @Test
    void testConstructorThrowsWhenNameIsBlank() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "John", "USA"));
        assertThrows(IllegalArgumentException.class,
                () -> new Competition("   ", "Running", competitors));
    }

    @Test
    void testConstructorThrowsWhenDisciplineIsNull() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "John", "USA"));
        assertThrows(IllegalArgumentException.class,
                () -> new Competition("Olympics", null, competitors));
    }

    @Test
    void testConstructorThrowsWhenDisciplineIsBlank() {
        Set<Competitor> competitors = Set.of(new Athlete("1", "John", "USA"));
        assertThrows(IllegalArgumentException.class,
                () -> new Competition("Olympics", "   ", competitors));
    }

    @Test
    void testConstructorThrowsWhenCompetitorsIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Competition("Olympics", "Running", null));
    }

    @Test
    void testConstructorThrowsWhenCompetitorsIsEmpty() {
        assertThrows(IllegalArgumentException.class,
                () -> new Competition("Olympics", "Running", Set.of()));
    }

    @Test
    void testEqualsAndHashCode() {
        Set<Competitor> set1 = Set.of(new Athlete("1", "John", "USA"));
        Set<Competitor> set2 = Set.of(new Athlete("2", "Jane", "UK"));

        Competition c1 = new Competition("Olympics", "Running", set1);
        Competition c2 = new Competition("Olympics", "Running", set2);

        assertEquals(c1, c2, "Competitions with same name and discipline should be equal");
    }

    @Test
    void testHashCode() {
        Set<Competitor> set1 = Set.of(new Athlete("1", "John", "USA"));
        Set<Competitor> set2 = Set.of(new Athlete("2", "Jane", "UK"));

        Competition c1 = new Competition("Olympics", "Running", set1);
        Competition c2 = new Competition("Olympics", "Running", set2);

        assertEquals(c1.hashCode(), c2.hashCode(), "Equal competitions must have same hashCode");
    }
}
