package wordhunt;

public class DocumentInfo {
    private final boolean documentExists;
    private final boolean isDirectory;
    private final boolean isReadable;

    public DocumentInfo(boolean aDocumentExists, boolean aIsDirectory, boolean aIsReadable) {
        this.documentExists = aDocumentExists;
        this.isDirectory = aIsDirectory;
        this.isReadable = aIsReadable;
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

