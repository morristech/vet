package com.cosium.vet.command.status;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeRepository;
import com.cosium.vet.gerrit.ChangeRepositoryFactory;
import com.cosium.vet.git.GitClient;
import com.cosium.vet.git.GitProvider;
import com.cosium.vet.runtime.UserOutput;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class StatusCommand implements VetCommand {

  private final GitClient git;
  private final ChangeRepository changeRepository;
  private final UserOutput userOutput;

  private StatusCommand(GitClient git, ChangeRepository changeRepository, UserOutput userOutput) {
    this.git = requireNonNull(git);
    this.changeRepository = requireNonNull(changeRepository);
    this.userOutput = requireNonNull(userOutput);
  }

  @Override
  public void execute() {
    Optional<Change> change = changeRepository.getTrackedChange();
    userOutput.display(git.status());
    if (change.isPresent()) {
      userOutput.display("Tracking change " + change.get());
    } else {
      userOutput.display("No tracked change.");
    }
  }

  public static class Factory implements StatusCommandFactory {

    private final GitProvider gitProvider;
    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserOutput userOutput;

    public Factory(
        GitProvider gitProvider,
        ChangeRepositoryFactory changeRepositoryFactory,
        UserOutput userOutput) {
      this.gitProvider = requireNonNull(gitProvider);
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public StatusCommand build() {
      return new StatusCommand(gitProvider.build(), changeRepositoryFactory.build(), userOutput);
    }
  }
}