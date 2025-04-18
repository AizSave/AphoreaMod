package aphorea.registry;

import aphorea.containers.book.BookContainer;
import aphorea.containers.book.BookContainerForm;
import aphorea.containers.initialrune.InitialRuneContainer;
import aphorea.containers.initialrune.InitialRuneContainerForm;
import aphorea.containers.runesinjector.RunesInjectorContainer;
import aphorea.containers.runesinjector.RunesInjectorContainerForm;
import necesse.engine.registries.ContainerRegistry;

public class AphContainers {
    public static int RUNES_INJECTOR_CONTAINER;
    public static int INITIAL_RUNE_CONTAINER;
    public static int BOOK_CONTAINER;

    public static void registerCore() {
        RUNES_INJECTOR_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new RunesInjectorContainerForm<>(client, new RunesInjectorContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new RunesInjectorContainer(client, uniqueSeed, packet));
        INITIAL_RUNE_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new InitialRuneContainerForm<>(client, new InitialRuneContainer(client.getClient(), uniqueSeed, packet)), (client, uniqueSeed, packet, serverObject) -> new InitialRuneContainer(client, uniqueSeed, packet));
        BOOK_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> new BookContainerForm<>(client, new BookContainer(client.getClient(), uniqueSeed)), (client, uniqueSeed, packet, serverObject) -> new BookContainer(client, uniqueSeed));
    }
}
