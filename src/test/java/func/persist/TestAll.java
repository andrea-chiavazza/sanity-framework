package func.persist;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.testng.annotations.Test;

public class TestAll {

    @Test
    public void testMySql() throws Exception {
        test("jdbc:mysql://localhost:3306/testdb");
    }

    @Test
    public void testPostgresql() throws Exception {
        test("jdbc:postgresql://localhost:5432/testdb");
    }

//    @Test
    public void testHyperSQL() throws Exception {
        test("jdbc:hsqldb:mem:testdb");
    }

    private static void test(String dbUrl) {
        System.out.println(dbUrl);
        TestDB.setDbUrl(dbUrl);
        TestNG testNG = new TestNG();
        testNG.setTestClasses(new Class[] {TestDB.class});
        testNG.addListener(new TestListenerAdapter());
        testNG.run();
    }
}
