package com.cosium.gerrit.vet;

import com.cosium.gerrit.vet.push.PushCommandArgParser;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
public class App {

  public static final String NAME = "vet";

  private static final List<VetCommandArgParser> parsers =
      Lists.newArrayList(new PushCommandArgParser());

  public static void main(String[] args) {
    parsers
        .stream()
        .map(parser -> parser.parse(Arrays.copyOf(args, args.length)))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(HelpCommand::new)
        .execute();
  }
}
