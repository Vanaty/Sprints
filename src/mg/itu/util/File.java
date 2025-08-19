package mg.itu.util;

public class File {
    byte[] content;
    String name = "";
    String type = "application/octet-stream";

    public File() {
    }

    public File(byte[] content, String contentType, String name) {
        this.setContent(content);
        this.setName(name);
        this.setContentType(type);;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return type;
    }

    public void setContentType(String type) {
        this.type = type;
    }
    
    
}
