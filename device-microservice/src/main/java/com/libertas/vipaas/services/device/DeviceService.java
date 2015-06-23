package com.libertas.vipaas.services.device;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.DuplicateDeviceException;
import com.libertas.vipaas.common.exceptions.NoSuchDeviceException;

public interface DeviceService {


     JSONObject  registerDevice( String deviceId, JSONObject device) throws DuplicateDeviceException ;
	 JSONObject findAll(Integer pageSize, Integer pageNumber, String sortField, String sortOrder);
	  void deleteDevice(String deviceId) throws NoSuchDeviceException;
	void updateDevice(String deviceId, JSONObject device)throws NoSuchDeviceException;
	JSONObject getDeviceById(String deviceId) throws NoSuchDeviceException;
}
