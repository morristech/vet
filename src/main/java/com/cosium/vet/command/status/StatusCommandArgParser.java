package com.cosium.vet.command.status;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.command.VetCommand;
import com.cosium.vet.thirdparty.apache_commons_cli.*;
import com.cosium.vet.thirdparty.apache_commons_lang3.StringUtils;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class StatusCommandArgParser implements VetAdvancedCommandArgParser {

  private static final String COMMAND_NAME = "status";

  private final StatusCommandFactory factory;
  private final Options options;

  public StatusCommandArgParser(StatusCommandFactory factory) {
    this.factory = requireNonNull(factory);
    options = new Options();
  }

  @Override
  public void displayHelp(String executableName) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(
        String.format("%s %s", executableName, COMMAND_NAME),
        StringUtils.EMPTY,
        options,
        "Display the current status",
        true);
  }

  @Override
  public String getCommandArgName() {
    return COMMAND_NAME;
  }

  @Override
  public boolean canParse(String... args) {
    return Arrays.stream(args).anyMatch(COMMAND_NAME::equals);
  }

  @Override
  public VetCommand parse(String... args) {
    CommandLineParser parser = new DefaultParser();
    try {
      parser.parse(options, args);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    return factory.build();
  }
}