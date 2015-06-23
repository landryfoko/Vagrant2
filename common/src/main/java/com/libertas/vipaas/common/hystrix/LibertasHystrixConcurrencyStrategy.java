package com.libertas.vipaas.common.hystrix;

import java.util.concurrent.Callable;

import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;


public class LibertasHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
	@Override
	public Callable wrapCallable(Callable callable) {
		return new HystrixContextCallable(callable);
	}
}