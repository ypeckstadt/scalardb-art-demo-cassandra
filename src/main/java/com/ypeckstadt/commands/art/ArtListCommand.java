package com.ypeckstadt.commands.art;

import com.scalar.db.api.Result;
import com.ypeckstadt.dao.art.ArtRecord;
import com.ypeckstadt.service.art.ArtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "list"
)
public class ArtListCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(ArtListCommand.class);


    @CommandLine.Option(names = {"-o", "--ownerAccountId"}, paramLabel = "ACCOUNT ID", description = "the account id", required = true)
    String accountId;

    private final ArtService artService;

    @Inject
    public ArtListCommand(ArtService artService) {
        this.artService = artService;
    }

    @Override
    public Integer call() {
        try {
            List<ArtRecord> list = artService.list(accountId);
            for (ArtRecord record : list) {
               LOG.info(record.toString());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}