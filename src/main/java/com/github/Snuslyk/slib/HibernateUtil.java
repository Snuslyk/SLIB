package com.github.Snuslyk.slib;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            return configuration.buildSessionFactory(new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build());
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void fastSave(Object object){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        session.save(object);

        session.getTransaction().commit();
        session.close();
    }
    public static void remove(Object object){
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

    private static final EntityManager entityManager = sessionFactory.createEntityManager();

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

        Query query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

}