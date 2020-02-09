package software.leonov.client.http;

import java.nio.charset.Charset;
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
public final class ContentType {
    // https://tools.ietf.org/html/rfc2045#section-5.1
//    private static final String ASCII = "\\p{ASCII}";
//    private static final String NOT_CNTRL = "[^\\p{Cntrl}]";
//    private static final String NOT_TSPECIALS = "[^" + " " + "(" + ")" + "<" + ">" + "@" + "," + ";" + ":" + "\\" + "\"" + "/" + "\\[" + "\\]" + "?" + "=" + "]";
//
    private final static Pattern TOKEN = Pattern.compile("[\\p{ASCII}&&[^\\p{Cntrl} ()<>@,;:\"/\\[\\]?=]]+");
//            "([\\p{ASCII}&&[^\\p{Cntrl} ;/=\\[\\]\\(\\)\\<\\>\\@\\,\\:\\\"\\?\\=]]+)";

//    private final static Pattern CONTENT_TYPE = Pattern.compile("(?s)([^\\s/=;\"]+)/([^\\s/=;\"]+)\\s*(;.*)"); // parameters (G3) or null

//    private final static Pattern PARAMETER = Pattern.compile("\\s*;\\s*([^\\s/=;\"]+)=(\"([^\"]*)\"|[^\\s;\"]*)"); // G2 (if quoted) and else G3

//    private final static String TOKEN2 = "[" + ASCII + "&&" + NOT_CNTRL + "&&" + NOT_TSPECIALS + "]";

//    private static final Pattern CTYPE = Pattern.compile(TOKEN_OR_WILDCARD + "/" + TOKEN_OR_WILDCARD);
//    private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:" + TOKEN_OR_WILDCARD + "=(?:" + TOKEN_OR_WILDCARD + "|\"([^\"]*)\"))?");

    private final static Pattern CONTENT_TYPE = Pattern.compile("(?s)\\s*([^\\s/=;\"]+)/([^\\s/=;\"]+)\\s*(;.*)?"); // parameters (G3) or null

    private static final Pattern PARAMETER = Pattern.compile("\\s*;\\s*([^\\s/=;\"]+)=(" + "\"([^\"]*)\"|[^\\s;\"]*)"); // G2 (if quoted) and else G3

    private final String type;
    private final String subType;

    private Charset charset;

    private final Map<String, String> parameters = new TreeMap<>();
    private final Map<String, String> _parameters = Collections.unmodifiableMap(parameters);

    ContentType(final String type, final String subtype) {
        if (type == null)
            throw new NullPointerException("type == null");
        if (subtype == null)
            throw new NullPointerException("subtype == null");

        if (type.equals("*") && !subtype.equals("*"))
            throw new IllegalArgumentException("cannot have a declared subtype with a wildcard type");

        if (!TOKEN.matcher(type).matches())
            throw new IllegalArgumentException("type contains reserved characters");

        if (!TOKEN.matcher(subtype).matches())
            throw new IllegalArgumentException("subtype contains reserved characters");

        this.type = type;
        this.subType = subtype;
    }

    ContentType parameter(final String name, final String value) {
        if (name == null)
            throw new NullPointerException("name == null");
        if (value == null)
            throw new NullPointerException("value == null");

        if (!TOKEN.matcher(name).matches())
            throw new IllegalArgumentException("name contains reserved characters");

        parameters.put(name, value);
        return this;
    }

    ContentType charset(final Charset charset) {
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
        return subType;
    }

    /**
     * Returns the charset of this media type, or null if this media type doesn't specify a charset.
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
     * Returns an unmodifiable {@code Map} of the parameters of this {@code ContentType}.
     * <p>
     * The parameter names are case-insensitive and the order of the request headers is <b>not</b> maintained.
     * 
     * @return an unmodifiable {@code Map} of the parameters of this {@code ContentType}
     */
    public Map<String, String> parameters() {
        return _parameters;
    }

    public static ContentType parse(final String contentType) {
        if (contentType == null)
            throw new NullPointerException("contentType == null");

        final Matcher m = CONTENT_TYPE.matcher(contentType);
        if (!m.matches())
            throw new IllegalArgumentException("Content-Type does not match 'type/subtype; parameter=value' format");

        final ContentType ct = new ContentType(m.group(1), m.group(2));

        if (m.group(3) != null) {
            System.out.println(m.group(3));
            final Matcher matcher = PARAMETER.matcher(m.group(3));
            while (matcher.find()) {
                // 1=key, 2=valueWithQuotes, 3=valueWithoutQuotes
                String name = matcher.group(1);
                String value = matcher.group(3) == null ? matcher.group(3) : matcher.group(2);
                
                if((value.startsWith("\"") && value.endsWith("\"")) || !TOKEN.matcher(value).matches())
                       throw new IllegalArgumentException("parameter value contains reserved characters: " + value);

                ct.parameter(name, value);
            }
        }else {
            System.out.println(m.end(2));
            if(m.end(2) != contentType.length())
                    throw new IllegalArgumentException("Content-Type parameters do not match '; parameter=value' format: " + contentType.substring(m.end(2)));
        }

        return ct;
    }

//  public static ContentType parse(final String contentType) {
//      if(contentType == null)
//          throw new NullPointerException("contentType == null");
//      
//      
//      final Matcher m = CTYPE.matcher(contentType);
//      
//      if (!m.lookingAt()) 
//        throw new IllegalArgumentException("Content-Type is not valid: " + contentType);
//      
//      final String type = m.group(1).toLowerCase(Locale.US);
//      final String subtype = m.group(2).toLowerCase(Locale.US);
//
//      String charset = null;
//      Matcher parameter = PARAMETER.matcher(contentType);
//      for (int s = m.end(); s < contentType.length(); s = parameter.end()) {
//        parameter.region(s, contentType.length());
//        if (!parameter.lookingAt()) {
//          throw new IllegalArgumentException("Parameter is not formatted correctly: \""
//              + contentType.substring(s)
//              + "\" for: \""
//              + contentType
//              + '"');
//        }
//
//        String name = parameter.group(1);
//        if (name == null || !name.equalsIgnoreCase("charset")) continue;
//        String charsetParameter;
//        String token = parameter.group(2);
//        if (token != null) {
//          // If the token is 'single-quoted' it's invalid! But we're lenient and strip the quotes.
//          charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2)
//              ? token.substring(1, token.length() - 1)
//              : token;
//        } else {
//          // Value is "double-quoted". That's valid and our regex group already strips the quotes.
//          charsetParameter = parameter.group(3);
//        }
//        if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
//          throw new IllegalArgumentException("Multiple charsets defined: \""
//              + charset
//              + "\" and: \""
//              + charsetParameter
//              + "\" for: \""
//              + contentType
//              + '"');
//        }
//        charset = charsetParameter;
//      }

//      return new ContentType(contentType, type, subtype, null);
//    }

//    /**
//     * Returns the {@code Content-Type} header (for example: {@code text/plain; charset=utf-8}).
//     */
//    @Override
//    public String toString() {
//        return contentType;
//    }

    public static void main(String[] args) {
        final String test = "text/pla;in";

        ContentType ctype = ContentType.parse(test);

        System.out.println(ctype.type());
        System.out.println(ctype.subtype());
        System.out.println(ctype.charset());
        
        
//        HttpMediaType mediaType = new HttpMediaType("text/pl;ain");
//        System.out.println(mediaType);
    }

}
