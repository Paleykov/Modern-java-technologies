package bg.sofia.uni.fmi.mjt.olympics.competitor;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AthleteTest {
    @Test
    void testAddMedalNullMedal() {
        Medal medal = null;
        Competitor competitor = new Athlete("123", "John Doe", "USA");

        assertThrows(IllegalArgumentException.class,
                () -> competitor.addMedal(medal),
                "AddMedal should throw an IllegalArgumentException");
    }

    @Test
    void testAddMedal() {
        Competitor competitor = new Athlete("123", "John Doe", "USA");
        Medal medal = Medal.GOLD;

        competitor.addMedal(medal);

        assertTrue(competitor.getMedals().contains(medal),
                "Medal should be added to the competitor's medals");
    }

    @Test
    void testEquals() {
        Athlete a1 = new Athlete("123", "John Doe", "USA");
        Athlete a2 = new Athlete("123", "John Doe", "USA");

        a1.addMedal(Medal.GOLD);
        a2.addMedal(Medal.GOLD);

        assertTrue(a1.equals(a2), "Athletes with same name, nationality, and medals should be equal");
    }

    @Test
    void testNotEquals() {
        Athlete a1 = new Athlete("123", "John Doe", "USA");
        Athlete a2 = new Athlete("456", "Jane Smith", "Canada");

        assertTrue(!a1.equals(a2), "Different athletes should not be equal");
    }

    @Test
    void testHashCodeConsistentWithEquals() {
        Athlete a1 = new Athlete("123", "John Doe", "USA");
        Athlete a2 = new Athlete("456", "John Doe", "USA");

        a1.addMedal(Medal.GOLD);
        a2.addMedal(Medal.GOLD);

        assertTrue(a1.equals(a2) && a1.hashCode() == a2.hashCode(),
                "Equal athletes should have the same hashCode");
    }
}
