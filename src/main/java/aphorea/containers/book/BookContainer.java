package aphorea.containers.book;

import necesse.engine.network.NetworkClient;
import necesse.inventory.container.Container;

public class BookContainer extends Container {
    public BookContainer(NetworkClient client, int uniqueSeed) {
        super(client, uniqueSeed);
    }
}