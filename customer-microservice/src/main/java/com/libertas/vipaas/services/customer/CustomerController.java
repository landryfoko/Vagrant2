package com.libertas.vipaas.services.customer;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.libertas.vipaas.common.exceptions.NoEmailInRequestException;
import com.libertas.vipaas.common.exceptions.NoNewPasswordInRequestException;
import com.libertas.vipaas.common.exceptions.NoOldPasswordInRequestException;
import com.libertas.vipaas.common.exceptions.NoPasswordInRequestException;
import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RequestMapping(value={"/v1/customer","/internal/v1/customer"})
@Slf4j
public class CustomerController {
    @Autowired
    private CustomerService service;

 @HystrixCommand(fallbackMethod = "error")
    @RequestMapping(value = "/password", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void changePassword(@RequestBody final JSONObject customer) throws Exception {
        if (StringUtils.isEmpty((String) customer.get("email")))
            throw new NoEmailInRequestException("Missing email in request");
        if (StringUtils.isEmpty((String) customer.get("oldPassword")))
            throw new NoOldPasswordInRequestException("Missing old password in request");
        if (StringUtils.isEmpty((String) customer.get("newPassword")))
            throw new NoNewPasswordInRequestException("Missing new password in request");
        service.changePassword((String) customer.get("email"), (String) customer.get("oldPassword"), (String) customer.get("newPassword"));
    }

    @RequestMapping(method = RequestMethod.POST)
   @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject createCustomer(@RequestBody final JSONObject customer) throws Exception {
        log.info("Customer creation started");
        if (StringUtils.isEmpty((String) customer.get("email")))
            throw new NoEmailInRequestException("Missing email in request");
        if (StringUtils.isEmpty((String) customer.get("password")))
            throw new NoPasswordInRequestException("Missing password in request");
        log.info("Customer creation validation completed");
        return service.createCustomer((String) customer.get("email"), (String) customer.get("password"), customer);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @HystrixCommand(fallbackMethod = "error")
    public void deleteCustomerById() throws Exception {
        service.deleteCustomerById((String)CredentialsThreadLocal.getCredentials().get("customerId"));
    }

    public JSONObject error(final JSONObject resource) {
        return new JSONObject();
    }

    public JSONObject error(final String text) {
        return new JSONObject();
    }

    public JSONObject error(final String text, final JSONObject resource) {
        return new JSONObject();
    }

    @RequestMapping(method = RequestMethod.GET)
 @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject getCustomerById() throws Exception {
        final JSONObject customer = service.getCustomerById((String)CredentialsThreadLocal.getCredentials().get("customerId"));
        return customer;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "error")
    public @ResponseBody JSONObject login(@RequestBody final JSONObject credentials) throws Exception {
        if (StringUtils.isEmpty((String) credentials.get("email")))
            throw new NoEmailInRequestException("Missing email in request");
        if (StringUtils.isEmpty((String) credentials.get("password")))
            throw new NoEmailInRequestException("Missing password in request");
        final JSONObject customer = service.login((String) credentials.get("email"), (String) credentials.get("password"));
        return customer;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @HystrixCommand(fallbackMethod = "error")
    public void logout(@RequestBody final JSONObject metadata) throws Exception {
        service.logout((String)CredentialsThreadLocal.getCredentials().get("customerId"), metadata);
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
 @HystrixCommand(fallbackMethod = "error")
    public void resetPassword(@RequestBody final JSONObject customer) throws Exception {
        if (StringUtils.isEmpty((String) customer.get("email")))
            throw new NoEmailInRequestException("Missing email in request");

        service.resetPassword((String) customer.get("email"));
    }

    @RequestMapping(method = RequestMethod.PUT)
    @HystrixCommand(fallbackMethod = "error")
    public void updateCustomer(@RequestBody final JSONObject customer) throws Exception {
        service.updateCustomer((String)CredentialsThreadLocal.getCredentials().get("customerId"), customer);
    }
}
