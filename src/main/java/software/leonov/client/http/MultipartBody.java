package software.leonov.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * A <a href="https://tools.ietf.org/html/rfc1521#section-7.2" target="_blank">Multipart</a> {@code RequestBody} in
 * which one or more different sets of data called <i>body</i> {@code Part}s are combined in a single
 * {@code Message-Body}.
 * 
 * @author Zhenya Leonov
 */
public final class MultipartBody implements RequestBody {

    private abstract static class AbstractBuilder {

        private final List<Part> parts = new ArrayList<>();
        private String boundary = "__END_OF_PART__" + UUID.randomUUID().toString();
        private String contentType;

        private AbstractBuilder(final String contentType) {
            this.contentType = contentType;
        }

        public AbstractBuilder part(final Part part) {
            if (part == null)
                throw new NullPointerException("part == null");

            parts.add(part);
            return this;
        }

//        /**
//         * Overrides the default encapsulation <i>boundary</i> string used to separate body {@code Part}s of a
//         * {@code MultipartBody}.
//         * <p>
//         * The default boundary string is calculated in the following manner:
//         * {@code __END_OF_PART__ + the hexadecimal representation of current time in milliseconds}.
//         * 
//         * @param boundary the specified boundary
//         * 
//         * @return this {@code Builder} instance
//         */
//        public AbstractBuilder boundary(final String boundary) {
//            if (boundary == null)
//                throw new NullPointerException("boundary == null");
//
//            this.boundary = boundary;
//            return this;
//        }

    }

    /**
     * A builder of {@code MultipartBody} instances of the {@code multipart/mixed} subtype.
     * <p>
     * The {@code multipart/mixed} subtype is the primary subtype intended for use when the <i>body</i> {@code Part}s are
     * independent. Any multipart subtypes that a server does not recognized are usually treated as being of subtype
     * {@code mixed}.
     */
    public static final class Builder extends AbstractBuilder {

        private Builder() {
            super("multipart/mixed");
        }

        /**
         * Adds the specified <i>body</i> {@code Part} to the {@code MultipartBody}.
         * 
         * @param part the specified part
         * @return this {@code Builder} instance
         */
        @Override
        public Builder part(final Part part) {
            return (Builder) super.part(part);
        }

        /**
         * Adds a new <i>body</i> {@code Part} composed of the specified {@code RequestBody} to the {@code MultipartBody}.
         * <p>
         * If the {@link RequestBody#getContentEncoding() Content-Encoding}, {@link RequestBody#length() Content-Length}, or
         * {@link RequestBody#getContentType() Content-Type} values are defined, they will be inherited.
         * 
         * @param body the specified {@code RequestBody}
         * @return this {@code Builder} instance
         */
        public Builder part(final RequestBody body) {
            if (body == null)
                throw new NullPointerException("part == null");

            return (Builder) super.part(new Part(body));
        }

        /**
         * Returns a new {@code MultipartBody} composed of the {@code Part}s added to this builder.
         * 
         * @return a new {@code MultipartBody} composed of the {@code Part}s added to this builder
         */
        public MultipartBody build() {
            if (super.parts.isEmpty())
                throw new IllegalArgumentException("multipart/mixed content must have at least one body part");
            return new MultipartBody(super.parts, super.contentType, super.boundary);
        }

    }

    /**
     * A builder of {@code MultipartBody} instances of the
     * <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2" target="_blank">multipart/form-data</a>
     * subtype.
     */
    public static final class FormBuilder extends AbstractBuilder {

        private FormBuilder() {
            super("multipart/form-data");
        }

        /**
         * Adds a new multipart/form-data {@code Part} composed of the specified {@code RequestBody} to the
         * {@code MultipartBody}.
         * <p>
         * The {@code Content-Disposition} header will be automatically composed and the {@link RequestBody#getContentEncoding()
         * Content-Encoding}, {@link RequestBody#length() Content-Length}, or {@link RequestBody#getContentType() Content-Type}
         * values will be inherited if they are defined.
         * <p>
         * <b>Note:</b> <a href="https://tools.ietf.org/html/rfc2388" target="_blank">RFC-2388</a> and later
         * <a href="https://tools.ietf.org/html/rfc7578" target="_blank">RFC-7578</a> are somewhat
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-A" taget="_blank">unclear</a> and at times
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-B" target="_blank">contradictory</a> as to how Non-ASCII
         * characters in field names should be
         * <a href="https://tools.ietf.org/html/rfc7578#section-5.1" target="_blank">treated</a>. <u>This method does not use
         * any encoding scheme to escape the field name.</u> <b>It is highly recommended to avoid
         * Non-{@link StandardCharsets#US_ASCII US_ASCII} field names</b>.
         * 
         * @param field the input field name (must be <b>unquoted</b>)
         * @param body  the {@code RequestBody} to add
         * @return this {@code FormDataBuilder} instance
         * @return
         */
        public FormBuilder field(final String field, final RequestBody body) {
            if (field == null)
                throw new NullPointerException("field == null");
            if (body == null)
                throw new NullPointerException("body == null");

            final String param = field.startsWith("\"") && field.endsWith("\"") ? field.substring(1, field.length() - 1) : field;

            return (FormBuilder) super.part(new Part(body).setHeader("Content-Disposition", "form-data; name=\"" + param + "\""));
        }

        /**
         * Adds a new multipart/form-data {@code Part} composed of the specified text to the {@code MultipartBody}.
         * <p>
         * The {@code Content-Disposition} header will be automatically composed and {@code Content-Type} of the body part will
         * be set to {@code text/plain; charset="UTF-8"}.
         * <p>
         * <b>Note:</b> <a href="https://tools.ietf.org/html/rfc2388" target="_blank">RFC-2388</a> and later
         * <a href="https://tools.ietf.org/html/rfc7578" target="_blank">RFC-7578</a> are somewhat
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-A" taget="_blank">unclear</a> and at times
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-B" target="_blank">contradictory</a> as to how Non-ASCII
         * characters in field names should be
         * <a href="https://tools.ietf.org/html/rfc7578#section-5.1" target="_blank">treated</a>. <u>This method does not use
         * any encoding scheme to escape the field name.</u> <b>It is highly recommended to avoid
         * Non-{@link StandardCharsets#US_ASCII US_ASCII} field names</b>.
         * 
         * @param field the input field name (must be <b>unquoted</b>)
         * @param value the text value
         * @return this {@code FormDataBuilder} instance
         */
        public FormBuilder field(final String field, final String value) {
            if (field == null)
                throw new NullPointerException("field == null");
            if (value == null)
                throw new NullPointerException("value == null");

            final RequestBody body = ByteArrayBody.encode(value).setContentType("text/plain; charset=\"UTF-8\"");
            return field(field, body);
        }

        /**
         * Adds a new multipart/form-data file {@code Part} composed of the specified {@code RequestBody} to the
         * {@code MultipartBody}.
         * <p>
         * The {@code Content-Disposition} header will be automatically composed and the {@link RequestBody#getContentEncoding()
         * Content-Encoding}, {@link RequestBody#length() Content-Length}, or {@link RequestBody#getContentType() Content-Type}
         * values will be inherited if they are defined.
         * <p>
         * <b>Note:</b> <a href="https://tools.ietf.org/html/rfc2388" target="_blank">RFC-2388</a> and later
         * <a href="https://tools.ietf.org/html/rfc7578" target="_blank">RFC-7578</a> are somewhat
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-A" taget="_blank">unclear</a> and at times
         * <a href="https://tools.ietf.org/html/rfc7578#appendix-B" target="_blank">contradictory</a> as to how Non-ASCII
         * characters in field names should be
         * <a href="https://tools.ietf.org/html/rfc7578#section-5.1" target="_blank">treated</a>. Likewise RFC-7578
         * <a href="https://tools.ietf.org/html/rfc7578#section-4.2" target="_blank">contradicts</a>
         * <a href="https://tools.ietf.org/html/rfc6266#section-4.3" target="_blank">RFC-6266</a> as to how the <i>filename*</i>
         * parameter should be used. <u>This method does not use any encoding scheme to escape the field name or the filename.
         * </u> <b>It is highly recommended to avoid Non-{@link StandardCharsets#US_ASCII US_ASCII} in both the field name and
         * the filename</b>.
         * 
         * @param field    the file input field name (must be <b>unquoted</b>)
         * @param filename the filename (must be <b>unquoted</b>)
         * @param body     the {@code RequestBody} to add, typically a {@code FileBody}
         * @return this {@code FormDataBuilder} instance
         */
        public FormBuilder file(final String field, final String filename, final RequestBody body) {
            if (field == null)
                throw new NullPointerException("field == null");
            if (filename == null)
                throw new NullPointerException("filename == null");
            if (body == null)
                throw new NullPointerException("body == null");

            return (FormBuilder) part(new Part(body).setHeader("Content-Disposition", "form-data; name=\"" + field + "\"; filename=\"" + filename + "\""));
        }

        /**
         * Returns a new {@code MultipartBody} composed of the input fields and files added to this builder.
         * 
         * @return a new {@code MultipartBody} composed of the input fields and files added to this builder
         */
        public MultipartBody build() {
            if (super.parts.isEmpty())
                throw new IllegalArgumentException("multipart/form-data content must have at least one field");
            return new MultipartBody(super.parts, super.contentType, super.boundary);
        }

    }

    /**
     * A <i>body</i> {@code Part} of a {@code MultipartBody} composed of a {@code RequestBody} and optional headers.
     */
    public static final class Part {

        private final Map<String, String> headers = new CaseInsensitiveMap<>(Locale.US);
        private final Map<String, String> _headers = Collections.unmodifiableMap(headers);
        private final RequestBody body;

        /**
         * Constructs a new <i>body</i> {@code Part} composed of the specified {@code RequestBody}.
         * <p>
         * If the {@link RequestBody#getContentEncoding() Content-Encoding}, {@link RequestBody#length() Content-Length}, or
         * {@link RequestBody#getContentType() Content-Type} values are defined, they will be inherited.
         * 
         * @param body the specified {@code RequestBody}
         */
        public Part(final RequestBody body) {
            if (body == null)
                throw new NullPointerException("body == null");

            this.body = body;

            if (body.getContentEncoding() != null)
                headers.put("Content-Encoding", body.getContentEncoding());

            if (body.length() >= 0)
                headers.put("Content-Length", Long.toString(body.length()));

            if (body.getContentType() != null)
                headers.put("Content-Type", body.getContentType());

            headers.put("Content-Transfer-Encoding", "binary");
        }

        /**
         * Returns the {@code RequestBody} used to construct this <i>body</i> {@code Part}.
         * 
         * @return the {@code RequestBody} used to construct this <i>body</i> {@code Part}
         */
        public RequestBody getBody() {
            return body;
        }

        /**
         * Returns an unmodifiable {@code Map} of the headers for this <i>body</i> {@code Part}.
         * <p>
         * The keys are strings (which are case-insensitive) that represent the header field names, the values are strings that
         * represents the corresponding field values.
         * 
         * @return an unmodifiable {@code Map} of the headers for this <i>body</i> {@code Part}
         */
        public Map<String, String> headers() {
            return _headers;
        }

        /**
         * Sets an optional header for this <i>body</i> {@code Part}.
         * <p>
         * All header values are accepted, but as stated in <a href="https://tools.ietf.org/html/rfc1521#section-7.2
         * target="_blank">RFC-1521</a>: <i>The only header fields that have defined meaning for body parts are those the names
         * of which begin with "Content-"</i>.
         * 
         * @param name  the header name
         * @param value the header value
         * @return this {@code Part} instance
         */
        public Part setHeader(final String name, final String value) {
            if (name == null)
                throw new NullPointerException("name == null");
            if (value == null)
                throw new NullPointerException("value == null");

            headers.put(name, value);
            return this;
        }

    }

    private final List<Part> parts;
    private final String boundary;
    private final String contentType;

    private MultipartBody(final List<Part> parts, final String contentType, final String boundary) {
        this.parts = parts;
        this.boundary = boundary;
        this.contentType = contentType + "; boundary=\"" + boundary + "\"";
    }

    /**
     * Returns a builder of {@code MultipartBody} instances.
     * <p>
     * The default {@link RequestBody#getContentType() Content-Type} will be {@code multipart/mixed} which is the primary
     * subtype intended for use when the body parts are independent. Any multipart subtypes that a server does not
     * recognized are usually treated as being of subtype {@code mixed}.
     * 
     * @return a builder of {@code MultipartBody} instances
     */
    public static Builder mixed() {
        return new Builder();
    }

    /**
     * Returns a builder of {@code MultipartBody} instances of the
     * <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2" target="_blank">multipart/form-data</a>
     * subtype.
     * 
     * @return a builder of {@code MultipartBody} instances of the
     *         <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.2" target=
     *         "_blank">multipart/form-data</a> subtype
     */
    public static FormBuilder formData() {
        return new FormBuilder();
    }

//    /**
//     * Returns the encapsulation <i>boundary</i> string used to separate <i>body</i> {@code Part}s of a
//     * {@code MultipartBody}.
//     * 
//     * @return the encapsulation <i>boundary</i> string used to separate <i>body</i> {@code Part}s of a
//     *         {@code MultipartBody}
//     */
//    public String getBoundary() {
//        return boundary;
//    }

    /**
     * Returns the {@code Content-Type} of this {@code MultipartBody}.
     * 
     * @return the {@code Content-Type} of this {@code MultipartBody}
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * This operation is not supported.
     * 
     * @throws UnsupportedOperationException always
     */
    @Override
    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(final OutputStream to) throws IOException {
        if (to == null)
            throw new NullPointerException("to == null");

        final Writer writer = new OutputStreamWriter(to, StandardCharsets.ISO_8859_1);

        for (final Part part : parts) {
            writer.write("--" + boundary + "\r\n");

            for (final Entry<String, String> entry : part.headers().entrySet())
                writer.write(entry.getKey() + ": " + entry.getValue() + "\r\n");

            writer.write("\r\n");
            writer.flush();
            part.getBody().write(to);
            writer.write("\r\n");
        }

        writer.write("--" + boundary + "--\r\n");
        writer.flush();
    }

}