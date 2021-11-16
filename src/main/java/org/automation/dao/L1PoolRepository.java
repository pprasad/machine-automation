package org.automation.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.automation.model.L1PoolEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface L1PoolRepository extends MongoRepository<L1PoolEntity,String>{
	
   List<L1PoolEntity> findByValue(String value);
   
   List<L1PoolEntity> findByValueIn(List<String> value);
   
   Optional<L1PoolEntity> findByNameAndEndDateAndValueIn(String name,Date date,List<String> value);
   
   @Aggregation(pipeline={"{$group:{_id:$L1Name,maxDate:{$max:$enddate}}}"})
   List<L1PoolEntity> groupBy();
}
