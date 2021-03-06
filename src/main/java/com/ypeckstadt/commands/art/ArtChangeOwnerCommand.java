package com.ypeckstadt.commands.art;

import com.ypeckstadt.dao.art.ArtRecord;
import com.ypeckstadt.service.art.ArtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "changeOwner"
)
public class ArtChangeOwnerCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(ArtChangeOwnerCommand.class);

    @CommandLine.Option(names = {"-id", "--artId"}, paramLabel = "ART ID", description = "the art id", required = true)
    String artId;

    @CommandLine.Option(names = {"-co", "--currentOwner"}, paramLabel = "CURRENT OWNER", description = "the art's current owner id", required = true)
    String currentOwnerAccountId;

    @CommandLine.Option(names = {"-no", "--newOwner"}, paramLabel = "NEW OWNER", description = "the art's new owner id", required = true)
    String newOwnerAccountId;

    private final ArtService artService;

    @Inject
    public ArtChangeOwnerCommand(ArtService artService) {
        this.artService = artService;
    }

    @Override
    public Integer call() {
        try {
            ArtRecord artRecord = artService.changeOwner(currentOwnerAccountId, newOwnerAccountId, artId);
            LOG.info("the art's owner has been updated successfully");
            LOG.info(artRecord.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}