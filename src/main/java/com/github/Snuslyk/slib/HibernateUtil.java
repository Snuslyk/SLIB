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
import java.util.*;
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

    /**
     * Reads an Excel file and returns its content with advanced options
     * @param fileLocation Path to the Excel file
     * @param sheetName Optional sheet name (null to use first sheet)
     * @param hasHeaderRow Whether the first row contains headers
     * @return Map with row data and column headers
     */
    public static ExcelData readExcel(String fileLocation, String sheetName, boolean hasHeaderRow) {
        try (FileInputStream file = new FileInputStream(fileLocation);
             ReadableWorkbook wb = new ReadableWorkbook(file)) {

            Sheet sheet;
            if (sheetName != null) {
                Optional<Sheet> matchingSheet = wb.getSheets()
                        .filter(s -> sheetName.equals(s.getName()))
                        .findFirst();
                sheet = matchingSheet.orElseGet(wb::getFirstSheet);
            } else {
                sheet = wb.getFirstSheet();
            }

            Map<Integer, List<String>> data = new HashMap<>();
            List<String> headers = new ArrayList<>();

            try (Stream<Row> rows = sheet.openStream()) {
                rows.forEach(r -> {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : r) {
                        rowData.add(cell.getRawValue());
                    }

                    if (r.getRowNum() == 0 && hasHeaderRow) {
                        headers.addAll(rowData);
                    } else {
                        data.put(hasHeaderRow ? r.getRowNum() - 1 : r.getRowNum(), rowData);
                    }
                });
            }

            return new ExcelData(headers, data);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Loads Excel data into database with automatic field mapping
     * @param path Excel file path
     * @param clazz Entity class
     * @param sheetName Optional sheet name
     */
    public static <T> void loadExcelToDatabase(String path, Class<T> clazz, String sheetName) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            ExcelData excelData = readExcel(path, sheetName, true);

            if (excelData == null || excelData.headers.isEmpty()) {
                throw new IllegalArgumentException("No data or headers found in Excel file");
            }

            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);

            HashMap<String, Field> cachedFields = new HashMap<>();
            Map<String, String> fieldMappings = buildFieldMappings(clazz, excelData.headers);

            // Process in batches of 50
            int batchSize = 50;
            int count = 0;

            for (Map.Entry<Integer, List<String>> entry : excelData.data.entrySet()) {
                T entity = constructor.newInstance();
                List<String> rowData = entry.getValue();

                for (int i = 0; i < excelData.headers.size(); i++) {
                    if (i >= rowData.size()) continue;

                    String header = excelData.headers.get(i);
                    String fieldName = fieldMappings.getOrDefault(header, null);

                    if (fieldName == null) continue;

                    try {
                        if (!cachedFields.containsKey(fieldName)) {
                            try {
                                Field field = clazz.getDeclaredField(fieldName);
                                field.setAccessible(true);
                                cachedFields.put(fieldName, field);
                            } catch (NoSuchFieldException e) {
                                System.err.println("Field not found: " + fieldName);
                                continue;
                            }
                        }

                        Field field = cachedFields.get(fieldName);
                        String cellValue = rowData.get(i);

                        if (cellValue != null && !cellValue.isEmpty()) {
                            Class<?> type = field.getType();
                            ExcelParser parser = excelParser.getOrDefault(type, s -> s);
                            field.set(entity, parser.parse(cellValue));
                        }
                    } catch (Exception e) {
                        System.err.println("Error setting field " + fieldName + ": " + e.getMessage());
                    }
                }

                session.persist(entity);

                // Flush and clear session periodically to manage memory
                if (++count % batchSize == 0) {
                    session.flush();
                    session.clear();
                }
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    private static <T> Map<String, String> buildFieldMappings(Class<T> clazz, List<String> headers) {
        Map<String, String> mappings = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> normalizedFieldMap = new HashMap<>();

        // Build normalized field name map
        for (Field field : fields) {
            String fieldName = field.getName();
            normalizedFieldMap.put(normalizeFieldName(fieldName), fieldName);
        }

        // Map headers to fields
        for (String header : headers) {
            String normalizedHeader = normalizeFieldName(header);
            if (normalizedFieldMap.containsKey(normalizedHeader)) {
                mappings.put(header, normalizedFieldMap.get(normalizedHeader));
            }
        }

        return mappings;
    }

    /**
     * Normalizes field/header names for comparison
     * @param name Original name
     * @return Normalized name (lowercase, no spaces or special chars)
     */
    private static String normalizeFieldName(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    // Class to hold Excel data structure
    public static class ExcelData {
        final List<String> headers;
        final Map<Integer, List<String>> data;

        public ExcelData(List<String> headers, Map<Integer, List<String>> data) {
            this.headers = headers;
            this.data = data;
        }

        public List<String> getHeaders() {
            return headers;
        }

        public Map<Integer, List<String>> getData() {
            return data;
        }
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