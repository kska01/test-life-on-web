package com.lifeon.test.domain.plan;

import com.lifeon.test.domain.plan.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanFileRepository extends JpaRepository<Plan, Long> {

}
