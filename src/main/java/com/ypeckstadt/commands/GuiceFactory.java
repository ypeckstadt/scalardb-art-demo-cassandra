package com.ypeckstadt.commands;


import com.google.inject.AbstractModule;
import com.google.inject.ConfigurationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.ypeckstadt.service.account.AccountService;
import com.ypeckstadt.service.art.ArtService;
import picocli.CommandLine;
import picocli.CommandLine.IFactory;

public class GuiceFactory implements IFactory {


    private Injector injector;

    public GuiceFactory(AccountService accountService, ArtService artService) {
        injector = Guice.createInjector(new DemoModule(accountService, artService));
    }

    @Override
    public <K> K create(Class<K> aClass) throws Exception {
        try {
            return injector.getInstance(aClass);
        } catch (ConfigurationException ex) { // no implementation found in Guice configuration
            return CommandLine.defaultFactory().create(aClass); // fallback if missing
        }
    }

    static class DemoModule extends AbstractModule {

        private final AccountService accountService;
        private final ArtService artService;

        public DemoModule(AccountService accountService, ArtService artService) {
            this.accountService = accountService;
            this.artService = artService;
        }

        @Override
        protected void configure() {
            bind(AccountService.class).toInstance(accountService);
            bind(ArtService.class).toInstance(artService);
        }
    }
}