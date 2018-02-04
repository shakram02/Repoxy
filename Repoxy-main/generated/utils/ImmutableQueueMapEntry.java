package utils;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link QueueMap.QueueMapEntry}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableQueueMapEntry.<K, V>builder()}.
 * Use the static factory method to create immutable instances:
 * {@code ImmutableQueueMapEntry.<K, V>of()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "QueueMap.QueueMapEntry<K, V>"})
public final class ImmutableQueueMapEntry<K, V> extends QueueMap.QueueMapEntry<K, V> {
  private final K key;
  private final V value;

  private ImmutableQueueMapEntry(K key, V value) {
    this.key = Objects.requireNonNull(key, "key");
    this.value = Objects.requireNonNull(value, "value");
  }

  private ImmutableQueueMapEntry(ImmutableQueueMapEntry original, K key, V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * @return The value of the {@code key} attribute
   */
  @Override
  public K getKey() {
    return key;
  }

  /**
   * @return The value of the {@code value} attribute
   */
  @Override
  public V getValue() {
    return value;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link QueueMap.QueueMapEntry#getKey() key} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for key
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableQueueMapEntry<K, V> withKey(K value) {
    if (this.key == value) return this;
    K newValue = Objects.requireNonNull(value, "key");
    return new ImmutableQueueMapEntry<K, V>(this, newValue, this.value);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link QueueMap.QueueMapEntry#getValue() value} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for value
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableQueueMapEntry<K, V> withValue(V value) {
    if (this.value == value) return this;
    V newValue = Objects.requireNonNull(value, "value");
    return new ImmutableQueueMapEntry<K, V>(this, this.key, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableQueueMapEntry} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @SuppressWarnings("unchecked")
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableQueueMapEntry<?, ?>
        && equalTo((ImmutableQueueMapEntry<K, V>) another);
  }

  private boolean equalTo(ImmutableQueueMapEntry<K, V> another) {
    return key.equals(another.key)
        && value.equals(another.value);
  }

  /**
   * Prints the immutable value {@code QueueMapEntry} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("QueueMapEntry")
        .omitNullValues()
        .add("key", key)
        .add("value", value)
        .toString();
  }

  /**
   * Construct a new immutable {@code QueueMapEntry} instance.
   * @param key The value for the {@code key} attribute
   * @param value The value for the {@code value} attribute
   * @return An immutable QueueMapEntry instance
   */
  public static <K, V> ImmutableQueueMapEntry<K, V> of(K key, V value) {
    return new ImmutableQueueMapEntry<K, V>(key, value);
  }

  /**
   * Creates an immutable copy of a {@link QueueMap.QueueMapEntry} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param <K> generic parameter K
   * @param <V> generic parameter V
   * @param instance The instance to copy
   * @return A copied immutable QueueMapEntry instance
   */
  public static <K, V> ImmutableQueueMapEntry<K, V> copyOf(QueueMap.QueueMapEntry<K, V> instance) {
    if (instance instanceof ImmutableQueueMapEntry<?, ?>) {
      return (ImmutableQueueMapEntry<K, V>) instance;
    }
    return ImmutableQueueMapEntry.<K, V>builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableQueueMapEntry ImmutableQueueMapEntry}.
   * @param <K> generic parameter K
   * @param <V> generic parameter V
   * @return A new ImmutableQueueMapEntry builder
   */
  public static <K, V> ImmutableQueueMapEntry.Builder<K, V> builder() {
    return new ImmutableQueueMapEntry.Builder<K, V>();
  }

  /**
   * Builds instances of type {@link ImmutableQueueMapEntry ImmutableQueueMapEntry}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder<K, V> {
    private static final long INIT_BIT_KEY = 0x1L;
    private static final long INIT_BIT_VALUE = 0x2L;
    private long initBits = 0x3L;

    private K key;
    private V value;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code QueueMapEntry} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder<K, V> from(QueueMap.QueueMapEntry<K, V> instance) {
      Objects.requireNonNull(instance, "instance");
      key(instance.getKey());
      value(instance.getValue());
      return this;
    }

    /**
     * Initializes the value for the {@link QueueMap.QueueMapEntry#getKey() key} attribute.
     * @param key The value for key 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder<K, V> key(K key) {
      this.key = Objects.requireNonNull(key, "key");
      initBits &= ~INIT_BIT_KEY;
      return this;
    }

    /**
     * Initializes the value for the {@link QueueMap.QueueMapEntry#getValue() value} attribute.
     * @param value The value for value 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder<K, V> value(V value) {
      this.value = Objects.requireNonNull(value, "value");
      initBits &= ~INIT_BIT_VALUE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableQueueMapEntry ImmutableQueueMapEntry}.
     * @return An immutable instance of QueueMapEntry
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableQueueMapEntry<K, V> build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableQueueMapEntry<K, V>(null, key, value);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_KEY) != 0) attributes.add("key");
      if ((initBits & INIT_BIT_VALUE) != 0) attributes.add("value");
      return "Cannot build QueueMapEntry, some of required attributes are not set " + attributes;
    }
  }
}
