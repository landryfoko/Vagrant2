package com.libertas.vipaas.data.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.libertas.vipaas.data.model.*;

@RepositoryRestResource(collectionResourceRel = "device", path = "device")
public interface DeviceRepository extends MongoRepository<Device, String> {

	Page findByCustomerIdAndTenantId(@Param("customerId") String customerId,@Param("tenantId") String tenantId,Pageable p);
	List<Device> findByDeviceIdAndCustomerIdAndTenantId(@Param("deviceId") String id,@Param("customerId") String customerId,@Param("tenantId") String tenantId);

	
}