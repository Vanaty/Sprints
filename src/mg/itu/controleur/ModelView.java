package mg.itu.controleur;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.HashMap;
import java.util.Map;


public class ModelView {
    protected String methodUrl;
    protected HttpServletRequest request;
    protected RequestDispatcher dispatcher;

    protected String callbackValidation;
    String urlDestination;
    Map<String,Object> data;
    
    public Map<String, Object> getData() {
        return data;
    }

    public ModelView(String view, String methodUrl) {
        data = new HashMap<>();
        setUrlDestionation(view);
        setMethodUrl(methodUrl);
    }

    public ModelView(String view) {
        data = new HashMap<>();
        setUrlDestionation(view);
    }

    public ModelView() {
        data = new HashMap<>();
    }

    public void prepareRequestDispatcher(HttpServletRequest request) {
        if (getMethodUrl() == null) {
            this.request = request;
            this.dispatcher = request.getRequestDispatcher(getUrlDestionation());
        } else {
            this.request = new HttpServletRequestWrapper (request) {
                @Override
                public String getMethod() {
                    return getMethodUrl();
                }
            };
            this.dispatcher = this.request.getRequestDispatcher(getUrlDestionation());
        }
    }

    public String getMethodUrl() {
        return methodUrl;
    }

    public void setMethodUrl(String methodUrl) {
        this.methodUrl = methodUrl;
    }

    public void setCallbackValidation(String val) {
        this.callbackValidation = val;
    }

    public String getCallbackValidation() {
        return callbackValidation;
    }

    protected String getUrlDestionation() {
        return urlDestination;
    }

    private void setUrlDestionation(String view) {
        this.urlDestination = view;
    }

    public void addObject(String nom, Object o) {
        this.data.put(nom, o);
    }

    public Object getObject(String nom) {
        return this.data.getOrDefault(nom, null);
    }

    protected HttpServletRequest getRequest() {
        return this.request;
    }

    protected RequestDispatcher getDispatcher() {
        return this.dispatcher;
    }

    protected void setAttributs() throws Exception {
        try {
            this.setAttributs(this.request);
        } catch(Exception e) {
            throw new Exception("Le dispatcher n'est pas encore preparer"); 
        }
    }

    protected void setAttributs(HttpServletRequest request) {
        for (String  key : data.keySet()) {
            request.setAttribute(key, data.getOrDefault(key, null));
        }
    }

}
