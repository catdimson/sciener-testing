package news.di.container;

import news.dao.connection.ConnectionPool;
import news.web.http.HttpRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BeanFactory {

    private static BeanFactory BEAN_FACTORY;
    private final XmlConfiguration configuration;
    private final ConnectionPool dbPool;
    private final HttpRequest httpRequest;


    private BeanFactory(ConnectionPool dbPool, HttpRequest httpRequest, String pathToConfig) {
        this.dbPool = dbPool;
        this.httpRequest = httpRequest;
        this.configuration = new XmlConfiguration();
        configuration.setPathAndLoad(pathToConfig);
    }

    public static void setSettings(ConnectionPool dbPool, HttpRequest httpRequest, String pathToConfig) {
        if (BEAN_FACTORY == null) {
            BEAN_FACTORY = new BeanFactory(dbPool, httpRequest, pathToConfig);
        }
    }

    public static BeanFactory getInstance() {
        return BEAN_FACTORY;
    }

    public <T> T getBean(Class<T> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        System.out.println("-------------------");
        Class<? extends T> currentClass = clazz;
        T bean;
        /*System.out.println("BeanFactory (clazz.getPackageName()): " + clazz.getPackageName());
        System.out.println("BeanFactory (clazz.getPackage()): " + clazz.getPackage());
        System.out.println("BeanFactory (clazz.getName()): " + clazz.getName());
        System.out.println("BeanFactory (clazz.getTypeName()): " + clazz.getTypeName());
        //System.out.println("BeanFactory (clazz.getDeclaredField()): " + clazz.getDeclaredField("request"));
        System.out.println("BeanFactory (непоколебимый ConnectionPool): " + dbPool.getClass().getName());
        System.out.println("BeanFactory (непоколебимый HttpRequest): " + httpRequest.getClass().getName());*/
        System.out.println("BeanFactory. CurrentClassName: " + clazz.getName());
        String injectClassName = configuration.getRelationClassFromCurrent(clazz.getTypeName());
        System.out.println("BeanFactory. injectClassName: " + injectClassName);
        if (injectClassName.equals("")) {
            System.out.println("BeanFactory equals(\"\")");
            Constructor<?> currentConstructor = currentClass.getConstructors()[0];
            bean = (T) currentConstructor.newInstance(dbPool);
            System.out.println("BeanFactory equals(\"\"). Bean: " + bean);
        } else {
            Constructor<?> currentConstructor = currentClass.getConstructors()[0];
            Class<?>[] constructorParameterTypesForCurrentClass = currentConstructor.getParameterTypes();
            if (constructorParameterTypesForCurrentClass.length == 2) {
                System.out.println("BeanFactory length == 2");
                //Class<?> classParameterType = Class.forName(constructorParameterTypesForCurrentClass[0].getTypeName());
                Class<?> injectClass = Class.forName(injectClassName);
                //Class injectClazz = configuration.getRelationClassFromCurrent()
                //System.out.println("BeanFactory: конструктор содержит 2 параметра");
                //System.out.println("BeanFactory: первый параметр конструктора: " + Class.forName(constructorParameterTypesForCurrentClass[0].getTypeName()).getName());
                //System.out.println("BeanFactory: второй параметр конструктора: " + Class.forName(constructorParameterTypesForCurrentClass[1].getTypeName()).getName());
                bean = (T) currentConstructor.newInstance(getBean(injectClass), httpRequest);
                System.out.println("BeanFactory length == 2. Bean: " + bean);
                //return bean;
            } else {
                System.out.println("BeanFactory промежуточные бины");
                Class<?> injectClass = Class.forName(injectClassName);
                bean = (T) currentConstructor.newInstance((T) getBean(injectClass));
                System.out.println("BeanFactory промежуточные бины. Bean: " + bean);
            }
        }
        return bean;
    }
}
