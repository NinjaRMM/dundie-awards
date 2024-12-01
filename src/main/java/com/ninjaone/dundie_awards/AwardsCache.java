package com.ninjaone.dundie_awards;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
public class AwardsCache {
    private int totalAwards;

    public synchronized void addOneAward() {
        this.totalAwards += 1;
    }

    public synchronized void addAwards(int total) {
        this.totalAwards += total;
    }

    public synchronized void removeAwards(int total) {
        this.totalAwards -= total;
    }
}

