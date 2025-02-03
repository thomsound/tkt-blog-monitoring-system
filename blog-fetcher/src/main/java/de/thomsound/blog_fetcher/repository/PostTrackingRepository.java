package de.thomsound.blog_fetcher.repository;

import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class PostTrackingRepository {
    private final Set<Integer> postIds = new HashSet<>();

    public Set<Integer> prune(Set<Integer> currentPostIds) {
        Set<Integer> stalePostIds = this.postIds.stream()
                .filter(id -> !currentPostIds.contains(id))
                .collect(Collectors.toSet());

        this.postIds.clear();
        this.postIds.addAll(currentPostIds);

        return stalePostIds;
    }
}
