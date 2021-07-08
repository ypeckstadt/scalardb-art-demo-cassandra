package com.ypeckstadt.commands.art;

import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.dao.art.ArtRecord;
import com.ypeckstadt.service.account.AccountService;
import com.ypeckstadt.service.art.ArtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "purchase"
)
public class ArtPurchaseCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(ArtPurchaseCommand.class);

    @CommandLine.Option(names = {"-id", "--artId"}, paramLabel = "ART ID", description = "the art id", required = true)
    String artId;

    @CommandLine.Option(names = {"-b", "--buyer"}, paramLabel = "BUYER", description = "the art buyer's id", required = true)
    String ownerId;

    private final ArtService artService;
    private final AccountService accountService;

    @Inject
    public ArtPurchaseCommand(ArtService artService, AccountService accountService) {
        this.artService = artService;
        this.accountService = accountService;
    }

    @Override
    public Integer call() {
        try {
            ArtRecord artRecord = artService.purchase(artId, ownerId);
            AccountRecord accountRecord = accountService.view(ownerId);
            LOG.info("the purchase of the art has been successfully completed");
            LOG.info(artRecord);
            LOG.info(accountRecord);
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}