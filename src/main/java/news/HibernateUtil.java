//package news;
//
//import news.model.*;
//import org.hibernate.SessionFactory;
//import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
//import org.hibernate.cfg.Configuration;
//
//import java.util.Properties;
//
//public class HibernateUtil {
//
//    private static SessionFactory sessionFactory;
//    private static Properties properties = new Properties();
//
//    private static void createSessionFactory() {
//        Configuration cfg = new Configuration().configure();
//        cfg.addProperties(properties);
//        cfg.addAnnotatedClass(Mailing.class);
//        cfg.addAnnotatedClass(Group.class);
//        cfg.addAnnotatedClass(Source.class);
//        cfg.addAnnotatedClass(Afisha.class);
//        cfg.addAnnotatedClass(Category.class);
//        cfg.addAnnotatedClass(User.class);
//        cfg.addAnnotatedClass(Comment.class);
//        cfg.addAnnotatedClass(CommentAttachment.class);
//        cfg.addAnnotatedClass(Tag.class);
//        cfg.addAnnotatedClass(Article.class);
//        cfg.addAnnotatedClass(ArticleImage.class);
//        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties());
//        sessionFactory = cfg.buildSessionFactory(registryBuilder.build());
//    }
//
//    public static SessionFactory getSessionFactory() {
//        if (sessionFactory == null) {
//            try {
//                createSessionFactory();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return sessionFactory;
//    }
//
//    public static void setConnectionProperties(String dbConnectionUrl, String dbUser, String dbPassword) {
//        properties.setProperty("hibernate.connection.url", dbConnectionUrl);
//        properties.setProperty("hibernate.connection.username", dbUser);
//        properties.setProperty("hibernate.connection.password", dbPassword);
//        createSessionFactory();
//    }
//}
