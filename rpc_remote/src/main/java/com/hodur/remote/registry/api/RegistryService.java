package com.hodur.remote.registry.api;

import com.hodur.common.URL;

import java.util.List;

public interface RegistryService {
    void register(URL url);

    void unregister(URL url);

    List<URL> lookup(URL url);

}
