package com.ninjaone.dundie_awards.util;

import lombok.experimental.UtilityClass;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.List;

@UtilityClass
public class BatchUtil {

    public List<List<Long>> chunkIntoBatches(List<Long> ids, int batchSize) {
        if (batchSize <= 0) throw new IllegalArgumentException("Batch size must be greater than zero");

        return IntStream.range(0, (int) Math.ceil((double) ids.size() / batchSize))
                .mapToObj(i -> ids.subList(i * batchSize, Math.min((i + 1) * batchSize, ids.size())))
                .collect(Collectors.toList());
    }
}
