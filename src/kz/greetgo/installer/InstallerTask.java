package kz.greetgo.installer;

import java.util.ArrayList;
import java.util.List;

public class InstallerTask {
  public static void main(String[] args) throws Exception {
    InstallerTask it = new InstallerTask();
    it.initRepos();
    it.execute();
  }

  Commander current = Commander.current();

  class Repo {
    public final String repo, subDir;

    public final List<String> versionCommitList = new ArrayList<>();

    Repo(String repo, String subDir) {
      this.repo = repo;
      this.subDir = subDir;
    }

    public Repo commit(String id) {
      versionCommitList.add(id);
      return this;
    }
  }

  private void initRepos() {
    addRepo("https://github.com/greetgo/greetgo.util.git", "greetgo.util/greetgo.util")
      .commit("12b46d0 - version 0.0.26")
      .commit("2b1413d - version 0.0.27")
    ;
    addRepo("https://github.com/greetgo/greetgo.class_scanner.git", "greetgo.class_scanner")
      .commit("aaf0226 - version 0.0.1")
    ;
    addRepo("https://github.com/greetgo/greetgo.java_compiler.git", "greetgo.java_compiler")
      .commit("b7462cd - version 0.0.3")
    ;
    
    addRepo("https://github.com/greetgo/depinject.git", "depinject/greetgo.depinject.parent")
      .commit("722bb47 - version 1.1.3")
    ;
  }

  final List<Repo> repoList = new ArrayList<>();

  private Repo addRepo(String repo, String subDir) {
    Repo ret = new Repo(repo, subDir);
    repoList.add(ret);
    return ret;
  }

  private void execute() throws Exception {
    Commander ip = current.cd("build/InstallingProjects");
    ip.mkdirs().check();

    for (Repo repo : repoList) {
      Commander prg = ip.cd(repo.subDir);
      if (!prg.exists()) ip.cmd("git clone " + repo.repo);
      prg.check();
      prg.cmdExitCode("git pull");
      for (String commitVersion : repo.versionCommitList) {
        String[] split = commitVersion.split("-");
        String commit = split[0].trim();
        String version = split[1].trim();
        System.out.println("git checkout " + commit + " for " + version);
        prg.cmd("git checkout " + commit);
        prg.cmd("gradle clean install");
      }

    }
  }
}
