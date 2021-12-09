package org.automation.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.automation.model.SchedulerJob;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface SchedulerJobRepository extends MongoRepository<SchedulerJob,String> {
   
   List<SchedulerJob> findByStatus(String status);
   
   List<SchedulerJob> findByStatusIn(List<String> status);
   
   @Aggregation(pipeline= {"{$group:{_id:null,minDate:{$max:'$startdate'}}}"})
   Optional<Date> findMaxDate();
 
   Optional<SchedulerJob> findByNameAndAlarmNameAndStatusIn(String name,String alarmName,List<String> status);
   
   @Query("{$and:[{L1Name:?0},{alarm_name:?1},{status:{$in:?2}},{prod_start_date:null}]}")
   Optional<SchedulerJob> findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(String name,String alarmName,List<String> status);
   
   @Query("{$and:[{L1Name:?0},{alarmName:?1},{end_date:{$gte:?2,$lte:?3}},{status:{$in:?4}}]}")
   Optional<SchedulerJob> findByNameAndAlarmNameWithStatusAndEndDateProductStartDateIsNull(String name,String alarmName,Date startDate,Date endDate,List<String> status);
 
   @Query("{$and:[{L1Name:?0},{alarmName:?1},{status:{$in:?2}},{prod_start_date:?3}]}")
   Optional<SchedulerJob> findByNameAndAlarmNameAndStatusInAndProdStartDateIsNull(String name,String alarmName,List<String> status,Date prodStartDate); 

}