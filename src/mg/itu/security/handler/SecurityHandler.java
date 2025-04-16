package mg.itu.security.handler;

import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.annotation.Security;
import mg.itu.security.User;

public class SecurityHandler {
    private Method method;
    private String errorPage;
    public static String SESSION_USER = "user_xxxxxxxxx";
    public static HttpServletRequest request;
    
    public SecurityHandler(Method method) {
        setMethod(method);
    }

    public Method getMethod() {
        return method;
    }
    
    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean requireAuth() {
        return method.isAnnotationPresent(Security.class) || method.getDeclaringClass().isAnnotationPresent(Security.class);
    }

    public static void saveUser(User user) {
        if (request != null) {
            request.getSession().setAttribute(SecurityHandler.SESSION_USER, user);
        }
    }

    public static void removeUser() {
        if (request != null) {
            request.getSession().removeAttribute(SecurityHandler.SESSION_USER);
        }
    }

    public static User getUser() {
        if (request != null) {
            User u = (User) request.getSession().getAttribute(SecurityHandler.SESSION_USER);
            if(u != null) return u;
        }
        return null;
    }



    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    public String getErrorPage() {
        return errorPage;
    }

    public boolean isGranted(HttpServletRequest request) throws Exception {
        boolean reqAuth = requireAuth();
        Object userObject = request.getSession().getAttribute(SecurityHandler.SESSION_USER);
        
        Security secClass = method.getDeclaringClass().getAnnotation(Security.class);
        Security secMethod = method.getAnnotation(Security.class);
        if (secClass != null) setErrorPage(secClass.errorPage());
        if (secMethod != null) setErrorPage(secMethod.errorPage());
        if (!reqAuth) {
            return true;
        } else if (userObject == null && reqAuth) {
            return false;
        }
        boolean isGranted = true;

        try{
            User user = (User) userObject;
            if (secClass != null && user.getLevelUser() < secClass.levelUser()) {
                isGranted = false;
            }

            if (secMethod != null && user.getLevelUser() < secMethod.levelUser()) {
                isGranted = false;
            } else if (secMethod != null) {
                isGranted = true;
            }
        } catch(Exception e) {
            throw new Exception(String.format("User doit etre une instance de [%s]", User.class.getName()));
        }
        return isGranted;
    }

}
