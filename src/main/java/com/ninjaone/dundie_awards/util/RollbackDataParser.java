package com.ninjaone.dundie_awards.util;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RollbackDataParser {

    public Set<AbstractMap.SimpleEntry<Long, Integer>> parse(UUID uuid, String savedRollbackData) {
    	log.info("UUID: {} - RollbackDataParser - Parsing rollback data", uuid);
        if (savedRollbackData == null || savedRollbackData.isBlank()) {
            log.info("UUID: {} - Rollback data is null or empty.");
            return Set.of();
        }

        Set<AbstractMap.SimpleEntry<Long, Integer>> parsedData = Arrays.stream(savedRollbackData.split("\\|"))
                .map(record -> record.split(",", 2))
                .filter(fields -> fields.length == 2)
                .map(fields -> {
                    try {
                        Long id = Long.parseLong(fields[0].trim());
                        Integer value = Integer.parseInt(fields[1].trim());
                        return new AbstractMap.SimpleEntry<>(id, value);
                    } catch (NumberFormatException e) {
                        log.info("Invalid record: {}", String.join(",", fields));
                        return null;
                    }
                })
                .filter(entry -> entry != null)
                .collect(Collectors.toSet());
        log.info("UUID: {} - RollbackDataParser - Parsed rollback data", uuid);
        return parsedData;
    }
}
