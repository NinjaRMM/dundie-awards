package com.ninjaone.dundie_awards.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BatchUtilTest {

    @Test
    void shouldChunkIntoBatchesCorrectly() {
        List<Long> ids = LongStream.rangeClosed(1, 3_000_000).boxed().toList();
        int batchSize = 1000;

        List<List<Long>> batches = BatchUtil.chunkIntoBatches(ids, batchSize);

        assertThat(batches).hasSize(3000);
        assertThat(batches.get(0)).hasSize(1000);
        assertThat(batches.get(2999)).hasSize(1000);
        assertThat(batches.get(0).get(0)).isEqualTo(1L);
        assertThat(batches.get(2999).get(999)).isEqualTo(3_000_000L);
    }

    @Test
    void shouldThrowExceptionForInvalidBatchSize() {
        List<Long> ids = List.of(1L, 2L, 3L);
        int batchSize = 0;

        assertThatThrownBy(() -> BatchUtil.chunkIntoBatches(ids, batchSize))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Batch size must be greater than zero");
    }
}
