package bg.sofia.uni.fmi.mjt.socialnetwork;

import bg.sofia.uni.fmi.mjt.socialnetwork.comparators.SortUsersByNumberOfFriends;
import bg.sofia.uni.fmi.mjt.socialnetwork.exception.UserRegistrationException;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.Post;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.ReactionType;
import bg.sofia.uni.fmi.mjt.socialnetwork.post.SocialFeedPost;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.util.*;

public class SocialNetworkImpl implements SocialNetwork {
    private Set<UserProfile> users;
    private Collection<Post> posts;

    public SocialNetworkImpl() {
        this.users = new HashSet<>();
        this.posts = new HashSet<>();
    }

    @Override
    public void registerUser(UserProfile userProfile) throws UserRegistrationException {
        if(userProfile == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if(users.contains(userProfile)) {
            throw new UserRegistrationException("User is already registered");
        }

        users.add(userProfile);
    }

    @Override
    public Set<UserProfile> getAllUsers() {
        return Collections.unmodifiableSet(users);
    }

    @Override
    public Post post(UserProfile userProfile, String content) throws UserRegistrationException {
        if(userProfile == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if(content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }

        if(!users.contains(userProfile)) {
            throw new UserRegistrationException("User is not registered");
        }

        Post newPost = new SocialFeedPost(userProfile, content);
        posts.add(newPost);
        return newPost;
    }

    @Override
    public Collection<Post> getPosts() {
        return Collections.unmodifiableCollection(posts);
    }

    @Override
    public Set<UserProfile> getReachedUsers(Post post) {
        if(post == null){
            throw new IllegalArgumentException("Post cannot be null");
        }

        Set<UserProfile> result = new HashSet<>();

        Set<UserProfile> visited = new HashSet<>();
        Queue<UserProfile> queue = new LinkedList<>();

        UserProfile author = post.getAuthor();
        visited.add(author);
        queue.add(author);

        while (!queue.isEmpty()) {
            UserProfile current = queue.poll();

            for (UserProfile friend : current.getFriends()) {
                if (!visited.contains(friend)) {
                    visited.add(friend);
                    queue.add(friend);

                    if (hasCommonInterest(author, friend)) {
                        result.add(friend);
                    }
                }
            }
        }

        return result;
    }

    private boolean hasCommonInterest(UserProfile author, UserProfile friend) {
        for(Interest interest : author.getInterests()) {
            if(friend.getInterests().contains(interest)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<UserProfile> getMutualFriends(UserProfile userProfile1, UserProfile userProfile2) throws UserRegistrationException {
        if(userProfile1 == null || userProfile2 == null) {
            throw new IllegalArgumentException("Users cannot be null");
        }

        if(!users.contains(userProfile1) || !users.contains(userProfile2)) {
            throw new UserRegistrationException("User is not registered");
        }

        Set<UserProfile> commonFriends = new HashSet<>();
        Set<UserProfile> friendsOfUser2 = new HashSet<>(userProfile2.getFriends());

        for(UserProfile friend : userProfile1.getFriends()) {
            if(friendsOfUser2.contains(friend)) {
                commonFriends.add(friend);
            }
        }

        return commonFriends;
    }

    @Override
    public SortedSet<UserProfile> getAllProfilesSortedByFriendsCount() {
        SortedSet<UserProfile> sortedUsers = new TreeSet<>(new SortUsersByNumberOfFriends());
        sortedUsers.addAll(users);

        return sortedUsers;
    }
}
