package com.cosium.vet.command.push;

import com.cosium.vet.command.VetAdvancedCommandArgParser;
import com.cosium.vet.gerrit.PatchSubject;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

/**
 * Created on 23/02/18.
 *
 * @author Reda.Housni-Alaoui
 */
public class PushCommandArgParserUnitTest {

  private PushCommandFactory factory;
  private VetAdvancedCommandArgParser tested;

  @Before
  public void before() {
    factory = mock(PushCommandFactory.class);
    when(factory.build(any(), any(), any(), any(), any())).thenReturn(mock(PushCommand.class));
    tested = new PushCommandArgParser(factory);
  }

  @Test
  public void testNonPushCommand() {
    assertThat(tested.canParse("help")).isFalse();
  }

  @Test
  public void testZeroArg() {
    assertThat(tested.canParse()).isFalse();
  }

  @Test
  public void testPatchSetSubjectShort() {
    tested.parse("push", "-s", "hello");
    verify(factory).build(isNull(), isNull(), eq(PatchSubject.of("hello")), isNull(), isNull());
  }

  @Test
  public void testPatchSetSubjectLong() {
    tested.parse("push", "--patch-set-subject", "hello");
    verify(factory).build(isNull(), isNull(), eq(PatchSubject.of("hello")), isNull(), isNull());
  }

  @Test
  public void testPublishDraftedCommentsShort() {
    tested.parse("push", "-p");
    verify(factory).build(eq(true), isNull(), isNull(), isNull(), isNull());
  }

  @Test
  public void testPublishDraftedCommentsLong() {
    tested.parse("push", "--publish-drafted-comments");
    verify(factory).build(eq(true), isNull(), isNull(), isNull(), isNull());
  }

  @Test
  public void testWipShort() {
    tested.parse("push", "-w");
    verify(factory).build(isNull(), eq(true), isNull(), isNull(), isNull());
  }

  @Test
  public void testWipLong() {
    tested.parse("push", "--work-in-progress");
    verify(factory).build(isNull(), eq(true), isNull(), isNull(), isNull());
  }

  @Test
  public void testBypassReviewShort() {
    tested.parse("push", "-f");
    verify(factory).build(isNull(), isNull(), isNull(), eq(true), isNull());
  }

  @Test
  public void testBypassReviewLong() {
    tested.parse("push", "--bypass-review");
    verify(factory).build(isNull(), isNull(), isNull(), eq(true), isNull());
  }

  @Test
  public void testAll() {
    tested.parse(
        "push",
        "--patch-set-subject",
        "hello",
        "--publish-drafted-comments",
        "--work-in-progress",
        "--bypass-review");
    verify(factory).build(eq(true), eq(true), eq(PatchSubject.of("hello")), eq(true), isNull());
  }

  @Test
  public void displayHelp() {
    tested.displayHelp("vet");
  }
}
