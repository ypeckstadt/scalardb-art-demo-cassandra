package com.ypeckstadt.commands.art;

import com.ypeckstadt.dao.art.ArtRecord;
import com.ypeckstadt.service.art.ArtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "add"
)
public class ArtCreateCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(ArtCreateCommand.class);

    @CommandLine.Option(names = {"-id", "--identifier"}, paramLabel = "ID", description = "the art id", required = true)
    String artId;

    @CommandLine.Option(names = {"-o", "--owner"}, paramLabel = "ACCOUNT ID", description = "the art owner's account id", required = true)
    String accountId;

    @CommandLine.Option(names = {"-p", "--price"}, paramLabel = "PRICE", description = "the art price", required = true)
    int price;

    private final ArtService artService;

    @Inject
    public ArtCreateCommand(ArtService artService) {
        this.artService = artService;
    }

    @Override
    public Integer call() {
       try {
           ArtRecord artRecord = artService.create(artId, accountId, price);
            LOG.info("the art has been created");
            LOG.info(artRecord.toString());
       } catch (Exception e) {
            LOG.error(e.getMessage());
       }
        return 0;
    }
}