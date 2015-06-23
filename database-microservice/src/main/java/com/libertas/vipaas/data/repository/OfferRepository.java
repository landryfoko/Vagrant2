package com.libertas.vipaas.data.repository;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "offer", path = "offer")
public interface OfferRepository extends MongoRepository<Offer, String> {
	Page findByTenantId(@Param("tenantId") String tenantId,Pageable p);
	List<Offer> findByNameAndTenantId(@Param("name") String name,@Param("tenantId") String tenantId,Pageable p);
	List<Offer> findByIdIn(@Param("ids") String [] ids);

}
