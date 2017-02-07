package kz.greetgo.installer;

public class InstallerTask {
  public static void main(String[] args) {
    Commander commander = Commander.aboutDir(".");

    System.out.println("commander = " + commander);
  }
}
