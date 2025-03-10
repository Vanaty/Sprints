package mg.itu.util;

import java.lang.reflect.Method;

import mg.itu.security.handler.SecurityHandler;

/**
 * VerbAction
 */
public class VerbAction {

    Class<?> cls;
    Method method;
    String verb;
    SecurityHandler security;

    public VerbAction(String verb,Class<?> cls, Method action) {
        setMethod(action);
        setSecurity(new SecurityHandler(action));
        setVerb(verb);
        setCls(cls);
    }

    public boolean isVerbAllowed(String verb) {
        return this.verb.equals(verb);
    }

    

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public void setCls(Class <?> cls) {
        this.cls = cls;
    }
    
    public Class<?> getCls() {
        return this.cls;
    }

    public SecurityHandler getSecurity() {
        return security;
    }

    public void setSecurity(SecurityHandler security) {
        this.security = security;
    }
}