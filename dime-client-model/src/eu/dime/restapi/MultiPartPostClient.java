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

package eu.dime.restapi;

import eu.dime.model.ModelConfiguration;
import eu.dime.model.ModelHelper;
import eu.dime.model.file.CrawlData;
import java.io.File;
import java.io.IOException;
import sit.web.client.HttpHelper;

public class MultiPartPostClient {
 
    private final ModelConfiguration configuration;

    public MultiPartPostClient(ModelConfiguration configuration) {
        this.configuration = configuration;
    }

    public void uploadFile(File file) throws IOException {
        new HttpHelper().postMultiPartContainer(configuration.hostname, configuration.port, ModelHelper.getPathForFileUpload(configuration.mainSAID), new CrawlData(file).getMPC(), configuration.useHTTPS, configuration.token);
    }
    
}
