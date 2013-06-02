package func.persist;

import func.values.*;
import org.pcollections.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

import static func.values.Values.*;
import static org.testng.Assert.*;

public class TestDB {
    private static final String DB_USERNAME = "testuser";
    private static final String DB_PASSWORD = "testuserpwd";

    private static String dbUrl = "jdbc:hsqldb:mem:testdb";

    static {
//        try {
//            Class.forName("org.hsqldb.jdbcDriver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static DB getInstance() throws Exception {
        return new DB(DriverManager.getConnection(dbUrl, DB_USERNAME, DB_PASSWORD));
    }

    public static void setDbUrl(String newDbUrl) {
        dbUrl = newDbUrl;
    }

    private static void dropTables(Class... classes) throws Exception {
        DB db = getInstance();
        for (Class cl : classes) {
            if (db.tableExists(cl)) {
                db.getConnection().createStatement().executeUpdate("DROP TABLE " + DB.classToTableName(cl) + " CASCADE");
            }
        }
    }

    @BeforeClass
    public static void setUp() throws Exception {
        DB db = getInstance();
        dropTables(
            Integer.class,
            Long.class,
            String.class,
            Primitives.class,
            NonPrimitives.class,
            Composite.class,
            Mixed.class,
            EPVector.class,
            EPSet.class,
            PMap.class,
            WithPCollections.class,
            WithPMap.class);
        db.getConnection().createStatement().executeUpdate("DROP TABLE \"lists_\"");

        Connection conn = db.getConnection();
//        ResultSet res = conn.getMetaData().getTables(conn.getCatalog(), null, "%", null);
        ResultSet res = conn.createStatement().executeQuery(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + conn.getCatalog() + "'");
        boolean failed = false;
        StringBuilder sb = new StringBuilder();
        while (res.next()) {
            failed = true;
            sb.append(res.getString(1)).append(", ");
        }
        if (failed) {
            fail("not all tables have been dropped: " + sb.toString());
        }

        conn.close();
    }

    @Test
    public void testConvertResultsFromSelectWithId() throws Exception {
        DB db = getInstance();

        ObjId<Primitives> id1 = db.write(Values.p1);
        ObjId<Primitives> id2 = db.write(Values.p2);
        ObjId<Primitives> id3 = db.write(p3);
        ObjId<Primitives> id4 = db.write(p4);

        Connection conn = db.getConnection();

        ResultSet resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM " + DB.classToTableName(Primitives.class) + " WHERE " + DB.ID + " BETWEEN 1 AND 2");
        assertEquals(
            set(p2, p3),
            db.convert(resultSet, Primitives.class));

        db.removeAll(Primitives.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testConvertIntegerResultsFromSelectWithValue() throws Exception {
        DB db = getInstance();

        ObjId<Integer> id1 = db.write(12);
        ObjId<Integer> id2 = db.write(20);
        ObjId<Integer> id3 = db.write(32);
        ObjId<Integer> id4 = db.write(44);

        Connection conn = db.getConnection();

        ResultSet resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM " + DB.classToTableName(Integer.class) + " WHERE " + DB.VALUE + " BETWEEN 19 AND 33");
        assertEquals(
            set(20, 32),
            db.convert(resultSet, Integer.class));

        db.removeAll(Integer.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testConvertCompositeResultsFromSelectWithValue() throws Exception {
        DB db = getInstance();

        ObjId<Composite> id1 = db.write(c1);
        ObjId<Composite> id2 = db.write(c2);
        ObjId<Composite> id3 = db.write(c3);
        ObjId<Composite> id4 = db.write(c4);

        Connection conn = db.getConnection();

        String compositeTable = DB.classToTableName(Composite.class);
        ResultSet resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM " + compositeTable + " WHERE " + DB.methodToColumn("getI2") + "=134");
        assertEquals(
            set(c1, c3),
            db.convert(resultSet, Composite.class));

        String getN2 = DB.methodToColumn("getNonPrimitives2");

        resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM " + DB.classToTableName(NonPrimitives.class) + " WHERE " + DB.ID +
                " IN (SELECT " + getN2 + " FROM " + compositeTable + ")");
        assertEquals(
            set(n2, n4, n2, n4),
            db.convert(resultSet, NonPrimitives.class));

        resultSet = conn.createStatement().executeQuery(
            "SELECT * FROM " + DB.classToTableName(NonPrimitives.class) + " WHERE " + DB.ID +
                " IN (SELECT " + getN2 + " FROM " + compositeTable + " WHERE " + DB.methodToColumn("getI2") + "=974)");
        assertEquals(
            set(n4),
            db.convert(resultSet, NonPrimitives.class));

        db.removeAll(Composite.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testRetrievePrimitiveFromId() throws Exception {
        DB db = getInstance();

        ObjId<Primitives> id1 = db.write(p1);
        ObjId<Primitives> id2 = db.write(p2);
        ObjId<Primitives> id3 = db.write(p3);
        ObjId<Primitives> id4 = db.write(p4);

        assertEquals(p1, id1.object);
        assertEquals(p2, id2.object);
        assertEquals(p3, id3.object);
        assertEquals(p4, id4.object);

        assertEquals(p1, db.retrieve(id1.rowId));
        assertEquals(p2, db.retrieve(id2.rowId));
        assertEquals(p3, db.retrieve(id3.rowId));
        assertEquals(p4, db.retrieve(id4.rowId));

        db.remove(id1);
        db.remove(id2);
        db.remove(id3);
        db.remove(id4);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testRetrieveNonPrimitiveFromId() throws Exception {
        DB db = getInstance();

        ObjId<NonPrimitives> id1 = db.write(n1);
        ObjId<NonPrimitives> id2 = db.write(n2);
        ObjId<NonPrimitives> id3 = db.write(n3);
        ObjId<NonPrimitives> id4 = db.write(n4);

        assertEquals(n1, id1.object);
        assertEquals(n2, id2.object);
        assertEquals(n3, id3.object);
        assertEquals(n4, id4.object);

        assertEquals(n1, db.retrieve(id1.rowId));
        assertEquals(n2, db.retrieve(id2.rowId));
        assertEquals(n3, db.retrieve(id3.rowId));
        assertEquals(n4, db.retrieve(id4.rowId));

        db.remove(id1);
        db.remove(id2);
        db.remove(id3);
        db.remove(id4);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testWritePrimitivesToRowID() throws Exception {
        DB db = getInstance();

        ObjId<Primitives> id1 = db.write(p1);
        ObjId<Primitives> id2 = db.write(p2);
        ObjId<Primitives> id3 = db.write(p3);
        ObjId<Primitives> id4 = db.write(p4);

        Primitives newP2 = p2.withL(3333L);
        Primitives newP3 = p3.withWF(null);

        db.write(p3, id1); // writes all different values
        db.write(newP2, id2); // writes a value over a value
        db.write(newP3, id3); // writes a null over a value
        db.write(p3, id4); // writes a value over a null

        assertEquals(p3, id1.object);
        assertEquals(newP2, id2.object);
        assertEquals(newP3, id3.object);
        assertEquals(p3, id4.object);

        assertEquals(p3, db.retrieve(id1.rowId));
        assertEquals(newP2, db.retrieve(id2.rowId));
        assertEquals(newP3, db.retrieve(id3.rowId));
        assertEquals(p3, db.retrieve(id4.rowId));

        assertEquals(
            set(p3, newP2, newP3, p3),
            db.retrieveAll(Primitives.class));

        db.remove(id1);
        db.remove(id2);

        assertEquals(
            set(newP3, p3),
            db.retrieveAll(Primitives.class));

        db.remove(id3);
        db.remove(id4);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testWriteNonPrimitivesToRowID() throws Exception {
        DB db = getInstance();

        ObjId<NonPrimitives> id1 = db.write(n1);
        ObjId<NonPrimitives> id2 = db.write(n2);
        ObjId<NonPrimitives> id3 = db.write(n3);
        ObjId<NonPrimitives> id4 = db.write(n4);

        NonPrimitives newN1 = n1.withBi(new BigInteger("999999"));
        NonPrimitives newN2 = n2.withBd(new BigDecimal("999.99"));
        NonPrimitives newN3 = n3.withStr(null);

        db.write(newN1, id1);
        db.write(newN2, id2);
        db.write(newN3, id3);
        db.write(n3, id4);

        assertEquals(newN1, id1.object);
        assertEquals(newN2, id2.object);
        assertEquals(newN3, id3.object);
        assertEquals(n3, id4.object);

        assertEquals(
            set(newN1, newN2, newN3, n3),
            db.retrieveAll(NonPrimitives.class));

        db.remove(id1);
        db.remove(id2);

        assertEquals(
            set(newN3, n3),
            db.retrieveAll(NonPrimitives.class));

        db.remove(id3);
        db.remove(id4);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testWriteMixedToRowID() throws Exception {
        DB db = getInstance();

        ObjId<Mixed> id1 = db.write(m1);
        ObjId<Mixed> id2 = db.write(m2);

        assertEquals(
            set(m1, m2),
            db.retrieveAll(Mixed.class));

        assertEquals(m1, id1.object);

        Mixed mixed = id1.object;
        db.write(mixed.withVs(mixed.getVs().plus("newString")), id1); // will modify collections and maps

        assertEquals(
            set(m1.withVs(m1.getVs().plus("newString")), m2),
            db.retrieveAll(Mixed.class));

        db.remove(id1);

        assertEquals(
            set(m2),
            db.retrieveAll(Mixed.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testRemoveAll() throws Exception {
        DB db = getInstance();

        List<Mixed> objs = Arrays.asList(m1, m2);

        for (Mixed obj : objs) {
            db.write(obj);
        }

        assertEquals(
            new HashSet<>(objs),
            db.retrieveAll(Mixed.class));

        db.removeAll(Mixed.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testComposite() throws Exception {
        DB db = getInstance();

        ObjId<Composite> id1 = db.write(c1);
        ObjId<Composite> id2 = db.write(c2);

        assertEquals(
            set(c1, c2),
            db.retrieveAll(Composite.class));

        db.remove(id1);

        assertEquals(
            set(c2),
            db.retrieveAll(Composite.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testWithPVector() throws Exception {
        DB db = getInstance();

        ObjId<WithPCollections> id1 = db.write(wpc1);
        ObjId<WithPCollections> id2 = db.write(wpc2);

        assertEquals(
            set(wpc1, wpc2),
            db.retrieveAll(WithPCollections.class));

        db.remove(id1);

        assertEquals(
            set(wpc2),
            db.retrieveAll(WithPCollections.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testWithPMap() throws Exception {
        DB db = getInstance();

        ObjId<WithPMap> id1 = db.write(wpm1);
        ObjId<WithPMap> id2 = db.write(wpm2);

        assertEquals(
            set(wpm1, wpm2),
            db.retrieveAll(WithPMap.class));

        db.remove(id1);

        assertEquals(
            set(wpm2),
            db.retrieveAll(WithPMap.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testMixed() throws Exception {
        DB db = getInstance();

        ObjId<Mixed> id1 = db.write(m1);
        ObjId<Mixed> id2 = db.write(m2);

        assertEquals(
            set(m1, m2),
            db.retrieveAll(Mixed.class));

        db.remove(id1);

        assertEquals(
            set(m2),
            db.retrieveAll(Mixed.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testCollectionIterator() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);

        assertFalse(new ValuesIterator<>(db.write(Empty.vector()).rowId, db).hasNext());

        ValuesIterator<String> it = new ValuesIterator<>(id1.rowId, db);

        assertTrue(it.hasNext());
        assertEquals("vs1", it.next());
        assertEquals("how", it.next());
        assertEquals("are", it.next());
        assertEquals("you", it.next());
        assertFalse(it.hasNext());

        db.removeAll(EPVector.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testCollectionIterable() throws Exception {
        DB db = getInstance();

        for (PVector<String> coll : Arrays.asList(vs1, vs2, vs3)) {
            ObjId<PVector<String>> id = db.write(coll);
            ValuesIterable<String> itbl = new ValuesIterable<>(id.rowId, db);
            List<String> l = new ArrayList<>(coll);
            int i = 0;
            for (String s : itbl) {
                assertEquals(l.get(i++), s);
            }
        }

        db.removeAll(EPVector.class);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    ////////////////////// PVector

    @Test
    public void testEPVectorPlus() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plus("newString"), id1);

        assertEquals(
            set(vs1.plus("newString"), vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPVectorPlus() throws Exception {
        DB db = getInstance();

        PVector<String> ev = Empty.vector();
        ObjId<PVector<String>> id1 = db.write(ev);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(Empty.vector(), vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plus("newString"), id1);

        assertEquals(
            set(ev.plus("newString"), vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorPlusAll() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);

        assertEquals(
            set(vs1, vs2),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plusAll(vs3), id1);

        assertEquals(
            set(vs1.plusAll(vs3), vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorPlusAllI() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plusAll(2, vs4), id1); // in the middle
        db.write(id2.object.plusAll(0, vs4), id2); // at the beginning
        db.write(id3.object.plusAll(3, vs4), id3); // at the end

        assertEquals(
            set(
                vs1.plusAll(2, vs4),
                vs2.plusAll(0, vs4),
                vs3.plusAll(3, vs4)),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(
                vs2.plusAll(0, vs4),
                vs3.plusAll(3, vs4)),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3.plusAll(3, vs4)),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPVectorPlusAll() throws Exception {
        DB db = getInstance();

        PVector<String> ev = Empty.vector();
        ObjId<PVector<String>> id1 = db.write(ev);
        ObjId<PVector<String>> id2 = db.write(vs2);

        assertEquals(
            set(ev, vs2),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plusAll(vs3), id1);

        assertEquals(
            set(ev.plusAll(vs3), vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorPlusAllAtEnd() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);

        assertEquals(
            set(vs1),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plusAll(vs3), id1);

        assertEquals(
            set(vs1.plusAll(vs3)),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorPlusI() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plus(2, "newString"), id1); // insert in the middle
        db.write(id2.object.plus(0, "newString"), id2); // insert at the beginning
        db.write(id3.object.plus(3, "newString"), id3); // inserts at the end

        assertEquals(
            set(
                vs1.plus(2, "newString"),
                vs2.plus(0, "newString"),
                vs3.plus(3, "newString")),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2.plus(0, "newString"), vs3.plus(3, "newString")),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3.plus(3, "newString")),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorMinusI() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.minus(2), id1); // delete in the middle
        db.write(id2.object.minus(0), id2); // remove the first element
        db.write(id3.object.minus(2), id3); // remove the last

        PVector<String> vs2New = vs2.minus(0);
        PVector<String> vs3New = vs3.minus(2);

        assertEquals(
            set(vs1.minus(2), vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testRemoveFirstElementOfSingletonNonLastList() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);

        assertEquals(
            set(vs1, vs2),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.minus(1), id1); // delete in the middle

        assertEquals(
            set(vs1.minus(1), vs2),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.minus(0), id1); // delete in the middle

        assertEquals(
            set(vs1.minus(1).minus(0), vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorWith() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.with(2, "new2"), id1); // set in the middle
        db.write(id2.object.with(0, "new0"), id2); // set the first element
        db.write(id3.object.with(2, "new2"), id3); // set the last

        PVector<String> vs2New = vs2.with(0, "new0");
        PVector<String> vs3New = vs3.with(2, "new2");

        assertEquals(
            set(vs1.with(2, "new2"), vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorMinus() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.minus("how"), id1); // delete in the middle
        db.write(id2.object.minus("vs2"), id2); // remove the first element
        db.write(id3.object.minus("3rd"), id3); // remove the last

        PVector<String> vs1New = vs1.minus("how");
        PVector<String> vs2New = vs2.minus("vs2");
        PVector<String> vs3New = vs3.minus("3rd");

        assertEquals(
            set(vs1New, vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        PVector<String> empty = id2.object.minus("two").minus("three");
        db.write(empty, id2); // remove all

        assertEquals(
            set(vs1New, empty, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(empty, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorMinusComposite() throws Exception {
        DB db = getInstance();

        ObjId<PVector<Composite>> id1 = db.write(vc1);
        ObjId<PVector<Composite>> id2 = db.write(vc2);
        ObjId<PVector<Composite>> id3 = db.write(vc3);

        assertEquals(
            set(vc1, vc2, vc3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.minus(c2), id1); // delete in the middle
        db.write(id2.object.minus(c3), id2); // remove the first element
        db.write(id3.object.minus(c1), id3); // remove the last

        PVector<Composite> vc1New = vc1.minus(c2);
        PVector<Composite> vc2New = vc2.minus(c3);
        PVector<Composite> vc3New = vc3.minus(c1);

        assertEquals(
            set(vc1New, vc2New, vc3New),
            db.retrieveAll(EPVector.class));

        PVector<Composite> empty = id2.object.minus(c3).minus(c4).minus(c3);
        db.write(empty, id2); // remove all

        assertEquals(
            set(vc1New, empty, vc3New),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(empty, vc3New),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vc3New),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPVectorMixedChanges() throws Exception {
        DB db = getInstance();

        ObjId<PVector<String>> id1 = db.write(vs1);
        ObjId<PVector<String>> id2 = db.write(vs2);
        ObjId<PVector<String>> id3 = db.write(vs3);

        assertEquals(
            set(vs1, vs2, vs3),
            db.retrieveAll(EPVector.class));

        db.write(id1.object.plus("newS").minus("how"), id1); // delete in the middle
        db.write(id2.object.plus(0, "newS").minus("vs2").plusAll(vs4), id2); // remove the first element
        db.write(id3.object.minus("3rd").plusAll(1, vs4), id3); // remove the last

        PVector<String> vs1New = vs1.plus("newS").minus("how");
        PVector<String> vs2New = vs2.plus(0, "newS").minus("vs2").plusAll(vs4);
        PVector<String> vs3New = vs3.minus("3rd").plusAll(1, vs4);

        assertEquals(
            set(vs1New, vs2New, vs3New),
            db.retrieveAll(EPVector.class));

        PVector<String> empty = id2.object.minus("two").minus("three").minus("newS").minusAll(Arrays.asList("vs4", "22", "333"));
        db.write(empty, id2); // remove all

        assertEquals(
            set(vs1New, empty, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id1);

        assertEquals(
            set(empty, vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id2);

        assertEquals(
            set(vs3New),
            db.retrieveAll(EPVector.class));

        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    ////////////////////// PSet

    @Test
    public void testEPSetPlus() throws Exception {
        DB db = getInstance();

        ObjId<PSet<Primitives>> id1 = db.write(sp1);
        ObjId<POrderedSet<Primitives>> id2 = db.write(sp2);

        assertEquals(
            set(sp1, sp2),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.plus(p4), id1);

        assertEquals(
            set(sp1.plus(p4), sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        assertEquals(
            set(sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPSetPlus() throws Exception {
        DB db = getInstance();

        PSet<Primitives> es = Empty.set();
        ObjId<PSet<Primitives>> id1 = db.write(es);
        ObjId<POrderedSet<Primitives>> id2 = db.write(sp2);

        assertEquals(
            set(es, sp2),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.plus(p4), id1);

        assertEquals(
            set(es.plus(p4), sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        assertEquals(
            set(sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPSetPlusAll() throws Exception {
        DB db = getInstance();

        ObjId<PSet<Primitives>> id1 = db.write(sp1);
        ObjId<POrderedSet<Primitives>> id2 = db.write(sp2);

        assertEquals(
            set(sp1, sp2),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.plusAll(sp3), id1);

        assertEquals(
            set(sp1.plusAll(sp3), sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        assertEquals(
            set(sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPSetPlusAll() throws Exception {
        DB db = getInstance();

        PSet<Primitives> es = Empty.set();
        ObjId<PSet<Primitives>> id1 = db.write(es);
        ObjId<POrderedSet<Primitives>> id2 = db.write(sp2);

        assertEquals(
            set(es, sp2),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.plusAll(sp3), id1);

        assertEquals(
            set(es.plusAll(sp3), sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        assertEquals(
            set(sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    // might not make sense
    @Test
    public void testEPSetPlusAllAtEnd() throws Exception {
        DB db = getInstance();

        ObjId<PSet<Primitives>> id1 = db.write(sp1);

        assertEquals(
            set(sp1),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.plusAll(sp3), id1);

        assertEquals(
            set(sp1.plusAll(sp3)),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPSetMinus() throws Exception {
        DB db = getInstance();

        ObjId<PSet<Primitives>> id1 = db.write(sp1);
        ObjId<POrderedSet<Primitives>> id2 = db.write(sp2);

        assertEquals(
            set(sp1, sp2),
            db.retrieveAll(EPSet.class));

        db.write(id1.object.minus(p1), id1);

        assertEquals(
            set(sp1.minus(p1), sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id1);

        assertEquals(
            set(sp2),
            db.retrieveAll(EPSet.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    ////////////////////// PMap

    @Test
    public void testEPMapPlus() throws Exception {
        DB db = getInstance();

        ObjId<PMap<String,PVector<Long>>> id1 = db.write(msv1);
        ObjId<PMap<String,PVector<Long>>> id2 = db.write(msv2);

        assertEquals(
            set(msv1, msv2),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.plus("newKey", TreePVector.from(Arrays.asList(1L, 5L, 0L))), id1);

        assertEquals(
            set(msv1.plus("newKey", TreePVector.from(Arrays.asList(1L, 5L, 0L))), msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id1);

        assertEquals(
            set(msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPMapPlusAll() throws Exception {
        DB db = getInstance();

        ObjId<PMap<String,PVector<Long>>> id1 = db.write(msv1);
        ObjId<PMap<String,PVector<Long>>> id2 = db.write(msv2);

        assertEquals(
            set(msv1, msv2),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.plusAll(msv3), id1);

        assertEquals(
            set(msv1.plusAll(msv3), msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id1);

        assertEquals(
            set(msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPMapPlusAll() throws Exception {
        DB db = getInstance();

        PMap<String,PVector<Long>> em = Empty.map();
        ObjId<PMap<String,PVector<Long>>> id1 = db.write(em);
        ObjId<PMap<String,PVector<Long>>> id2 = db.write(msv2);

        assertEquals(
            set(em, msv2),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.plusAll(msv3), id1);

        assertEquals(
            set(em.plusAll(msv3), msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id1);

        assertEquals(
            set(msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEmptyEPMapPlus() throws Exception {
        DB db = getInstance();

        PMap<String,PVector<Long>> em = Empty.map();
        ObjId<PMap<String,PVector<Long>>> id1 = db.write(em);
        ObjId<PMap<String,PVector<Long>>> id2 = db.write(msv2);

        assertEquals(
            set(em, msv2),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.plus("newKey", TreePVector.from(Arrays.asList(1L, 5L, 0L))), id1);

        assertEquals(
            set(em.plus("newKey", TreePVector.from(Arrays.asList(1L, 5L, 0L))), msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id1);

        assertEquals(
            set(msv2),
            db.retrieveAll(EPMap.class));

        db.remove(id2);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPMapMinus() throws Exception {
        DB db = getInstance();

        ObjId<PMap<String,Integer>> id1 = db.write(pm1);
        ObjId<PMap<String,Integer>> id2 = db.write(pm1);
        ObjId<PMap<String,Integer>> id3 = db.write(pm1);

        assertEquals(
            set(pm1, pm1, pm1),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.minus("pm1"), id1);
        db.write(id2.object.minus("aaa"), id2);
        db.write(id3.object.minus("bb"), id3);

        assertEquals(
            set(pm1.minus("pm1"), pm1.minus("aaa"), pm1.minus("bb")),
            db.retrieveAll(EPMap.class));

        db.remove(id1);
        db.remove(id2);
        db.remove(id3);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    @Test
    public void testEPMapMinusAll() throws Exception {
        DB db = getInstance();

        ObjId<PMap<Composite,PVector<Long>>> id1 = db.write(msc1);

        assertEquals(
            set(msc1),
            db.retrieveAll(EPMap.class));

        db.write(id1.object.minusAll(Arrays.asList(c1, c2)), id1);

        assertEquals(
            set(msc1.minusAll(Arrays.asList(c1, c2))),
            db.retrieveAll(EPMap.class));

        db.remove(id1);

        checkAllTablesAreEmptyAndCloseConn(db.getConnection());
    }

    private void checkAllTablesAreEmptyAndCloseConn(Connection conn) throws Exception {
//        ResultSet res = conn.getMetaData().getTables(null, null, "%", null);
        ResultSet res = conn.createStatement().executeQuery(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + conn.getCatalog() + "'");

        while (res.next()) {
            String tableName = res.getString(1);
            ResultSet rowsSet = conn.createStatement().executeQuery("SELECT * from \"" + tableName + "\"");
            if (rowsSet.next()) {
                fail("Table " + tableName + " is not empty");
            }
        }
        conn.close();
    }

    @SafeVarargs
    protected final <T> Set<T> set(T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }
}
