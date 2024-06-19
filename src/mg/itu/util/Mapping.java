package mg.itu.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import mg.itu.annotation.Param;

public class Mapping {
    String className;
    String methodName;
    Parameter[] parameters;

    public Mapping(String className, String methodName, Parameter[] parameters) {
        setClassName(className);
        setParameters(parameters);
        setMethodName(methodName);
    }

    public Mapping(String className, String methodName) {
        setClassName(className);
        setMethodName(methodName);
    }

    public Mapping() {

    }

    private Object cast(Class<?> type, Object value) {
        return value;
    }

    private Object getInstance(Class<?> c) throws Exception {
        System.out.println(c.getName());
        return c.getConstructor().newInstance();
    }

    public Object getResponse(HttpServletRequest request) throws Exception {
        Class<?> class1 = Class.forName(this.getClassName());
        Object instance = class1.getConstructor().newInstance();
        Method method = class1.getMethod(methodName, getParameterTypes());

        Map<String, Object> mapInstances =  new HashMap<>();
        
        Object[] params = new Object[method.getParameterCount()];
        Enumeration<String> values = request.getParameterNames();

        while (values.hasMoreElements()) {
            String name = values.nextElement();
            
            for (int i = 0; i < parameters.length; i++) {
                //Object 
                if (!parameters[i].getType().isPrimitive()) {
                    String[] data = name.split("\\.");
                    Object model = null;
                    if(mapInstances.containsKey(data[0])) {
                        model = mapInstances.get(data[0]);
                    } else {
                        model = getInstance(parameters[i].getType());
                        mapInstances.put(data[0], model);
                        params[i] = mapInstances.get(data[0]);
                    }

                    Object[] value = { request.getParameter(name) };
                    Method m = getMethod(model.getClass(), data[1]);
                    m.invoke(model, value);
                }


                // argument = parameter.Name
                else if(parameters[i].getName().equals(name)) {
                    params[i] = request.getParameter(name);
                }

                
                //par annotation
                else if (parameters[i].isAnnotationPresent(Param.class)) {
                    String val = parameters[i].getAnnotation(Param.class).value();
                    if(val.equals(name)) {
                        params[i] = request.getParameter(name);
                    }
                }
            }
        }

        return method.invoke(instance, params);
    }

    private String toSetters(String name) {
        return "set" + name.substring(0,1).toUpperCase() + name.substring(1);
    }

    private Method getMethod(Class<?> c, String fieldName) throws Exception {
        Class<?> fieldType = c.getDeclaredField(fieldName).getType();
        String fieldSetter = toSetters(fieldName);
        System.out.println(fieldSetter + fieldType.toString());
        return c.getMethod(fieldSetter, fieldType);
    }


    private Class<?>[] getParameterTypes() {
        Class<?>[] types = new Class[parameters.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = parameters[i].getType();
        }
        return types;
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

    public Parameter[] getParameters() {
        return parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.parameters = parameters;
    }    
}