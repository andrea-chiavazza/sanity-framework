package func.persist;

import org.pcollections.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * todo: enable foreign constraints ? In that case the recursive remove() must be done right
 * todo: make lists_table implemented with the framework itself ?
 * todo: use objId -> Object caching
 * todo: create table only when asked
 * todo: test nested collections like list of lists
 * todo: memoize caches
 * todo: support all PCollection's and PMap
 * todo: thread-safety
 * todo: use AUTO_INCREMENT for id ?
 * todo: id overflow ?
 * todo: review all namings
 * todo: remove fragmentation whenever possible
 * todo: feature: getters must begin with get or is
 * todo: encapsulate all sql queries ?
 * todo: support String's containing unicode characters
 * todo: test null values in all situations
 * todo: method that converts the result of a manual SELECT to a collection of instances of the correct class
 */
public class DB {
    private static final Map<String,PMap<String,String>> dbPropertiesCache = new HashMap<>();

    // ID column should have a name that can't be returned by classToTableName(). A trailing '_' should do the job
    public static final String ID = "id_"; // can be used by user queries
    private static final int ID_INDEX = 1;

    public static final String VALUE = "value_"; // can be used by user queries
    private static final int VALUE_INDEX = 2;

    private static final String V_LISTS_ID = "v_lists_id_";
    private static final int V_LISTS_ID_INDEX = 2;

    private static final String V_TYPE = "v_type_";
    private static final int V_TYPE_INDEX = 3;

    private static final String K_LISTS_ID = "k_lists_id_";
    private static final int K_LISTS_ID_INDEX = 4;

    private static final String K_TYPE = "k_type_";
    private static final int K_TYPE_INDEX = 5;

    private static final String CREATE_TABLE_POSTFIX = "create-table-postfix";
    private final Connection conn;
    private final PMap<String,String> dbProperties;
    private final IdCounter idCounter = new IdCounter(this);
    private final LinkedLists lists;

    public DB(Connection conn) throws SQLException {
        try {
            this.conn = conn;
            String dbName = conn.getMetaData().getDatabaseProductName().split(" ")[0];
            synchronized (dbPropertiesCache) {
                if (! dbPropertiesCache.containsKey(dbName)) {
                    Properties properties = new Properties();
                    try {
                        properties.load(getClass().getResourceAsStream("/func/persist/" + dbName + ".properties"));
                    } catch (IOException e) {
                        properties.load(getClass().getResourceAsStream("/func/persist/SQL-92.properties"));
                    }
                    dbPropertiesCache.put(dbName, HashTreePMap.<String,String>from((Map) properties));
                }
                dbProperties = dbPropertiesCache.get(dbName);
            }
            String init = dbProperties.get("init");
            if (init != null && !init.isEmpty()) {
                conn.createStatement().execute(init);
            }
            lists = new LinkedLists(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String classToTableName(Class cl) {
        return "\"" + classToUnquotedTableName(cl) + "\"";
    }

    public static String methodToColumn(String methodName) {
        return Utility.camelToUnderscored(methodName);
    }

    public synchronized Connection getConnection() {
        return conn;
    }

    public synchronized <T> PSet<T> retrieveAll(Class<T> cl) throws SQLException {
        if (!tableExists(cl)) {
            return Empty.set();
        } else {
            ResultSet resultSet = conn.createStatement().executeQuery("SELECT * from " + classToTableName(cl));
            PSet<T> objects = Empty.set();
            while (resultSet.next()) {
                objects = objects.plus(retrieve(resultSet, cl));
            }
            return objects;
        }
    }

    public synchronized <T> PSet<T> convert(ResultSet resultSet,
                                            Class<T> cl) throws SQLException {
        if (!tableExists(cl)) {
            return Empty.set();
        } else {
            PSet<T> objects = Empty.set();
            while (resultSet.next()) {
                objects = objects.plus(retrieve(resultSet, cl));
            }
            return objects;
        }
    }

    public synchronized <T> ObjId<T> write(T obj) throws SQLException {
        Class<? extends T> cl = (Class<? extends T>) obj.getClass(); // cast correct according to the API

        if (obj instanceof PCollection) {
            return (ObjId<T>) writePCollection((PCollection) obj); // cast correct according to the API
        } else if (obj instanceof PMap) {
            return (ObjId<T>) writePMap((PMap) obj); // cast correct according to the API
        }
        if (!tableExists(cl)) {
            createTable(cl);
        }
        List<Object> values = Refl.getFieldsValues(obj);
        int size = values.size();
        List<String> quesMarks = new ArrayList<>(size);
        for (Object value1 : values) {
            quesMarks.add("?");
        }
        String tableName = classToTableName(cl);
        PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO " + tableName + " VALUES(?, " + Utility.commaSeparate(quesMarks) + ")");
        long id = idCounter.getNextAndIncrementId(tableName);
        ps.setLong(1, id);
        PVector<Object> eValues = TreePVector.from(values);
        boolean hasChanged = false;

        for (int i = 2; i <= size + 1; i++) {
            Object value = values.get(i - 2);
            if (!setValue(ps, value, i)) {
                ObjId<Object> objId = write(value);
                eValues = eValues.with(i - 2, objId.object);
                ps.setLong(i, objId.rowId.id);
                hasChanged = true;
            }
        }
        ps.executeUpdate();
        T value;
        if (hasChanged) {
            value = Refl.instantiate(Refl.findConstructor(cl), eValues.toArray());
        } else {
            value = obj;
        }
        return new ObjId<>(
            new RowId<>(cl, id),
            value);
    }

    //todo: refactor in smaller methods
    public synchronized <T> void write(T obj,
                                       ObjId<T> objId) throws SQLException {
        if (obj == objId.object) {
            return;
        } else if (obj instanceof PCollection) {
            writePCollection((PCollection) obj, (ObjId<PCollection>) objId); // cast correct according to the API
            return;
        } else if (obj instanceof PMap) {
            writePMap((PMap) obj, (ObjId<PMap>) objId); // cast correct according to the API
            return;
        }
        Class<? extends T> cl = objId.rowId.cl;
        if (Utility.isBasicType(cl)) {
            PreparedStatement ps = conn.prepareStatement(
                Utility.makeUpdateQuery(
                    cl,
                    Collections.singletonList(VALUE),
                    objId.rowId.id));
            ps.setObject(1, obj);
            ps.executeUpdate();
            objId.object = obj;
            return;
        }
        RowId<? extends T> rowId = objId.rowId;
        if (cl != obj.getClass()) {
            throw new RuntimeException("The objId class (" + cl +
                ") doesn't match the object to be written (" + obj.getClass() + ")");
        }
        List<Object> values = Refl.getFieldsValues(obj);
        PVector<Method> getters = Refl.findGetters(cl);
        List<Object> previousValues = Refl.getFieldsValues(objId.object);
        int size = values.size();
        Map<Integer,Object> toBeUpdated = new LinkedHashMap<>();
        PMap<Integer,RowId<?>> rowIds = null;
        for (int i = 0; i < size; i++) {
            Object value = values.get(i);
            Object previousValue = previousValues.get(i);
            if (! nullEqual(value, previousValue)) {
                if (value == null || Utility.isBasicType(value.getClass())) {
                    toBeUpdated.put(i, value);
                } else {
                    if (rowIds == null) {
                        rowIds = getRowIds(rowId);
                    }
                    write(value, new ObjId<>(rowIds.get(i), previousValue));
                }
            }
        }
        if (!toBeUpdated.isEmpty()) {
            List<String> tokens = new ArrayList<>();
            for (Map.Entry<Integer,Object> entry : toBeUpdated.entrySet()) {
                tokens.add(Utility.camelToUnderscored(getters.get(entry.getKey()).getName()));
            }
            PreparedStatement ps = conn.prepareStatement(Utility.makeUpdateQuery(cl, tokens, rowId.id));
            int i = 1;
            for (Map.Entry<Integer,Object> entry : toBeUpdated.entrySet()) {
                Object value = entry.getValue();
                setValue(ps, value, i);
                i++;
            }
            ps.executeUpdate();
            objId.object = obj;
        }
    }

    public synchronized void remove(ObjId<?> objId) throws SQLException {
        RowId rowId = objId.rowId;
        remove(rowId);
        objId.object = null;
    }

    public synchronized void removeAll(Class<?> cl) throws SQLException {
        String tableName = classToTableName(cl);
        if (Refl.hasReferences(cl)) {
            if (tableExists(cl)) {
                if (Refl.hasReferences(cl)) {
                    ResultSet res = conn.createStatement().executeQuery("SELECT * from " + tableName);
                    while (res.next()) {
                        remove(new RowId<>(cl, res.getLong(1)));
                    }
                }
                idCounter.reset(tableName);
            }
        } else {
            conn.createStatement().executeUpdate("DELETE FROM " + tableName);
        }
    }

    public synchronized  <T> T retrieve(RowId<T> rowId) throws SQLException {
        return retrieve(getResultSet(rowId), rowId.cl);
    }

    /** Removes all references recursively */
    public synchronized void remove(RowId rowId) throws SQLException {
        if (Refl.hasReferences(rowId.cl)) {
            ResultSet res = getResultSet(rowId);
            if (PCollection.class.isAssignableFrom(rowId.cl)) {
                long listsIndex = res.getLong(V_LISTS_ID_INDEX);
                while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
                    listsIndex = lists.removeList(listsIndex, valueType(res));
                }
            } else if (PMap.class.isAssignableFrom(rowId.cl)) {
                long vListsIndex = res.getLong(V_LISTS_ID_INDEX);
                while (vListsIndex != BaseIterator.LISTS_INDEX_NONE) {
                    vListsIndex = lists.removeList(vListsIndex, valueType(res));
                }
                long kListsIndex = res.getLong(K_LISTS_ID_INDEX);
                while (kListsIndex != BaseIterator.LISTS_INDEX_NONE) {
                    kListsIndex = lists.removeList(kListsIndex, keyType(res));
                }
            } else {
                for (RowId id : getReferences(res, rowId)) {
                    remove(id);
                }
            }
        }
        conn.createStatement().executeUpdate(
            "DELETE FROM " + classToTableName(rowId.cl) + " WHERE " + ID + "=" + rowId.id);
    }

    //__________________________________________________________________________________________________________________

    private static boolean setValue(PreparedStatement ps,
                                    Object value,
                                    int i) throws SQLException {
        if (value == null) {
            ps.setObject(i, null);
        } else {
            Class<?> valueClass = value.getClass();
            if (valueClass == Boolean.class) {
                ps.setBoolean(i, (Boolean) value);
            } else if (valueClass == Character.class) {
                ps.setInt(i, ((Character) value));
            } else if (valueClass == String.class) {
                ps.setString(i, (String) value);
            } else if (valueClass == Byte.class) {
                ps.setByte(i, (Byte) value);
            } else if (valueClass == Short.class) {
                ps.setShort(i, (Short) value);
            } else if (valueClass == Integer.class) {
                ps.setInt(i, (Integer) value);
            } else if (valueClass == Long.class) {
                ps.setLong(i, (Long) value);
            } else if (valueClass == Float.class) {
                ps.setFloat(i, (Float) value);
            } else if (valueClass == Double.class) {
                ps.setDouble(i, (Double) value);
            } else if (valueClass == BigInteger.class) {
                ps.setBigDecimal(i, new BigDecimal((BigInteger) value));
            } else if (valueClass == BigDecimal.class) {
                ps.setBigDecimal(i, (BigDecimal) value);
            } else {
                return false;
            }
        }
        return true;
    }

    private static String classToUnquotedTableName(Class cl) {
        String name;
        if (PVector.class.isAssignableFrom(cl)) {
            name = PVector.class.getName();
        } else if (PSet.class.isAssignableFrom(cl)) {
            name = PSet.class.getName();
        } else if (PMap.class.isAssignableFrom(cl)) {
            name = PMap.class.getName();
        } else {
            name = cl.getName();
        }
        return Utility.camelToUnderscored(name);
    }

    private static boolean nullEqual(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    private PMap<Integer,RowId<?>> getRowIds(RowId<?> rowId) throws SQLException {
        PMap<Integer,RowId<?>> map = Empty.map();
        ResultSet res = null;
        PVector<Method> getters = Refl.findGetters(rowId.cl);
        for (int i = 0, findGettersSize = getters.size(); i < findGettersSize; i++) {
            Method getter = getters.get(i);
            Class<?> returnType = getter.getReturnType();
            if (!Utility.isBasicType(returnType)) {
                if (res == null) {
                    res = getResultSet(rowId);
                }
                map = map.plus(i, new RowId<>(returnType, res.getLong(i + 2)));
            }
        }
        return map;
    }

    /** Used when deleting a objId
     * Must be a rowId with references*/
    private PVector<RowId<?>> getReferences(ResultSet res,
                                            RowId<?> rowId) throws SQLException {
        PVector<RowId<?>> references = Empty.vector();
        int r = 2;
        for (Method getter : Refl.findGetters(rowId.cl)) {
            Class<?> retType = getter.getReturnType();
            if (!Utility.isBasicType(retType)) {
                Object obj = res.getObject(r); // this could be null
                if (obj != null) {
                    references = references.plus(new RowId<>(retType, (Long) obj));
                }
            }
            r++;
        }
        return references;
    }

    boolean tableExists(Class cl) throws SQLException {
        return tableExists(classToUnquotedTableName(cl));
//        ResultSet resultSet = conn.createStatement().executeQuery(
//            "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='" + conn.getCatalog() +
//                "' AND TABLE_NAME='" + classToUnquotedTableName(cl) + "'");
//            "SELECT COUNT(*) " +
//                "FROM information_schema.tables " +
//                "WHERE table_schema=" + conn.getSchema() +
//                " AND table_name=" + classToTableName(cl));
//        return resultSet.next();
//        return conn.getMetaData().getTables(null, null, "`" + classToUnquotedTableName(cl) + "`", null).first();
//        return conn.getMetaData().getTables(null, null, DB.classToTableName(cl), null).first();
    }

    boolean tableExists(String unquotedTableName) throws SQLException {
        // doesn't work with MySQL
//        return conn.getMetaData().getTables(conn.getCatalog(), null, unquotedTableName, null).first();

        ResultSet resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + unquotedTableName + "'");
        // fails with postgreSQL
//        " AND TABLE_SCHEMA='" + conn.getCatalog() + "'");
        return resultSet.next();
    }

    // table must exist
    private String makeColumnDefinition(String name, Class returnType) throws SQLException {
        String s = name + " " + classToSQLType(returnType);
        if (returnType.isPrimitive()) {
            s += " NOT NULL";
        } else if (!Utility.isBasicType(returnType)) {
//            if (!tableExists(returnType)) {
//                createTable(returnType);
//            }
//            s += " REFERENCES " + classToTableName(returnType) + "(" + ID + ") ON DELETE CASCADE";
        }
        return s;
    }

    private void createTable(Class<?> cl) throws SQLException {
        List<String> tokens = new ArrayList<>();
        if (Utility.isBasicType(cl)) {
            tokens.add(makeColumnDefinition(VALUE, cl));
        } else {
            for (Method method : Refl.findGetters(cl)) {
                tokens.add(
                    makeColumnDefinition(
                        Utility.camelToUnderscored(method.getName()),
                        method.getReturnType()));
            }
        }
        String tableName = classToTableName(cl);
        conn.createStatement().executeUpdate(
            "CREATE TABLE " + tableName + "(" +
                ID + " " + classToSQLType(Long.class) + ", " +
                Utility.commaSeparate(tokens) +
                ", PRIMARY KEY(" + ID + ")) " + dbProperties.get(CREATE_TABLE_POSTFIX));
        idCounter.reset(tableName);
    }

    private <T extends PCollection> ObjId<T> writePCollection(T coll) throws SQLException {
        Class<? extends T> collClass = (Class<? extends T>) coll.getClass(); // cast correct according to the API
        String tableName = classToTableName(collClass);
        if (!tableExists(collClass)) {
            String idType = classToSQLType(Long.class);
            conn.createStatement().executeUpdate(
                "CREATE TABLE " + tableName + "(" +
                    ID + " " + idType + ", " +
                    V_LISTS_ID + " " + idType + ", " +
                    V_TYPE + " " + classToSQLType(String.class) + ", " +
                    "PRIMARY KEY(" + ID + ")) " + dbProperties.get(CREATE_TABLE_POSTFIX));
        }
        long id = idCounter.getNextAndIncrementId(tableName);
        insertValue(collClass, id, coll);
        EPCollection ePColl;
        if (coll instanceof EPCollection) {
            ePColl = (EPCollection) coll;
        } else {
            if (coll instanceof PVector) {
                ePColl = new EPVector((PVector) coll);
            } else if (coll instanceof PSet) {
                ePColl = new EPSet((PSet) coll);
            } else {
                throw new RuntimeException("Unsupported PCollection implementation: " + coll.getClass().getName());
            }
        }
        return new ObjId<>(
            new RowId<>(collClass, id),
            (T) ePColl);
    }

    private void addValueToEPCollection(ObjId<EPCollection> objId,
                                        Object e) throws SQLException {
        RowId<? extends EPCollection> rowId = objId.rowId;
        ResultSet res = getResultSet(rowId);
        long listsIndex = res.getLong(V_LISTS_ID_INDEX);
        ListEntry entry = null;
        long lastId = BaseIterator.LISTS_INDEX_NONE;
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            entry = lists.retrieveEntry(listsIndex);
            lastId = listsIndex;
            listsIndex = entry.getNext();
        }
        if (entry != null) {
            long first = entry.getFirst();
            int size = entry.getSize();
            //if the element on the collection is last in its table
            RowId newRowId = write(e).rowId;
            if (idCounter.getNextId(classToTableName(valueType(res))) == first + size + 1) {
                // then increase the collection
                lists.updateListSize(lastId, size + 1);
            } else {
                // else create new LISTS_TABLE
                long newId = lists.addList(newRowId, 1, BaseIterator.LISTS_INDEX_NONE);
                lists.updateListNext(lastId, newId);
            }
        } else { // coll is empty
            RowId firstRowId = write(e).rowId;
            addNewFirstList(rowId, firstRowId, 1, BaseIterator.LISTS_INDEX_NONE);
        }
    }

    private void addNewFirstList(RowId<? extends EPCollection> collRowId,
                                 RowId firstRowId,
                                 int size,
                                 long nextIndex) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
            Utility.makeUpdateQuery(collRowId.cl, Arrays.asList(V_LISTS_ID, V_TYPE), collRowId.id));
        long newListId = lists.addList(firstRowId, size, nextIndex);
        ps.setLong(1, newListId);
        ps.setString(2, firstRowId.cl.getName());
        ps.executeUpdate();
    }

    private void addCollToEPCollection(ObjId<EPCollection> objId,
                                       Collection coll) throws SQLException {
        ResultSet res = getResultSet(objId.rowId);
        long listsIndex = res.getLong(V_LISTS_ID_INDEX);
        ListEntry entry = null;
        long lastId = BaseIterator.LISTS_INDEX_NONE;
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            entry = lists.retrieveEntry(listsIndex);
            lastId = listsIndex;
            listsIndex = entry.getNext();
        }
        Iterator it = coll.iterator();
        if (it.hasNext()) {
            if (entry != null) { // the collection we are adding to is not empty
                long first = entry.getFirst();
                int size = entry.getSize();
                //if the element on the collection is last in its table
                if (idCounter.getNextId(classToTableName(valueType(res))) == first + size) {
                    // then increase the collection
                    lists.updateListSize(lastId, size + coll.size());
                    while (it.hasNext()) {
                        write(it.next());
                    }
                } else { // create new LISTS_TABLE
                    RowId firstRowId = write(it.next()).rowId;
                    while (it.hasNext()) {
                        write(it.next());
                    }
                    long newId = lists.addList(firstRowId, coll.size(), BaseIterator.LISTS_INDEX_NONE);
                    lists.updateListNext(lastId, newId);
                }
            } else { // the collection we are adding to is empty
                RowId firstRowId = write(it.next()).rowId;
                while (it.hasNext()) {
                    write(it.next());
                }
                addNewFirstList(objId.rowId, firstRowId, coll.size(), BaseIterator.LISTS_INDEX_NONE);
            }
        }
    }

    /** can be either a key or a value */
    private int removeElement(long listsIndex,
                              Object e) throws SQLException {
        Class<?> cl = e.getClass();
        String tableName = classToTableName(cl);
        int counter = 0;
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            ListEntry entry = lists.retrieveEntry(listsIndex);
            int size = entry.getSize();
            long first = entry.getFirst();
            ResultSet res = conn.createStatement().executeQuery(
                "SELECT * FROM " + tableName + " WHERE " + ID + " BETWEEN " + first + " AND " + (first + size - 1));
            int i = 0;
            while (res.next()) {
                if (retrieve(res, cl).equals(e)) {
                    removeIndexFromList(i, cl, listsIndex);
                    return counter + i;
                }
                i++;
            }
            counter += size;
            listsIndex = entry.getNext();
        }
        return -1;
    }

    private void setIndexInList(int i,
                                Object e,
                                long listsIndex) throws SQLException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("index is negative (" + i + ")");
        }
        long counter = 0;
        Class<?> cl = e.getClass();
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            ListEntry entry = lists.retrieveEntry(listsIndex);
            int size = entry.getSize();
            if (i >= counter && i < counter + size) {
                RowId<?> rowId = new RowId<>(cl, entry.getFirst() + i - counter);
                write(e, new ObjId<>(rowId, retrieve(rowId)));
                return;
            }
            counter += size;
            listsIndex = entry.getNext();
        }
        throw new IndexOutOfBoundsException("index " + i + " is bigger than the collection size (" + counter + ")");
    }

    private void addValueToList(int i,
                                Object e,
                                RowId<? extends EPCollection> rowId) throws SQLException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("index is negative (" + i + ")");
        }
        long counter = 0;
        Class<?> cl = e.getClass();
        long listsIndex = getResultSet(rowId).getLong(V_LISTS_ID_INDEX);
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            ListEntry entry = lists.retrieveEntry(listsIndex);
            int size = entry.getSize();
            long nextListId = entry.getNext();
            if (i >= counter && i < counter + size) {
                // adds the new element to the database
                ObjId<?> eObjId = write(e);
                int firstListNewSize = (int) (i - counter);
                if (i > counter) { // not the first item in the list
                    // creates the second list
                    long secondListId = lists.addList(
                        new RowId<>(cl, entry.getFirst() + firstListNewSize),
                        size - firstListNewSize,
                        nextListId);

                    // truncates the first list
                    lists.updateListSize(listsIndex, firstListNewSize);

                    // creates the singleton list
                    long singletonListId = lists.addList(eObjId.rowId, 1, secondListId);

                    // sets the first list next to the second list
                    lists.updateListNext(listsIndex, singletonListId);
                } else {
                    addNewFirstList(rowId, eObjId.rowId, 1, listsIndex);
                }
                return;
            }
            counter += size;
            listsIndex = nextListId;
        }
        throw new IndexOutOfBoundsException("index " + i + " is bigger than the collection size (" + counter + ")");
    }

    // coll must be non-empty ?
    private void addCollToList(int i,
                               Collection coll,
                               RowId<? extends EPCollection> rowId) throws SQLException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("index is negative (" + i + ")");
        }
        long counter = 0;
        Class<?> cl = coll.iterator().next().getClass();
        long listsIndex = getResultSet(rowId).getLong(V_LISTS_ID_INDEX);
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            ListEntry entry = lists.retrieveEntry(listsIndex);
            int size = entry.getSize();
            long nextListId = entry.getNext();
            if (i >= counter && i < counter + size) {
                // adds the new collection to the database
                Iterator it = coll.iterator();
                //todo: remove check and accept only non-empty ?
                ObjId<?> firstObjId;
                if (it.hasNext()) {
                    firstObjId = write(it.next());
                    while (it.hasNext()) {
                        write(it.next());
                    }
                } else {
                    return;
                }

                if (i > counter) { // not the first item in the list
                    int firstListNewSize = (int) (i - counter);
                    // creates the second list
                    long secondListId = lists.addList(
                        new RowId<>(cl, entry.getFirst() + i),
                        size - firstListNewSize,
                        nextListId);

                    // truncates the first list
                    lists.updateListSize(listsIndex, firstListNewSize);

                    // creates the new list
                    long newListId = lists.addList(firstObjId.rowId, coll.size(), secondListId);

                    // sets the first list next to the second list
                    lists.updateListNext(listsIndex, newListId);
                } else {
                    if (counter == 0) {
                        addNewFirstList(rowId, firstObjId.rowId, coll.size(), listsIndex);
                    } else {
                        //todo: test it
                        throw new RuntimeException("TODO");
                    }
                }
                return;
            }
            counter += size;
            listsIndex = nextListId;
        }
        throw new IndexOutOfBoundsException("index " + i + " is bigger than the collection size (" + counter + ")");
    }

    private void removeIndexFromList(int i,
                                     Class<?> cl,
                                     long listsIndex) throws SQLException {
        if (i < 0) {
            throw new IndexOutOfBoundsException("index is negative (" + i + ")");
        }
        long previousListsIndex = -1;
        long counter = 0;
        while (listsIndex != BaseIterator.LISTS_INDEX_NONE) {
            ListEntry entry = lists.retrieveEntry(listsIndex);
            int size = entry.getSize();
            long nextListId = entry.getNext();
            if (i >= counter && i < counter + size) {
                long first = entry.getFirst();
                long idToBeDeleted = first + i - counter;

                remove(new RowId<>(cl, idToBeDeleted));
                int newSize = (int) (i - counter); // could the long not fit ?

                if (idToBeDeleted == first) { // the first element is being deleted
                    lists.updateListFirst(listsIndex, first + 1);
                    if (size == 1 && previousListsIndex != -1) { //todo: test this case
                        lists.removeList(listsIndex, cl);
                        lists.updateListNext(previousListsIndex, nextListId);
                    } else {
                        // truncate the original list
                        lists.updateListSize(listsIndex, size - 1);
                    }
                } else {
                    // truncate the original list
                    lists.updateListSize(listsIndex, newSize);

                    int secondListSize = size - newSize - 1;
                    if (secondListSize > 0) {
                        // create a new list
                        long newListId = lists.addList(
                            new RowId<>(cl, idToBeDeleted + 1),
                            secondListSize,
                            nextListId);

                        // change the original list's next to point to the new list
                        lists.updateListNext(listsIndex, newListId);
                    }
                }
                return;
            }
            counter += size;
            previousListsIndex = listsIndex;
            listsIndex = nextListId;
        }
        throw new IndexOutOfBoundsException("index " + i + " is bigger than the collection size (" + counter + ")");
    }

    private void writeEPCollection(EPCollection<Object> coll,
                                   ObjId<EPCollection> objId) throws SQLException {
        PCollection<Object> origColl = coll.getOriginal();
        for (CollChange change : coll.getChanges()) {
// PCollection changes
            if (change instanceof Plus) {
                addValueToEPCollection(objId, ((Plus) change).e);
            } else if (change instanceof PlusAll) {
                Collection newColl = ((PlusAll) change).list;
                if (newColl.isEmpty()) {
                    // do nothing
                } else if (coll instanceof PVector) {
                    addCollToEPCollection(objId, newColl);
                } else if (coll instanceof PSet) {
                    for (Object obj : newColl) {
                        addValueToEPCollection(objId, obj);
                    }
                } else {
                    throw new RuntimeException("Unknown implementation of PCollection: " + coll.getClass().getName());
                }
            } else if (change instanceof Minus) {
                removeElement(
                    getResultSet(objId.rowId).getLong(V_LISTS_ID_INDEX),
                    ((Minus) change).e);
            } else if (change instanceof MinusAll) {
                for (Object e : ((MinusAll) change).list) {
                    removeElement(
                        getResultSet(objId.rowId).getLong(V_LISTS_ID_INDEX),
                        e);
                }
// PVector changes
            } else if (change instanceof With) {
                With with = (With) change;
                setIndexInList(
                    with.i,
                    with.e,
                    getResultSet(objId.rowId).getLong(V_LISTS_ID_INDEX));
            } else if (change instanceof PlusI) {
                PlusI plusI = (PlusI) change;
                if (plusI.i == ((PCollection) coll).size() - 1) {
                    addValueToEPCollection(objId, plusI.e);
                } else {
                    addValueToList(
                        plusI.i,
                        plusI.e,
                        objId.rowId);
                }
            } else if (change instanceof PlusAllI) {
                PlusAllI plusAllI = (PlusAllI) change;
                Collection newColl = plusAllI.list;
                if (newColl.isEmpty()) {
                    // do nothing
                } else if (plusAllI.i == ((PCollection) origColl).size()) {
                    addCollToEPCollection(objId, newColl);
                } else {
                    addCollToList(
                        plusAllI.i,
                        newColl,
                        objId.rowId);
                }
            } else if (change instanceof MinusI) {
                int index = ((MinusI) change).i;
                if (index < 0 || index >= origColl.size()) {
                    //todo: test
                    throw new IndexOutOfBoundsException("index " + index + " for a size " + (origColl.size()));
                } else {
                    ResultSet res = getResultSet(objId.rowId);
                    removeIndexFromList(index,
                        valueType(res),
                        res.getLong(V_LISTS_ID_INDEX));
                }
            } else {
                throw new RuntimeException("Illegal CollChange: " + change);
            }
        }
        objId.object = coll.withResetChanges();
    }

    private void writePCollection(PCollection coll,
                                  ObjId<PCollection> objId) throws SQLException {
        if (coll == objId.object) {
            return;
        }

        if (coll instanceof EPCollection && objId.object instanceof EPCollection &&
            ((EPCollection) coll).getOriginal() == ((EPCollection) objId.object).getOriginal()) {
            writeEPCollection((EPCollection) coll, (ObjId) objId);
            return;
        }
        remove(objId);
        insertValue(objId.rowId.cl, objId.rowId.id, coll);
        objId.object = coll;
    }

    private void insertValue(Class cl,
                             long id,
                             Collection coll) throws SQLException {
        String typeName;
        long lId;
        Iterator it = coll.iterator();
        if (it.hasNext()) {
            RowId rowId = write(it.next()).rowId;
            typeName = rowId.cl.getName();
            lId = lists.addList(rowId, coll.size(), BaseIterator.LISTS_INDEX_NONE);
        } else {
            lId = BaseIterator.LISTS_INDEX_NONE;
            typeName = null;
        }
        while (it.hasNext()) {
            write(it.next());
        }
        PreparedStatement ps = conn.prepareStatement("INSERT INTO " + classToTableName(cl) + " VALUES(?, ?, ?)");
        ps.setLong(ID_INDEX, id);
        ps.setLong(V_LISTS_ID_INDEX, lId);
        ps.setString(V_TYPE_INDEX, typeName);
        ps.executeUpdate();
    }

    private <T extends PMap> ObjId<T> writePMap(T map) throws SQLException {
        Class<? extends T> mapClass = (Class<? extends T>) map.getClass(); // cast correct according to the API
        String tableName = classToTableName(mapClass);
        if (!tableExists(mapClass)) {
            String idType = classToSQLType(Long.class);
            String stringType = classToSQLType(String.class);
            conn.createStatement().executeUpdate(
                "CREATE TABLE " + tableName + "(" +
                    ID + " " + idType + ", " +
                    V_LISTS_ID + " " + idType + ", " +
                    V_TYPE + " " + stringType + ", " +
                    K_LISTS_ID + " " + idType + ", " +
                    K_TYPE + " " + stringType + ", " +
                    "PRIMARY KEY(" + ID + ")) " + dbProperties.get(CREATE_TABLE_POSTFIX));
        }
        Iterator<Map.Entry> it = map.entrySet().iterator();
        long id = insertMapEntry(map, tableName, it);
        EPMap ePMap;
        if (map instanceof EPMap) {
            ePMap = (EPMap) map;
        } else {
            ePMap = new EPMap<>(map);
        }
        return new ObjId<>(
            new RowId<>(mapClass, id),
            (T) ePMap);
    }

    private <T extends PMap> long insertMapEntry(T map,
                                                 String tableName,
                                                 Iterator<Map.Entry> it) throws SQLException {
        String vType;
        String kType;
        long vlId, klId;
        if (it.hasNext()) {
            int size = map.size();
            Map.Entry entry = it.next();
            RowId<?> vFirstRowId = write(entry.getValue()).rowId;
            vlId = lists.addList(vFirstRowId, size, BaseIterator.LISTS_INDEX_NONE);
            vType = vFirstRowId.cl.getName();
            RowId<?> kFirstRowId = write(entry.getKey()).rowId;
            klId = lists.addList(kFirstRowId, size, BaseIterator.LISTS_INDEX_NONE);
            kType = kFirstRowId.cl.getName();
        } else {
            kType = null;
            vType = null;
            vlId = BaseIterator.LISTS_INDEX_NONE;
            klId = BaseIterator.LISTS_INDEX_NONE;
        }
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            write(entry.getKey());
            write(entry.getValue());
        }
        long id = idCounter.getNextAndIncrementId(tableName);
        PreparedStatement ps = conn.prepareStatement("INSERT INTO " + tableName + " VALUES(?, ?, ?, ?, ?)");
        ps.setLong(ID_INDEX, id);
        ps.setLong(V_LISTS_ID_INDEX, vlId);
        ps.setString(V_TYPE_INDEX, vType);
        ps.setLong(K_LISTS_ID_INDEX, klId);
        ps.setString(K_TYPE_INDEX, kType);
        ps.executeUpdate();
        return id;
    }

    private Class<?> valueType(ResultSet res) throws SQLException {
        try {
            return Class.forName(res.getString(V_TYPE_INDEX));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> keyType(ResultSet res) throws SQLException {
        try {
            return Class.forName(res.getString(K_TYPE_INDEX));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeEntryFromEPMap(ObjId<EPMap> objId,
                                      Object key) throws SQLException {
        ResultSet res = getResultSet(objId.rowId);
        // removes the key
        int removedKeyPos = removeElement(
            res.getLong(K_LISTS_ID_INDEX),
            key);
        // removes the value
        removeIndexFromList(
            removedKeyPos,
            valueType(res),
            res.getLong(V_LISTS_ID_INDEX));
    }

    private void addEntryToEMap(ObjId<EPMap> objId,
                                Object newKey,
                                Object newValue) throws SQLException {
        ResultSet res = getResultSet(objId.rowId);
        ListEntry vEntry = null;
        long vListsIndex = res.getLong(V_LISTS_ID_INDEX);
        long vLastId = BaseIterator.LISTS_INDEX_NONE;
        while (vListsIndex != BaseIterator.LISTS_INDEX_NONE) {
            vEntry = lists.retrieveEntry(vListsIndex);
            vLastId = vListsIndex;
            vListsIndex = vEntry.getNext();
        }
        ListEntry kEntry = null;
        long kListsIndex = res.getLong(K_LISTS_ID_INDEX);
        long kLastId = BaseIterator.LISTS_INDEX_NONE;
        while (kListsIndex != BaseIterator.LISTS_INDEX_NONE) {
            kEntry = lists.retrieveEntry(kListsIndex);
            kLastId = kListsIndex;
            kListsIndex = kEntry.getNext();
        }
        RowId vNewRowId = write(newValue).rowId;
        RowId kNewRowId = write(newKey).rowId;
        if (vEntry != null && kEntry != null) { // the map is not empty
            long vFirst = vEntry.getFirst();
            int vSize = vEntry.getSize();
            //if the element on the collection is last in its table
            if (idCounter.getNextId(classToTableName(valueType(res))) == vFirst + vSize - 1) {
                // then increase the collection
                lists.updateListSize(vLastId, vSize + 1); // wrong ?
            } else {
                // else create new LISTS_TABLE
                long newId = lists.addList(vNewRowId, 1, BaseIterator.LISTS_INDEX_NONE);
                lists.updateListNext(vLastId, newId);
            }
            long kFirst = kEntry.getFirst();
            int kSize= kEntry.getSize();
            //if the element on the collection is last in its table
            long nextId = idCounter.getNextId(classToTableName(keyType(res)));
            if (nextId == kFirst + kSize - 1) {
                // then increase the collection
                lists.updateListSize(kLastId, kSize + 1); // wrong ?
            } else {
                // else create new LISTS_TABLE
                long newId = lists.addList(kNewRowId, 1, BaseIterator.LISTS_INDEX_NONE);
                lists.updateListNext(kLastId, newId);
            }
        } else {
            PreparedStatement ps = conn.prepareStatement(
                Utility.makeUpdateQuery(
                    objId.rowId.cl,
                    Arrays.asList(V_LISTS_ID, V_TYPE, K_LISTS_ID, K_TYPE),
                    objId.rowId.id));
            long vNewListId = lists.addList(vNewRowId, 1, BaseIterator.LISTS_INDEX_NONE);
            ps.setLong(1, vNewListId);
            ps.setString(2, vNewRowId.cl.getName());
            long kNewListId = lists.addList(vNewRowId, 1, BaseIterator.LISTS_INDEX_NONE);
            ps.setLong(3, kNewListId);
            ps.setString(4, kNewRowId.cl.getName());
            ps.executeUpdate();
        }
    }

    private void writeEPMap(EPMap<?,?> map,
                            ObjId<EPMap> objId) throws SQLException {
        for (MapChange change : map.getChanges()) {
            if (change instanceof MPlus) {
                addEntryToEMap(objId, ((MPlus) change).key, ((MPlus) change).value);
            } else if (change instanceof MPlusAll) {
                for (Map.Entry entry : ((MPlusAll<?,?>) change).map.entrySet()) {
                    addEntryToEMap(objId, entry.getKey(), entry.getValue());
                }
            } else if (change instanceof MMinus) {
                removeEntryFromEPMap(objId, ((MMinus) change).key);
            } else if (change instanceof MMinusAll) {
                for (Object key : ((MMinusAll) change).keys) {
                    removeEntryFromEPMap(objId, key);
                }
            } else {
                throw new RuntimeException("Illegal CollChange: " + change);
            }
        }
        objId.object = map.withResetChanges();
    }

    private void writePMap(PMap map,
                           ObjId<PMap> objId) throws SQLException {
        if (map == objId.object) {
            return;
        }
        if (map instanceof EPMap) {
            writeEPMap((EPMap) map, (ObjId) objId);
            return;
        }
        remove(objId);
        Iterator<Map.Entry> it = map.entrySet().iterator();
        insertMapEntry(map, classToTableName(objId.rowId.cl), it);
    }

    ResultSet getResultSet(RowId rowId) throws SQLException {
        ResultSet res = conn.createStatement().executeQuery(
            "SELECT * from " + classToTableName(rowId.cl) + " WHERE " + ID + "=" + rowId.id);
        if (!res.next()) {
            throw new RuntimeException("RowId " + rowId + " can not be retrieved");
        }
        return res;
    }

    /** Needed otherwise byte short, etc are all read as int */
    private Object getWrapperValue(ResultSet res,
                                   Class<?> cl,
                                   int index) throws SQLException {
        if (cl == Boolean.TYPE) {
            return res.getBoolean(index);
        } else if (cl == Character.TYPE) {
            return (char) res.getInt(index);
        } else if (cl == Byte.TYPE) {
            return res.getByte(index);
        } else if (cl == Short.TYPE) {
            return res.getShort(index);
        } else if (cl == Integer.TYPE) {
            return res.getInt(index);
        } else if (cl == Long.TYPE) {
            return res.getLong(index);
        } else if (cl == Float.TYPE) {
            return res.getFloat(index);
        } else if (cl == Double.TYPE) {
            return res.getDouble(index);
        } else {
            throw new IllegalArgumentException("cl must be a primitive wrapper but was: " + cl.getName());
        }
    }

    private Object getValue(ResultSet res,
                            Class<?> cl,
                    int i) throws SQLException {
        if (cl.isPrimitive()) {
            return getWrapperValue(res, cl, i);
        } else {
            Object object = res.getObject(i);
            if (object == null) {
                return null;
            }
            if (cl == Float.class) { // otherwise HSQL returns a Double when it is Float
                return res.getFloat(i);
            }
            if (cl == BigInteger.class) {
                return res.getBigDecimal(i).toBigIntegerExact();
            } else if (Utility.isBasicType(cl)) {
                return object;
            } else {
                return retrieve(new RowId<>(cl, (Long) object));
            }
        }
    }

    <T> T retrieve(ResultSet res,
                   Class<T> cl) throws SQLException {
        if (Utility.isBasicType(cl)) {
            return (T) getValue(res, Utility.wrapperToPrimitiveType(cl), VALUE_INDEX);
        } else if (PCollection.class.isAssignableFrom(cl)) {
            return (T) retrievePCollection(new RowId<>(cl, res.getLong(ID_INDEX)));
        } else if (PMap.class.isAssignableFrom(cl)) {
            return (T) retrievePMap(new RowId<>(cl, res.getLong(ID_INDEX)));
        }
        PVector<Method> methods = Refl.findGetters(cl);
        Object[] values = new Object[methods.size()];
        int i = 0;
        for (Method method : methods) {
            Class<?> returnType = method.getReturnType();
            values[i] = getValue(res, returnType, i + 2);
            i++;
        }
        return Refl.instantiate(Refl.findConstructor(cl), values);
    }

    private PCollection<Object> retrievePCollection(RowId rowId) throws SQLException {
        PCollection<Object> pColl;
        if (PVector.class.isAssignableFrom(rowId.cl)) {
            pColl = Empty.vector();
        } else if (POrderedSet.class.isAssignableFrom(rowId.cl)) {
            pColl = Empty.orderedSet();
        } else if (PSet.class.isAssignableFrom(rowId.cl)) {
            pColl = Empty.set();
        } else {
            //todo PStack and PQueue
            throw new RuntimeException("Unsupported PCollection implementation");
        }
        for (Object o : new ValuesIterable<>(rowId, this)) {
            pColl = pColl.plus(o);
        }
        return pColl;
    }

    private PMap<Object,Object> retrievePMap(RowId rowId) throws SQLException {
        PMap<Object,Object> pMap;
        if (PMap.class.isAssignableFrom(rowId.cl)) {
            pMap = Empty.map();
        } else {
            throw new RuntimeException("Unsupported PMap implementation");
        }
        Iterator<Object> kIt = new KeysIterator<>(rowId, this);
        Iterator<Object> vIt = new ValuesIterator<>(rowId, this);
        while (kIt.hasNext() && vIt.hasNext()) {
            pMap = pMap.plus(kIt.next(), vIt.next());
        }
        return pMap;
    }

    LinkedLists getLists() {
        return lists;
    }

    String classToSQLType(Class<?> cl) {
        String property = dbProperties.get(Utility.wrapperToPrimitiveType(cl).getSimpleName());
        if (property != null) {
            return property;
        } else {
            return dbProperties.get(Long.TYPE.getSimpleName());
        }
    }
}
