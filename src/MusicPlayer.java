import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {

    private static final Object playSignal = new Object();

    private  MusicPlayerGUI musicPlayerGUI;

    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }

    private ArrayList<Song> playList;

    private int currentPlayListIndex;

    private AdvancedPlayer advancedPlayer;

    private boolean isPuased;

    private boolean songFinished;

    private boolean pressedNext, pressedPrev;

    private int currentFrame;
    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    private int currentTimeInMilli;
    public void setCurrentTimeInMilli(int timeInMilli){
        currentTimeInMilli = timeInMilli;
    }


    public  MusicPlayer(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;

    }

    public void loadSong(Song song){
        currentSong = song;

        playList = null;

        if(!songFinished)
            stopSong();

        if(currentSong != null){

            currentFrame = 0;

            currentTimeInMilli = 0;

            musicPlayerGUI.setPlayBackSliderValue(0);

            playCurrentSong();
        }
    }

    public void loadPlayList(File playListFile){
        playList = new ArrayList<>();

        try{
            FileReader fileReader = new FileReader(playListFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                Song song = new Song(songPath);

                playList.add(song);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        if(playList.size() > 0){
            musicPlayerGUI.setPlayBackSliderValue(0);
            currentTimeInMilli = 0;

            currentSong = playList.get(0);

            currentFrame = 0;

            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            playCurrentSong();
        }
    }





    public void pauseSong(){
        if(advancedPlayer != null){
            isPuased = true;
        }

        stopSong();
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void nextSong(){
        if(playList == null) return;

        if(currentPlayListIndex + 1 >= playList.size() - 1) return;

        pressedNext = true;

        if(!songFinished)
            stopSong();


        currentPlayListIndex++;

        currentSong = playList.get(currentPlayListIndex);

        currentFrame = 0;

        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);


        playCurrentSong();
    }

    public void prevSong(){
        if(playList == null) return;

        if(currentPlayListIndex - 1 < 0) return;

        pressedPrev = true;

        if(!songFinished)
            stopSong();


        currentPlayListIndex--;

        currentSong = playList.get(currentPlayListIndex);

        currentFrame = 0;

        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);


        playCurrentSong();
    }


    public void playCurrentSong(){
        if(currentSong == null) return;

        try{
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer =new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();

            startPlayBackSliderThread();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPuased){

                        synchronized (playSignal){
                            isPuased = false;

                            playSignal.notify();
                        }

                        advancedPlayer.play(currentFrame,Integer.MAX_VALUE);
                    }else{
                        advancedPlayer.play();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
     private void startPlayBackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                if(isPuased){
                    try {

                        synchronized (playSignal){
                            playSignal.wait();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                while(!isPuased && !songFinished && !pressedNext && !pressedPrev){
                    try {
                        currentTimeInMilli++;



                        int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());

                        musicPlayerGUI.setPlayBackSliderValue(calculatedFrame);

                        Thread.sleep(1);
                    }catch(Exception e){
                        e.printStackTrace();
                    }


                }
            }
        }).start();
     }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("playback started");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("playback finished");

        if(isPuased){
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }else{

            if(pressedNext || pressedPrev) return;

            songFinished = true;

            if(playList == null){
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }else{

                if(currentPlayListIndex == playList.size() - 1){

                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                }else{

                    nextSong();
                }
            }
        }
    }
}

