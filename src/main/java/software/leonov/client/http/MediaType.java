package software.leonov.client.http;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class represents an <a href="https://en.wikipedia.org/wiki/Media_type" target="_blank">Internet Media Type</a>
 * suitable for use as an
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1" target="_blank">HTTP</a>
 * <a href="http://en.wikipedia.org/wiki/Internet_media_type" target="_blank">Content-Type</a> header defined in
 * <a href="http://www.ietf.org/rfc/rfc2045.txt" target="_blank">RFC-2045</a> and
 * <a href="http://www.ietf.org/rfc/rfc2046.txt" target="_blank">RFC-2046</a>.
 */
public final class MediaType {

    // @formatter:off
    /*
     * https://tools.ietf.org/html/rfc2045#section-5.1
     * -----------------------------------------------
     * tspecials :=  "(" / ")" / "<" / ">" / "@" /
     *               "," / ";" / ":" / "\" / <">
     *                "/" / "[" / "]" / "?" / "="
     */
    // @formatter:on

    private static final String TSPECIALS = "(" + ")" + "<" + ">" + "@" + "," + ";" + ":" + "\\\\" + "\"" + "/" + "\\[" + "\\]" + "?" + "=";

    private static final String TOKEN_REGEX = "[\\p{ASCII}&&[^\\p{Cntrl} " + TSPECIALS + "]]+";
    private static final String PARAMETER_REGEX = "\\s*;\\s*(" + TOKEN_REGEX + ")=" + "(" + TOKEN_REGEX + "|\"[^\"\\r\\n]*\")\\s*";

    private static final Pattern TOKEN = Pattern.compile(TOKEN_REGEX);
    private static final Pattern PARAMETER = Pattern.compile(PARAMETER_REGEX, Pattern.DOTALL);
    private static final Pattern TYPE_SUBTYPE = Pattern.compile("\\s*(" + TOKEN_REGEX + ")/(" + TOKEN_REGEX + ")\\s*");

    private final String type;
    private final String subtype;
    private final Charset charset;
    private final Iterable<String> keyOrder;
    private final Map<String, String> parameters;
    private final String contentType;

    private MediaType(final String type, final String subtype, final Charset charset, final Map<String, String> parameters, final Iterable<String> keyOrder) {
        if (type == null)
            throw new NullPointerException("type == null");
        if (subtype == null)
            throw new NullPointerException("subtype == null");
        if (parameters == null)
            throw new NullPointerException("parameters == null");
        if (keyOrder == null)
            throw new NullPointerException("keyOrder == null");

        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
        this.charset = charset;
        this.keyOrder = keyOrder;
        this.contentType = toContentType();
    }

    /**
     * Returns the type, for example: {@code application}, {@code text}, {@code image}, {@code audio}, or {@code video}.
     */
    public String type() {
        return type;
    }

    /**
     * Returns the subtype, for example: {@code plain}, {@code png}, {@code mp4} or {@code xml}.
     */
    public String subtype() {
        return subtype;
    }

    /**
     * Returns the charset of this media type or {@code null} if this media type doesn't specify a charset or it is not
     * supported.
     */
    public Charset charset() {
        return charset;
    }

    /**
     * Returns the value of the parameter with the specified name or {@code null} if it is not defined.
     * 
     * @param name the name of the parameter
     * @return the value of the parameter with the specified name or {@code null} if it is not defined
     */
    public String parameter(final String name) {
        if (name == null)
            throw new NullPointerException("name == null");

        return parameters.get(name);
    }

    /**
     * Returns an unmodifiable {@code Map} of the parameters of this media type.
     * <p>
     * The parameter names are case-insensitive and the order of the parameters is <b>not</b> maintained.
     * 
     * @return an unmodifiable {@code Map} of the parameters of this media type
     */
    public Map<String, String> parameters() {
        return parameters;
    }

    static MediaType tryParse(final String mediaType) {
        if (mediaType == null)
            throw new NullPointerException("mediaType == null");

        try {
            return parse(mediaType);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns a new {@code MediaType} parsed from the specified string.
     * 
     * @param mediaType the media type string, for example: {@code text/plain; charset=utf-8}
     * @return a new {@code MediaType} parsed from the specified string
     */
    public static MediaType parse(final String mediaType) {
        if (mediaType == null)
            throw new NullPointerException("mediaType == null");

        final Matcher matcher = TYPE_SUBTYPE.matcher(mediaType);

        if (!matcher.lookingAt())
            throw new IllegalArgumentException("cannot parse type/subtype: " + mediaType);

        final String type = matcher.group(1);
        final String subtype = matcher.group(2);

        if (type.equals("*") && !subtype.equals("*"))
            throw new IllegalArgumentException("cannot have a declared subtype with a wildcard type");

        final Map<String, String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        final Set<String> order = new LinkedHashSet<>();
        final Matcher parameter = PARAMETER.matcher(mediaType);

        Charset charset = null;

        for (int index = matcher.end(); index < mediaType.length(); index = parameter.end()) {
            parameter.region(index, mediaType.length());

            if (!parameter.lookingAt())
                throw new IllegalArgumentException("cannot parse parameters at index " + index + ": " + mediaType.substring(index));

            final String name = parameter.group(1).toLowerCase(Locale.US);
            final String value = unquoteIfNeeded(parameter.group(2));

            if (name.equals("charset") && isLegalCharsetName(value) && Charset.isSupported(value)) {
                charset = Charset.forName(value);
                parameters.put(name, value.toLowerCase(Locale.US));
            } else
                parameters.put(name, value);

            order.add(name);

        }

        return new MediaType(type.toLowerCase(Locale.US), subtype.toLowerCase(Locale.US), charset, Collections.unmodifiableMap(parameters), order);
    }

    /**
     * Returns the string representation of this media type suitable for use in an HTTP {@code Content-Type} header.
     */
    @Override
    public String toString() {
        return contentType;
    }

    private String toContentType() {
        final StringBuilder builder = new StringBuilder().append(type).append('/').append(subtype);

        for (final String key : keyOrder)
            builder.append("; ").append(key).append("=").append(quoteIfNeeded(parameters.get(key)));

        return builder.toString();
    }

    private static String unquoteIfNeeded(final String str) {
        if (str.startsWith("\"") && str.endsWith("\""))
            return str.substring(1, str.length() - 1);
        return str;
    }

    private static String quoteIfNeeded(final String str) {
        if (TOKEN.matcher(str).matches())
            return str;
        else
            return "\"" + str + "\"";
    }

    private static boolean isLegalCharsetName(final String charset) {
        if (charset.isEmpty())
            return false;

        for (int i = 0; i < charset.length(); i++) {
            final char c = charset.charAt(i);
            if (!(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' && i != 0 || c == '+' && i != 0 || c == ':' && i != 0 || c == '_' && i != 0 || c == '.' && i != 0))
                return false;
        }

        return true;
    }

    public static void main(String[] args) {

        MediaType t = MediaType.parse("text/plain; abc=\"x]yz\" ; charset=utf-8 ");

        System.out.println(t.parameter("abc"));

        System.out.println(t);
    }
}
