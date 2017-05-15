package pl.godziatkowski.webrobot.shared;

import java.util.Arrays;

public enum IndexField {
    ADDRESS("Address"), CONTENT("Content"), SEARCH("Search");

    public String value;

    IndexField(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static String[] getArrayOfValues() {
        return Arrays.asList(IndexField.values())
                .stream()
                .filter(v -> !v.equals(SEARCH))
                .map(Object::toString)
                .toArray(String[]::new);
    }

    public static IndexField getFromString(String value) {
        IndexField qt = null;
        switch (value) {
            case "Address":
                qt = ADDRESS;
                break;
            case "Content":
                qt = CONTENT;
                break;
            case "Search":
                qt = SEARCH;
                break;
            default:
                throw new IllegalArgumentException("Unknown enum value " + value);
        }
        return qt;
    }

}
