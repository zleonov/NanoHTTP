package software.leonov.client.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaTypeTest {

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @AfterEach
    void tearDown() throws Exception {
    }

    @Test
    public void testNoSubtype() {
        try {
            MediaType.parse("text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testInvalidType() {
        try {
            MediaType.parse("te><t/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testInvalidSubtype() {
        try {
            MediaType.parse("text/pl@in");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWildcardTypeDeclaredSubtype() {
        try {
            MediaType.parse("*/text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertEquals("cannot have a declared subtype with a wildcard type", expected.getMessage());
        }
    }

    @Test
    public void testNonAsciiType() {
        try {
            MediaType.parse("£/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiSubtype() {
        try {
            MediaType.parse("text/£");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNoType() {
        try {
            MediaType.parse("/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetType() {
        assertEquals("text", MediaType.parse("text/plain").type());
        assertEquals("application", MediaType.parse("application/atom+xml; charset=utf-8").type());
    }

    @Test
    public void testGetSubtype() {
        assertEquals("plain", MediaType.parse("text/plain").subtype());
        assertEquals("atom+xml", MediaType.parse("application/atom+xml; charset=utf-8").subtype());
    }

    private static Map<String, String> newHashMap(final String... mappings) {
        final Map<String, String> map = new HashMap<>();
        for (int i = 0; i < mappings.length; i += 2)
            map.put(mappings[i], mappings[i + 1]);
        return map;
    }

    @Test
    public void testGetParameters() {
        assertEquals(Collections.emptyMap(), MediaType.parse("text/plain").parameters());
        assertEquals(newHashMap("one", "1", "2", "two", "three", "3", "charset", "utf-8"), MediaType.parse("application/atom+xml; one=1; 2=two; three=3; charset=utf-8").parameters());
    }

    @Test
    public void testWithParametersInvalidAttribute() {
        try {
            MediaType.parse("text/plain; a=@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiParameterValue() {
        try {
            MediaType.parse("text/plain; a=a; b=b; c=£");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testNonAsciiParameterName() {
        try {
            MediaType.parse("text/plain; a=a; b=b; £=f");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParse_empty() {
        try {
            MediaType.parse("");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    public void testParseBadContentType() {
        try {
            MediaType.parse("/");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("te<t/plain");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/pl@in");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain;");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; ");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\"@");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1;");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; ");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b=");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\u2025");
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetCharset() {
        assertEquals(null, MediaType.parse("text/plain").charset());
        assertEquals(StandardCharsets.UTF_8, MediaType.parse("text/plain; charset=utf-8").charset());
        assertEquals(StandardCharsets.UTF_16, MediaType.parse("text/plain; charset=utf-16").charset());
    }

    @Test
    public void testIllegalCharsetName() {
        final MediaType type = MediaType.parse("text/plain; charset=\"!@#$%^&*()\"");
        assertEquals(null, type.charset());
    }

    @Test
    public void testUnsupportedCharset() {
        final MediaType type = MediaType.parse("text/plain; charset=utf-64");
        assertEquals(null, type.charset());
    }

    @Test
    public void testToString() {
        final String type = "text/plain; something=\"cr@zy\"; something-else=\"crazy with spaces\"; and-another-thing=\"\"; normal-thing=foo";
        assertEquals(type, MediaType.parse(type).toString());
    }

}
