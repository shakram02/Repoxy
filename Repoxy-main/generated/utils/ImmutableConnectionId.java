package utils;

import java.util.Objects;
import javax.annotation.Generated;

/**
 * Immutable implementation of {@link ConnectionId}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableConnectionId.builder()}.
 */
@SuppressWarnings({"all"})
@Generated({"Immutables.generator", "ConnectionId"})
public final class ImmutableConnectionId extends ConnectionId {

  private ImmutableConnectionId(ImmutableConnectionId.Builder builder) {
  }


  private static ImmutableConnectionId validate(ImmutableConnectionId instance) {
    instance.setId();
    return instance;
  }

  /**
   * Creates an immutable copy of a {@link ConnectionId} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable ConnectionId instance
   */
  public static ImmutableConnectionId copyOf(ConnectionId instance) {
    if (instance instanceof ImmutableConnectionId) {
      return (ImmutableConnectionId) instance;
    }
    return ImmutableConnectionId.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableConnectionId ImmutableConnectionId}.
   * @return A new ImmutableConnectionId builder
   */
  public static ImmutableConnectionId.Builder builder() {
    return new ImmutableConnectionId.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableConnectionId ImmutableConnectionId}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  public static final class Builder {

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code ConnectionId} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(ConnectionId instance) {
      Objects.requireNonNull(instance, "instance");
      return this;
    }

    /**
     * Builds a new {@link ImmutableConnectionId ImmutableConnectionId}.
     * @return An immutable instance of ConnectionId
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableConnectionId build() {
      return ImmutableConnectionId.validate(new ImmutableConnectionId(this));
    }
  }
}
