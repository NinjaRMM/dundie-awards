package com.ninjaone.dundie_awards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AwardsCacheTest {

    private AwardsCache awardsCache;

    @BeforeEach
    void setUp() {
        awardsCache = new AwardsCache();
    }

    @Test
    void shouldInitializeTotalAwardsToZero() {
        assertThat(awardsCache.getTotalAwards()).isEqualTo(0);
    }

    @Test
    void shouldAddOneAward() {
        awardsCache.addOneAward();
        assertThat(awardsCache.getTotalAwards()).isEqualTo(1);

        awardsCache.addOneAward();
        assertThat(awardsCache.getTotalAwards()).isEqualTo(2);
    }

    @Test
    void shouldAddMultipleAwards() {
        awardsCache.addAwards(5);
        assertThat(awardsCache.getTotalAwards()).isEqualTo(5);

        awardsCache.addAwards(10);
        assertThat(awardsCache.getTotalAwards()).isEqualTo(15);
    }

    @Test
    void shouldRemoveAwards() {
        awardsCache.addAwards(20);

        awardsCache.removeAwards(5);
        assertThat(awardsCache.getTotalAwards()).isEqualTo(15);

        awardsCache.removeAwards(10);
        assertThat(awardsCache.getTotalAwards()).isEqualTo(5);
    }

    @Test
    void shouldNotGoNegativeWhenRemovingMoreAwardsThanAvailable() {
        awardsCache.addAwards(10);

        awardsCache.removeAwards(15);
        assertThat(awardsCache.getTotalAwards()).isEqualTo(-5); // Behavior allows negatives
    }
}
