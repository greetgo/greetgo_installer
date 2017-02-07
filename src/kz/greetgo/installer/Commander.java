package kz.greetgo.installer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Commander {

  private final File current;

  private Commander(File current) {
    this.current = current;
  }

  public static Commander aboutDir(@SuppressWarnings("SameParameterValue") String currentDir) {
    if (currentDir == null) currentDir = ".";
    File current = new File(currentDir);
    return new Commander(current);
  }

  public static Commander current() {
    return aboutDir(null);
  }

  public void check() {
    if (!current.isDirectory()) throw new IllegalArgumentException(current.toPath() + " is not a directory");
  }

  public Commander cd(String subPath) {
    return new Commander(current.toPath().resolve(subPath).toFile());
  }

  public void cmd(String commandWithArguments) throws IOException, InterruptedException {
    run(commandWithArguments.split("\\s+"));
  }

  public int cmdExitCode(String commandWithArguments) throws IOException, InterruptedException {
    return runExitCode(commandWithArguments.split("\\s+"));
  }

  public Commander mkdirs() {
    current.mkdirs();
    return this;
  }

  public void run(String... commandArray) throws IOException, InterruptedException {
    int exitCode = runExitCode(commandArray);
    if (exitCode == 0) return;
    throw new RuntimeException("Exit code = " + exitCode + " for: "
      + Arrays.stream(commandArray).collect(Collectors.joining(", ")));
  }

  public int runExitCode(String... commandArray) throws IOException, InterruptedException {
    check();
    Process process = new ProcessBuilder(commandArray)
      .directory(current)
      .inheritIO()
      .start();
    return process.waitFor();
  }

  public String pwd() {
    try {
      return current.getAbsoluteFile().getCanonicalPath();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean exists() {
    return current.exists();
  }
}
