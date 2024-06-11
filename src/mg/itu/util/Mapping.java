package mg.itu.util;

import java.lang.reflect.Method;
import java.util.Enumeration;

import jakarta.servlet.http.HttpServletRequest;

public class Mapping {
    String className;
    String methodName;
    Class[] parameterTypes;

    public Mapping(String className, String methodName, Class[] parameterTypes) {
        setClassName(className);
        setParameterTypes(parameterTypes);
        setMethodName(methodName);
    }

    public Mapping(String className, String methodName) {
        setClassName(className);
        setMethodName(methodName);
    }

    public Mapping() {

    }

    public Object getResponse(HttpServletRequest request) throws Exception {
        Class class1 = Class.forName(this.getClassName());
        Object instance = class1.getConstructor().newInstance();
        Method method = class1.getMethod(methodName, getParameterTypes());
        
        Object[] params = new Object[method.getParameterCount()];
        Enumeration<String> values = request.getParameterNames();
        
        for (int i = 0; i < params.length; i++) {
            if(values.hasMoreElements()) {
                params[i] = request.getParameter(values.nextElement());
            }
        }

        return method.invoke(instance, params);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }    
}