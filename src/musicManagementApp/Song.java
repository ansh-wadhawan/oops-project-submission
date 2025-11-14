package musicManagementApp;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    public int id;
    public String name;
    public String artist;
    public String genre;
    public String path;
    public ArrayList<String> tags;

    public Song(int id, String name, String artist, String genre, String path, ArrayList<String> tags,
            int numTimesListened) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.genre = genre;
        this.path = path;
        this.tags = (tags != null) ? tags : new ArrayList<>();
    }

    public Song() {
        this(0, "", "", "", "", new ArrayList<>(), 0);
    }

}