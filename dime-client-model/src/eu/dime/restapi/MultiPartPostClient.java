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
