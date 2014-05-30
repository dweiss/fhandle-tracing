package com.carrotsearch;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class TraceInstrumenter {
  public static void premain(String agentArgs, Instrumentation inst) throws Exception { 
    for (Class<?> c : inst.getAllLoadedClasses()) {
      // System.out.println("premain, loaded: " + c.toString() + " => " + inst.isModifiableClass(c));
    }

    System.out.println("isRedefineClassesSupported: " + inst.isRedefineClassesSupported());
    System.out.println("isRetransformClassesSupported: " + inst.isRetransformClassesSupported());
    
    inst.addTransformer(new ClassFileTransformer() {
      @Override
      public byte[] transform(ClassLoader loader, String className,
          Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
          byte[] classfileBuffer) throws IllegalClassFormatException {
        System.out.println(">> Retransform of: " + className + "; " + classBeingRedefined);
        if (className.equals("java/io/FileInputStream")) {
          try {
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("FileInputStream.bytecode");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte [] buf = new byte [1024];
            int len;
            while ((len = is.read(buf)) >= 0) {
              baos.write(buf, 0, len);
            }
            return baos.toByteArray();
          } catch (IOException e) {
            throw new RuntimeException();
          }
        } else {
          return classfileBuffer;
        }
      }
    }, true);
    inst.retransformClasses(FileInputStream.class);
  }
}
