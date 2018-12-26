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
package org.onosproject.nchc;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import org.apache.felix.scr.annotations.Service;

/**
 * NCHC BGP REST Interface component.
 */
@Component(immediate = true)
public class BgpRestComponent {

   // @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
  //  protected IntentService intentService;

  //  private final IntentListener intentListener = new Listener();

    private final Logger log = LoggerFactory.getLogger(getClass());

  //  private final String intentTypeString = "org.onosproject.net.intent.MultiPointToSinglePointIntent";

    @Activate
    protected void activate() {


        log.info("Started");
     //   intentService.addListener(intentListener);
    }

    @Deactivate
    protected void deactivate() {

        log.info("Stopped");
    //    intentService.removeListener(intentListener);
    }

    /*still keeping coding.
    public class Listener implements IntentListener {
        @Override
        public void event(IntentEvent event) {
            //System.out.println(event.subject().getClass().getName());
            //event.subject().getClass().getName().
            String intentType = event.subject().getClass().getName();
            switch (event.type()) {
                case INSTALL_REQ :
                    if (intentType.equals(intentTypeString)) {
                        System.out.println(intentType);
                        System.out.println(event.type());
                    }
                    break;
                default :
                    break;
            }
        }
    }*/

}
