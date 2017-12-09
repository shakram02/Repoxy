package utils.events;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Objects;
import javax.annotation.CheckReturnValue;
import javax.annotation.Generated;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import utils.SenderType;

/**
 * Immutable implementation of {@link ControllerFailureArgs}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableControllerFailureArgs.builder()}.
 */
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Generated({"Immutables.generator", "ControllerFailureArgs"})
@Immutable
@CheckReturnValue
public final class ImmutableControllerFailureArgs extends ControllerFailureArgs {

  private ImmutableControllerFailureArgs(ImmutableControllerFailureArgs.Builder builder) {
  }

  /**
   * This instance is equal to all instances of {@code ImmutableControllerFailureArgs} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableControllerFailureArgs
        && equalTo((ImmutableControllerFailureArgs) another);
  }

  private boolean equalTo(ImmutableControllerFailureArgs another) {
    return true;
  }

  /**
   * Returns a constant hash code value.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    return -2118228654;
  }

  private volatile long lazyInitBitmap;

  private static final long SENDER_TYPE_LAZY_INIT_BIT = 0x1L;

  private SenderType senderType;

  /**
   * {@inheritDoc}
   * <p>
   * Returns a lazily initialized value of the {@link ControllerFailureArgs#getSenderType() senderType} attribute.
   * Initialized once and only once and stored for subsequent access with proper synchronization.
   * @return A lazily initialized value of the {@code l.name} attribute
   */
  @Override
  public SenderType getSenderType() {
    if ((lazyInitBitmap & SENDER_TYPE_LAZY_INIT_BIT) == 0) {
      synchronized (this) {
        if ((lazyInitBitmap & SENDER_TYPE_LAZY_INIT_BIT) == 0) {
          this.senderType = Objects.requireNonNull(super.getSenderType(), "senderType");
          lazyInitBitmap |= SENDER_TYPE_LAZY_INIT_BIT;
        }
      }
    }
    return senderType;
  }

  private static final long REPLY_TYPE_LAZY_INIT_BIT = 0x2L;

  private EventType replyType;

  /**
   * {@inheritDoc}
   * <p>
   * Returns a lazily initialized value of the {@link ControllerFailureArgs#getReplyType() replyType} attribute.
   * Initialized once and only once and stored for subsequent access with proper synchronization.
   * @return A lazily initialized value of the {@code l.name} attribute
   */
  @Override
  public EventType getReplyType() {
    if ((lazyInitBitmap & REPLY_TYPE_LAZY_INIT_BIT) == 0) {
      synchronized (this) {
        if ((lazyInitBitmap & REPLY_TYPE_LAZY_INIT_BIT) == 0) {
          this.replyType = Objects.requireNonNull(super.getReplyType(), "replyType");
          lazyInitBitmap |= REPLY_TYPE_LAZY_INIT_BIT;
        }
      }
    }
    return replyType;
  }

  /**
   * Creates an immutable copy of a {@link ControllerFailureArgs} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ControllerFailureArgs instance
   */
  public static ImmutableControllerFailureArgs copyOf(ControllerFailureArgs instance) {
    if (instance instanceof ImmutableControllerFailureArgs) {
      return (ImmutableControllerFailureArgs) instance;
    }
    return ImmutableControllerFailureArgs.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableControllerFailureArgs ImmutableControllerFailureArgs}.
   * @return A new ImmutableControllerFailureArgs builder
   */
  public static ImmutableControllerFailureArgs.Builder builder() {
    return new ImmutableControllerFailureArgs.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableControllerFailureArgs ImmutableControllerFailureArgs}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @NotThreadSafe
  public static final class Builder {

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ControllerFailureArgs} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    @CanIgnoreReturnValue 
    public final Builder from(ControllerFailureArgs instance) {
      Objects.requireNonNull(instance, "instance");
      return this;
    }

    /**
     * Builds a new {@link ImmutableControllerFailureArgs ImmutableControllerFailureArgs}.
     * @return An immutable instance of ControllerFailureArgs
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableControllerFailureArgs build() {
      return new ImmutableControllerFailureArgs(this);
    }
  }
}
