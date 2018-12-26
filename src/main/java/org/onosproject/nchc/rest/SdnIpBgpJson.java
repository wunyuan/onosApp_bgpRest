package org.onosproject.nchc.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import org.onlab.packet.IpAddress;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.routing.RoutingService;
import org.onosproject.routing.bgp.BgpConstants;
import org.onosproject.routing.bgp.BgpRouteEntry;
import org.onosproject.routing.bgp.BgpSession;
import org.onosproject.routing.config.BgpConfig;
import org.onosproject.utils.Comparators;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * related to SDN IP Bgp command date.
 * Created by nchclab on 7/26/17.
 */
public class SdnIpBgpJson {

    private static final Comparator<BgpConfig.BgpSpeakerConfig> SPEAKERS_COMPARATOR = (s1, s2) ->
            Comparators.CONNECT_POINT_COMPARATOR.compare(s1.connectPoint(), s2.connectPoint());

    private ObjectNode node;
    private String type;
    private ObjectMapper mapper;;



    public SdnIpBgpJson(ObjectMapper mapper) {
        node = mapper.createObjectNode();
    };



    /**
     * parser bgp-neighbor and bgp-routes command data to JSON.
     *
     * @param bgpRoutes the bgp routes to print
     * @param bgpSessions the bgpSesstion to print
     * @return return json data to restWeb
     */
    protected ObjectNode parser(String type, Collection<BgpRouteEntry> bgpRoutes,
                                Collection<BgpSession> bgpSessions) {

        this.mapper = new ObjectMapper();
        ArrayNode jsonArray = mapper.createArrayNode();
        this.type = type;

        node = type.equals("bgpneighbor") ? bgpNeighborJson(jsonArray, bgpSessions)
                : bgpRouteJson(jsonArray, bgpRoutes);

        return node;
    }

    /**
     * put bgpNeighbor data in Json node.
     *
     * @param jsonArray store all json node
     * @param bgpSessions the bgpSesstion to print
     * @return return json data to restWeb
     */
    private ObjectNode bgpNeighborJson(ArrayNode jsonArray, Collection<BgpSession> bgpSessions) {

        for (BgpSession bgpSession : bgpSessions) {
          //  result.add(json(mapper, bgpSession));

            ObjectNode jsonNode = mapper.createObjectNode();

            jsonNode.put("remoteAddress", bgpSession.remoteInfo().address().toString());
            jsonNode.put("remoteBgpVersion", bgpSession.remoteInfo().bgpVersion());
            jsonNode.put("remoteAs", bgpSession.remoteInfo().asNumber());
            jsonNode.put("remoteAs4", bgpSession.remoteInfo().as4Number());
            jsonNode.put("remoteHoldtime", bgpSession.remoteInfo().holdtime());
            jsonNode.put("remoteBgpId", bgpSession.remoteInfo().bgpId().toString());
            jsonNode.put("remoteIpv4Unicast", bgpSession.remoteInfo().ipv4Unicast());
            jsonNode.put("remoteIpv4Multicast", bgpSession.remoteInfo().ipv4Multicast());
            jsonNode.put("remoteIpv6Unicast", bgpSession.remoteInfo().ipv6Unicast());
            jsonNode.put("remoteIpv6Multicast", bgpSession.remoteInfo().ipv6Multicast());

            jsonNode.put("localAddress", bgpSession.localInfo().address().toString());
            jsonNode.put("localBgpVersion", bgpSession.localInfo().bgpVersion());
            jsonNode.put("localAs", bgpSession.localInfo().asNumber());
            jsonNode.put("localAs4", bgpSession.localInfo().as4Number());
            jsonNode.put("localHoldtime", bgpSession.localInfo().holdtime());
            jsonNode.put("localBgpId", bgpSession.localInfo().bgpId().toString());
            jsonNode.put("localIpv4Unicast", bgpSession.localInfo().ipv4Unicast());
            jsonNode.put("localIpv4Multicast", bgpSession.localInfo().ipv4Multicast());
            jsonNode.put("localIpv6Unicast", bgpSession.localInfo().ipv6Unicast());
            jsonNode.put("localIpv6Multicast", bgpSession.localInfo().ipv6Multicast());

            jsonArray.add(jsonNode);

        }

        node.set(type, jsonArray);
        return node;

    }


    /**
     * put bgp route data in Json node.
     *
     * @param jsonArray store all json node
     * @param bgpRoutes the bgp route to print
     * @return return json data to restWeb
     */
    private ObjectNode bgpRouteJson(ArrayNode jsonArray, Collection<BgpRouteEntry> bgpRoutes) {

        for (BgpRouteEntry bgpRoute : bgpRoutes) {
            ObjectNode jsonNode = mapper.createObjectNode();
            jsonNode.put("prefix", bgpRoute.prefix().toString());
            jsonNode.put("nextHop", bgpRoute.nextHop().toString());
            jsonNode.put("bgpId", bgpRoute.getBgpSession().remoteInfo().bgpId().toString());
            jsonNode.put("origin", BgpConstants.Update.Origin.typeToString(bgpRoute.getOrigin()));
           // jsonNode.set("asPath", asPathjsonMasker(mapper, bgpRoute.getAsPath()));
            jsonNode.set("asPath", asPathjsonMasker(bgpRoute.getAsPath()));
            jsonNode.put("localPref", bgpRoute.getLocalPref());
            jsonNode.put("multiExitDisc", bgpRoute.getMultiExitDisc());
            jsonArray.add(jsonNode);
        }

        node.set(type, jsonArray);
        return node;

    }

    /**
     * Formats the AS Path as a string that can be shown on the CLI (copy from BgpRoutesListCommand).
     *
     * @param asPath the AS Path to format
     * @return the AS Path as a string
     */
    private JsonNode asPathjsonMasker(BgpRouteEntry.AsPath asPath) {
        ObjectNode result = mapper.createObjectNode();
        ArrayNode pathSegmentsJson = mapper.createArrayNode();
        for (BgpRouteEntry.PathSegment pathSegment : asPath.getPathSegments()) {
            ObjectNode pathSegmentJson = mapper.createObjectNode();
            pathSegmentJson.put("type",
                                BgpConstants.Update.AsPath.typeToString(pathSegment.getType()));
            ArrayNode segmentAsNumbersJson = mapper.createArrayNode();
            for (Long asNumber : pathSegment.getSegmentAsNumbers()) {
                segmentAsNumbersJson.add(asNumber);
            }
            pathSegmentJson.set("segmentAsNumbers", segmentAsNumbersJson);
            pathSegmentsJson.add(pathSegmentJson);
        }
        result.set("pathSegments", pathSegmentsJson);

        return result;
    }


    /**
     * Parser bgp-speakers command data to Json.
     *
     * @param type identify data type, ie:bgproutes or bgpneighbor
     * @return return json data to restWeb
     */
    protected ObjectNode bgpSpeakerParser(String type, NetworkConfigService configService,
                                          CoreService coreService) {

        ApplicationId appId = coreService.getAppId(RoutingService.ROUTER_APP_ID);

        BgpConfig config = configService.getConfig(appId, BgpConfig.class);
        if (config == null) {
            node.put("errorReason", "No speakers configured");
            return node;
        }

        List<BgpConfig.BgpSpeakerConfig> bgpSpeakers =
                Lists.newArrayList(config.bgpSpeakers());

        Collections.sort(bgpSpeakers, SPEAKERS_COMPARATOR);

        if (config.bgpSpeakers().isEmpty()) {
            node.put("errorReason", "No speakers configured");
        } else {

            this.mapper = new ObjectMapper();
            ArrayNode jsonArray = mapper.createArrayNode();

            for (BgpConfig.BgpSpeakerConfig speaker : bgpSpeakers) {

                ArrayNode peerJsonArray = mapper.createArrayNode();
                ObjectNode jsonNode = mapper.createObjectNode();

                for (IpAddress ip : speaker.peers()) {
                    ObjectNode peerJsonNode = mapper.createObjectNode();
                    peerJsonNode.put("ip", ip.getIp4Address().toString());
                    peerJsonArray.add(peerJsonNode);
                }

                if (speaker.name().isPresent()) {

                    jsonNode.put("speakerName", speaker.name().get());
                    jsonNode.put("connectPoint_deviceId", speaker.connectPoint().deviceId().toString());
                    jsonNode.put("connectPoint_port", speaker.connectPoint().port().toString());
                    jsonNode.put("vlan", speaker.vlan().toString());
                    jsonNode.set("peers", peerJsonArray);
                } else {

                    jsonNode.put("connectPoint_deviceId", speaker.connectPoint().deviceId().toString());
                    jsonNode.put("connectPoint_port", speaker.connectPoint().port().toString());
                    jsonNode.put("vlan", speaker.vlan().toString());
                    jsonNode.set("peers", peerJsonArray);

                }
                jsonArray.add(jsonNode);
            }
            node.set(type, jsonArray);
        }
        return node;
    }
}
