import javax.swing.*;

public class MusicPlayerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MusicPlayerGUI().setVisible(true);

//                Song song = new Song("src/assets/QadiatAmAhmed.mp3");
//                System.out.println(song.getSongTitle());
//                System.out.println(song.getSongArtist());
           }
        });
    }


}
