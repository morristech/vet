package com.cosium.vet.git;

/**
 * Created on 19/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public interface GitConfigRepositoryProvider {

  /** @return The current git config repository */
  GitConfigRepository getRepository();
}
