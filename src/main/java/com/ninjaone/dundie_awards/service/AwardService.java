package com.ninjaone.dundie_awards.service;

import java.util.UUID;

public interface AwardService {

    int giveDundieAwards(UUID requestId, long organizationId);
}
