package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "tenant", path = "tenant")
public interface TenantRepository extends MongoRepository<Tenant, String> {

	List<Tenant> findByOwnerId(@Param("ownerId") String ownerId);
}
