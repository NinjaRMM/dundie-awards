package com.ninjaone.dundie_awards.organization;

import org.springframework.stereotype.Component;


@Component
public class AwardsCache {
    private int totalAwards;

    public void setTotalAwards(int totalAwards) {
        this.totalAwards = totalAwards;
    }

    public int getTotalAwards(){
        return totalAwards;
    }

    public void addOneAward(){
        this.totalAwards += 1;
    }
}
