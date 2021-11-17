package org.automation.dao;

import java.util.List;

import org.automation.model.SchedulerJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerJobRepo extends JpaRepository<SchedulerJobEntity,String>{
    List<SchedulerJobEntity> findByStatus(String status);
    List<SchedulerJobEntity> findByStatusIn(List<String> status);
}
