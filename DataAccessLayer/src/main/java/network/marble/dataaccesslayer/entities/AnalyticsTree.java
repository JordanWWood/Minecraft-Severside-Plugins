package network.marble.dataaccesslayer.entities;


import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;

@ToString
public class AnalyticsTree {

    @Getter
    private long value;

    @Getter
    private HashMap<String, AnalyticsTree> children = new HashMap<>();

    public AnalyticsTree(long value) {
        this.value = value;
    }

    public AnalyticsTree() {
        this.value = 0;
    }

    public AnalyticsTree getNode(String identifier) {
        return getNode(identifier.split("\\."));
    }

    public AnalyticsTree getNode(String[] identifier) {
        if (identifier.length <= 0) return this;
        if (!children.containsKey(identifier[0])) children.put(identifier[0], new AnalyticsTree());
        return children.get(identifier[0]).getNode(Arrays.copyOfRange(identifier, 1, identifier.length));
    }

    public boolean alterNode(String identifier, long amount) {
        return alterNode(identifier.split("\\."), amount);
    }

    public boolean alterNode(String[] identifier, long amount) {
        if (identifier.length <= 0) {
            value += amount;
            return true;
        }
        if (!children.containsKey(identifier[0])) children.put(identifier[0], new AnalyticsTree());
        return children.get(identifier[0]).alterNode(Arrays.copyOfRange(identifier, 1, identifier.length), amount);
    }

    public boolean setNode(String identifier, long amount) {
        return setNode(identifier.split("\\."), amount);
    }

    public boolean setNode(String[] identifier, long amount) {
        if (identifier.length <= 0) {
            value = amount;
            return true;
        }
        if (!children.containsKey(identifier[0])) children.put(identifier[0], new AnalyticsTree());
        return children.get(identifier[0]).setNode(Arrays.copyOfRange(identifier, 1, identifier.length), amount);
    }

    public boolean deleteNode(String identifier) {
        return deleteNode(identifier.split("\\."));
    }

    public boolean deleteNode(String[] identifier) {
        if (identifier.length <= 0) return false;
        if (identifier.length > 1 && children.containsKey(identifier[0])) return children.get(identifier[0]).deleteNode(Arrays.copyOfRange(identifier, 1, identifier.length));
        return children.containsKey(identifier[0]) && children.remove(identifier[0]) != null;
    }
}
