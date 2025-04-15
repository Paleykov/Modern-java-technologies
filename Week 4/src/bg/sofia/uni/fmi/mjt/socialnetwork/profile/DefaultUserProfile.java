package bg.sofia.uni.fmi.mjt.socialnetwork.profile;

import java.util.*;

public class DefaultUserProfile implements UserProfile {
    private String username;
    Collection<Interest> interests;
    Collection<UserProfile> friends;


    @Override
    public String getUsername() {
        return this.username;
    }

    public DefaultUserProfile(String username) {
        this.username = username;
        this.interests = new HashSet<>();
        this.friends = new HashSet<>();
    }

    @Override
    public Collection<Interest> getInterests() {
        return Collections.unmodifiableCollection(this.interests);
    }

    @Override
    public boolean addInterest(Interest interest) {
        if(interest == null) {
            throw new IllegalArgumentException("interest cannot be null");
        }

        if(this.interests.contains(interest)) {
            return false;
        } else {
            this.interests.add(interest);
            return true;
        }
    }

    @Override
    public boolean removeInterest(Interest interest) {
        if(interest == null) {
            throw new IllegalArgumentException("interest cannot be null");
        }

        if(!this.interests.contains(interest)) {
            return false;
        } else {
            this.interests.remove(interest);
            return true;
        }
    }

    @Override
    public Collection<UserProfile> getFriends() {
        return Collections.unmodifiableCollection(this.friends);
    }

    @Override
    public boolean addFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        if (isFriend(userProfile)) {
            return false;
        }

        this.friends.add(userProfile);

        // Make sure the friendship is mutual
        if (!userProfile.isFriend(this)) {
            userProfile.addFriend(this);
        }

        return true;
    }

    @Override
    public boolean unfriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        if (!isFriend(userProfile)) {
            return false;
        }

        this.friends.remove(userProfile);

        if (userProfile.isFriend(this)) {
            userProfile.unfriend(this);
        }

        return true;
    }

    @Override
    public boolean isFriend(UserProfile userProfile) {
        if (userProfile == null) {
            throw new IllegalArgumentException("userProfile cannot be null");
        }

        return this.friends.contains(userProfile);
    }
}
