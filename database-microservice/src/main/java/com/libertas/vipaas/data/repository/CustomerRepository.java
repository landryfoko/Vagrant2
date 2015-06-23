package com.libertas.vipaas.data.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "customer", path = "customer")
public interface CustomerRepository extends MongoRepository<Customer, String> {

	List<Customer> findByLastName(@Param("lastName") String name);
	List<Customer> findByFirstName(@Param("firstName") String name);
	List<Customer> findByEmailAndTenantId(@Param("email") String email,@Param("tenantId") String tenantId);
	List<Customer> findByIdIn(@Param("ids") String [] ids);

}