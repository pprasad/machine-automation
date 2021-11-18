package org.automation.dao;

import java.util.Date;
import java.util.List;

import org.automation.model.AlarmHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface AlarmHistoryRepository extends MongoRepository<AlarmHistory,String> {
	
	@Query("{enddate:{$lte:?0},type:{$ne:?1}}")
    List<AlarmHistory> findByEndDateLessThanEqualAndTypeNotContain(Date date,String type);
	
	//@Query("{updatedate:{$gte:?0},enddate:{$lte:?1},type:{$ne:?2}}")
	@Query("{enddate:{$gte:?0,$lte:?1},type:{$ne:?2}}")
    List<AlarmHistory> findByEndDateBetweenAndTypeNotContain(Date startDate,Date endDate,String type);
}
