package software.leonov.client.http;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class represents an
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1" target="_blank">HTTP</a>
 * <a href="http://en.wikipedia.org/wiki/Internet_media_type" target="_blank">Content-Type</a> header defined in
 * <a href="http://www.ietf.org/rfc/rfc2045.txt" target="_blank">RFC-2045</a> and
 * <a href="http://www.ietf.org/rfc/rfc2046.txt" target="_blank">RFC-2046</a>.
 */
public final class HttpContentType {

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

    private final static String TOKEN_REGEX = "[\\p{ASCII}&&[^\\p{Cntrl} " + TSPECIALS + "]]+";
    private final static String PARAMETER_REGEX = "\\s*;\\s*(" + TOKEN_REGEX + ")=" + "(" + TOKEN_REGEX + "|\"[^\"\\r\\n]*\")\\s*";

    private final static Pattern TOKEN = Pattern.compile(TOKEN_REGEX);
    private static final Pattern PARAMETER = Pattern.compile(PARAMETER_REGEX, Pattern.DOTALL);

    private final String type;
    private final String subtype;
    private final Charset charset;
    private final Map<String, String> parameters;
    private final String contentType;

    private HttpContentType(final String type, final String subtype, final Charset charset, final Map<String, String> parameters) {
        if (type == null)
            throw new NullPointerException("type == null");
        if (subtype == null)
            throw new NullPointerException("subtype == null");
        if (parameters == null)
            throw new NullPointerException("subtype == null");

        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
        this.charset = charset;
        this.contentType = computeToString();
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
     * Returns the charset of this content type or {@code null} if this content type doesn't specify a charset or it is not
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
        if (charset == null)
            throw new NullPointerException("charset == null");

        return parameters.get(name);
    }

    /**
     * Returns an unmodifiable {@code Map} of the parameters of this content type.
     * <p>
     * The parameter names are case-insensitive and the order of the parameters is <b>not</b> maintained.
     * 
     * @return an unmodifiable {@code Map} of the parameters of this content type
     */
    public Map<String, String> parameters() {
        return parameters;
    }

    /**
     * Returns a new {@code HttpContentType} parsed from the specified string.
     * 
     * @param contentType the HTTP {@code Content-Type} header string, for example: {@code text/plain; charset=utf-8}
     * @return a new {@code HttpContentType} parsed from the specified string
     */
    public static HttpContentType parse(final String contentType) {
        if (contentType == null)
            throw new NullPointerException("contentType == null");

        final Matcher token = TOKEN.matcher(contentType);

        if (!token.lookingAt())
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        final String type = token.group();

        if (contentType.charAt(token.end()) != '/')
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        token.region(token.end() + 1, contentType.length());

        if (!token.lookingAt())
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        final String subtype = token.group();

        if (type.equals("*") && !subtype.equals("*"))
            throw new IllegalArgumentException("cannot have a declared subtype with a wildcard type");

        final Map<String, String> parameters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        final Matcher parameter = PARAMETER.matcher(contentType);

        Charset charset = null;

        for (int index = token.end(); index < contentType.length(); index = parameter.end()) {
            parameter.region(index, contentType.length());

            if (!parameter.lookingAt())
                throw new IllegalArgumentException("parameter does not match '; parameter=value' format: " + contentType.substring(index));

            final String name = parameter.group(1);
            final String value = parameter.group(2).startsWith("\"") && parameter.group(2).endsWith("\"") ? parameter.group(2).substring(1, parameter.group(2).length() - 1) : parameter.group(2);

            if (name.equalsIgnoreCase("charset") && Charset.isSupported(value)) {
                charset = Charset.forName(value);
                parameters.put(name.toLowerCase(Locale.US), value.toLowerCase(Locale.US));
            } else
                parameters.put(name.toLowerCase(Locale.US), value);

        }

        return new HttpContentType(type.toLowerCase(Locale.US), subtype.toLowerCase(Locale.US), charset, Collections.unmodifiableMap(parameters));
    }

    /**
     * Returns the string representation of this content type suitable for use in an HTTP {@code Content-Type} header.
     */
    @Override
    public String toString() {
        return contentType;
    }

    private String computeToString() {
        final StringBuilder builder = new StringBuilder().append(type).append('/').append(subtype);
        if (!parameters.isEmpty())
            builder.append("; ").append(parameters.entrySet().stream().map(e -> e.getKey() + "=" + (TOKEN.matcher(e.getValue()).matches() ? e.getValue() : "\"" + e.getValue() + "\"")).collect(Collectors.joining("; ")));
        return builder.toString();
    }

    public static void main(String[] args) {

        HttpContentType t = HttpContentType.parse("text/plain; abc=\"x]yz\" ; charset=utf-8 ");

        System.out.println(t.parameter("abc"));

        System.out.println(t);
    }
}
