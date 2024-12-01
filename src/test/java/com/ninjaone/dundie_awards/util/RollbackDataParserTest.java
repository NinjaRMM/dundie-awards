package com.ninjaone.dundie_awards.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.AbstractMap;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class RollbackDataParserTest {

    @Test
    void shouldParseValidRollbackData() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = "1,10|2,20|3,30";

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(
                new AbstractMap.SimpleEntry<>(1L, 10),
                new AbstractMap.SimpleEntry<>(2L, 20),
                new AbstractMap.SimpleEntry<>(3L, 30)
        );
    }

    @Test
    void shouldReturnEmptySetWhenRollbackDataIsNull() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = null;

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptySetWhenRollbackDataIsEmpty() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = "";

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSkipInvalidRecords() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = "1,10|invalid|2,20|3,abc|4,40";

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(
                new AbstractMap.SimpleEntry<>(1L, 10),
                new AbstractMap.SimpleEntry<>(2L, 20),
                new AbstractMap.SimpleEntry<>(4L, 40)
        );
    }

    @Test
    void shouldHandleWhitespaceInRollbackData() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = " 1 , 10 | 2 , 20 | 3 , 30 ";

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyInAnyOrder(
                new AbstractMap.SimpleEntry<>(1L, 10),
                new AbstractMap.SimpleEntry<>(2L, 20),
                new AbstractMap.SimpleEntry<>(3L, 30)
        );
    }

    @Test
    void shouldHandleDuplicateEntries() {
        UUID uuid = UUID.randomUUID();
        String rollbackData = "1,10|2,20|1,10";

        Set<AbstractMap.SimpleEntry<Long, Integer>> result = RollbackDataParser.parse(uuid, rollbackData);

        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(
                new AbstractMap.SimpleEntry<>(1L, 10),
                new AbstractMap.SimpleEntry<>(2L, 20)
        );
    }
}
