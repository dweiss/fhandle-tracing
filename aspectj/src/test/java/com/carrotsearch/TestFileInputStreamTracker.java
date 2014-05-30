package com.carrotsearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.carrotsearch.randomizedtesting.LifecycleScope;

public class TestFileInputStreamTracker extends AbstractTrackerTest {
  @Test
  public void testConstructorFile() throws IOException {
    expectClosed(close(new FileInputStream(newTempFile(LifecycleScope.TEST))));
    expectOpen(new FileInputStream(newTempFile(LifecycleScope.TEST)));
  }

  @Test
  public void testConstructorString() throws IOException {
    expectClosed(close(new FileInputStream(newTempFile(LifecycleScope.TEST).getAbsolutePath())));
    expectOpen(new FileInputStream(newTempFile(LifecycleScope.TEST).getAbsolutePath()));
  }

  @Test
  public void testConstructorFileHandle() throws IOException {
    try (FileInputStream fis = expectClosed(new FileInputStream(newTempFile(LifecycleScope.TEST)))) {
      expectClosed(close(new FileInputStream(fis.getFD())));
      expectOpen(new FileInputStream(fis.getFD()));
    }
  }
  
  @Test()
  public void testSubclassesBanned() throws IOException {
    class CustomFileInputStream extends FileInputStream {
      public CustomFileInputStream(File file) throws FileNotFoundException {
        super(file);
      }
    }
    
    try {
      new CustomFileInputStream(newTempFile(LifecycleScope.TEST)).close();
      fail();
    } catch (UnsupportedOperationException e) {
      // Expected.
    }
  }
}
