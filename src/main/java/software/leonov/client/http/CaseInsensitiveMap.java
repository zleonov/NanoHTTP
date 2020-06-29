package software.leonov.client.http;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A {@code Map} which stores {@code String} keys in a case-insensitive manner and maintains insertion iteration order.
 * <p>
 * The original casing of keys is maintained, but all operations which perform key comparisons are case agnostic.
 * <p>
 * Supports all optional operations, and allows for {@code null} keys and values.
 *
 * @param <V> the type of mapped values
 */
final public class CaseInsensitiveMap<V> extends AbstractMap<String, V> implements Cloneable {

    private LinkedHashMap<Key, V> delegate;
    private Locale locale;

    private Set<Map.Entry<String, V>> entrySet;

    /**
     * Create a new {@code CaseInsensitiveMap} that uses {@code Locale.US} to compare keys in a case-insensitive manner.
     */
    public CaseInsensitiveMap() {
        this(Locale.US);
    }

    /**
     * Create a new {@code CaseInsensitiveMap} that uses the specified {@code Locale} to compare keys in a case-insensitive
     * manner.
     * 
     * @param locale the {@code Locale} when comparing keys in a case-insensitive manner
     */
    public CaseInsensitiveMap(final Locale locale) {
        if (locale == null)
            throw new NullPointerException("locale");

        delegate = new LinkedHashMap<>();
        this.locale = (Locale) locale.clone();
    }

    /**
     * Return the {@code Locale} used by this {@code CaseInsensitiveMap} to compare keys in a case-insensitive manner.
     * 
     * @return the {@code Locale} used by this {@code CaseInsensitiveMap} to compare keys in a case-insensitive manner
     */
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public boolean containsKey(final Object o) {
        try {
            return delegate.containsKey(toLowerCase(o));
        } catch (final ClassCastException ex) {
            return false;
        }
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public Set<Entry<String, V>> entrySet() {
        return entrySet == null ? entrySet = new EntrySet() : entrySet;
    }

    @Override
    public V get(final Object o) {
        try {
            return delegate.get(toLowerCase(o));
        } catch (final ClassCastException ex) {
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public V put(final String o, final V value) {
        return delegate.put(toLowerCase(o), value);
    }

    @Override
    public V remove(final Object o) {
        try {
            return delegate.remove(toLowerCase(o));
        } catch (final ClassCastException ex) {
            return null;
        }
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public CaseInsensitiveMap<V> clone() {
        final CaseInsensitiveMap<V> m;
        try {
            m = (CaseInsensitiveMap<V>) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
        m.delegate = (LinkedHashMap<Key, V>) delegate.clone();
        m.locale = (Locale) locale.clone();
        m.entrySet = null;
        return m;
    }

//    private void writeObject(final ObjectOutputStream oos) throws IOException {
//        oos.defaultWriteObject();
//        oos.writeObject(locale);
//        oos.writeObject(delegate);
//    }
//
//    @SuppressWarnings("unchecked")
//    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
//        ois.defaultReadObject();
//        locale = (Locale) ois.readObject();
//        delegate = (LinkedHashMap<Key, V>) ois.readObject();
//        entrySet = null;
//    }

    private final class EntrySet extends AbstractSet<Map.Entry<String, V>> {

        @Override
        public void clear() {
            CaseInsensitiveMap.this.clear();
        }

        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> e = (Entry<?, ?>) o;

                try {
                    final Key key = toLowerCase(e.getKey());
                    return delegate.containsKey(key) && Objects.equals(delegate.get(key), e.getValue());
                } catch (final ClassCastException ex) {
                }
            }

            return false;
        }

        @Override
        public boolean remove(final Object o) {
            return o instanceof Map.Entry ? super.remove(o) : false;
        }

        @Override
        public boolean removeAll(final Collection<?> c) {
            //checkNotNull(c, "c == null");
            boolean modified = false;

            for (final Iterator<?> i = c.iterator(); i.hasNext();)
                modified |= remove(i.next());

            return modified;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean retainAll(final Collection<?> c) {
            //checkNotNull(c, "c == null");
            final Map<String, V> m = new CaseInsensitiveMap<>(locale);

            for (final Object o : c)
                if (o instanceof Map.Entry<?, ?> && o != null)
                    try {
                        final Map.Entry<String, V> e = (Map.Entry<String, V>) o;
                        m.put(e.getKey(), e.getValue());
                    } catch (final ClassCastException e) {
                    }

            return super.retainAll(m.entrySet());
        }

        @Override
        public Iterator<Entry<String, V>> iterator() {
            final Iterator<Entry<Key, V>> itor = delegate.entrySet().iterator();
            return new Iterator<Map.Entry<String, V>>() {

                @Override
                public boolean hasNext() {
                    return itor.hasNext();
                }

                @Override
                public Entry<String, V> next() {
                    final Entry<Key, V> e = itor.next();

                    return new Map.Entry<String, V>() {

                        @Override
                        public String getKey() {
                            return e.getKey().get();
                        }

                        @Override
                        public V getValue() {
                            return e.getValue();
                        }

                        @Override
                        public V setValue(V value) {
                            return e.setValue(value);
                        }

                        @Override
                        public String toString() {
                            return getKey() + "=" + getValue();
                        }

                        @Override
                        public int hashCode() {
                            return e.getKey().hashCode() ^ Objects.hashCode(getValue());
                        }

                        @Override
                        public boolean equals(final Object o) {
                            if (o instanceof Map.Entry) {
                                final Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                                return Objects.equals(getKey(), e.getKey()) && Objects.equals(getValue(), e.getValue());
                            }

                            return false;
                        }
                    };
                }

                @Override
                public void remove() {
                    itor.remove();
                }
            };
        }

        @Override
        public int size() {
            return CaseInsensitiveMap.this.size();
        }
    }

    private Key toLowerCase(final Object o) {
        return new Key(o);
    }

    private final class Key {

        private final String s;
        private final String lcstr;

        public Key(final Object o) {
            this(o == null ? null : o.toString());
        }

        public Key(final String s) {
            this.s = s;
            lcstr = s == null ? null : s.toLowerCase(getLocale());
        }

        @Override
        public boolean equals(final Object obj) {
            return obj == null || getClass() != obj.getClass() ? false : this == obj || Objects.equals(this.lcstr, ((CaseInsensitiveMap<?>.Key) obj).lcstr);
        }

        public String get() {
            return s;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(lcstr);
        }

    }

}
