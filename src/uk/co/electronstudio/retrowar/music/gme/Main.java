package uk.co.electronstudio.retrowar.music.gme;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by rich on 13/04/2017.
 */
public class Main {

    static PlayerWithUpdate player;

    public static void main(String[] args) {
        try {

            player = new PlayerWithUpdate();


            // Optionally start playing file immediately
            String url = "http://blargg.8bitalley.com/parodius/gme_java/vgm/sonic.vgz";
            // url = "http://iterations.org/files/music/chiptunes/archives/m/mega-man-2-nes-%5BNSF-ID2018%5D.nsf";
            //  url = "http://blargg.8bitalley.com/parodius/gme_java/vgm/Phantasy_Star.zip";
            String path = "";
            //"Phantasy_Star/Intro.vgz";
            // player.add(url, path, 1, "", -1, true);

            player.add(url);
            //  player.playIndex(0);

            // byte[] data = player.readFile(url, path);
            //InputStream in = DataReader.openHttp(url);
            File file = new File("/home/richard/retrogame/core/assets/test.nsf");
            // FileHandle fileHandle = Gdx.files.internal("sonic.vgz");
            InputStream in = new FileInputStream(file);

            //in = DataReader.openGZIP(in);
            byte[] data = DataReader.loadData(in);

            String name = url.toUpperCase();


            MusicEmu emu = player.createEmu(file.getName().toUpperCase());
            if (emu == null)
                return; // TODO: throw exception?
            int actualSampleRate = emu.setSampleRate(player.sampleRate);
            emu.loadFile(data);

            // now that new emulator is ready, replace old one
            player.setEmu(emu, actualSampleRate);
            player.loadedUrl = url;
            player.loadedPath = path;

            player.pause();
            if (player.line != null)
                player.line.flush();
            emu.startTrack(0);
            //	emu.setFade( time, 6 );
            player.play();
//
//            VGMPlayer player = new VGMPlayer();
//            player.loadFile(url, path);
            //     player.startTrack(0,-1 );
//            player.play();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
