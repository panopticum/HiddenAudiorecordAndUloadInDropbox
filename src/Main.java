import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;


public class Main
{
    public static void main (String[] args) {
        //Token for dropbox account access
        String ACCESS_TOKEN = "*****"; // hidden /private/

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        JavaSoundRecorder recorder = new JavaSoundRecorder(client);
        //Duration of audiorecord in ms
        recorder.recordAudio(10000);
    }
}
