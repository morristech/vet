package com.cosium.vet.command.track;

import com.cosium.vet.command.VetCommand;
import com.cosium.vet.gerrit.Change;
import com.cosium.vet.gerrit.ChangeNumericId;
import com.cosium.vet.gerrit.ChangeRepository;
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
public class TrackCommand implements VetCommand<Change> {

  private static final Logger LOG = LoggerFactory.getLogger(TrackCommand.class);

  private final ChangeRepository changeRepository;
  private final UserInput userInput;
  private final UserOutput userOutput;

  private final boolean force;
  private final ChangeNumericId numericId;
  private final BranchShortName targetBranch;

  private TrackCommand(
      ChangeRepository changeRepository,
      UserInput userInput,
      UserOutput userOutput,
      // Optionals
      Boolean force,
      ChangeNumericId numericId,
      BranchShortName targetBranch) {
    this.changeRepository = changeRepository;
    this.userInput = requireNonNull(userInput);
    this.userOutput = requireNonNull(userOutput);

    this.force = BooleanUtils.toBoolean(force);
    this.numericId = numericId;
    this.targetBranch = targetBranch;
  }

  @Override
  public Change execute() {
    if (preserveCurrentChange()) {
      throw new RuntimeException("Answered no to the confirmation. Aborted.");
    }
    LOG.debug("Untrack any tracked change");
    changeRepository.untrack();
    ChangeNumericId numericId = getNumericId();
    if (!changeRepository.exists(numericId)) {
      throw new RuntimeException(
          "Could not find any change identified by " + numericId + " on Gerrit. Aborted.");
    }
    Change change = changeRepository.trackChange(numericId, getTargetBranch());
    userOutput.display("Now tracking change " + change);
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
        "You are already tracking change "
            + gerritChange
            + ". Are you sure that you want to stop tracking the current change to track another change?",
        false);
  }

  private ChangeNumericId getNumericId() {
    return ofNullable(numericId)
        .orElseGet(() -> ChangeNumericId.of(userInput.askLong("Change numeric ID")));
  }

  private BranchShortName getTargetBranch() {
    return ofNullable(targetBranch)
        .orElseGet(
            () ->
                BranchShortName.of(
                    userInput.askNonBlank("Target branch", BranchShortName.MASTER.toString())));
  }

  public static class Factory implements TrackCommandFactory {

    private final ChangeRepository changeRepository;
    private final UserInput userInput;
    private final UserOutput userOutput;

    public Factory(ChangeRepository changeRepository, UserInput userInput, UserOutput userOutput) {
      this.changeRepository = requireNonNull(changeRepository);
      this.userInput = requireNonNull(userInput);
      this.userOutput = requireNonNull(userOutput);
    }

    @Override
    public TrackCommand build(
        Boolean force, ChangeNumericId numericId, BranchShortName targetBranch) {
      return new TrackCommand(
          changeRepository, userInput, userOutput, force, numericId, targetBranch);
    }
  }
}
