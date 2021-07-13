package com.ypeckstadt.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.scalar.db.api.DistributedStorage;
import com.scalar.db.api.DistributedTransactionManager;
import com.scalar.db.config.DatabaseConfig;
import com.scalar.db.service.StorageModule;
import com.scalar.db.service.StorageService;
import com.scalar.db.service.TransactionModule;
import com.scalar.db.service.TransactionService;

import java.io.File;
import java.io.IOException;

public class LocallyConfiguredCassandraFactory implements ScalarDbFactory {
    private static final String SCALARDB_PROPERTIES = "scalardb.properties";

    private DatabaseConfig dbConfiguration;

    public LocallyConfiguredCassandraFactory() throws IOException {
        dbConfiguration =
                new DatabaseConfig(
                        new File(getClass().getClassLoader().getResource(SCALARDB_PROPERTIES).getFile()));
    }

    @Override
    public DistributedStorage createDistributedStorage() {
        Injector injector = Guice.createInjector(new StorageModule(dbConfiguration));
        return injector.getInstance(StorageService.class);
    }

    @Override
    public DistributedTransactionManager createDistributedTransactionManager(
            DistributedStorage storage) {
        Injector injector = Guice.createInjector(new TransactionModule(dbConfiguration));
        return injector.getInstance(TransactionService.class);
    }
}
