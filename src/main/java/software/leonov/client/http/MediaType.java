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
 * <a href="https://tools.ietf.org/html/rfc2045" target="_blank">RFC-2045</a> and
 * <a href="https://tools.ietf.org/html/rfc2046" target="_blank">RFC-2046</a>.
 * <p>
 * The type, subtype, parameter names, and the value of the {@code charset} parameter (if present) are normalized to
 * lowercase, all others values are left as-is.
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
    private final Map<String, String> parameters;
    private final Iterable<String> keyOrder;
    private final String contentType;

    private MediaType(final String type, final String subtype, final Charset charset, final Map<String, String> parameters, final Iterable<String> keyOrder) {
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
     * <p>
     * If the charset is specified but cannot be discerned this method will return {@code null}, calling
     * {@link #parameters()}{@link Map#get(Object) .get("charset")} will return the original value for reference or
     * debugging purposes.
     * 
     * @return the charset of this media type or {@code null} if this media type doesn't specify a charset or it is not
     *         supported
     */
    public Charset charset() {
        return charset;
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

    static MediaType tryParse(final String str) {
        if (str == null)
            return null;

        try {
            return parse(str);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns a new {@code MediaType} parsed from the specified string.
     * 
     * @param str the media type string, for example: {@code text/plain; charset=utf-8}
     * @return a new {@code MediaType} parsed from the specified string
     */
    public static MediaType parse(final String str) {
        if (str == null)
            throw new NullPointerException("str == null");

        final Matcher matcher = TYPE_SUBTYPE.matcher(str);

        if (!matcher.lookingAt())
            throw new IllegalArgumentException("cannot parse type/subtype: " + str);

        final String type = matcher.group(1);
        final String subtype = matcher.group(2);

        if (type.equals("*") && !subtype.equals("*"))
            throw new IllegalArgumentException("cannot have a declared subtype with a wildcard type");

        final Map<String, String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        final Set<String> order = new LinkedHashSet<>();
        final Matcher parameter = PARAMETER.matcher(str);

        Charset charset = null;

        for (int index = matcher.end(); index < str.length(); index = parameter.end()) {
            parameter.region(index, str.length());

            if (!parameter.lookingAt())
                throw new IllegalArgumentException("cannot parse parameters at index " + index + ": " + str.substring(index));

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
     * Returns the string representation of this media type suitable for use as an HTTP {@code Content-Type} header.
     * 
     * @return the string representation of this media type suitable for use as an HTTP {@code Content-Type} header
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
        return str.startsWith("\"") && str.endsWith("\"") ? str.substring(1, str.length() - 1) : str;
    }

    private static String quoteIfNeeded(final String str) {
        return TOKEN.matcher(str).matches() ? str : "\"" + str + "\"";
    }

    private static boolean isLegalCharsetName(final String charset) {
        if (charset.isEmpty())
            return false;

        for (int index = 0; index < charset.length(); index++) {
            final char c = charset.charAt(index);
            if (!(c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '-' && index > 0 || c == '+' && index > 0 || c == ':' && index > 0 || c == '_' && index > 0 || c == '.' && index > 0))
                return false;
        }

        return true;
    }

}
