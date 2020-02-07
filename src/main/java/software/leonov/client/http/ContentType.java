package software.leonov.client.http;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.client.http.HttpMediaType;
import com.google.common.base.CharMatcher;

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
    
    
    
    
    
    
    
    // OkHTTP: "([    a-zA-Z0-9_    -    !    #    $    %    &    '    *    +    .    ^    `    {    |    }    ~    ]+)";
    // OkHTTP: "([%    '    *    `    {    |    }    ~    ]+)";
    
    // Google HTTP Client: "[    \\w    !    #    $    &    .    +    -    ^    ]+|[    *    ]");
    // Google HTTP Client: "[]+|[    *    ]");
    
    final String zhenyaType = "";
    
    
    
    
    //     ()<>@,;:\"/[]?=

    
    private static final String TOKEN = "([\\w-!#$%&'*+.^`{|}~]+)";
    private static final String QUOTED = "[\"\']([^\'\"]*)[\"\']";
    
    private static final Pattern TYPE_SUBTYPE = Pattern.compile(TOKEN + "/" + TOKEN);
    private static final Pattern PARAMETER = Pattern.compile(";\\s*(?:" + TOKEN + "=(?:" + TOKEN + "|" + QUOTED + "))?");

    private final String contentType;
    private final String type;
    private final String subtype;

    private ContentType(final String contentType, final String type, final String subtype, final Charset charset) {
        this.contentType = contentType;
        this.type = type;
        this.subtype = subtype;
        this.charset = charset;
    }
    
    private Charset charset;

    public static ContentType parse(final String contentType) {
        if(contentType == null)
            throw new NullPointerException("contentType == null");
        
        
        final Matcher typeSubtype = TYPE_SUBTYPE.matcher(contentType);
        
        if (!typeSubtype.find())
            throw new IllegalArgumentException("Content-Type is not valid: " +  contentType);
        
        final String type = typeSubtype.group(1).toLowerCase(Locale.US);
        final String subtype = typeSubtype.group(2).toLowerCase(Locale.US);
        
        if(type.equals("*") && !subtype.equals("*"))
            throw new IllegalArgumentException("Content-Type cannot have wildcard type with a declared subtype");

        final String params = contentType.substring(typeSubtype.end());
        final Matcher parameter = PARAMETER.matcher(params);

        Charset charset = null;
        while(parameter.find()) {
            String name = parameter.group(1);
            String value = parameter.group(2);
            
            if(name.equalsIgnoreCase("charset"))
                charset = Charset.forName(value);
        }
        
        
//        for (int s = typeSubtype.end(); s < contentType.length(); s = parameter.end()) {
//            parameter.region(s, contentType.length());
//            if (!parameter.lookingAt()) {
//                throw new IllegalArgumentException("Parameter is not formatted correctly: \"" + contentType.substring(s) + "\" for: \"" + contentType + '"');
//            }
//
//            String name = parameter.group(1);
//            if (name == null || !name.equalsIgnoreCase("charset"))
//                continue;
//            String charsetParameter;
//            String token = parameter.group(2);
//            if (token != null) {
//                // If the token is 'single-quoted' it's invalid! But we're lenient and strip the quotes.
//                charsetParameter = (token.startsWith("'") && token.endsWith("'") && token.length() > 2) ? token.substring(1, token.length() - 1) : token;
//            } else {
//                // Value is "double-quoted". That's valid and our regex group already strips the quotes.
//                charsetParameter = parameter.group(3);
//            }
//            if (charset != null && !charsetParameter.equalsIgnoreCase(charset)) {
//                throw new IllegalArgumentException("Multiple charsets defined: \"" + charset + "\" and: \"" + charsetParameter + "\" for: \"" + contentType + '"');
//            }
//            charset = charsetParameter;
//        }

        return new ContentType(contentType, type, subtype, charset);
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
     * Returns the charset of this media type, or null if this media type doesn't specify a charset.
     */
    public Charset charset() {
        return charset;
    }

    /**
     * Returns the {@code Content-Type} header (for example: {@code text/plain; charset=utf-8}).
     */
    @Override
    public String toString() {
        return contentType;
    }
    
    public static void main(String[] args) {
        final String test = "text/plain; charset=utf-8";
        
        ContentType ctype = ContentType.parse(test);
        
        System.out.println(ctype.type());
        System.out.println(ctype.subtype());
        System.out.println(ctype.charset());
    }
    
}
