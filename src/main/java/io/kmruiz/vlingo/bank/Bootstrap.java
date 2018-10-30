package io.kmruiz.vlingo.bank;


import io.kmruiz.vlingo.bank.infrastructure.customer.CustomerResource;
import io.kmruiz.vlingo.bank.infrastructure.transfer.TransferResource;
import io.vlingo.actors.World;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;

public class Bootstrap {
    private static final int PORT = 8080;

    public static void main(final String[] args) {
        System.out.println("=======================");
        System.out.println("bank: started.");
        System.out.println("=======================");

        final var world = World.startWithDefaults("bank");

        final var customerResource = new CustomerResource(world);
        final var transferResource = new TransferResource(world);

        final var resources = Resources.are(
                CustomerResource.resourcesOf(customerResource),
                TransferResource.resourcesOf(transferResource)
        );

        final var server = Server.startWith(
                world.stage(),
                resources,
                PORT,
                Configuration.Sizing.define(),
                Configuration.Timing.define()
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (server != null) {
                server.stop();

                System.out.println("\n");
                System.out.println("=======================");
                System.out.println("Stopping bank.");
                System.out.println("=======================");
                pause();
            }
        }));
    }

    private static void pause() {
        try {
            Thread.sleep(1000L);
        } catch (Exception e) {
            // ignore
        }
    }
}