package org.automation.dao;

import java.util.Optional;

import org.automation.model.ProductResultHistoryActive;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductResultHistoryActiveRepository extends MongoRepository<ProductResultHistoryActive,String> {
    Optional<ProductResultHistoryActive> findByName(String name);
}
