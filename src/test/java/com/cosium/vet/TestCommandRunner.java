package com.cosium.vet;

import com.cosium.vet.runtime.CommandRunner;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

/**
 * Created on 17/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class TestCommandRunner implements CommandRunner {

  private static final String DOCKER_CMD = "docker";
  private static final String DOCKER_GIT_IMAGE = "alpine/git:1.0.4";

  private final CommandRunner delegate;

  public TestCommandRunner(CommandRunner delegate) {
    requireNonNull(delegate);
    this.delegate = delegate;
  }

  @Override
  public String run(Path workingDir, String... command) {
    if ("git".equalsIgnoreCase(command[0])) {
      String[] gitBaseCommand = {
        DOCKER_CMD,
        "run",
        "-t",
        "--rm",
        "-v",
        String.format("%s:/git", workingDir),
        DOCKER_GIT_IMAGE
      };
      command = ArrayUtils.addAll(gitBaseCommand, ArrayUtils.remove(command, 0));
    }
    return delegate.run(workingDir, command);
  }
}
