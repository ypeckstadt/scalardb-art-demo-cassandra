package com.ypeckstadt.commands.art;

import com.ypeckstadt.dao.art.ArtRecord;
import com.ypeckstadt.service.art.ArtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "view"
)
public class ArtViewCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(ArtViewCommand.class);

    @CommandLine.Option(names = {"-art", "--artId"}, paramLabel = "ART ID", description = "the art id", required = true)
    String artId;

    @CommandLine.Option(names = {"-acc", "--accountId"}, paramLabel = "ACCOUNT ID", description = "the account id", required = true)
    String accountId;

    private final ArtService artService;

    @Inject
    public ArtViewCommand(ArtService artService) {
        this.artService = artService;
    }

    @Override
    public Integer call() {
        try {
            ArtRecord artRecord = artService.view(accountId, artId);
            LOG.info("the art has been found");
            LOG.info(artRecord.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}