package com.api.apimonitoring;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface IMonitoringRepository extends MongoRepository<Monitors, String> {

	@Query(value="{'_id':?0}")
	public Optional<Monitors> findById(String id);
	
	 @Query(value="{'statusCode': { $eq:200 } }",count=true)
     long findTotalSuccessCount();
	
	//Long countBy
	
	 @Query(value="{'_id':?0}") 
	 Monitors findOneMonitor(String id);
	 
	 @Query(value="{'_id' : ?0}", delete = true)
	 public void deleteBy(String id);
	
}
