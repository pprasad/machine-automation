package org.automation.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.automation.model.SchedulerJob;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchedulerJobRepository extends MongoRepository<SchedulerJob,String> {
   List<SchedulerJob> findByStatus(String status);
   List<SchedulerJob> findByStatusIn(List<String> status);
   @Aggregation(pipeline= {"{$group:{_id:null,minDate:{$max:'$startdate'}}}"})
   Optional<Date> findMaxDate();
}