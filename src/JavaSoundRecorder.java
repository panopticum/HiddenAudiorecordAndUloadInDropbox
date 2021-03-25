import com.dropbox.core.v2.DbxClientV2;
import javax.sound.sampled.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JavaSoundRecorder {

    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    TargetDataLine line;
    DataLine.Info info;
    DbxClientV2 client;
    AudioFormat format;

    public JavaSoundRecorder(DbxClientV2 dbxClient) {
        client = dbxClient;
        format = getAudioFormat();
        info = new DataLine.Info(TargetDataLine.class, format);
    }

    //Sound settings
    AudioFormat getAudioFormat() {
        float sampleRate = 32000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void recordAudio(int milliseconds)
    {
        //Create file with current date in name
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss" , Locale.ENGLISH);
        Date date = new Date();
        String now = sdf.format(date.getTime());
        String pathToFile = "C:/Temp/" + now + ".wav";
        File file = new File(pathToFile);
        start(file);
        finish(file, milliseconds);
    }
 
    void start(File file)
    {
        Thread thread = new Thread(() -> {
            try {
                // checks if system supports the data line
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Line not supported");
                    System.exit(0);
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();   // start capturing
                AudioInputStream ais = new AudioInputStream(line);
                AudioSystem.write(ais, fileType, file);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        thread.start();
    }
 
    void finish(File file, int milliseconds) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            line.stop();
            line.close();
            recordAudio(milliseconds);

            try {
                //upload file to Dropbox
                InputStream in = new FileInputStream(file);
                client.files().uploadBuilder("/" + file.getName()).uploadAndFinish(in);
                //delete file after uploading
                in.close();
                file.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}