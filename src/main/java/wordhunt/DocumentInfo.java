package wordhunt;

public class DocumentInfo {
    private final boolean documentExists;
    private final boolean isDirectory;
    private final boolean isReadable;

    public DocumentInfo(boolean documentExists, boolean isDirectory, boolean isReadable) {
        this.documentExists = documentExists;
        this.isDirectory = isDirectory;
        this.isReadable = isReadable;
    }

    public boolean documentExists() {
        return documentExists;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isReadable() {
        return isReadable;
    }
}

