package com.github.Snuslyk.slib;

import com.github.Snuslyk.slib.factory.ButtonFactory;
import com.github.Snuslyk.slib.util.FieldWork;
import com.github.Snuslyk.slib.util.FieldsUtil;
import javafx.scene.input.ScrollEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static final List<Class<?>> annotatedClassed = new ArrayList<>();
    private static boolean builded = false;

    public static void addAnnotatedClass(Class<?> clazz){
        annotatedClassed.add(clazz);
        if (builded) {
            //System.out.println(clazz.getSimpleName() + " is not added to mappings because they builded");
        }
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory != null) return sessionFactory;
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            annotatedClassed.forEach(configuration::addAnnotatedClass);
            builded = true;
            SessionFactory factory = configuration.buildSessionFactory(new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build());
            sessionFactory = factory;
            entityManager = factory.createEntityManager();
            return factory;
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void fastSave(Object object){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.persist(object);

        session.getTransaction().commit();
        session.close();
    }
    public static void merge(Object object){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.merge(object);

        session.getTransaction().commit();
        session.close();
    }
    public static <T> void merge(T object, Update<T> update){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        T t = (T) session.merge(object);
        t = update.update(t);

        session.getTransaction().commit();
        session.close();
    }

    public static <T> void update(Class<T> clazz, int id, Update<T> update){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        T old = session.get(clazz, id);
        if (old != null) {
            session.evict(old);
            old = update.update(old);
            if (old != null) session.merge(old);
        }

        session.getTransaction().commit();
        session.close();
    }

    public interface Update<T> {
        T update(T old);
    }


    public static void remove(Object object){
        if (object instanceof RowData rowData){
            rowData.delete();
        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.remove(object);

        session.getTransaction().commit();
        session.close();
    }

    public static <T> T getObjectById(Class<T> clazz, int id) {
        Transaction transaction = null;
        T get = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            get = session.get(clazz, id);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return get;
    }

    private static EntityManager entityManager;

    public static <T> List<T> getObjectWithFilter(Class<T> clazz, FilterIO... filters){
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);

        criteriaQuery.select(root);

        for (FilterIO filter : filters) {
            List<Predicate> predicates = new ArrayList<>();

            if (filter != null){
                filter.getPredicates(root,criteriaBuilder,predicates);
            }

            for (Predicate p : predicates) {
                criteriaQuery.where(p);
            }

        }

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        session.flush();
        Query query = session.createQuery(criteriaQuery);
        List<T> list =  query.getResultList();
        session.close();
        return list;
    }

}