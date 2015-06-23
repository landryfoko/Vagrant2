package com.libertas.vipaas.data;

import java.io.Serializable;

import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.UniqueIdentifier;

public class DataCenterInfoImpl implements Serializable, UniqueIdentifier, DataCenterInfo {
    private static final long serialVersionUID = 1L;
    private final String id;

    public DataCenterInfoImpl(final String host, final int port) {
        id = String.format("%s:%s", host, port);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Name getName() {
        return Name.MyOwn;
    }
}
