package com.libertas.vipaas.data.repository;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "genre", path = "genre")
public interface GenreRepository extends MongoRepository<Genre, String> {
	Page findByTenantId(@Param("tenantId") String tenantId,Pageable p);
	Page findByNameAndTenantId(@Param("name") String name,@Param("tenantId") String tenantId,Pageable p);


}
