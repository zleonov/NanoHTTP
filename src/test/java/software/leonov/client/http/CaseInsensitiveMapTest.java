package software.leonov.client.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.MapFeature;

import junit.framework.TestSuite;

public class CaseInsensitiveMapTest {

    private static Map<String, String> map;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    protected void setUp() throws Exception {
        map = new CaseInsensitiveMap<>(Locale.US);
    }

    @AfterEach
    protected void tearDown() throws Exception {
    }

    public static junit.framework.Test suite() {
        final TestSuite suite = new TestSuite();

        // suite.addTestSuite(EquivalenceMapTest.class);

        // @formatter:off

        suite.addTest(
            MapTestSuiteBuilder
                .using(
                    new TestStringMapGenerator() {
                    
                        @Override
                        protected Map<String, String> create(final Entry<String, String>[] entries) {
                            final Map<String, String> map = new CaseInsensitiveMap<>(Locale.US);
                            
                            for (final Entry<String, String> entry : entries)
                                map.put(entry.getKey(), entry.getValue());
                            
                            return map;
                        }
                    })
                .named("CaseInsensativeMap")
                .withFeatures(                        
                        CollectionSize.ANY,
                        MapFeature.ALLOWS_NULL_VALUES,
                        MapFeature.ALLOWS_NULL_KEYS,
                        MapFeature.ALLOWS_ANY_NULL_QUERIES,
                        MapFeature.GENERAL_PURPOSE,
                        MapFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionFeature.SUPPORTS_ITERATOR_REMOVE,
                        CollectionFeature.KNOWN_ORDER
                        // CollectionFeature.SERIALIZABLE
                    )
                .createTestSuite());  

        // @formatter:on

        return suite;
    }

    @Test
    public void testNullHandling() {
        map.put("One", "One");
        map.put("Two", "Two");
        map.put(null, "Three");
        assertEquals("Three", map.get(null));
        map.put(null, "Four");
        assertEquals("Four", map.get(null));
        final Set<String> keys = map.keySet();
        assertTrue(keys.contains("one"));
        assertTrue(keys.contains("two"));
        assertTrue(keys.contains(null));
        assertEquals(3, keys.size());
    }

    @Test
    public void testRemoveFromEntrySetViaIterator() {
        map.put("key", "value");
        final Iterator<?> itor = map.entrySet().iterator();
        itor.next();
        itor.remove();
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testPutWithOverlappingKeys() {
        assertNull(map.put("key", "value1"));
        assertEquals("value1", map.put("KEY", "value2"));
        assertEquals("value2", map.put("Key", "value3"));
        assertEquals(1, map.size());
        assertEquals("value3", map.get("key"));
        assertEquals("value3", map.get("KEY"));
        assertEquals("value3", map.get("Key"));
        assertTrue(map.containsKey("key"));
        assertTrue(map.containsKey("KEY"));
        assertTrue(map.containsKey("Key"));
        assertTrue(map.keySet().contains("key"));
        assertTrue(map.keySet().contains("KEY"));
        assertTrue(map.keySet().contains("Key"));
    }

    @Test
    public void testPutAndGet() {
        assertNull(map.put("key", "value1"));
        assertEquals("value1", map.put("key", "value2"));
        assertEquals("value2", map.put("key", "value3"));
        assertEquals(1, map.size());
        assertEquals("value3", map.get("key"));
        assertEquals("value3", map.get("KEY"));
        assertEquals("value3", map.get("Key"));
        assertTrue(map.containsKey("key"));
        assertTrue(map.containsKey("KEY"));
        assertTrue(map.containsKey("Key"));
        assertTrue(map.keySet().contains("key"));
        assertTrue(map.keySet().contains("KEY"));
        assertTrue(map.keySet().contains("Key"));
    }

    @Test
    public void testGetWithWrongClassKey() {
        assertNull(map.put("key", "value1"));
        assertNull(map.get(new Object()));
    }

    @Test
    public void testRemoveWithWrongClassKey() {
        assertNull(map.put("key", "value1"));
        assertNull(map.remove(new Object()));
    }

    @Test
    public void testContainsWithWrongClassKey() {
        assertNull(map.put("key", "value1"));
        assertFalse(map.containsKey(new Object()));
    }

    @Test
    public void testGetWithNullKey() {
        assertNull(map.put("key", "value1"));
        assertNull(map.get(null));
    }

    @Test
    public void testRemoveWithNullKey() {
        assertNull(map.put("key", "value1"));
        assertNull(map.remove(null));
    }

    @Test
    public void testContainsWithNullKey() {
        assertNull(map.put("key", "value1"));
        assertFalse(map.containsKey(null));
    }

    @Test
    public void testGetOrDefault() {
        assertNull(map.put("key", "value1"));
        assertEquals("value1", map.put("KEY", "value2"));
        assertEquals("value2", map.put("Key", "value3"));
        assertEquals("value3", map.getOrDefault("key", "N"));
        assertEquals("value3", map.getOrDefault("KEY", "N"));
        assertEquals("value3", map.getOrDefault("Key", "N"));
        assertEquals("N", map.getOrDefault("keeeey", "N"));
        assertEquals("N", map.getOrDefault(new Object(), "N"));
    }

    @Test
    public void testGetOrDefaultWithNullValue() {
        assertNull(map.put("key", null));
        assertNull(map.put("KEY", null));
        assertNull(map.put("Key", null));
        assertNull(map.getOrDefault("key", "N"));
        assertNull(map.getOrDefault("KEY", "N"));
        assertNull(map.getOrDefault("Key", "N"));
        assertEquals("N", map.getOrDefault("keeeey", "N"));
        assertEquals("N", map.getOrDefault(new Object(), "N"));
    }

    @Test
    public void testComputeIfAbsentWithExistingValue() {
        assertNull(map.putIfAbsent("key", "value1"));
        assertEquals("value1", map.putIfAbsent("KEY", "value2"));
        assertEquals("value1", map.put("Key", "value3"));
        assertEquals("value3", map.computeIfAbsent("key", key2 -> "value1"));
        assertEquals("value3", map.computeIfAbsent("KEY", key1 -> "value2"));
        assertEquals("value3", map.computeIfAbsent("Key", key -> "value3"));
    }

    @Test
    public void testComputeIfAbsentWithComputedValue() {
        assertEquals("value1", map.computeIfAbsent("key", key2 -> "value1"));
        assertEquals("value1", map.computeIfAbsent("KEY", key1 -> "value2"));
        assertEquals("value1", map.computeIfAbsent("Key", key -> "value3"));
    }

    @Test
    public void testClearFromKeySet() {
        map.put("key", "value");
        map.keySet().clear();
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testRemoveFromKeySet() {
        map.put("key", "value");
        map.keySet().remove("key");
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testRemoveFromKeySetViaIterator() {
        map.put("key", "value");
        final Iterator<?> itor = map.entrySet().iterator();
        itor.next();
        itor.remove();
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testClearFromValues() {
        map.put("key", "value");
        map.values().clear();
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testRemoveFromValues() {
        map.put("key", "value");
        map.values().remove("value");
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testRemoveFromValuesViaIterator() {
        map.put("key", "value");
        final Iterator<?> itor = map.entrySet().iterator();
        itor.next();
        itor.remove();
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testClearFromEntrySet() {
        map.put("key", "value");
        map.entrySet().clear();
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

    @Test
    public void testRemoveFromEntrySet() {
        map.put("key", "value");
        map.entrySet().remove(map.entrySet().iterator().next());
        assertEquals(0, map.size());
        map.computeIfAbsent("key", k -> "newvalue");
        assertEquals("newvalue", map.get("key"));
    }

}
