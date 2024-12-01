package com.ninjaone.dundie_awards.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RollbackDataComparator {

    public static List<Diff> findDifferences(String originalData, String comparisonData) {
        Map<Long, Integer> originalMap = parseToMap(originalData);
        Map<Long, Integer> comparisonMap = parseToMap(comparisonData);

        List<Diff> differences = new ArrayList<>();
        
        originalMap.forEach((id, originalValue) -> {
            int unexpectedValue = comparisonMap.getOrDefault(id, -1);
            if (unexpectedValue != originalValue) {
                differences.add(new Diff(id, originalValue, unexpectedValue));
            }
        });

        return differences;
    }

    private static Map<Long, Integer> parseToMap(String data) {
        if (data == null || data.trim().isEmpty()) {
            return Map.of(); 
        }
        return Arrays.stream(data.split("\\|"))
            .map(record -> record.split(",", 2))
            .filter(fields -> fields.length == 2)
            .map(fields -> {
                try {
                    Long id = Long.parseLong(fields[0].trim());
                    Integer value = Integer.parseInt(fields[1].trim());
                    return new AbstractMap.SimpleEntry<>(id, value);
                } catch (NumberFormatException e) {
                    return null; 
                }
            })
            .filter(entry -> entry != null)
            .collect(Collectors.toMap(
                AbstractMap.SimpleEntry::getKey,
                AbstractMap.SimpleEntry::getValue
            ));
    }


    // Static record to represent differences
    public static record Diff(Long id, int originalValue, int unexpectedValue) {}
}
