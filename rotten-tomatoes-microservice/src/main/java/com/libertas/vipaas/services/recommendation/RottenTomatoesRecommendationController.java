package com.libertas.vipaas.services.recommendation;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping({ "/v1/rt-recommendation", "/" })
public class RottenTomatoesRecommendationController {

    @Autowired
    private RottenTomatoesRecommendationService rottenTomatoesRecommendationService;

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

    public JSONObject error(final String recommendationId, final String productId, final JSONObject device) {
        return new JSONObject();
    }

    @RequestMapping(value = "/product/{productId}", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject getRecommendationByProductId(@PathVariable("productId") final String productId) throws Exception {
        return rottenTomatoesRecommendationService.getRecommendationByProductId(productId);
    }

    @RequestMapping("/")
    public String index() {
        return "Miguel";
    }

}
