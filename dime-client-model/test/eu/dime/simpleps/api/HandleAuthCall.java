/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api;

import eu.dime.model.StaticTestData;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.web.HttpConstants;
import sit.web.ServiceEndpointHelper;
import sit.web.WebRequest;

/**
 *
 * !ATTENTION - this is only a dummy implementation returning always the same
 * fix userid
 *
 * @author simon
 */
public class HandleAuthCall extends sit.web.ServiceEndpoint {

    private int identityId = 1;

    // endpoint should be /dime-communications/web/access/auth/

    //TODO adapt to new path /dime/rest/{said}/user/...
    public HandleAuthCall(String endpointName) {
        super(endpointName, true);
    }

    @Override
    public byte[] handleCall(WebRequest wr) {
        wr.fname = ServiceEndpointHelper.replaceBackSlashes(wr.fname);
        Logger.getLogger(HandleUserCalls.class.getName()).log(Level.INFO, "handling call:" + wr.httpCommand + " " + wr.fname);

        String postPath = ServiceEndpointHelper.getSubPath(wr.fname, endpointName);
        String[] pathParts = postPath.split("/");

        if ((wr.httpCommand.equals(HttpConstants.HTTP_COMMAND_GET)) && ("@me".equals(pathParts[pathParts.length - 1]))) {
            try {

                //if a parameter changeId is given then the id is changed to the specified number
                String changeParam = ServiceEndpointHelper.getURLParamForKeyFromWR(wr, "changeId");
                if (changeParam != null) {
                    try {
                        identityId = Integer.parseInt(changeParam);
                        if (identityId >= StaticTestData.SIMPLE_PS_SAIDS.length) {
                            return ("unsupported id > " + StaticTestData.SIMPLE_PS_SAIDS.length).getBytes();
                        }
                        return StaticTestData.SIMPLE_PS_SAIDS[identityId].getBytes();
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(HandleAuthCall.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);

                        return ex.getMessage().getBytes();
                    }
                } else {
                    return StaticTestData.SIMPLE_PS_SAIDS[identityId].getBytes();
                }


            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(HandleAuthCall.class.getName()).log(Level.SEVERE, null, ex);
            }



        }
        return "Request not supported, yet".getBytes();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
