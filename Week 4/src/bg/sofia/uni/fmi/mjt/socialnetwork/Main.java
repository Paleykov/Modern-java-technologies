package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.*;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Create users
            UserProfile alice = new DefaultUserProfile("alice");
            UserProfile bob = new DefaultUserProfile("bob");
            UserProfile charlie = new DefaultUserProfile("charlie");
            UserProfile dave = new DefaultUserProfile("dave");

            // 2. Add interests
            alice.addInterest(Interest.GAMES);
            alice.addInterest(Interest.MUSIC);

            bob.addInterest(Interest.MUSIC);
            bob.addInterest(Interest.SPORTS);

            charlie.addInterest(Interest.TRAVEL);
            charlie.addInterest(Interest.GAMES);

            dave.addInterest(Interest.FOOD);

            // 3. Make friends
            alice.addFriend(bob);
            bob.addFriend(charlie); // alice -> bob -> charlie

            // 4. Register users
            SocialNetworkImpl network = new SocialNetworkImpl();
            network.registerUser(alice);
            network.registerUser(bob);
            network.registerUser(charlie);
            network.registerUser(dave);

            // 5. Post
            Post post = network.post(alice, "Just played a new game and loved it!");

            // 6. Reactions
            post.addReaction(bob, ReactionType.LIKE);
            post.addReaction(charlie, ReactionType.LOVE);

            // 7. Show all reactions
            System.out.println("All reactions:");
            Map<ReactionType, Set<UserProfile>> reactions = post.getAllReactions();
            for (Map.Entry<ReactionType, Set<UserProfile>> entry : reactions.entrySet()) {
                System.out.print(entry.getKey() + ": ");
                for (UserProfile u : entry.getValue()) {
                    System.out.print(u.getUsername() + " ");
                }
                System.out.println();
            }

            // 8. Total reactions
            System.out.println("Total reactions: " + post.totalReactionsCount());

            // 9. Mutual friends
            Set<UserProfile> mutual = network.getMutualFriends(alice, charlie);
            System.out.print("Mutual friends of Alice and Charlie: ");
            for (UserProfile u : mutual) {
                System.out.print(u.getUsername() + " ");
            }
            System.out.println();

            // 10. Reached users
            Set<UserProfile> reached = network.getReachedUsers(post);
            System.out.print("Users who can see Alice's post: ");
            for (UserProfile u : reached) {
                System.out.print(u.getUsername() + " ");
            }
            System.out.println();

            // 11. Sorted users
            SortedSet<UserProfile> sortedProfiles = network.getAllProfilesSortedByFriendsCount();
            System.out.println("Users sorted by friend count:");
            for (UserProfile user : sortedProfiles) {
                System.out.println(user.getUsername() + " (" + user.getFriends().size() + " friends)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
