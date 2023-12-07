package com.ninjaone.dundie_awards.repository;

import com.ninjaone.dundie_awards.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

}
