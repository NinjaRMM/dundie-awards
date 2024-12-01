package com.ninjaone.dundie_awards.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

class RollbackDataComparatorTest {



    @Test
    void shouldReturnEmptyListWhenNoDifferences() {
        String original = "1,10|2,5|3,8|4,12";
        String updated = "1,10|2,5|3,8|4,12";

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, updated);

        assertThat(differences).isEmpty();
    }

    @Test
    void shouldHandleEmptyUpdatedData() {
        String original = "1,10|2,5|3,8|4,12";
        String updated = "";

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, updated);

        assertThat(differences).hasSize(4);
        assertThat(differences).containsExactlyInAnyOrder(
                new RollbackDataComparator.Diff(1L, 10, -1),
                new RollbackDataComparator.Diff(2L, 5, -1),
                new RollbackDataComparator.Diff(3L, 8, -1),
                new RollbackDataComparator.Diff(4L, 12, -1)
        );
    }

    @Test
    void shouldHandleLargeDataEfficiently() {
        String original = generateLargeRollbackData(1, 3_000_000);
        String updated = generateLargeRollbackDataWithDifferences(1, 3_000_000, Map.of(2L, 20, 3_000_000L, 30));

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, updated);

        assertThat(differences).hasSize(2);
        assertThat(differences).containsExactlyInAnyOrder(
                new RollbackDataComparator.Diff(2L, 10, 20),
                new RollbackDataComparator.Diff(3_000_000L, 10, 30)
        );
    }

    private String generateLargeRollbackData(long startId, long endId) {
        StringBuilder builder = new StringBuilder();
        for (long id = startId; id <= endId; id++) {
            builder.append(id).append(",").append(10).append("|");
        }
        return builder.substring(0, builder.length() - 1);
    }

    private String generateLargeRollbackDataWithDifferences(long startId, long endId, Map<Long, Integer> differences) {
        StringBuilder builder = new StringBuilder();
        for (long id = startId; id <= endId; id++) {
            int value = differences.getOrDefault(id, 10);
            builder.append(id).append(",").append(value).append("|");
        }
        return builder.substring(0, builder.length() - 1);
    }
    
    @Test
    void shouldIgnoreInvalidRecordsWithIncorrectFieldCount() {
        String original = "1,10|2,5|invalidRecord|3,8|4,12,,extra";

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, original);

        assertThat(differences).isEmpty();
    }
    
    @Test
    void shouldIgnoreInvalidRecordsWithNumberFormatException() {
        String original = "1,10|2,invalid|3,8|notANumber,12";

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, original);

        assertThat(differences).isEmpty();
    }
    
    @Test
    void shouldFilterOutNullEntries() {
        String original = "1,10|nullValue|2,5|notANumber,8";

        List<RollbackDataComparator.Diff> differences = RollbackDataComparator.findDifferences(original, original);

        assertThat(differences).isEmpty();
    }



}
