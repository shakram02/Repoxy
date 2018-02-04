package openflow;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link OFPacket}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableOFPacket.builder()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "OFPacket"})
public final class ImmutableOFPacket extends OFPacket {
  private final OFPacketHeader header;
  private final byte[] data;

  private ImmutableOFPacket(OFPacketHeader header, byte[] data) {
    this.header = header;
    this.data = data;
  }

  /**
   * @return The value of the {@code header} attribute
   */
  @Override
  public OFPacketHeader getHeader() {
    return header;
  }

  /**
   * @return A cloned {@code data} array
   */
  @Override
  public byte[] getData() {
    return data.clone();
  }

  /**
   * Copy the current immutable object by setting a value for the {@link OFPacket#getHeader() header} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for header
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableOFPacket withHeader(OFPacketHeader value) {
    if (this.header == value) return this;
    OFPacketHeader newValue = Objects.requireNonNull(value, "header");
    return new ImmutableOFPacket(newValue, this.data);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link OFPacket#getData() data}.
   * The array is cloned before being saved as attribute values.
   * @param elements The non-null elements for data
   * @return A modified copy of {@code this} object
   */
  public final ImmutableOFPacket withData(byte... elements) {
    byte[] newValue = elements.clone();
    return new ImmutableOFPacket(this.header, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableOFPacket} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableOFPacket
        && equalTo((ImmutableOFPacket) another);
  }

  private boolean equalTo(ImmutableOFPacket another) {
    return header.equals(another.header)
        && Arrays.equals(data, another.data);
  }

  /**
   * Computes a hash code from attributes: {@code header}, {@code data}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + header.hashCode();
    h += (h << 5) + Arrays.hashCode(data);
    return h;
  }

  /**
   * Creates an immutable copy of a {@link OFPacket} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable OFPacket instance
   */
  public static ImmutableOFPacket copyOf(OFPacket instance) {
    if (instance instanceof ImmutableOFPacket) {
      return (ImmutableOFPacket) instance;
    }
    return ImmutableOFPacket.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableOFPacket ImmutableOFPacket}.
   * @return A new ImmutableOFPacket builder
   */
  public static ImmutableOFPacket.Builder builder() {
    return new ImmutableOFPacket.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableOFPacket ImmutableOFPacket}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_HEADER = 0x1L;
    private static final long INIT_BIT_DATA = 0x2L;
    private long initBits = 0x3L;

    private OFPacketHeader header;
    private byte[] data;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code OFPacket} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(OFPacket instance) {
      Objects.requireNonNull(instance, "instance");
      header(instance.getHeader());
      data(instance.getData());
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacket#getHeader() header} attribute.
     * @param header The value for header 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder header(OFPacketHeader header) {
      this.header = Objects.requireNonNull(header, "header");
      initBits &= ~INIT_BIT_HEADER;
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacket#getData() data} attribute.
     * @param data The elements for data
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder data(byte... data) {
      this.data = data.clone();
      initBits &= ~INIT_BIT_DATA;
      return this;
    }

    /**
     * Builds a new {@link ImmutableOFPacket ImmutableOFPacket}.
     * @return An immutable instance of OFPacket
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableOFPacket build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableOFPacket(header, data);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_HEADER) != 0) attributes.add("header");
      if ((initBits & INIT_BIT_DATA) != 0) attributes.add("data");
      return "Cannot build OFPacket, some of required attributes are not set " + attributes;
    }
  }
}
