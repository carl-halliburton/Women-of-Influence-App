package nz.co.hawkefilms.womenofinfluence;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import static com.dropbox.core.v2.files.FolderMetadata.newBuilder;

/**
 * Description:
 * Creates the connection and authentication with the Dropbox account
 */
public class DropboxClient {

    public static DbxClientV2 getClient(String ACCESS_TOKEN) {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Pearls/v3.0").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }
}
