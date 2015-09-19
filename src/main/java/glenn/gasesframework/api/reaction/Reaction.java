package glenn.gasesframework.api.reaction;

/**
 * Abstract base class for reactions.
 * Reactions are bound to one or several gas types, and must be registered for gas types in order for them to be applied.
 * Reactions use A and B to differentiate the components of the reaction. A is always the gas the reaction is registered to, while B varies with the type.
 * Two main subtypes exist:
 * <ul>
 * <li>{@link glenn.gasesframework.api.reaction.GasReaction GasReaction} (A=gas/B=gas)</li>
 * <li>{@link glenn.gasesframework.api.reaction.BlockReaction BlockReaction} (A=gas/B=block).</li>
 * </ul>
 */
public abstract class Reaction
{
}
