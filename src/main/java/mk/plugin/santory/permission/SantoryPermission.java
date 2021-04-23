package mk.plugin.santory.permission;

public class SantoryPermission {

    private final String id;
    private final String permission;
    private final String message;

    public SantoryPermission(String id, String permission, String message) {
        this.id = id;
        this.permission = permission;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getPermission() {
        return permission;
    }

    public String getMessage() {
        return message;
    }
}
