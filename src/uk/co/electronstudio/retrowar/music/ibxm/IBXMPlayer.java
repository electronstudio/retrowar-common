package uk.co.electronstudio.retrowar.music.ibxm;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioDevice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import static uk.co.electronstudio.retrowar.GlobalsKt.log;

public class IBXMPlayer {
    private static final String[] EXTENSIONS = {"mod", "ft", "s3m", "xm"};
    private static final int SAMPLE_RATE = 48000, FADE_SECONDS = 16, REVERB_MILLIS = 50;


    private Module module;
    private IBXM ibxm;
    private volatile boolean playing;
    private int[] reverbBuf;
    private int interpolation, reverbIdx, reverbLen;
    private int sliderPos, samplePos, duration;
    private Thread playThread;

    public IBXMPlayer() {


    }

//	public void playMod(FileHandle f) throws IOException{
//		loadModule(Gdx.files.absolute("/Volumes/Home/rich/Downloads/woutervl_spcadv.mod").file());
//	}

    public void loadModule(InputStream inputStream) throws IOException {
        //	InputStream inputStream = new FileInputStream( modFile );
        try {
            Module module = new Module(inputStream);
            IBXM ibxm = new IBXM(module, SAMPLE_RATE);
            ibxm.setInterpolation(interpolation);
            duration = ibxm.calculateSongDuration();
            synchronized (this) {
                samplePos = sliderPos = 0;

                String songName = module.songName.trim();

                System.out.println("song: " + songName);

                Vector<String> vector = new Vector<String>();
                Instrument[] instruments = module.instruments;
                for (int idx = 0, len = instruments.length; idx < len; idx++) {
                    String name = instruments[idx].name;
                    if (name.trim().length() > 0)
                        vector.add(String.format("%03d: %s", idx, name));
                    System.out.println("instrument: " + name);
                }

                this.module = module;
                this.ibxm = ibxm;
            }
        } finally {
            inputStream.close();
        }
    }

    public static short twoBytesToShort(byte b1, byte b2) {
        return (short) ((b1 << 8) | (b2 & 0xFF));
    }

    public synchronized void play(float volume) {
        final AudioDevice audioDevice = Gdx.audio.newAudioDevice(SAMPLE_RATE, false);
        audioDevice.setVolume(volume);
        if (ibxm != null) {
            playing = true;
            playThread = new Thread(new Runnable() {
                public void run() {
                    Gdx.app.log("IBXM", "thread start");
                    int[] mixBuf = new int[ibxm.getMixBufferLength()];

                    short[] gBuf = new short[ibxm.getMixBufferLength()];

                    try {


                        long time = System.nanoTime();
                        while (playing) {
                            long elapsed = System.nanoTime() - time;
                            time = System.nanoTime();

                            int count = getAudio(mixBuf);

                            //   Gdx.app.log("IBMX","playing "+count+ " time "+elapsed/1000000+" ");
                            //IBMX: playing 800time 12548322
                            // 55855418

                            int outIdx = 0;
                            for (int mixIdx = 0; mixIdx < count * 2; mixIdx++) {


                                int ampl = mixBuf[mixIdx];
                                if (ampl > 32767) {
                                    ampl = 32767;

                                }
                                if (ampl < -32768) {
                                    ampl = -32768;

                                }


                                outIdx++;

                                int j = mixIdx * 2;
                                byte b1 = (byte) (ampl >> 8);
                                byte b2 = (byte) ampl;
                                gBuf[mixIdx] = twoBytesToShort(b1, b2);
                            }
                            long t = System.nanoTime();
                            audioDevice.writeSamples(gBuf, 0, outIdx);
                            long t2 = System.nanoTime() - t;
                            //   Gdx.app.log("IBMX","latency "+  audioDevice.getLatency()+" writing took "+t2/1000000 );


                        }
                        //	audioLine.drain();
                        Gdx.app.log("IBXM", "thread done");
                    } catch (Exception e) {
                        log(e.toString());
                    } finally {
                        //if( audioLine != null && audioLine.isOpen() ) audioLine.close();
                        if (audioDevice != null) audioDevice.dispose();
                    }
                }
            });
            playThread.start();
        }
    }

    public synchronized void stop() {
        playing = false;
        try {
            if (playThread != null) playThread.join();
        } catch (InterruptedException e) {
        }

    }

    private synchronized void seek(int pos) {
        samplePos = ibxm.seek(pos);
    }

    private synchronized void setInterpolation(int interpolation) {
        this.interpolation = interpolation;
        if (ibxm != null) ibxm.setInterpolation(interpolation);
    }

    private synchronized void setReverb(int millis) {
        reverbLen = ((SAMPLE_RATE * millis) >> 9) & -2;
        reverbBuf = new int[reverbLen];
        reverbIdx = 0;
    }

    private synchronized int getAudio(int[] mixBuf) {
        int count = ibxm.getAudio(mixBuf);
        samplePos += count;
        return count;
    }

//	private synchronized void saveWav( File wavFile, int time, int fade ) throws IOException {
//		stop();
//		seek( 0 );
//		WavInputStream wavInputStream = new WavInputStream( ibxm, time, fade );
//		FileOutputStream fileOutputStream = null;
//		try {
//			fileOutputStream = new FileOutputStream( wavFile );
//			byte[] buf = new byte[ ibxm.getMixBufferLength() * 2 ];
//			int remain = wavInputStream.getBytesRemaining();
//			while( remain > 0 ) {
//				int count = remain > buf.length ? buf.length : remain;
//				count = wavInputStream.read( buf, 0, count );
//				fileOutputStream.write( buf, 0, count );
//				remain -= count;
//			}
//		} finally {
//			if( fileOutputStream != null ) fileOutputStream.close();
//			seek( 0 );
//		}
//	}
//
//	private void reverb( int[] mixBuf, int count ) {
//		/* Simple cross-delay with feedback. */
//		int mixIdx = 0, mixEnd = count << 1;
//		while( mixIdx < mixEnd ) {
//			mixBuf[ mixIdx     ] = ( mixBuf[ mixIdx     ] * 3 + reverbBuf[ reverbIdx + 1 ] ) >> 2;
//			mixBuf[ mixIdx + 1 ] = ( mixBuf[ mixIdx + 1 ] * 3 + reverbBuf[ reverbIdx     ] ) >> 2;
//			reverbBuf[ reverbIdx     ] = mixBuf[ mixIdx ];
//			reverbBuf[ reverbIdx + 1 ] = mixBuf[ mixIdx + 1 ];
//			reverbIdx += 2;
//			if( reverbIdx >= reverbLen ) {
//				reverbIdx = 0;
//			}
//			mixIdx += 2;
//		}
//	}


}
