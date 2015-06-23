package com.libertas.vipaas.services.watchhistory;

import org.json.simple.JSONObject;

import com.libertas.vipaas.common.exceptions.NoSuchProductException;
import com.libertas.vipaas.common.exceptions.NoSuchWatchHistoryEntryException;

public interface WatchHistoryService {
	JSONObject createWatchHistoryEntry(String productId,JSONObject  metadata) throws NoSuchProductException;
		void deleteWatchHistoryEntryById(String promotionId) throws NoSuchWatchHistoryEntryException;
		JSONObject findAll(Integer pageSize,Integer pageNumber, String sortField, String sortOrder);

}
