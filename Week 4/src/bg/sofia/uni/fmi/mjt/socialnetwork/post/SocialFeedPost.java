package bg.sofia.uni.fmi.mjt.socialnetwork.post;

import bg.sofia.uni.fmi.mjt.socialnetwork.profile.Interest;
import bg.sofia.uni.fmi.mjt.socialnetwork.profile.UserProfile;

import java.time.LocalDateTime;
import java.util.*;

public class SocialFeedPost implements Post {
    private final UserProfile author;
    private String content;
    private LocalDateTime publishedOn;
    private String uniqueId;
    private Map<ReactionType, Set<UserProfile>> reactions;

    public SocialFeedPost(UserProfile author, String content){
        this.author = author;
        this.content = content;
        this.publishedOn = LocalDateTime.now();
        this.uniqueId = UUID.randomUUID().toString();
        this.reactions = new HashMap<>();
    }

    @Override
    public String getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public UserProfile getAuthor() {
        return this.author;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return this.publishedOn;
    }

    @Override
    public String getContent() {
        return this.content;
    }

    private ReactionType getUserReaction(UserProfile userProfile) {
        for (Map.Entry<ReactionType, Set<UserProfile>> entry : reactions.entrySet()) {
            if (entry.getValue().contains(userProfile)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean addReaction(UserProfile userProfile, ReactionType reactionType) {
        if(userProfile == null || reactionType == null){
            throw new NullPointerException("userProfile and ReactionType cannot be null");
        }

        // User reacts with the same emoji
        if(reactions.containsKey(reactionType) && reactions.get(reactionType).contains(userProfile)){
            return false;
        }

        // Check if reaction exists as a key
        Set<UserProfile> users = reactions.get(reactionType);

        if (users == null) {
            users = new HashSet<>();
            reactions.put(reactionType, users);
        }

        // User has reacted before and now reacts with a different emoji
        ReactionType userReactionType = getUserReaction(userProfile);
        if(userReactionType != null){
            reactions.get(userReactionType).remove(userProfile);

            users.add(userProfile);
            return false;
        } else {
            //User has not reacted before

            users.add(userProfile);
            return true;
        }
    }

    @Override
    public boolean removeReaction(UserProfile userProfile) {
        if(userProfile == null){
            throw new NullPointerException("userProfile cannot be null");
        }

        ReactionType userReactionType = getUserReaction(userProfile);
        if(userReactionType != null){
            Set<UserProfile> users = reactions.get(userReactionType);
            users.remove(userProfile);

            return true;
        }

        return false;
    }

    @Override
    public Map<ReactionType, Set<UserProfile>> getAllReactions() {
        Map<ReactionType, Set<UserProfile>> unmodifiableMap = new HashMap<>();

        for (Map.Entry<ReactionType, Set<UserProfile>> entry : reactions.entrySet()) {
            unmodifiableMap.put(entry.getKey(), Collections.unmodifiableSet(entry.getValue()));
        }

        return Collections.unmodifiableMap(unmodifiableMap);
    }

    @Override
    public int getReactionCount(ReactionType reactionType) {
        return reactions.get(reactionType).size();
    }

    @Override
    public int totalReactionsCount() {
        int total = 0;

        for(Set<UserProfile> users : reactions.values()){
            total += users.size();
        }

        return total;
    }
}
