package de.teamlapen.faction.api.factions.refinement;

import java.util.Set;

/**
 * Collection of active player refinements.
 */
public interface IRefinementSet {

    Set<IRefinement> getRefinements();

    boolean hasRefinement(IRefinement refinement);

    void addRefinement(IRefinement refinement);

    void removeRefinement(IRefinement refinement);
}
