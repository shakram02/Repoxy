package utils.events;

import com.google.common.collect.Lists;
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
 * Immutable implementation of {@link SocketAddressInfoEventArg}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableSocketAddressInfoEventArg.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "SocketAddressInfoEventArg"})
@Immutable
@CheckReturnValue
public final class ImmutableSocketAddressInfoEventArg extends SocketAddressInfoEventArg {
  private final String ip;
  private final int port;

  private ImmutableSocketAddressInfoEventArg(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  /**
   * @return The value of the {@code ip} attribute
   */
  @Override
  public String getIp() {
    return ip;
  }

  /**
   * @return The value of the {@code port} attribute
   */
  @Override
  public int getPort() {
    return port;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketAddressInfoEventArg#getIp() ip} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for ip
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketAddressInfoEventArg withIp(String value) {
    if (this.ip.equals(value)) return this;
    String newValue = Objects.requireNonNull(value, "ip");
    return new ImmutableSocketAddressInfoEventArg(newValue, this.port);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketAddressInfoEventArg#getPort() port} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for port
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketAddressInfoEventArg withPort(int value) {
    if (this.port == value) return this;
    return new ImmutableSocketAddressInfoEventArg(this.ip, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableSocketAddressInfoEventArg} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableSocketAddressInfoEventArg
        && equalTo((ImmutableSocketAddressInfoEventArg) another);
  }

  private boolean equalTo(ImmutableSocketAddressInfoEventArg another) {
    return ip.equals(another.ip)
        && port == another.port;
  }

  /**
   * Computes a hash code from attributes: {@code ip}, {@code port}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + ip.hashCode();
    h += (h << 5) + port;
    return h;
  }

  /**
   * Creates an immutable copy of a {@link SocketAddressInfoEventArg} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable SocketAddressInfoEventArg instance
   */
  public static ImmutableSocketAddressInfoEventArg copyOf(SocketAddressInfoEventArg instance) {
    if (instance instanceof ImmutableSocketAddressInfoEventArg) {
      return (ImmutableSocketAddressInfoEventArg) instance;
    }
    return ImmutableSocketAddressInfoEventArg.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableSocketAddressInfoEventArg ImmutableSocketAddressInfoEventArg}.
   * @return A new ImmutableSocketAddressInfoEventArg builder
   */
  public static ImmutableSocketAddressInfoEventArg.Builder builder() {
    return new ImmutableSocketAddressInfoEventArg.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableSocketAddressInfoEventArg ImmutableSocketAddressInfoEventArg}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_IP = 0x1L;
    private static final long INIT_BIT_PORT = 0x2L;
    private long initBits = 0x3L;

    private @Nullable String ip;
    private int port;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code SocketAddressInfoEventArg} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(SocketAddressInfoEventArg instance) {
      Objects.requireNonNull(instance, "instance");
      ip(instance.getIp());
      port(instance.getPort());
      return this;
    }

    /**
     * Initializes the value for the {@link SocketAddressInfoEventArg#getIp() ip} attribute.
     * @param ip The value for ip 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder ip(String ip) {
      this.ip = Objects.requireNonNull(ip, "ip");
      initBits &= ~INIT_BIT_IP;
      return this;
    }

    /**
     * Initializes the value for the {@link SocketAddressInfoEventArg#getPort() port} attribute.
     * @param port The value for port 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder port(int port) {
      this.port = port;
      initBits &= ~INIT_BIT_PORT;
      return this;
    }

    /**
     * Builds a new {@link ImmutableSocketAddressInfoEventArg ImmutableSocketAddressInfoEventArg}.
     * @return An immutable instance of SocketAddressInfoEventArg
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableSocketAddressInfoEventArg build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableSocketAddressInfoEventArg(ip, port);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_IP) != 0) attributes.add("ip");
      if ((initBits & INIT_BIT_PORT) != 0) attributes.add("port");
      return "Cannot build SocketAddressInfoEventArg, some of required attributes are not set " + attributes;
    }
  }
}
