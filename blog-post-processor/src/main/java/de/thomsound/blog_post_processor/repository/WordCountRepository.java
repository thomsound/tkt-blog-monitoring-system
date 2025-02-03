package de.thomsound.blog_post_processor.repository;

import de.thomsound.blog_post_processor.model.Post;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class WordCountRepository {
    private final Map<Integer, Map<String, Integer>> wordCountsById = new HashMap<>();
    private final Map<String, Integer> wordCountsTotal = new HashMap<>();

    public Map<String, Integer> getWordCountsTotal() {
        return wordCountsTotal;
    }

    public Map<String, Integer> getWordCounts(int id) {
        Map<String, Integer> counts = new HashMap<>();
        if (this.wordCountsById.containsKey(id)) {
            counts.putAll(this.wordCountsById.get(id));
        }
        return counts;
    }

    public void applyDelta(Post post, Map<String, Integer> wordCountDelta) {
        this.wordCountsById.merge(post.id(), new HashMap<>(wordCountDelta), (prev, delta) -> {
            mergeWordCountMaps(prev, delta);
            return prev;
        });
        mergeWordCountMaps(this.wordCountsTotal, wordCountDelta);
    }

    private void mergeWordCountMaps(Map<String, Integer> map, Map<String, Integer> delta) {
        for (Map.Entry<String, Integer> entry : delta.entrySet()) {
            map.merge(
                    entry.getKey(),
                    entry.getValue(),
                    (cnt, diff) -> (cnt + diff == 0) ? null : cnt + diff);
        }
    }
}
