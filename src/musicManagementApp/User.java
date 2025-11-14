package musicManagementApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;

public class User implements Serializable {
    public String username;
    public String password;
    public ArrayList<Song> likedSongs;
    public ArrayList<Playlist> myPlaylists;
    public HashMap<Song, Integer> history = new HashMap<>();

    public User(String username, String password, ArrayList<Song> likedSongs, ArrayList<Playlist> myPlaylists) {
        this.username = username;
        this.password = password;
        this.likedSongs = likedSongs;
        this.myPlaylists = myPlaylists;
    }

    public User() {
        this("", "", new ArrayList<>(), new ArrayList<>());
    }

    public boolean lookUpLikedSong(int id) {
        for (Song song : this.likedSongs) {
            if (song.id == id) {
                return true;
            }
        }
        return false;
    }

    public boolean lookUpPlaylist(String name) {
        for (Playlist playlist : this.myPlaylists) {
            if (playlist.name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void addSongToLikedSongs(Song newSong) {
        this.likedSongs.add(newSong);
    }

    public void createPlaylist(Playlist newPlaylist) {
        this.myPlaylists.add(newPlaylist);
    }

    public void deletePlaylist(int index) {
        this.myPlaylists.remove(index);
    }

    public void renamePlaylist(String oldName, String newName) {
        for (Playlist playlist : this.myPlaylists) {
            if (playlist.name.equals(oldName)) {
                playlist.name = newName;
            }
        }
    }

    public void addSongToPlaylist(String playlistName, Song newSong) {
        for (Playlist playlist : this.myPlaylists) {
            if (playlist.name.equals(playlistName)) {
                playlist.addSong(newSong);
            }
        }
    }

    public ArrayList<String> readAllMyPlaylistNames() {
        ArrayList<String> myPlaylistNames = new ArrayList<>();

        for (int i = 0; i < this.myPlaylists.size(); i++) {
            myPlaylistNames.add(i + "). " + this.myPlaylists.get(i).name);
        }

        return myPlaylistNames;
    }

    public void incrementHistory(Song song) {
        history.put(song, history.getOrDefault(song, 0) + 1);
    }

    public void clearHistory() {
        history = new HashMap<>();
    }
}
