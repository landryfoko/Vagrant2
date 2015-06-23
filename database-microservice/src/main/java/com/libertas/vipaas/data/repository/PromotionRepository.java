package com.libertas.vipaas.data.repository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.libertas.vipaas.data.model.*;


@RepositoryRestResource(collectionResourceRel = "promotion", path = "promotion")
public interface PromotionRepository extends MongoRepository<Promotion, String> {


	Page findByTenantId(@Param("tenantId") String tenantId, Pageable p);
	List<Promotion> findByNameAndTenantId(@Param("name") String name,@Param("tenantId") String tenantId);
	Page findByTagsAndTenantId(@Param("tag") String tag,@Param("tenantId") String tenantId,Pageable p);

}