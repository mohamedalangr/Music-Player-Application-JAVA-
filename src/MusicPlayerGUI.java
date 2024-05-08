import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private MusicPlayer musicPlayer;

    private JFileChooser fileChooser;

    private JLabel songTitle,songArtist;

    private JPanel playBackBtns;

    private JSlider playBackSlider;

    public MusicPlayerGUI() {

        super("Music Player");

        setSize(400, 600);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);

        fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new File("src/assets"));

        fileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));



        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

        JLabel songItem = new JLabel(loadImage("src/assets/recorded.png"));
        songItem.setBounds(0, 50, getWidth() - 20, 225);
        add(songItem);

        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        playBackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playBackSlider.setBounds(getWidth() / 2 - 300 / 2, 365, 300, 40);
        playBackSlider.setBackground(null);
        playBackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                JSlider source = (JSlider) e.getSource();

                int frame = source.getValue();

                musicPlayer.setCurrentFrame(frame);

                musicPlayer.setCurrentTimeInMilli((int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                musicPlayer.playCurrentSong();

                enablePauseButtonDisablePlayButton();

            }
        });
        add(playBackSlider);

        addPlayBackBtns();


    }

    private void addToolbar() {
        JToolBar toolbar = new JToolBar();
        toolbar.setBounds(0, 0, getWidth(), 20);

        toolbar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolbar.add(menuBar);

        JMenu songMenue = new JMenu("Song");
        menuBar.add(songMenue);

        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selctedFile = fileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selctedFile != null) {
                    Song song = new Song(selctedFile.getPath());

                    musicPlayer.loadSong(song);

                    updateSongTitleAndArtist(song);

                    updatePlaybackSlider(song);

                    enablePauseButtonDisablePlayButton();


                }
            }
        });
        songMenue.add(loadSong);

        JMenu playListMenue = new JMenu("Playlist");
        menuBar.add(playListMenue);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicPlayListDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playListMenue.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("PlayList", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));

                int result =jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    musicPlayer.stopSong();


                    musicPlayer.loadPlayList(selectedFile);

                }
            }
        });
        playListMenue.add(loadPlaylist);


        add(toolbar);
    }

    private void addPlayBackBtns() {
        playBackBtns = new JPanel(new FlowLayout());
        playBackBtns.setBounds(0, 435, getWidth() - 10, 80);
        playBackBtns.setBackground(null);


        JButton prevButton = new JButton(loadImage("src/assets/back.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.prevSong();
            }
        });
        playBackBtns.add(prevButton);

        JButton playButton = new JButton(loadImage("src/assets/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();

                musicPlayer.playCurrentSong();
            }
        });
        playBackBtns.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/assets/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();

                musicPlayer.pauseSong();
            }
        });
        pauseButton.setVisible(false);
        playBackBtns.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/assets/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.nextSong();
            }
        });
        playBackBtns.add(nextButton);

        add(playBackBtns);


    }

    public void setPlayBackSliderValue(int frame){
        playBackSlider.setValue(frame);

    }

    public void updateSongTitleAndArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());


    }

    public void updatePlaybackSlider(Song song){
        playBackSlider.setMaximum(song.getMp3File().getFrameCount());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        JLabel labelEnding = new JLabel(song.getSongLength());
        labelEnding.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnding.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(),labelEnding);

        playBackSlider.setLabelTable(labelTable);
        playBackSlider.setPaintLabels(true);
    }

    public void enablePauseButtonDisablePlayButton() {
        JButton playButton = (JButton) playBackBtns.getComponent(1);
        JButton pauseButton = (JButton) playBackBtns.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);

        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton() {
        JButton playButton = (JButton) playBackBtns.getComponent(1);
        JButton pauseButton = (JButton) playBackBtns.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);

        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath) {
        try {

            BufferedImage image = ImageIO.read(new File(imagePath));

            return new ImageIcon(image);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


}
