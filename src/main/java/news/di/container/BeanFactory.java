//package news.di.container;
//
//import news.web.http.HttpRequest;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//
//@Deprecated
//public class BeanFactory {
//
//    private static BeanFactory BEAN_FACTORY;
//    private final XmlBeansConfiguration configuration;
//    private final HttpRequest httpRequest;
//
//
//    private BeanFactory(HttpRequest httpRequest, String pathToConfig) {
//        this.httpRequest = httpRequest;
//        this.configuration = new XmlBeansConfiguration();
//        configuration.setPathAndLoad(pathToConfig);
//    }
//
//    public static void setSettings(HttpRequest httpRequest, String pathToConfig) {
//        BEAN_FACTORY = new BeanFactory(httpRequest, pathToConfig);
//    }
//
//    public static BeanFactory getInstance() {
//        return BEAN_FACTORY;
//    }
//
//    public <T> T getBean(Class<T> clazz) throws InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException {
//        Class<? extends T> currentClass = clazz;
//        T bean;
//        String injectClassName = configuration.getRelationClassFromCurrent(currentClass.getTypeName());
//        if (injectClassName != null) {
//            System.out.println("injectClassName: " + injectClassName);
//            Constructor<?> currentConstructor = currentClass.getConstructors()[0];
//            if (injectClassName.equals("")) {
//                bean = (T) currentConstructor.newInstance();
//            } else {
//                Class<?>[] constructorParameterTypesForCurrentClass = currentConstructor.getParameterTypes();
//                Class<?> injectClass = Class.forName(injectClassName);
//                if (constructorParameterTypesForCurrentClass.length == 2) {
//                    bean = (T) currentConstructor.newInstance(getBean(injectClass), httpRequest);
//                } else {
//                    bean = (T) currentConstructor.newInstance((T) getBean(injectClass));
//                }
//            }
//            return bean;
//        } else {
//            return null;
//        }
//    }
//}
