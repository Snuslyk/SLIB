package com.github.Snuslyk.slib;

import org.dhatim.fastexcel.reader.Cell;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.dhatim.fastexcel.reader.Sheet;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
        defaultExcelParsers();
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

    public static void loadExcelToDatabase(String path, Class<?> clazz){
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();

        Map<Integer, List<String>> data = readExcel(path);

        if (data == null) {
            session.close();
            return;
        }

        List<String> fields = data.get(1);

        int size = data.keySet().size();

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor();

            HashMap<String, Field> cachedFields = new HashMap<>();

            for (int i = 1; i < size; i++) {
                Object object = constructor.newInstance();

                for (String field : fields) {
                    System.out.println(field);
                    if (!cachedFields.containsKey(field))
                        cachedFields.put(field, clazz.getDeclaredField(field));

                    Class<?> type = cachedFields.get(field).getType();

                    cachedFields.get(field).set(object, excelParser.get(type).parse(data.get(i).get(fields.indexOf(field))));
                }

                session.persist(object);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        session.getTransaction().commit();
        session.close();
    }

    public static Map<Integer, List<String>> readExcel(String fileLocation) {
        try {
            Map<Integer, List<String>> data = new HashMap<>();

            try (FileInputStream file = new FileInputStream(fileLocation); ReadableWorkbook wb = new ReadableWorkbook(file)) {
                Sheet sheet = wb.getFirstSheet();
                try (Stream<Row> rows = sheet.openStream()) {
                    rows.forEach(r -> {
                        data.put(r.getRowNum(), new ArrayList<>());

                        for (Cell cell : r) {
                            data.get(r.getRowNum()).add(cell.getRawValue());
                        }
                    });
                }
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final HashMap<Class<?>, ExcelParser> excelParser = new HashMap<>();

    public interface ExcelParser {
        Object parse(String value);
    }

    public static void defaultExcelParsers(){
        excelParser.put(String.class, s -> s);
        excelParser.put(Integer.class, Integer::parseInt);
        excelParser.put(int.class, Integer::parseInt);
        excelParser.put(Double.class, Double::parseDouble);
        excelParser.put(double.class, Double::parseDouble);
        excelParser.put(Float.class, Float::parseFloat);
        excelParser.put(float.class, Float::parseFloat);
        excelParser.put(Boolean.class, Boolean::parseBoolean);
        excelParser.put(boolean.class, Boolean::parseBoolean);
        excelParser.put(Long.class, Long::parseLong);
        excelParser.put(long.class, Long::parseLong);
        excelParser.put(Short.class, Short::parseShort);
        excelParser.put(short.class, Short::parseShort);
        excelParser.put(Byte.class, Byte::parseByte);
        excelParser.put(byte.class, Byte::parseByte);
        excelParser.put(Character.class, s -> s.charAt(0));
        excelParser.put(char.class, s -> s.charAt(0));
        excelParser.put(Object.class, s -> s);
        excelParser.put(Class.class, s -> {
            try {
                return Class.forName(s);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}