package org.automation.dao;

import java.util.List;

import org.automation.model.ProductResultHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductResultHistoryRepository extends MongoRepository<ProductResultHistory,String> {
  @Query("{L1Name:?0,productresult:{$gt:?1}}")	
  List<ProductResultHistory> findWithProdcutResult(String name,Long prodResult);
}
