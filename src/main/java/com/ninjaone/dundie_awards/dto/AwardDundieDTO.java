package com.ninjaone.dundie_awards.dto;

public class AwardDundieDTO {

    int awardQuantity;

    public AwardDundieDTO() {}

    public AwardDundieDTO(int awardQuantity) {
        this.awardQuantity = awardQuantity;
    }

    public int getAwardQuantity() {
        return awardQuantity;
    }

    public void setAwardQuantity(int awardQuantity) {
        this.awardQuantity = awardQuantity;
    }
}
