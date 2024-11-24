package com.ninjaone.dundie_awards;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;


@Component
@Getter
@Setter
public class AwardsCache {
    private int totalAwards;

    public void addOneAward(){
        this.totalAwards += 1;
    }
}
