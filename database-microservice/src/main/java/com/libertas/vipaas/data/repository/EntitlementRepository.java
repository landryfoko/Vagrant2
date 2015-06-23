package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "entitlement", path = "entitlement")
public interface EntitlementRepository extends MongoRepository<Entitlement, String> {
	Page findByCustomerIdAndTenantId(@Param("customerId") String customerId,@Param("tenantId") String tenantId, Pageable p);
	Page findByCustomerIdAndTenantIdAndProductId(@Param("customerId") String customerId,@Param("tenantId") String tenantId,@Param("productId") String productId, Pageable p);

	
}
