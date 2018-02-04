package utils.events;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import javax.annotation.Generated;
import openflow.OFPacket;
import utils.ConnectionId;
import utils.SenderType;

/**
 * Immutable implementation of {@link SocketDataEventArg}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableSocketDataEventArg.builder()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "SocketDataEventArg"})
public final class ImmutableSocketDataEventArg extends SocketDataEventArg {
  private final OFPacket packet;
  private final ConnectionId id;
  private final SenderType senderType;

  private ImmutableSocketDataEventArg(OFPacket packet, ConnectionId id, SenderType senderType) {
    this.packet = packet;
    this.id = id;
    this.senderType = senderType;
  }

  /**
   * @return The value of the {@code packet} attribute
   */
  @Override
  public OFPacket getPacket() {
    return packet;
  }

  /**
   * @return The value of the {@code id} attribute
   */
  @Override
  public ConnectionId getId() {
    return id;
  }

  /**
   * @return The value of the {@code senderType} attribute
   */
  @Override
  public SenderType getSenderType() {
    return senderType;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketDataEventArg#getPacket() packet} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for packet
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketDataEventArg withPacket(OFPacket value) {
    if (this.packet == value) return this;
    OFPacket newValue = Objects.requireNonNull(value, "packet");
    return new ImmutableSocketDataEventArg(newValue, this.id, this.senderType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketDataEventArg#getId() id} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketDataEventArg withId(ConnectionId value) {
    if (this.id == value) return this;
    ConnectionId newValue = Objects.requireNonNull(value, "id");
    return new ImmutableSocketDataEventArg(this.packet, newValue, this.senderType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketDataEventArg#getSenderType() senderType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for senderType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketDataEventArg withSenderType(SenderType value) {
    if (this.senderType == value) return this;
    SenderType newValue = Objects.requireNonNull(value, "senderType");
    return new ImmutableSocketDataEventArg(this.packet, this.id, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableSocketDataEventArg} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(Object another) {
    if (this == another) return true;
    return another instanceof ImmutableSocketDataEventArg
        && equalTo((ImmutableSocketDataEventArg) another);
  }

  private boolean equalTo(ImmutableSocketDataEventArg another) {
    return packet.equals(another.packet)
        && id.equals(another.id)
        && senderType.equals(another.senderType);
  }

  /**
   * Computes a hash code from attributes: {@code packet}, {@code id}, {@code senderType}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + packet.hashCode();
    h += (h << 5) + id.hashCode();
    h += (h << 5) + senderType.hashCode();
    return h;
  }

  /**
   * Creates an immutable copy of a {@link SocketDataEventArg} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable SocketDataEventArg instance
   */
  public static ImmutableSocketDataEventArg copyOf(SocketDataEventArg instance) {
    if (instance instanceof ImmutableSocketDataEventArg) {
      return (ImmutableSocketDataEventArg) instance;
    }
    return ImmutableSocketDataEventArg.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableSocketDataEventArg ImmutableSocketDataEventArg}.
   * @return A new ImmutableSocketDataEventArg builder
   */
  public static ImmutableSocketDataEventArg.Builder builder() {
    return new ImmutableSocketDataEventArg.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableSocketDataEventArg ImmutableSocketDataEventArg}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {
    private static final long INIT_BIT_PACKET = 0x1L;
    private static final long INIT_BIT_ID = 0x2L;
    private static final long INIT_BIT_SENDER_TYPE = 0x4L;
    private long initBits = 0x7L;

    private OFPacket packet;
    private ConnectionId id;
    private SenderType senderType;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code utils.events.SocketDataEventArg} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(SocketDataEventArg instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    /**
     * Fill a builder with attribute values from the provided {@code utils.events.SocketEventArguments} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(SocketEventArguments instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    private void from(Object object) {
      if (object instanceof SocketDataEventArg) {
        SocketDataEventArg instance = (SocketDataEventArg) object;
        packet(instance.getPacket());
      }
      if (object instanceof SocketEventArguments) {
        SocketEventArguments instance = (SocketEventArguments) object;
        senderType(instance.getSenderType());
        id(instance.getId());
      }
    }

    /**
     * Initializes the value for the {@link SocketDataEventArg#getPacket() packet} attribute.
     * @param packet The value for packet 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder packet(OFPacket packet) {
      this.packet = Objects.requireNonNull(packet, "packet");
      initBits &= ~INIT_BIT_PACKET;
      return this;
    }

    /**
     * Initializes the value for the {@link SocketDataEventArg#getId() id} attribute.
     * @param id The value for id 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder id(ConnectionId id) {
      this.id = Objects.requireNonNull(id, "id");
      initBits &= ~INIT_BIT_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link SocketDataEventArg#getSenderType() senderType} attribute.
     * @param senderType The value for senderType 
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder senderType(SenderType senderType) {
      this.senderType = Objects.requireNonNull(senderType, "senderType");
      initBits &= ~INIT_BIT_SENDER_TYPE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableSocketDataEventArg ImmutableSocketDataEventArg}.
     * @return An immutable instance of SocketDataEventArg
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableSocketDataEventArg build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableSocketDataEventArg(packet, id, senderType);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_PACKET) != 0) attributes.add("packet");
      if ((initBits & INIT_BIT_ID) != 0) attributes.add("id");
      if ((initBits & INIT_BIT_SENDER_TYPE) != 0) attributes.add("senderType");
      return "Cannot build SocketDataEventArg, some of required attributes are not set " + attributes;
    }
  }
}
