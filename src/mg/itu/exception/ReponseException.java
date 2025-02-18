package mg.itu.exception;

public class ReponseException extends Exception {
    private int statusCode;
    private String pageRedirection;

    public ReponseException(int statusCode, Exception e) {
        super(e);
        this.statusCode = statusCode;
    }

    public ReponseException(int statusCode, Exception e, String pageRedirection) {
        super(e);
        this.statusCode = statusCode;
        this.setPageRedirection(pageRedirection);
    }

    

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getPageRedirection() {
        return pageRedirection;
    }

    public void setPageRedirection(String pageRedirection) {
        this.pageRedirection = pageRedirection;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
}
