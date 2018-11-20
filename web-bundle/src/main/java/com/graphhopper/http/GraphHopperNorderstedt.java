package com.graphhopper.http;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphhopper.jackson.Jackson;
import com.graphhopper.json.geo.JsonFeature;
import com.graphhopper.json.geo.JsonFeatureCollection;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.profiles.IntEncodedValue;
import com.graphhopper.routing.util.*;
import com.graphhopper.routing.weighting.CustomNorderstedtWeighting;
import com.graphhopper.routing.weighting.Weighting;
import com.graphhopper.storage.Graph;
import com.graphhopper.storage.StorableProperties;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.QueryResult;
import com.graphhopper.util.CmdArgs;
import com.graphhopper.util.EdgeIteratorState;
import org.locationtech.jts.geom.Coordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class GraphHopperNorderstedt extends GraphHopperOSM {

    private final String litLocation;
    private final String treeLocation;
    private final String crashLocation;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final IntEncodedValue litEnc;
    private final IntEncodedValue treeEnc;
    private final IntEncodedValue crashEnc;
    private final ObjectMapper mapper = Jackson.newObjectMapper();

    public GraphHopperNorderstedt(CmdArgs args) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        litLocation = args.get("lit.location", "");
        treeLocation = args.get("tree.location", "");
        crashLocation = args.get("crash.location", "");
        EncodingManager.Builder builder = new EncodingManager.Builder(8);
        builder.add(treeEnc = new IntEncodedValue("tree", 9, 0, false));
        builder.add(litEnc = new IntEncodedValue("lit", 6, 0, false));
        builder.add(crashEnc = new IntEncodedValue("crash", 6, 0, false));
        builder.add(new FootFlagEncoder());
        setEncodingManager(builder.build());
    }

    @Override
    public Weighting createWeighting(HintsMap hintsMap, FlagEncoder encoder, Graph graph) {
        if (hintsMap.getWeighting().equals("norderstedt"))
            return new CustomNorderstedtWeighting(encoder, hintsMap);

        return super.createWeighting(hintsMap, encoder, graph);
    }

    @Override
    public void postProcessing() {
        // DO CSV import
        initLocationIndex();

        importFile("crashes", new File(crashLocation), crashEnc);
        importFile("trees", new File(treeLocation), treeEnc);
        importFile("lits", new File(litLocation), litEnc);

        // skip for now super.postProcessing();
    }

    public void importFile(String name, File file, IntEncodedValue enc) {
        StorableProperties props = getGraphHopperStorage().getProperties();
        String storageKey = "files." + name + ".preprocessing";
        String preprocessing = props.get(storageKey);
        if (!preprocessing.isEmpty()) {
            logger.info("Already preprocessed " + name);
            return;
        }

        LocationIndex index = getLocationIndex();
        try {
            JsonFeatureCollection coll = mapper.readValue(file, JsonFeatureCollection.class);
            int invalid = 0;
            for (JsonFeature feature : coll.getFeatures()) {
                Coordinate coord = feature.getGeometry().getCoordinate();
                QueryResult qr = index.findClosest(coord.y, coord.x, EdgeFilter.ALL_EDGES);
                // skip items too far away from the city
                if (qr.getQueryDistance() > 20) {
                    invalid++;
                    continue;
                }
                EdgeIteratorState edge = qr.getClosestEdge();
                // default is zero
                edge.set(enc, edge.get(enc) + 1);
            }
            props.put(storageKey, "done").flush();
            logger.info(name + " loaded " + coll.getFeatures().size() + " (invalid " + invalid + ")");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
