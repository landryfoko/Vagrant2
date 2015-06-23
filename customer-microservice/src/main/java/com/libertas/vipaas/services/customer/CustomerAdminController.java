package com.libertas.vipaas.services.customer;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoEmailInRequestException;
import com.libertas.vipaas.common.exceptions.NoPasswordInRequestException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping("/v1/admin/customer")
@Slf4j
public class CustomerAdminController {
    @Autowired
    private CustomerService service;

    public JSONObject error(final JSONObject resource) {
        return new JSONObject();
    }

    public JSONObject error(final String text) {
        return new JSONObject();
    }

    public JSONObject error(final String text, final JSONObject resource) {
        return new JSONObject();
    }

    @RequestMapping(value = "/email/{email}", method = RequestMethod.GET)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject findCustomerByEmail(@PathVariable final String email) throws Exception {
        return service.getCustomerByEmail(email);
    }

    @RequestMapping(method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject validateCredentials(@RequestBody final JSONObject customer) throws Exception {
        if (StringUtils.isEmpty((String) customer.get("email")))
            throw new NoEmailInRequestException("Missing email in request");
        if (StringUtils.isEmpty((String) customer.get("password")))
            throw new NoPasswordInRequestException("Missing password in request");
        return service.validateCredentials(customer.get("email") + "", customer.get("password") + "");
    }
}
