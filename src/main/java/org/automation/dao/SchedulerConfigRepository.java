package org.automation.dao;

import org.automation.model.SchedulerConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchedulerConfigRepository extends MongoRepository<SchedulerConfig,String>{

}
