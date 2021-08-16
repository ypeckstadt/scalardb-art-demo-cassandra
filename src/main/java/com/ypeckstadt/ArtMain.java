package com.ypeckstadt;

import com.ypeckstadt.commands.GuiceFactory;
import com.ypeckstadt.commands.account.AccountCommand;
import com.ypeckstadt.commands.art.ArtCommand;
import com.ypeckstadt.dao.LocallyConfiguredCassandraFactory;
import com.ypeckstadt.dao.ScalarDbManager;
import com.ypeckstadt.dao.account.AccountDao;
import com.ypeckstadt.dao.art.ArtDao;
import com.ypeckstadt.service.account.AccountService;
import com.ypeckstadt.service.account.AccountServiceForStorage;
import com.ypeckstadt.service.account.AccountServiceForTransaction;
import com.ypeckstadt.service.art.ArtService;
import com.ypeckstadt.service.art.ArtServiceForStorage;
import com.ypeckstadt.service.art.ArtServiceForTransaction;
import picocli.CommandLine;

import java.io.IOException;


@CommandLine.Command(name = "app", description = "Scalar DB art demo CLI",
        mixinStandardHelpOptions = true, version = "1.0",
        subcommands = {AccountCommand.class, ArtCommand.class})
public class ArtMain {

    @CommandLine.Option(names = {"-m", "--mode"},
            description = "storage or transaction", defaultValue = "storage")
    String mode;

    public static void main(String[] args) throws IOException {
        ArtMain app = new ArtMain();

        // Read mode arg parameter manually because we need to pass it with the GuiceFactory for the PicoCLI commands
        String mode = "storage";
        for (int i = 0; i < args.length; ++i) {
            if ("--mode".equals(args[i])) {
                mode = args[i + 1];
                break;
            }
            if ("-m".equals(args[i])) {
                mode = args[i + 1];
                break;
            }
        }

        AccountService accountService;
        ArtService artService;
        AccountDao accountDao = new AccountDao();
        ArtDao artDao = new ArtDao();
        ScalarDbManager scalarDbManager = new ScalarDbManager(new LocallyConfiguredCassandraFactory());

        // Determine to create services for transaction or storage mode
        if (mode.equals("transaction")) {
            accountService = new AccountServiceForTransaction(accountDao, scalarDbManager);
            artService = new ArtServiceForTransaction(artDao, accountDao, scalarDbManager);
        } else {
            accountService = new AccountServiceForStorage(accountDao, scalarDbManager);
            artService = new ArtServiceForStorage(artDao, accountDao, scalarDbManager);
        }

        int exitCode = new CommandLine(app, new GuiceFactory(accountService, artService)).execute(args);
        System.exit(exitCode);
    }
}
