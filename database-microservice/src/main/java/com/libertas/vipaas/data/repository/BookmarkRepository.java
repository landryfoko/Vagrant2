package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "bookmark", path = "bookmark")
public interface BookmarkRepository extends MongoRepository<Bookmark, String> {

	Page findByCustomerIdAndTenantId(@Param("customerId") String customerId,@Param("tenantId") String tenantId, Pageable p);
	List<Bookmark> findByIdAndCustomerIdAndTenantId(@Param("bookmarkId") String bookmarkId,@Param("customerId") String customerId,@Param("tenantId") String tenantId);

}