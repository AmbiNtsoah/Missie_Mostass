package frames;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoiceRecorder {
    private static final int RECORD_TIME = 60000; // 1 minute
    private TargetDataLine line;
    private Thread recordingThread;
    private boolean isRecording = false;

    public void startRecording(String filePath) {
        File audioFile = new File(filePath);
        AudioFormat format = new AudioFormat(16000, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }

        try {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            isRecording = true;

            recordingThread = new Thread(() -> {
                try {
                    System.out.println("Recording started...");
                    AudioInputStream ais = new AudioInputStream(line);
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            recordingThread.start();
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    public void stopRecording() {
        if (line != null) {
            line.stop();
            line.close();
            isRecording = false;
            System.out.println("Recording stopped.");
        }

        if (recordingThread != null) {
            try {
                recordingThread.join();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isRecording() {
        return isRecording;
    }

    public static String generateFilePath() {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return "recorded_audio_" + timestamp + ".wav";
    }
}