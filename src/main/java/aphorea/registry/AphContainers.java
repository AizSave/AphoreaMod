package aphorea.registry;

import aphorea.containers.RunesInjectorContainer;
import aphorea.containers.RunesInjectorContainerForm;
import necesse.engine.registries.ContainerRegistry;

public class AphContainers {
    public static int RUNES_INJECTOR_CONTAINER;

    public static void registerCore() {
        RUNES_INJECTOR_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new RunesInjectorContainerForm<>(client, new RunesInjectorContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new RunesInjectorContainer(client, uniqueSeed, packet));
    }
}
