package de.thomsound.blog_fetcher.repository;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PostTrackingRepositoryTest {

    @InjectMocks
    PostTrackingRepository repository;

    @ParameterizedTest
    @MethodSource("casesPrune")
    void prune(List<Set<Integer>> in, Set<Integer> expected) {
        Set<Integer> staleIds = null;

        for(Set<Integer> ids : in) {
            staleIds = repository.prune(ids);
        }

        assertThat(staleIds).isEqualTo(expected);
    }
    private static Stream<Arguments> casesPrune() {
        return Stream.of(
                Arguments.of(List.of(Set.of()), Set.of()),
                Arguments.of(List.of(Set.of(1, 2)), Set.of()),
                Arguments.of(List.of(Set.of(1, 2), Set.of(1, 2)), Set.of()),
                Arguments.of(List.of(Set.of(1, 2), Set.of(1, 2, 3)), Set.of()),
                Arguments.of(List.of(Set.of(1, 2, 3), Set.of(1, 3)), Set.of(2)),
                Arguments.of(List.of(Set.of(1, 2, 3), Set.of(2, 3, 4)), Set.of(1)),
                Arguments.of(List.of(Set.of(1, 2, 3), Set.of(1, 3, 4), Set.of(1, 3)), Set.of(4))
        );
    }
}