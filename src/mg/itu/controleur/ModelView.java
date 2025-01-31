package mg.itu.controleur;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public class ModelView {
    protected String callbackValidation;
    String urlDestination;
    Map<String,Object> data;
    
    public Map<String, Object> getData() {
        return data;
    }

    public ModelView(String view) {
        data = new HashMap<>();
        setUrlDestionation(view);
    }

    public ModelView() {
        data = new HashMap<>();
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

    protected void setAttributs(HttpServletRequest request) {
        for (String  key : data.keySet()) {
            request.setAttribute(key, data.getOrDefault(key, null));
        }
    }

}
