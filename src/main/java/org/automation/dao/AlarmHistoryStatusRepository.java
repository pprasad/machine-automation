package org.automation.dao;

import org.automation.model.AlarmHistoryStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlarmHistoryStatusRepository extends MongoRepository<AlarmHistoryStatus,String> {

}
