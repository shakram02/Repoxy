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
import utils.ConnectionId;
import utils.SenderType;

/**
 * Immutable implementation of {@link SocketConnectionIdArgs}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableSocketConnectionIdArgs.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "SocketConnectionIdArgs"})
@Immutable
@CheckReturnValue
public final class ImmutableSocketConnectionIdArgs extends SocketConnectionIdArgs {
  private final ConnectionId id;
  private final SenderType senderType;
  private final EventType replyType;

  private ImmutableSocketConnectionIdArgs(ConnectionId id, SenderType senderType, EventType replyType) {
    this.id = id;
    this.senderType = senderType;
    this.replyType = replyType;
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
   * @return The value of the {@code replyType} attribute
   */
  @Override
  public EventType getReplyType() {
    return replyType;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketConnectionIdArgs#getId() id} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for id
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketConnectionIdArgs withId(ConnectionId value) {
    if (this.id == value) return this;
    ConnectionId newValue = Objects.requireNonNull(value, "id");
    return new ImmutableSocketConnectionIdArgs(newValue, this.senderType, this.replyType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketConnectionIdArgs#getSenderType() senderType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for senderType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketConnectionIdArgs withSenderType(SenderType value) {
    if (this.senderType == value) return this;
    SenderType newValue = Objects.requireNonNull(value, "senderType");
    return new ImmutableSocketConnectionIdArgs(this.id, newValue, this.replyType);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link SocketConnectionIdArgs#getReplyType() replyType} attribute.
   * A value equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for replyType
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableSocketConnectionIdArgs withReplyType(EventType value) {
    if (this.replyType == value) return this;
    EventType newValue = Objects.requireNonNull(value, "replyType");
    return new ImmutableSocketConnectionIdArgs(this.id, this.senderType, newValue);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableSocketConnectionIdArgs} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableSocketConnectionIdArgs
        && equalTo((ImmutableSocketConnectionIdArgs) another);
  }

  private boolean equalTo(ImmutableSocketConnectionIdArgs another) {
    return id.equals(another.id)
        && senderType.equals(another.senderType)
        && replyType.equals(another.replyType);
  }

  /**
   * Computes a hash code from attributes: {@code id}, {@code senderType}, {@code replyType}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + id.hashCode();
    h += (h << 5) + senderType.hashCode();
    h += (h << 5) + replyType.hashCode();
    return h;
  }

  /**
   * Creates an immutable copy of a {@link SocketConnectionIdArgs} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable SocketConnectionIdArgs instance
   */
  public static ImmutableSocketConnectionIdArgs copyOf(SocketConnectionIdArgs instance) {
    if (instance instanceof ImmutableSocketConnectionIdArgs) {
      return (ImmutableSocketConnectionIdArgs) instance;
    }
    return ImmutableSocketConnectionIdArgs.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableSocketConnectionIdArgs ImmutableSocketConnectionIdArgs}.
   * @return A new ImmutableSocketConnectionIdArgs builder
   */
  public static ImmutableSocketConnectionIdArgs.Builder builder() {
    return new ImmutableSocketConnectionIdArgs.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableSocketConnectionIdArgs ImmutableSocketConnectionIdArgs}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_ID = 0x1L;
    private static final long INIT_BIT_SENDER_TYPE = 0x2L;
    private static final long INIT_BIT_REPLY_TYPE = 0x4L;
    private long initBits = 0x7L;

    private @Nullable ConnectionId id;
    private @Nullable SenderType senderType;
    private @Nullable EventType replyType;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code utils.events.SocketEventArguments} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(SocketEventArguments instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    /**
     * Fill a builder with attribute values from the provided {@code utils.events.SocketConnectionIdArgs} instance.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(SocketConnectionIdArgs instance) {
      Objects.requireNonNull(instance, "instance");
      from((Object) instance);
      return this;
    }

    private void from(Object object) {
      if (object instanceof SocketEventArguments) {
        SocketEventArguments instance = (SocketEventArguments) object;
        replyType(instance.getReplyType());
        senderType(instance.getSenderType());
        id(instance.getId());
      }
    }

    /**
     * Initializes the value for the {@link SocketConnectionIdArgs#getId() id} attribute.
     * @param id The value for id 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder id(ConnectionId id) {
      this.id = Objects.requireNonNull(id, "id");
      initBits &= ~INIT_BIT_ID;
      return this;
    }

    /**
     * Initializes the value for the {@link SocketConnectionIdArgs#getSenderType() senderType} attribute.
     * @param senderType The value for senderType 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder senderType(SenderType senderType) {
      this.senderType = Objects.requireNonNull(senderType, "senderType");
      initBits &= ~INIT_BIT_SENDER_TYPE;
      return this;
    }

    /**
     * Initializes the value for the {@link SocketConnectionIdArgs#getReplyType() replyType} attribute.
     * @param replyType The value for replyType 
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder replyType(EventType replyType) {
      this.replyType = Objects.requireNonNull(replyType, "replyType");
      initBits &= ~INIT_BIT_REPLY_TYPE;
      return this;
    }

    /**
     * Builds a new {@link ImmutableSocketConnectionIdArgs ImmutableSocketConnectionIdArgs}.
     * @return An immutable instance of SocketConnectionIdArgs
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableSocketConnectionIdArgs build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableSocketConnectionIdArgs(id, senderType, replyType);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = Lists.newArrayList();
      if ((initBits & INIT_BIT_ID) != 0) attributes.add("id");
      if ((initBits & INIT_BIT_SENDER_TYPE) != 0) attributes.add("senderType");
      if ((initBits & INIT_BIT_REPLY_TYPE) != 0) attributes.add("replyType");
      return "Cannot build SocketConnectionIdArgs, some of required attributes are not set " + attributes;
    }
  }
}
