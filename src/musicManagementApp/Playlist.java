package musicManagementApp;

import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    public String name;
    public ArrayList<Song> songs;

    public Playlist(String name, ArrayList<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    public Playlist() {
        this("", new ArrayList<>());
    }

    public void addSong(Song newSong) {
        this.songs.add(newSong);
    }

    public ArrayList<String> listSongs() {
        ArrayList<String> songNames = new ArrayList<>();

        for (int i = 0; i < this.songs.size(); i++) {
            songNames.add(i + "). " + this.songs.get(i).name);
        }

        return songNames;
    }

    public void removeSong(int index) {
        if (index >= 0 && index < (int) songs.size()) {
            songs.remove(index);
        }
    }

    public void addSongs(ArrayList<Song> newSongs) {
        songs.addAll(newSongs);
    }

    public int noOfSongs() {
        return songs.size();
    }
}