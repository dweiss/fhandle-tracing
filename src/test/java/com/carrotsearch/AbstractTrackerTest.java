package com.carrotsearch;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;

import com.carrotsearch.aspects.Tracker;
import com.carrotsearch.aspects.Tracker.TrackingStats;
import com.carrotsearch.randomizedtesting.RandomizedTest;

public abstract class AbstractTrackerTest extends RandomizedTest {
  private List<Object> expectClosed = new ArrayList<>();
  private List<Object> expectOpened = new ArrayList<>();

  @Before
  public void setup() {
    // Enable tracking.
    Tracker.startTracking();
  }

  @After
  public void checkTrackingAssertionsAndCleanup() throws IOException {
    TrackingStats stats = Tracker.snapshot();

    // Close any unclosed resources.
    for (Object o : stats.open.keySet()) {
      ((Closeable) o).close();
    }
    
    // Disable tracking and reset tracking buffers.
    Tracker.endTracking();
    Tracker.reset();

    // Verify assertions.
    System.out.println(String.format(Locale.ENGLISH, 
        "Tracked %d objects, %d closed, %d open.",
        stats.closed.size() + stats.open.size(),
        stats.closed.size(),
        stats.open.size()));
    
    for (Object o : expectClosed) {
      assertTrue("Expected " + o + " to be closed.", stats.closed.containsKey(o));
    }

    for (Object o : expectOpened) {
      assertTrue(stats.open.containsKey(o));
    }
  }

  protected <T extends Closeable> T expectClosed(T c) throws IOException {
    expectClosed.add(c);
    return c;
  }

  protected <T extends Closeable> T expectOpen(T c) throws IOException {
    expectOpened.add(c);
    return c;
  }

  /** Chainable close(), returning the argument or throwing an exception on failure. */
  protected <T extends Closeable> T close(T c) throws IOException {
    c.close();
    return c;
  }
}
