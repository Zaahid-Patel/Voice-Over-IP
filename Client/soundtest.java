package Client;
import javax.sound.sampled.*;

public class soundtest {
    
    public static void main(String[] args) {
        
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        System.out.println("Mixers:");
        for (int i = 0; i < mixers.length; i++) {
            System.out.println("\t" + mixers[i].getDescription());
        }

        Mixer mixer = AudioSystem.getMixer(mixers[1]);

        Line.Info[] targets = mixer.getTargetLineInfo();
        Line.Info[] sources = mixer.getSourceLineInfo();

        TargetDataLine inLine;
        SourceDataLine outLine;

        try {
            // Target line for reading
            // inLine = (TargetDataLine) mixer.getLine(targets[0]);
            // Source line for writing
            // outLine = (SourceDataLine) mixer.getLine(sources[0]);

            AudioFormat format = new AudioFormat(8000, 16, 1, true, true);
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();

            inLine = AudioSystem.getTargetDataLine(format);
            outLine = AudioSystem.getSourceDataLine(format);

            int len = (int) (frameSize * frameRate * 5);
            byte[] data = new byte[len];
            inLine.open(format);
            outLine.open(format);

            inLine.start();
            inLine.read(data, 0, len);
            inLine.stop();
            inLine.close();
            int max = 0;
            for (int i = 0; i < data.length; i += 2) {
                short temp = data[i];
                temp <<= 8;
                temp += data[i+1];
                if (Math.abs(temp) > max) max = Math.abs(temp);
                System.out.println(temp);
            }
            System.out.println(max);
            outLine.start();
            outLine.write(data, 0, len);
            outLine.stop();
            outLine.close();

            

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(0);
        }



    }

}
