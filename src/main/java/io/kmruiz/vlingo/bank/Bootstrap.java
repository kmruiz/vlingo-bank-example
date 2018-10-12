package io.kmruiz.vlingo.bank;


import io.vlingo.actors.World;
import io.vlingo.http.resource.Server;

public class Bootstrap {
  private static Bootstrap instance;

  public static final Bootstrap instance() {
    if (instance == null) instance = new Bootstrap();
    return instance;
  }

  public final Server server;
  public final World world;

  public static void main(final String[] args) throws Exception {
    System.out.println("=======================");
    System.out.println("bank: started.");
    System.out.println("=======================");

    Bootstrap.instance();
  }

  private Bootstrap() {
    this.world = World.startWithDefaults("bank");
    this.server = Server.startWith(world.stage());

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (instance != null) {
        instance.server.stop();

        System.out.println("\n");
        System.out.println("=======================");
        System.out.println("Stopping bank.");
        System.out.println("=======================");
        pause();
      }
    }));
  }

  private void pause() {
    try {
      Thread.sleep(1000L);
    } catch (Exception e) {
      // ignore
    }
  }
}