package com.graphhopper.routing.weighting;

import com.graphhopper.routing.profiles.IntEncodedValue;
import com.graphhopper.routing.util.FlagEncoder;
import com.graphhopper.util.EdgeIteratorState;
import com.graphhopper.util.PMap;

public class CustomNorderstedtWeighting extends FastestWeighting {

    private final double treeFactor;
    private final double litFactor;
    private final double crashFactor;
    private final double siteFactor;
    private IntEncodedValue litEnc;
    private IntEncodedValue treeEnc;
    private IntEncodedValue crashEnc;
    private IntEncodedValue siteEnc;

    public CustomNorderstedtWeighting(FlagEncoder encoder, PMap map) {
        super(encoder, map);

        litEnc = encoder.getIntEncodedValue("lit");
        litFactor = convert(map.getDouble("lit", 1));

        treeEnc = encoder.getIntEncodedValue("tree");
        treeFactor = convert(map.getDouble("tree", 1));

        crashEnc = encoder.getIntEncodedValue("crash");
        crashFactor = convert(map.getDouble("crash", 1));

        siteEnc = encoder.getIntEncodedValue("site");
        siteFactor = convert(map.getDouble("site", 1));
    }

    double convert(double val) {
        //val = Math.min(10, Math.max(-10, val));
        //return 1 + val / 10;
        // Sliders go from 0 to 100 now...
        val = Math.min(100, Math.max(0, val));
        return val / 50;
    }

    double max = 0;

    @Override
    public double calcWeight(EdgeIteratorState edgeState, boolean reverse, int prevOrNextEdgeId) {
        double weight = super.calcWeight(edgeState, reverse, prevOrNextEdgeId);

        // TODO take into account: count/distance and combine with factor
        // crash density is between 0.01 and 0.15  => 1
        // lit density is between 0.1 and 0.4      => 2
        // tree density is between 0.1 and 1.3     => 6
//        double density = edgeState.get(crashEnc) / edgeState.getDistance();
//        if (density > max)
//            max = density;
        double length = edgeState.getDistance();
        double litDensity = edgeState.get(litEnc) / length;
        double treeDensity = edgeState.get(treeEnc) / length;
        double crashDensity = edgeState.get(crashEnc) / length;
        double siteDensity = edgeState.get(siteEnc) / length;

        weight *= (1 + 100.0 * crashDensity * crashFactor);
        weight *= (1 + 10.0 * treeDensity * treeFactor);
        weight *= (1 + 100.0 * siteDensity * siteFactor);
        weight /= (1 + 20.0 * litDensity * litFactor);

/*
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
            */

        return weight;
    }

    @Override
    public String getName() {
        return "norderstedt";
    }
}
