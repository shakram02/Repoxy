package of_packets;

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.List;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * Immutable implementation of {@link OFPacketHeader}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableOFPacketHeader.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "OFPacketHeader"})
@Immutable
@CheckReturnValue
public final class ImmutableOFPacketHeader extends OFPacketHeader {
  private final byte version;
  private final byte messageCode;
  private final int len;
  private final int xid;

  private ImmutableOFPacketHeader(byte version, byte messageCode, int len, int xid) {
    this.version = version;
    this.messageCode = messageCode;
    this.len = len;
    this.xid = xid;
  }

  /**
   * @return The value of the {@code version} attribute
   */
  @Override
  public byte getVersion() {
    return version;
  }

  /**
   * @return The value of the {@code messageCode} attribute
   */
  @Override
  public byte getMessageCode() {
    return messageCode;
  }

  /**
   * @return The value of the {@code len} attribute
   */
  @Override
  public int getLen() {
    return len;
  }

  /**
   * @return The value of the {@code xid} attribute
   */
  @Override
  public int getXid() {
    return xid;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link OFPacketHeader#getVersion() version} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for version
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableOFPacketHeader withVersion(byte value) {
    if (this.version == value) return this;
    return new ImmutableOFPacketHeader(value, this.messageCode, this.len, this.xid);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link OFPacketHeader#getMessageCode() messageCode} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for messageCode
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableOFPacketHeader withMessageCode(byte value) {
    if (this.messageCode == value) return this;
    return new ImmutableOFPacketHeader(this.version, value, this.len, this.xid);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link OFPacketHeader#getLen() len} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for len
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableOFPacketHeader withLen(int value) {
    if (this.len == value) return this;
    return new ImmutableOFPacketHeader(this.version, this.messageCode, value, this.xid);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link OFPacketHeader#getXid() xid} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for xid
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableOFPacketHeader withXid(int value) {
    if (this.xid == value) return this;
    return new ImmutableOFPacketHeader(this.version, this.messageCode, this.len, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableOFPacketHeader} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableOFPacketHeader
        && equalTo((ImmutableOFPacketHeader) another);
  }

  private boolean equalTo(ImmutableOFPacketHeader another) {
    return version == another.version
        && messageCode == another.messageCode
        && len == another.len
        && xid == another.xid;
  }

  /**
   * Computes a hash code from attributes: {@code version}, {@code messageCode}, {@code len}, {@code xid}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + Bytes.hashCode(version);
    h += (h << 5) + Bytes.hashCode(messageCode);
    h += (h << 5) + len;
    h += (h << 5) + xid;
    return h;
  }

  /**
   * Creates an immutable copy of a {@link OFPacketHeader} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable OFPacketHeader instance
   */
  public static ImmutableOFPacketHeader copyOf(OFPacketHeader instance) {
    if (instance instanceof ImmutableOFPacketHeader) {
      return (ImmutableOFPacketHeader) instance;
    }
    return ImmutableOFPacketHeader.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableOFPacketHeader ImmutableOFPacketHeader}.
   * @return A new ImmutableOFPacketHeader builder
   */
  public static ImmutableOFPacketHeader.Builder builder() {
    return new ImmutableOFPacketHeader.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableOFPacketHeader ImmutableOFPacketHeader}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_VERSION = 0x1L;
    private static final long INIT_BIT_MESSAGE_CODE = 0x2L;
    private static final long INIT_BIT_LEN = 0x4L;
    private static final long INIT_BIT_XID = 0x8L;
    private long initBits = 0xfL;

    private byte version;
    private byte messageCode;
    private int len;
    private int xid;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code OFPacketHeader} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(OFPacketHeader instance) {
      Objects.requireNonNull(instance, "instance");
      version(instance.getVersion());
      messageCode(instance.getMessageCode());
      len(instance.getLen());
      xid(instance.getXid());
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacketHeader#getVersion() version} attribute.
     * @param version The value for version 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder version(byte version) {
      this.version = version;
      initBits &= ~INIT_BIT_VERSION;
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacketHeader#getMessageCode() messageCode} attribute.
     * @param messageCode The value for messageCode 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder messageCode(byte messageCode) {
      this.messageCode = messageCode;
      initBits &= ~INIT_BIT_MESSAGE_CODE;
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacketHeader#getLen() len} attribute.
     * @param len The value for len 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder len(int len) {
      this.len = len;
      initBits &= ~INIT_BIT_LEN;
      return this;
    }

    /**
     * Initializes the value for the {@link OFPacketHeader#getXid() xid} attribute.
     * @param xid The value for xid 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder xid(int xid) {
      this.xid = xid;
      initBits &= ~INIT_BIT_XID;
      return this;
    }

    /**
     * Builds a new {@link ImmutableOFPacketHeader ImmutableOFPacketHeader}.
     * @return An immutable instance of OFPacketHeader
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableOFPacketHeader build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableOFPacketHeader(version, messageCode, len, xid);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_VERSION) != 0) attributes.add("version");
      if ((initBits & INIT_BIT_MESSAGE_CODE) != 0) attributes.add("messageCode");
      if ((initBits & INIT_BIT_LEN) != 0) attributes.add("len");
      if ((initBits & INIT_BIT_XID) != 0) attributes.add("xid");
      return "Cannot build OFPacketHeader, some of required attributes are not set " + attributes;
    }
  }
}
