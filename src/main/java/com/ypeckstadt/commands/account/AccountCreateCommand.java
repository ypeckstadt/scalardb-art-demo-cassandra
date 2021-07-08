package com.ypeckstadt.commands.account;

import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.service.account.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "create"
)
public class AccountCreateCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(AccountCreateCommand.class);

    @CommandLine.Option(names = {"-id", "--identifier"}, paramLabel = "ID", description = "the account id", required = true)
    String accountId;

    private final AccountService accountService;

    @Inject
    public AccountCreateCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Integer call() {
        try {
            AccountRecord accountRecord = accountService.create(accountId);
            LOG.info("the account has been created");
            LOG.info(accountRecord.toString());
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return 0;
    }
}