package com.ypeckstadt.commands.account;

import com.ypeckstadt.dao.account.AccountRecord;
import com.ypeckstadt.service.account.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;

import javax.inject.Inject;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "charge"
)
public class AccountChargeCommand implements Callable {

    private static final Logger LOG = LogManager.getLogger(AccountChargeCommand.class);

    @CommandLine.Option(names = {"-id", "--accountId"}, paramLabel = "ACCOUNT", description = "the account id", required = true)
    String accountId;

    @CommandLine.Option(names = {"-a", "--amount"}, paramLabel = "AMOUNT", description = "the amount to charge", required = true)
    int amount;

    private final AccountService accountService;

    @Inject
    public AccountChargeCommand(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public Integer call() {
        try {
            AccountRecord accountRecord = accountService.charge(accountId, amount);
            LOG.info("the account has been charged successfully");
            LOG.info(accountRecord.toString());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return 0;
    }
}