package com.ypeckstadt.commands.art;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "art",
        description = "manage art",
        subcommands = {
                ArtCreateCommand.class,
                ArtBuyCommand.class,
                ArtChangeOwnerCommand.class,
                ArtViewCommand.class,
                ArtListCommand.class,
        }
)
public class ArtCommand implements Callable<Integer> {

    @CommandLine.Spec
    CommandLine.Model.CommandSpec spec;

    @Override
    public Integer call() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand");
    }
}
