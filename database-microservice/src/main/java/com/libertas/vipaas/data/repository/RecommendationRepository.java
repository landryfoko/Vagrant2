package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "recommendation", path = "recommendation")
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {

	Page findByProductIdAndTenantId(@Param("productId") String productId,@Param("tenantId") String tenantId, Pageable p);
	
}
