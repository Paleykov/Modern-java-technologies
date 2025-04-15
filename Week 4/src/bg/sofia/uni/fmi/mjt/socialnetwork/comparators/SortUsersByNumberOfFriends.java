package bg.sofia.uni.fmi.mjt.socialnetwork.comparators;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.util.Comparator;
import java.util.Objects;

// Returns a sorted set of all user profiles ordered by the number of friends they have in descending order.
public class SortUsersByNumberOfFriends implements Comparator<UserProfile> {
    @Override
    public int compare(UserProfile u1, UserProfile u2) {
        int size1 = u1.getFriends().size();
        int size2 = u2.getFriends().size();

        if (size1 != size2) {
            return Integer.compare(size2, size1);
        }

        return u1.getUsername().compareTo(u2.getUsername());
    }
}
