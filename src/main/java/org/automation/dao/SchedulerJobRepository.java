package org.automation.dao;

import java.util.List;

import org.automation.model.SchedulerJob;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchedulerJobRepository extends MongoRepository<SchedulerJob,String> {
   List<SchedulerJob> findByStatus(String status);
   List<SchedulerJob> findByStatusIn(List<String> status);
}
