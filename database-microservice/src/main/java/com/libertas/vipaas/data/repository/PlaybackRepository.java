package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.Playback;

@RepositoryRestResource(collectionResourceRel = "playback", path = "playback")
public interface PlaybackRepository extends MongoRepository<Playback, String> {

	List<Playback> findByCustomerIdAndProductIdAndTenantIdAndStatus(@Param("customerId") String customerId,@Param("productId") String productId,@Param("tenantId") String tenantId, @Param("status") String status);

	
}