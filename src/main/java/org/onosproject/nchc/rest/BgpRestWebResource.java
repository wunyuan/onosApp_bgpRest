/*
 * Copyright 2017-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.nchc.rest;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.rest.AbstractWebResource;
import org.onosproject.routing.bgp.BgpInfoService;
import org.onosproject.routing.bgp.BgpSession;
import org.onosproject.utils.Comparators;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

//import static org.onlab.util.Tools.nullIsNotFound;

/**
 * NCHC BGP Rest web resource.
 */
@Path("nchc")
public class BgpRestWebResource extends AbstractWebResource {



 //   @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
  //  protected IntentService intentService;

 //   private final String intentType = "org.onosproject.net.intent.MultiPointToSinglePointIntent";

    /**
     * Get all functions and its descriptions.
     *
     * @return 200 OK
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsage() {
        ObjectNode node = mapper().createObjectNode();
        node.put("Get app list", "/bgpRest/nchc/apps");
        node.put("Get device list", "/bgpRest/nchc/devices");
        node.put("Set custom data", "/bgpRest/nchc/{id}/{data}");
        node.put("Get SdnIp usage", "/bgpRest/nchc/sdnip/");

      /*  intentService = getService(IntentService.class);
        for (IntentData data : intentService.getIntentData()) {
            if (data.intent().getClass().getName().equals(intentType)) {
                System.out.println("installable size:" + data.installables().size());
            }

        }*/


        return ok(node).build();
    }


    /**
     * Get app list.
     *
     * @return 200 OK
     */
    @GET
    @Path("apps")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getApplicationList() {
        // ObjectNode node = mapper().createObjectNode().put("3333333", "gooooooood");

        ObjectNode node = mapper().createObjectNode();
        CoreService service = get(CoreService.class);
        List<ApplicationId> ids = new ArrayList(service.getAppIds());
        Collections.sort(ids, Comparators.APP_ID_COMPARATOR);

        if (node.size() != 0) {
            node.removeAll();
        }

        for (ApplicationId id : ids) {
            node.put(Short.toString(id.id()).toString(), id.name());
        }

        return ok(node).build();
    }


    /**
     * Get device list.
     *
     * @return 200 OK
     */
    @GET
    @Path("devices")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevices() {

        Iterable<Device> devices = get(DeviceService.class).getDevices();

        return ok(encodeArray(Device.class, "devices", devices)).build();
    }


    /**
     * Set custom data.
     *
     * @return 200 OK
     */
    @GET
    @Path("{id}/{data}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDevicePorts(@PathParam("id") String id,
                                   @PathParam("data") String data) {
        ObjectNode node = mapper().createObjectNode();
        node.put(id, data);
        return ok(node).build();
    }


    /**
     * Get SDNIP Usage.
     *
     * @return 200 OK
     */
    @GET
    @Path("sdnip")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSdnIpUsageIndex() {

        ObjectNode node = mapper().createObjectNode();
        node = getSdnIpUsage();
        return ok(node).build();
    }


    /**
     * Get sdnip bgp information.
     *@param id command type, ie:bgproutes or bgpneighbor
     * @return 200 OK
     */
    @GET
    @Path("sdnip/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSdnIpBgpInfo(@PathParam("id") String id) {

        if (id.matches("[a-zA-Z]]")) {
            id = id.toLowerCase();
        }

        ObjectNode node = mapper().createObjectNode();
        SdnIpBgpJson sdnIpBgpJson = new SdnIpBgpJson(mapper());
        BgpInfoService service = get(BgpInfoService.class);

        switch (id) {
            case "bgproutes":
                node = sdnIpBgpJson.parser("routes4", service.getBgpRoutes4(), null);
                node = sdnIpBgpJson.parser("routes6", service.getBgpRoutes6(), null);
                break;
            case "bgpneighbor":
                Collection<BgpSession> bgpSessions = service.getBgpSessions();
                node = sdnIpBgpJson.parser(id, null, bgpSessions);
                break;
            case "bgpspeakers":
                NetworkConfigService configService = get(NetworkConfigService.class);
                CoreService coreService = get(CoreService.class);
                node = sdnIpBgpJson.bgpSpeakerParser(id, configService, coreService);
                break;
            default :
                node = getSdnIpUsage();
        }
        return ok(node).build();
    }

    /**
     * Get SDNIP Usage.
     *
     * @return 200 OK
     */
    private ObjectNode getSdnIpUsage() {
        ObjectNode node = mapper().createObjectNode();
        node.put("Get bgp routes", "/bgpRest/nchc/sdnip/bgproutes");
        node.put("Get device list", "/bgpRest/nchc/sdnip/bgpneighbor");
        node.put("Set custom data", "/bgpRest/nchc/sdnip/bgpspeakers");
        return node;
    }

}

