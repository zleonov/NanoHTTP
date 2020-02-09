package software.leonov.client.http;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpMediaType;
import com.google.api.client.util.Preconditions;

/**
 * This class represents an
 * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1" target="_blank">HTTP</a>
 * <a href="http://en.wikipedia.org/wiki/Internet_media_type" target="_blank">Content-Type</a> header defined in
 * <a href="http://www.ietf.org/rfc/rfc2045.txt" target="_blank">RFC-2045</a> and
 * <a href="http://www.ietf.org/rfc/rfc2046.txt" target="_blank">RFC-2046</a>. Media ranges are supported. The {@code *}
 * character is treated as a wildcard.
 * <p>
 * The type/subtype and parameter names are normalized to lowercase. Parameter values are left as-is.
 * <p>
 * No specific support comments is provided.
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

    private final static String TOKEN = "[\\p{ASCII}&&[^\\p{Cntrl} " + TSPECIALS + "]]+";
    private final static String PARAMETER = "\\s*;\\s*(" + TOKEN + ")=" + "(" + TOKEN + "|\"[^\"\\r\\n]*\")";

    private final static Pattern TOKEN_PATTERN = Pattern.compile(TOKEN);
    private static final Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER, Pattern.DOTALL);

    private String type;
    private String subtype;
    
    private Charset charset;

    private final Map<String, String> parameters = new TreeMap<>();
    private final Map<String, String> _parameters = Collections.unmodifiableMap(parameters);

    MediaType() {
    }

    MediaType(final String type, final String subtype) {
        if (type == null)
            throw new NullPointerException("type == null");
        if (subtype == null)
            throw new NullPointerException("subtype == null");

        if (subtype.equals("*") && !type.equals("*"))
            throw new IllegalArgumentException("cannot have a declared subtype with a wildcard type");

        if (!TOKEN_PATTERN.matcher(type).matches())
            throw new IllegalArgumentException("type contains reserved characters");

        if (!TOKEN_PATTERN.matcher(subtype).matches())
            throw new IllegalArgumentException("subtype contains reserved characters");

        this.type = type;
        this.subtype = subtype;
    }

    MediaType parameter(final String name, final String value) {
        if (name == null)
            throw new NullPointerException("name == null");
        if (value == null)
            throw new NullPointerException("value == null");

        if (!TOKEN_PATTERN.matcher(name).matches())
            throw new IllegalArgumentException("name contains reserved characters");

        parameters.put(name, value);
        return this;
    }

    MediaType charset(final Charset charset) {
        if (charset == null)
            throw new NullPointerException("charset == null");

        this.charset = charset;
        parameters.put("charset", charset.toString());
        return this;
    }

    /**
     * Returns the type (for example: {@code application}, {@code text}, {@code image}, {@code audio}, or {@code video}).
     */
    public String type() {
        return type;
    }

    /**
     * Returns a specific media subtype (for example: {@code plain}, {@code png}, {@code mp4} or {@code xml}).
     */
    public String subtype() {
        return subtype;
    }

    /**
     * Returns the charset of this media type or null if this media type doesn't specify a charset.
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
     * Returns an unmodifiable {@code Map} of the parameters of this media type.
     * <p>
     * The parameter names are case-insensitive and the order of the request headers is <b>not</b> maintained.
     * 
     * @return an unmodifiable {@code Map} of the parameters of this media type
     */
    public Map<String, String> parameters() {
        return _parameters;
    }

    public static MediaType parse(final String contentType) {
        if (contentType == null)
            throw new NullPointerException("contentType == null");

        final Matcher token = TOKEN_PATTERN.matcher(contentType);

        if (!token.lookingAt())
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        final String type = token.group();

        if (contentType.charAt(token.end()) != '/')
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        token.region(token.end() + 1, contentType.length());

        if (!token.lookingAt())
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        final String subtype = token.group();

        final MediaType mediaType = new MediaType();
        mediaType.type = type;
        mediaType.subtype = subtype;

        final Matcher parameter = PARAMETER_PATTERN.matcher(contentType);

        for (int index = token.end(); index < contentType.length(); index = parameter.end()) {
            parameter.region(index, contentType.length());

            if (!parameter.lookingAt())
                throw new IllegalArgumentException("parameter does not match '; parameter=value' format: " + contentType.substring(index));

            final String name = parameter.group(1);
            final String value = parameter.group(2);

            if (value.startsWith("\"") && value.endsWith("\""))
                mediaType.parameters.put(name, value.substring(1, value.length() - 1));
            else
                mediaType.parameters.put(name, value);

            if (name.equalsIgnoreCase("charset"))
                mediaType.charset(Charset.forName(value));
        }

        return mediaType;
    }

    public static void main(String[] args) {

        MediaType t = MediaType.parse("text/plain; abc=xyz ; charset=utf-8");

        System.out.println(t);

//        String text = "\\";
//        Pattern p = Pattern.compile("[\\p{ASCII}&&[^\\p{Cntrl} ;/=\\[\\]\\(\\)\\<\\>\\@\\,\\:\\\"\\?\\=]]+");
//        Matcher m = p.matcher(text);
//        
//        System.out.println(m.matches());

//        final String test = "text/pla;in";
//
//        MediaType ctype = MediaType.parse(test);
//
//        System.out.println(ctype.type());
//        System.out.println(ctype.subtype());
//        System.out.println(ctype.charset());

//        HttpMediaType mediaType = new HttpMediaType("text/pl;ain");
//        System.out.println(mediaType);
    }

    @Override
    public String toString() {
        return "MediaType [type=" + type + ", subtype=" + subtype + ", charset=" + charset + ", parameters=" + parameters + "]";
    }

}
