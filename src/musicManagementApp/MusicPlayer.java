package musicManagementApp;

import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Basic MP3 player using JLayer.
 * 
 * Supports:
 * - play(filePath)
 * - stop()
 * 
 * This player is terminal-friendly and does not require any GUI or JavaFX.
 * 
 * Usage example:
 * MusicPlayer player = new MusicPlayer();
 * player.play("song.mp3");
 * // ...
 * player.stop();
 */
public class MusicPlayer {

    private Player player;
    private Thread playThread;
    private volatile boolean isPlaying = false;

    /**
     * Play an MP3 file.
     * If another track is playing, it will be stopped first.
     */
    public synchronized void play(String filePath) {
        stop(); // stop any previous playback

        try {
            InputStream inputStream = new FileInputStream(filePath);
            player = new Player(inputStream);

            playThread = new Thread(() -> {
                try {
                    isPlaying = true;
                    System.out.println("🎵 Now playing: " + filePath);
                    player.play(); // Blocks until playback completes
                } catch (Exception e) {
                    System.err.println("Playback error: " + e.getMessage());
                } finally {
                    isPlaying = false;
                }
            });

            playThread.setDaemon(true);
            playThread.start();

        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Stop the currently playing track (if any).
     */
    public synchronized void stop() {
        if (isPlaying && player != null) {
            try {
                player.close();
                System.out.println("⏹️ Playback stopped.");
            } catch (Exception ignored) {
            }
        }

        isPlaying = false;
        player = null;

        if (playThread != null) {
            try {
                playThread.join(100);
            } catch (InterruptedException ignored) {
            }
            playThread = null;
        }
    }

    /**
     * Check if something is currently playing.
     */
    public boolean isPlaying() {
        return isPlaying;
    }
}
