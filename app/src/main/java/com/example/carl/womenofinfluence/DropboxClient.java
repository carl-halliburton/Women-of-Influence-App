package com.example.carl.womenofinfluence;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import static com.dropbox.core.v2.files.FolderMetadata.newBuilder;

/**
 * Created by nick- on 8/09/2016.
 */
public class DropboxClient {

    public static DbxClientV2 getClient(String ACCESS_TOKEN) {
        // Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("Pearls/v3.0").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);
        return client;
    }
}
