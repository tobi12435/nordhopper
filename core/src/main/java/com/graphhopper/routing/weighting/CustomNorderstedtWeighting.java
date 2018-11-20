package com.graphhopper.routing.weighting;

import com.graphhopper.routing.profiles.IntEncodedValue;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

public class CustomNorderstedtWeighting extends FastestWeighting {

    private final double treeFactor;
    private final double litFactor;
    private final double crashFactor;
    private IntEncodedValue litEnc;
    private IntEncodedValue treeEnc;
    private IntEncodedValue crashEnc;

    public CustomNorderstedtWeighting(FlagEncoder encoder, PMap map) {
        super(encoder, map);

        litEnc = encoder.getIntEncodedValue("lit");
        litFactor = map.getDouble("lit.factor", 1.2);

        treeEnc = encoder.getIntEncodedValue("tree");
        treeFactor = map.getDouble("tree.factor", 1.2);

        crashEnc = encoder.getIntEncodedValue("crash");
        crashFactor = map.getDouble("crash.factor", 1.2);
    }

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        double weight = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);

        // TODO take into account: count/distance and combine with factor
        if (edgeState.get(litEnc) == 0)
            weight = weight * litFactor;
        else
            weight = weight / litFactor;

        if (edgeState.get(treeEnc) == 0)
            weight = weight * treeFactor;
        else
            weight = weight / treeFactor;

        if (edgeState.get(crashEnc) == 0)
            weight = weight * crashFactor;
        else
            weight = weight / crashFactor;

        return weight;
    }

    @Override
    public String getName() {
        return "norderstedt";
    }
}
