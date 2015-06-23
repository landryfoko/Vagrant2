package com.libertas.vipaas.services.watchhistory;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "", "/v1/watchhistory" })
public class WatchHistoryController {
    @Autowired
    private WatchHistoryService watchHistoryService;

    @RequestMapping(method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject createWatchHistory(@RequestParam(value = "productId") final String productId, @RequestBody final JSONObject metadata) throws Exception {
        return watchHistoryService.createWatchHistoryEntry(productId, metadata);
    }

    @RequestMapping(value = "/{watchHistoryId}", method = RequestMethod.DELETE)
    @HystrixCommand(fallbackMethod = "error")
    public void deleteWatchHistoryEntryById(@PathVariable("watchHistoryId") final String watchHistoryId) throws Exception {
        watchHistoryService.deleteWatchHistoryEntryById(watchHistoryId);
    }

    public JSONObject error(final Integer pageNumber, final Integer pageSize) {
        return new JSONObject();
    }

    public JSONObject error(final JSONObject device) {
        return new JSONObject();
    }

    public JSONObject error(final String text) {
        return new JSONObject();
    }

    public JSONObject error(final String text, final JSONObject device) {
        return new JSONObject();
    }

    public JSONObject error(final String watchHistoryId, final String productId, final JSONObject device) {
        return new JSONObject();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject findAll(@RequestParam(value = "pageSize") final Integer pageSize, @RequestParam(value = "pageNumber") final Integer pageNumber,
            @RequestParam(value = "sortField", required = false) final String sortField, @RequestParam(value = "sortOrder", required = false) final String sortOrder) throws Exception {
        return watchHistoryService.findAll(pageSize, pageNumber, sortField, sortOrder);
    }
}
