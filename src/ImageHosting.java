import com.google.firebase.*;
import com.google.firebase.auth.*;

import java.io.*;

/**
 * Created by penguin on 17. 6. 19.
 */
public class ImageHosting {
    ImageHosting(){}

    public void initImageServer() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("simplechattingapp-609fb-firebase-adminsdk-o0543-66f460047f.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://simplechattingapp-609fb.firebaseio.com")
                .build();

        FirebaseApp.initializeApp(options);

        
    }
}
