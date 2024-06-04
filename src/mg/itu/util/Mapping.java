package mg.itu.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import mg.itu.controleur.ModelView;

public class Mapping {
    String className;
    String methodName;
    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public Mapping() {

    }

    public Object getResponse() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException {
        Class class1 = Class.forName(this.getClassName());
        Object instance = class1.getConstructor().newInstance();
        Method m = class1.getMethod(methodName);
        return m.invoke(instance);
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
}