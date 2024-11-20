package com.ninjaone.dundie_awards.cache;

import org.springframework.stereotype.Component;


@Component
public class AwardsCache {
    private volatile int totalAwards;

    public synchronized void setTotalAwards(int totalAwards) {
        this.totalAwards = totalAwards;
    }

    public synchronized int getTotalAwards(){
        return totalAwards;
    }

    public synchronized void addOneAward(){
        this.totalAwards += 1;
    }

    public synchronized void addAwards(int amount) {
        this.totalAwards+=amount;
    }
}
