package com.cosium.vet.gerrit;

import com.cosium.vet.git.CommitMessage;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface PatchSetCommitMessageFactory {

  CommitMessage build(Patch latestPatch);
}
