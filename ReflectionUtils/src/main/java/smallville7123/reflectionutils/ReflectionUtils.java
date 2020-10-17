package smallville7123.reflectionutils;

import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import smallville7123.taggable.Taggable;

public class ReflectionUtils {
    
    ArrayList<Object> internalList;

    public Object getField(Object object, String fieldName) {
        ReflectionField x = new ReflectionField();
        internalList.add(x);
        return x.get(object, fieldName);
    }

    public void setField(Object object, String fieldName, Object value) {
        ReflectionField x = new ReflectionField();
        internalList.add(x);
        x.set(object, fieldName, value);
    }

    public Object invokeMethod(Object object, String methodName) {
        ReflectionMethod x = new ReflectionMethod();
        internalList.add(x);
        return x.invoke(object, methodName);
    }

    public Object invokeMethod(Object object, String methodName, Object ... args) {
        ReflectionMethod x = new ReflectionMethod();
        internalList.add(x);
        return x.invoke(object, methodName, args);
    }

    public Object instantiate(Object object, String className) {
        ReflectionInstantiator x = new ReflectionInstantiator();
        internalList.add(x);
        return x.instantiate(object, className);
    }

    public Object instantiate(Object object, String className, Object ... args) {
        ReflectionInstantiator x = new ReflectionInstantiator();
        internalList.add(x);
        return x.instantiate(object, className, args);
    }

    private class ReflectionField {
        String TAG = Taggable.getTag(this);

        private Field field;
        private boolean fieldFetched;

        Object get(Object object, String fieldName) {
            if (!fieldFetched) {
                try {
                    // try public
                    field = object.getClass().getField(fieldName);
                    field.setAccessible(true);
                    fieldFetched = true;
                } catch (NoSuchFieldException e) {
                    try {
                        // try non-public
                        field = object.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        fieldFetched = true;
                    } catch (NoSuchFieldException ex) {
                        Log.e(TAG, "No Such Field: Failed to retrieve field: " + fieldName, e);
                        Log.e(TAG, "No Such Field: Failed to retrieve field: " + fieldName, ex);
                    }
                }
            }

            if (field != null) {
                try {
                    return field.get(object);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Illegal Access: Failed to get value from field: " + fieldName, e);
                    field = null;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Illegal Argument: Failed to get value from field: " + fieldName, e);
                    field = null;
                }
            } else {
                Log.e(TAG, "field is null: " + fieldName);
            }
            return null;
        }

        void set(Object object, String fieldName, Object value) {
            if (!fieldFetched) {
                try {
                    // try public
                    field = object.getClass().getField(fieldName);
                    field.setAccessible(true);
                    fieldFetched = true;
                } catch (NoSuchFieldException e) {
                    try {
                        // try non-public
                        field = object.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        fieldFetched = true;
                    } catch (NoSuchFieldException ex) {
                        Log.e(TAG, "No Such Field: Failed to retrieve field: " + fieldName, e);
                        Log.e(TAG, "No Such Field: Failed to retrieve field: " + fieldName, ex);
                    }
                }
            }

            if (field != null) {
                try {
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Illegal Access: Failed to set value from field: " + fieldName, e);
                    field = null;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Illegal Argument: Failed to set value from field: " + fieldName, e);
                    field = null;
                }
            } else {
                Log.e(TAG, "field is null: " + fieldName);
            }
        }
    }

    private Class[] toClassArray(Object ... args) {
        if (args == null) return null;
        Class[] classes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i].getClass();
        }
        return classes;
    }

    private class ReflectionMethod {
        String TAG = Taggable.getTag(this);

        private Method method;
        private boolean methodFetched;

        Object invoke(Object object, String methodName) {
            return invoke(object, methodName, (Object[]) null);
        }


        Object invoke(Object object, String methodName, Object ... args) {
            if (!methodFetched) {
                try {
                    // try public
                    if (args != null) {
                        method = object.getClass().getMethod(methodName, toClassArray(args));
                    } else {
                        method = object.getClass().getMethod(methodName);
                    }
                    method.setAccessible(true);
                    methodFetched = true;
                } catch (NoSuchMethodException e) {
                    try {
                        // try non-public
                        if (args != null) {
                            method = object.getClass().getDeclaredMethod(methodName, toClassArray(args));
                        } else {
                            method = object.getClass().getDeclaredMethod(methodName);
                        }
                        method.setAccessible(true);
                        methodFetched = true;
                    } catch (NoSuchMethodException ex) {
                        Log.e(TAG, "No Such Field: Failed to retrieve method: " + methodName, e);
                        Log.e(TAG, "No Such Field: Failed to retrieve method: " + methodName, ex);
                    }
                }
            }

            if (method != null) {
                try {
                    if (args != null) {
                        return method.invoke(object, args);
                    } else {
                        return method.invoke(object);
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Illegal Access: Failed to invoke method: " + methodName, e);
                    method = null;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Illegal Argument: Failed to invoke method: " + methodName, e);
                    method = null;
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Invocation Target: Failed to invoke method: " + methodName, e);
                    method = null;
                }
            } else {
                Log.e(TAG, "method is null: " + methodName);
            }
            return null;
        }
    }

    private class ReflectionInstantiator {
        String TAG = Taggable.getTag(this);

        private Constructor constructor;
        private boolean constructorFetched;

        public Object instantiate(Object object, String className) {
            return instantiate(object, className, (Object[]) null);
        }

        public Object instantiate(Object object, String className, Object ... args) {
            if (!constructorFetched) {
                try {
                    // try public
                    if (args != null) {
                        constructor = object.getClass().getConstructor(toClassArray(args));
                    } else {
                        constructor = object.getClass().getConstructor();
                    }
                    constructor.setAccessible(true);
                    constructorFetched = true;
                } catch (NoSuchMethodException e) {
                    try {
                        // try non-public
                        if (args != null) {
                            constructor = object.getClass().getDeclaredConstructor(toClassArray(args));
                        } else {
                            constructor = object.getClass().getDeclaredConstructor();
                        }
                        constructor.setAccessible(true);
                        constructorFetched = true;
                    } catch (NoSuchMethodException ex) {
                        Log.e(TAG, "No Such Field: Failed to retrieve constructor: " + className, e);
                        Log.e(TAG, "No Such Field: Failed to retrieve constructor: " + className, ex);
                    }
                }
            }

            if (constructor != null) {
                try {
                    if (args != null) {
                        return constructor.newInstance(args);
                    } else {
                        return constructor.newInstance();
                    }
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Illegal Access: Failed to invoke constructor: " + className, e);
                    constructor = null;
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Illegal Argument: Failed to invoke constructor: " + className, e);
                    constructor = null;
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Invocation Target: Failed to invoke constructor: " + className, e);
                    constructor = null;
                } catch (InstantiationException e) {
                    Log.e(TAG, "Instantiation: Failed to invoke constructor: " + className, e);
                    constructor = null;
                }
            } else {
                Log.e(TAG, "constructor is null: " + className);
            }
            return null;
        }
    }
}
