package smallville7123.taggable;

public class Taggable {
    public final static String getTag(Object object) {
        return getName(object) + "@" + getHash(object);
    }

    public final static String getName(Object object) {
        return object.getClass().getName();
    }
    public final static String getHash(Object object) {
        return Integer.toHexString(object.hashCode());
    }
}