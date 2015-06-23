package com.libertas.vipaas.common.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ "/cache"})
public class CacheController {


	@RequestMapping( method = RequestMethod.DELETE )
	@CacheEvict(value={"devices","bookmarks","entitlements","playbacks","promotions","ratings","watch","recommendations","genres","reviews","purchases","offers","products","devices","customers","tenants","creditcards","subscriptions"})
	public  void   deleteCache()  {
	}

}
