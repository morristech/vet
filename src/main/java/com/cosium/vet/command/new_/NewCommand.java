package com.cosium.vet.command.new_;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.*;
import com.cosium.vet.git.BranchShortName;
import com.cosium.vet.log.Logger;
import com.cosium.vet.log.LoggerFactory;
import com.cosium.vet.runtime.UserInput;
import com.cosium.vet.runtime.UserOutput;
import com.cosium.vet.thirdparty.apache_commons_lang3.BooleanUtils;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Created on 09/05/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class NewCommand implements VetCommand<Change> {

  private static final Logger LOG = LoggerFactory.getLogger(NewCommand.class);

  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final BranchShortName targetBranch;

  private NewCommand(
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      BranchShortName targetBranch) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.targetBranch = targetBranch;
  }

  @Override
  public Change execute() {
    if (preserveCurrentChange()) {
      throw new RuntimeException("Answered no to the confirmation. Aborted.");
    }
    changeRepository.untrack();

    BranchShortName targetBranch = getTargetBranch();
    CreatedChange change =
        changeRepository.createAndTrackChange(targetBranch, PatchOptions.DEFAULT);
    userOutput.display(change.getCreationLog());
    userOutput.display("Now tracking new change " + change);
    return change;
  }

  private boolean preserveCurrentChange() {
    if (force) {
      return false;
    }
    Change gerritChange = changeRepository.getTrackedChange().orElse(null);
    if (gerritChange == null) {
      return false;
    }
    LOG.debug("Found current tracked change {}", gerritChange);
    return !userInput.askYesNo(
        "You are tracking change "
            + gerritChange
            + ".\nAre you sure that you want to create and track a new one?",
        true);
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  public static class Factory implements NewCommandFactory {

    private final ChangeRepositoryFactory changeRepositoryFactory;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(
        ChangeRepositoryFactory changeRepositoryFactory,
        UserInput userInput,
        UserOutput userOutput) {
      this.changeRepositoryFactory = requireNonNull(changeRepositoryFactory);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public NewCommand build(Boolean force, BranchShortName targetBranch) {
      return new NewCommand(
          changeRepositoryFactory.build(), userInput, userOutput, force, targetBranch);
    }
  }
}
