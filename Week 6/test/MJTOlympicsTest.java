import bg.sofia.uni.fmi.mjt.olympics.MJTOlympics;
import bg.sofia.uni.fmi.mjt.olympics.competition.Competition;
import bg.sofia.uni.fmi.mjt.olympics.competition.CompetitionResultFetcher;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Athlete;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Competitor;
import bg.sofia.uni.fmi.mjt.olympics.competitor.Medal;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MJTOlympicsTest {
    @Test
    void testUpdateMedalStatisticsThrowsIAEifCompetitorIsNull() {
        Set<Competitor> registered = Set.of();
        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(registered, fetcher);

        Competition competition = null;

        assertThrows(IllegalArgumentException.class,
                () -> olympics.updateMedalStatistics(competition),
                "Competition argument cannot be null ib updateMedalStatistics");
    }

    @Test
    void testUpdateMedalStatisticsThrowsIfUnregisteredCompetitor() {
        Competitor registered = new Athlete("1", "John", "USA");
        Competitor unregistered = new Athlete("2", "Jane", "UK");

        Set<Competitor> registeredSet = Set.of(registered);
        Set<Competitor> competitionSet = Set.of(registered, unregistered);

        Competition competition = mock(Competition.class);
        when(competition.competitors()).thenReturn(competitionSet);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(registeredSet, fetcher);

        assertThrows(IllegalArgumentException.class,
                () -> olympics.updateMedalStatistics(competition),
                "Expected exception when competition has unregistered competitors");
    }

    @Test
    void testUpdateMedalStatisticsAssignsCorrectMedals() {
        Competitor c1 = new Athlete("1", "Alice", "USA");
        Competitor c2 = new Athlete("2", "Bob", "UK");

        Set<Competitor> registered = Set.of(c1, c2);

        Competition competition = mock(Competition.class);
        when(competition.competitors()).thenReturn(registered);

        TreeSet<Competitor> result = new TreeSet<>(Comparator.comparing(Competitor::getName));
        result.add(c1);
        result.add(c2);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        when(fetcher.getResult(competition)).thenReturn(result);

        MJTOlympics olympics = new MJTOlympics(registered, fetcher);

        olympics.updateMedalStatistics(competition);

        System.out.println("Ranking order:");
        for (Competitor c : result) {
            System.out.println(" - " + c.getName() + " (" + c.getIdentifier() + ")");
        }

        System.out.println("C1 medals: " + c1.getMedals());
        System.out.println("C2 medals: " + c2.getMedals());

        assertTrue(c1.getMedals().contains(Medal.GOLD), "First competitor (Alice) should get GOLD");
        assertTrue(c2.getMedals().contains(Medal.SILVER), "Second competitor (Bob) should get SILVER");
    }


    @Test
    void testGetTotalMedalsThrowsWhenNationalityIsNull() {
        MJTOlympics olympics = new MJTOlympics(Set.of(), mock(CompetitionResultFetcher.class));

        assertThrows(IllegalArgumentException.class,
                () -> olympics.getTotalMedals(null),
                "Should throw IAE when nationality is null");
    }

    @Test
    void testGetTotalMedalsThrowsWhenNationalityNotRegistered() {
        MJTOlympics olympics = new MJTOlympics(Set.of(), mock(CompetitionResultFetcher.class));

        assertThrows(IllegalArgumentException.class,
                () -> olympics.getTotalMedals("Wakanda"),
                "Should throw IAE when nationality is not in the medal table");
    }

    @Test
    void testGetTotalMedalsReturnsZeroIfNoMedals() {
        Competitor c1 = new Athlete("1", "Test", "EmptyNation");
        Set<Competitor> registered = Set.of(c1);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(registered, fetcher);

        olympics.getNationsMedalTable().put("EmptyNation", new EnumMap<>(Medal.class));

        int total = olympics.getTotalMedals("EmptyNation");
        assertEquals(0, total, "Should return 0 if nation has no medals yet");
    }

    @Test
    void testGetTotalMedalsReturnsCorrectSum() {
        Competitor c1 = new Athlete("1", "Alice", "Canada");
        Set<Competitor> registered = Set.of(c1);

        Competition competition = mock(Competition.class);
        when(competition.competitors()).thenReturn(registered);

        TreeSet<Competitor> result = new TreeSet<>(Comparator.comparing(Competitor::getIdentifier));
        result.add(c1);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        when(fetcher.getResult(competition)).thenReturn(result);

        MJTOlympics olympics = new MJTOlympics(registered, fetcher);
        olympics.updateMedalStatistics(competition);

        olympics.getNationsMedalTable().get("Canada").put(Medal.SILVER, 2);

        int total = olympics.getTotalMedals("Canada");
        assertEquals(3, total, "Should return the total count of all medal types");
    }

    @Test
    void testNationsRankListSortedByMedalCount() {
        Competitor c1 = new Athlete("1", "Alice", "USA");
        Competitor c2 = new Athlete("2", "Bob", "Canada");
        Set<Competitor> registered = Set.of(c1, c2);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(registered, fetcher);

        olympics.getNationsMedalTable().put("USA", new EnumMap<>(Map.of(Medal.GOLD, 2)));
        olympics.getNationsMedalTable().put("Canada", new EnumMap<>(Map.of(Medal.SILVER, 1)));

        TreeSet<String> ranking = olympics.getNationsRankList();
        List<String> expectedOrder = List.of("USA", "Canada");

        assertEquals(expectedOrder, new ArrayList<>(ranking),
                "Nations should be sorted by total medals descending");
    }

    @Test
    void testNationsRankListAlphabeticalWhenMedalsAreEqual() {
        Competitor c1 = new Athlete("1", "Anna", "Germany");
        Competitor c2 = new Athlete("2", "Lena", "France");
        Set<Competitor> registered = Set.of(c1, c2);

        CompetitionResultFetcher fetcher = mock(CompetitionResultFetcher.class);
        MJTOlympics olympics = new MJTOlympics(registered, fetcher);

        olympics.getNationsMedalTable().put("Germany", new EnumMap<>(Map.of(Medal.SILVER, 1)));
        olympics.getNationsMedalTable().put("France", new EnumMap<>(Map.of(Medal.GOLD, 1)));

        TreeSet<String> ranking = olympics.getNationsRankList();
        List<String> expectedOrder = List.of("France", "Germany");

        assertEquals(expectedOrder, new ArrayList<>(ranking),
                "If medal counts are equal, nations should be sorted alphabetically");
    }

    @Test
    void testNationsRankListEmptyWhenNoData() {
        MJTOlympics olympics = new MJTOlympics(Set.of(), mock(CompetitionResultFetcher.class));

        TreeSet<String> ranking = olympics.getNationsRankList();

        assertTrue(ranking.isEmpty(), "Ranking should be empty when no nations have medals");
    }

}
