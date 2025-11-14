package musicManagementApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Database {

    private final String USER_FILE = "users.dat";
    private final String SONG_FILE = "songs.dat";

    public ArrayList<User> Users = new ArrayList<>();
    public ArrayList<Song> Songs = new ArrayList<>();

    public boolean isEmpty(String filename) {
        File file = new File(filename);
        return file.length() == 0;
    }

    @SuppressWarnings("unchecked")
    public void loadUsers() {
        File file = new File(USER_FILE);

        try {
            if (file.createNewFile()) {
                Users = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Users = new ArrayList<>();
        }

        if (file.length() == 0) {
            Users = new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            Users = (ArrayList<User>) obj;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            Users = new ArrayList<>();
        }
    }

    public boolean saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(Users);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User readUser(String username) {
        loadUsers();
        for (User user : Users) {
            if (user.username.equals(username)) {
                return user;
            }
        }
        return new User();
    }

    public boolean createUser(String username, String password) {
        User newUser = new User(username, PasswordHash.hashPassword(password), new ArrayList<>(), new ArrayList<>());
        Users.add(newUser);
        return saveUsers();
    }

    public boolean lookUpUsername(String username) {
        if (isEmpty("users.dat")) {
            return false;
        }
        User u = readUser(username);
        return !u.username.equals("");
    }

    public boolean verifyPassword(String username, String password) {
        User u = readUser(username);
        return u.password.equals(PasswordHash.hashPassword(password));
    }

    @SuppressWarnings("unchecked")
    public void loadSongs() {
        File file = new File(SONG_FILE);

        try {
            if (file.createNewFile()) {
                Songs = new ArrayList<>();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Songs = new ArrayList<>();
            return;
        }

        if (file.length() == 0) {
            Songs = new ArrayList<>();
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            Songs = (ArrayList<Song>) obj;
            return;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            Songs = new ArrayList<>();
            return;
        }
    }

    public boolean saveSongs() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SONG_FILE))) {
            out.writeObject(Songs);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Song readSong(int id) {
        loadSongs();
        for (Song song : Songs) {
            if (song.id == id) {
                return song;
            }
        }
        return new Song();
    }

    public boolean lookUpSong(int id) {
        if (isEmpty("songs.dat")) {
            return false;
        }
        Song s = readSong(id);
        return !s.name.equals("");
    }

    public ArrayList<String> readAllSongNames() {
        loadSongs();
        ArrayList<String> songNames = new ArrayList<>();

        for (Song song : Songs) {
            songNames.add(song.id + "). " + song.name + " by " + song.artist);
        }

        return songNames;
    }

    public boolean createSong(String name, String artist, String genre, String path,
            ArrayList<String> tags, int numTimesListened) {
        Song newSong = new Song(Songs.size(), name, artist, genre, path, tags, numTimesListened);
        Songs.add(newSong);
        return saveSongs();
    }

    public ArrayList<Song> songsWithGenre(String genre) {
        loadSongs();
        ArrayList<Song> res = new ArrayList<>();

        for (Song song : Songs) {
            if (song.genre.equals(genre)) {
                res.add(song);
            }
        }
        return res;
    }

    public ArrayList<Song> songsWithArtist(String artist) {
        loadSongs();
        ArrayList<Song> res = new ArrayList<>();

        for (Song song : Songs) {
            if (song.artist.equals(artist)) {
                res.add(song);
            }
        }
        return res;
    }

    public ArrayList<Song> songsWithTags(ArrayList<String> tags) {
        loadSongs();
        ArrayList<Song> res = new ArrayList<>();

        for (Song song : Songs) {
            boolean containsTags = true;
            for (String tag : tags) {
                containsTags = containsTags && song.tags.contains(tag);
            }
            if (containsTags) {
                res.add(song);
            }
        }
        return res;
    }
}
