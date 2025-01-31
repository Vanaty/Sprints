package mg.itu.security.handler;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.annotation.Security;
import mg.itu.security.User;
import mg.itu.util.Session;

public class SecurityHandler {
    private Method method;
    
    public SecurityHandler(Method method) {
        setMethod(method);
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    

    public boolean isGranted(HttpServletRequest request) throws Exception {
        Object userObject = request.getSession().getAttribute("user");
        if (userObject == null && !method.isAnnotationPresent(Security.class)) {
            return false;
        }

        try{
            User user = (User) userObject;
            Security sec = method.getAnnotation(Security.class);
            if (user.getLevelUser() < sec.levelUser()) {
                return false;
            }
        } catch(Exception e) {
            throw new Exception(String.format("User doit etre une instance de [%s]", User.class.getName()));
        }
        return true;
    }
}
