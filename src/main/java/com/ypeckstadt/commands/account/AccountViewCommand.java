package com.ypeckstadt.commands.account;

import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.service.account.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "view"
)
public class AccountViewCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(AccountViewCommand.class);

    @CommandLine.Option(names = {"-id", "--accountId"}, paramLabel = "ACCOUNT", description = "the account id", required = true)
    String accountId;

    private final AccountService accountService;

    @Inject
    public AccountViewCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Integer call() throws Exception {
        try {
            AccountRecord accountRecord = accountService.view(accountId);
            LOG.info("the account has been found");
            LOG.info(accountRecord.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}