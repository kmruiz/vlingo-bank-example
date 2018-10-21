package io.kmruiz.vlingo.bank;

import io.vlingo.actors.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class ActorTest {
    private World world;

    @BeforeEach
    void setUpWorld() {
        world = World.startWithDefaults(getClass().getSimpleName());
    }

    @AfterEach
    void tearDownWorld() {
        world.terminate();
    }

    protected final World world() {
        return world;
    }
}
