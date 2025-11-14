package musicManagementApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.Console;
import java.io.IOException;

public class menu {

    private static Scanner scanner = new Scanner(System.in);
    private static Console console = System.console();

    private static User currentUser;
    private static Playlist interfacedPlaylist;

    private static Database DB = new Database();
    private static MusicPlayer musicPlayer = new MusicPlayer();

    enum Pages {
        LOGIN,
        WELCOME,
        SIGNUP,
        PLAYLISTMANAGEMENT,
        PLAYLISTINTERFACE,
        LIKEDSONGS,
        HOME,
        EXPLORE,
        EXIT
    }

    // Helpers
    private static boolean keyPressed(char target) throws IOException {
        if (System.in.available() > 0) {
            int key = System.in.read();
            return key == target;
        }
        return false;
    }

    private static String arrayListToNumberedList(ArrayList<String> strings) {
        String list = "";
        for (int i = 0; i < strings.size(); i++) {
            list += (i + ".) " + strings.get(i) + "\n");
        }

        return list;

    }

    // AudioPlayer
    private static void playSong(int songID) {
        Song currentSong = DB.readSong(songID);
        currentUser.incrementHistory(currentSong);

        if (!currentSong.name.equals("")) {
            musicPlayer.play(currentSong.path);
            System.out.println("\n🎵 Playing " + currentSong.name + " by " + currentSong.artist);
            System.out.println("Press [SPACE] and [ENTER] to stop playback.");

            while (musicPlayer.isPlaying()) {
                try {
                    if (keyPressed(' ')) {
                        musicPlayer.stop();
                        System.out.println("Song stopped by user");
                        break;
                    }
                    Thread.sleep(100);
                } catch (Exception e) {
                    System.out.println("Encountered error, exiting playback");
                }

            }
            System.out.println("Song Over");
        } else {
            System.out.println("There is no song of this ID");
        }
    }

    // Main
    public static void main(String[] args) {
        System.out.println("WELCOME TO THE MUSIC MANAGEMENT APP!");
        Pages currentPage = Pages.WELCOME;

        while (currentPage != Pages.EXIT) {
            switch (currentPage) {
                case Pages.WELCOME:
                    currentPage = Welcome();
                    break;
                case Pages.HOME:
                    currentPage = Home();
                    break;
                case Pages.EXPLORE:
                    currentPage = Explore();
                    break;
                case Pages.LOGIN:
                    currentPage = Login();
                    break;
                case Pages.SIGNUP:
                    currentPage = SignUp();
                    break;
                case Pages.PLAYLISTMANAGEMENT:
                    currentPage = playlistManagement();
                    break;
                case Pages.PLAYLISTINTERFACE:
                    currentPage = playlistInterface();
                    break;
                case Pages.LIKEDSONGS:
                    currentPage = likedSongs();
                    break;
                default:
                    continue;
            }
        }
        System.out.println("GOODBYE, SEE YOU AGAIN SOON!");
    }

    // Playlist Interfacing
    private static void playFromStart() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            for (Song song : interfacedPlaylist.songs) {
                playSong(song.id);
            }

            System.out.print("");
            System.out.println("****PLAYBACK OVER****");
            System.out.print("");
            return;

        }
    }

    private static void playFromIndex() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's index in the playlist\n> ");
            int songIndex = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            for (Song song : interfacedPlaylist.songs.subList(songIndex, interfacedPlaylist.songs.size())) {
                playSong(song.id);
            }

            System.out.print("");
            System.out.println("****PLAYBACK OVER****");
            System.out.print("");
            return;

        }
    }

    private static void removeSongAtIndex(ArrayList<Song> songs) {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's index in the playlist\n> ");
            int songIndex = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            if (songIndex >= 0 && songIndex < songs.size()) {
                songs.remove(songIndex);
                if (DB.saveUsers()) {
                    System.out.println("Your song has been removed successfully");
                    return;
                } else {
                    System.out.println("Encountered error, please try again or contact support");
                }
            } else {
                System.out.println("Please enter a valid index");
            }

        }
    }

    private static void changePositionOfSong() {
        while (true) {

            ArrayList<Song> interfacedPlaylistSongs = interfacedPlaylist.songs;

            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's current index in the playlist\n> ");
            int oldIndex = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            if (oldIndex >= 0 && oldIndex < interfacedPlaylistSongs.size()) {
                Song songToReorder = interfacedPlaylistSongs.remove(oldIndex);

                displaySongsInPlaylist(interfacedPlaylist);

                System.out.print("Please enter your song's new index in the playlist()\n> ");
                int newIndex = scanner.nextInt();
                System.out.print("\n");
                scanner.nextLine();
                if (newIndex >= 0 && newIndex < interfacedPlaylistSongs.size()) {

                    interfacedPlaylistSongs.add(newIndex, songToReorder);

                    if (DB.saveUsers()) {
                        return;
                    } else {
                        System.out.println("Encoutnered error, try again, if error persists contact support");
                    }
                } else {
                    System.out.println("Please enter a valid index");
                }
            } else {
                System.out.println("Please enter a valid index");
            }

        }
    }

    private static Pages playlistInterface() {
        System.out.println("");
        System.out.println("****PLAYLIST: " + interfacedPlaylist.name + "****");
        System.out.println("");
        displaySongsInPlaylist(interfacedPlaylist);
        Pages exit = Pages.PLAYLISTINTERFACE;
        while (true) {
            System.out.println("");
            displaySongs();
            System.out.println("");

            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Play from start");
            System.out.println("2. Play from index");
            System.out.println("3. Change position of song");
            System.out.println("4. Add song to playlist");
            System.out.println("5. Remove Song from playlist");
            System.out.println("6. Got to Playlist Management");
            System.out.println("7. Go to home");
            System.out.print("8. Quit\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");

            switch (choice) {
                case 1:
                    playFromStart();
                    break;

                case 2:
                    playFromIndex();
                    break;

                case 3:
                    changePositionOfSong();
                    break;

                case 4:
                    displaySongs();
                    addSongToPlaylist(interfacedPlaylist.name);
                    break;

                case 5:
                    removeSongAtIndex(interfacedPlaylist.songs);
                    break;

                case 6:
                    exit = Pages.PLAYLISTMANAGEMENT;
                    break;

                case 7:
                    exit = Pages.HOME;
                    break;

                case 8:
                    exit = Pages.EXIT;
                    break;

                default:
                    System.out.println("Please enter a number between 1 & 8");
            }
            if (exit != Pages.EXPLORE) {
                break;
            }

        }
        return exit;
    }

    // Welcome
    private static menu.Pages SignUp() {
        while (true) {
            System.out.println("");
            System.out.println("****SIGN UP****");
            System.out.println("");

            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();

            System.out.println("\n");

            if (back.equals("y")) {
                return Pages.WELCOME;
            }

            System.out.print("Please enter a username\n> ");
            String username = scanner.nextLine();
            System.out.print("\n");

            if (!DB.lookUpUsername(username)) {
                while (true) {
                    String password = new String(console.readPassword("Please enter a strong password\n> "));
                    if (DB.createUser(username, password)) {
                        System.out.println("Signed up successfully!");
                        currentUser = DB.readUser(username);
                        System.out.println("Logging in as " + username);
                        return Pages.HOME;
                    } else {
                        System.out.println("This password does not match this username");
                    }
                }
            } else {
                System.out.println("This username is already registered, maybe you meant to Login");
            }
        }
    }

    private static menu.Pages Login() {
        while (true) {
            System.out.println("");
            System.out.println("****Login****");
            System.out.println("");

            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return Pages.WELCOME;
            }

            System.out.print("Please enter your username\n> ");
            String username = scanner.nextLine();
            System.out.print("\n");

            if (DB.lookUpUsername(username)) {
                while (true) {
                    System.out.print("Do you wish to go back? (y/n)\n> ");
                    String back2 = scanner.nextLine();
                    System.out.println("\n");

                    if (back2.equals("y")) {
                        return Pages.LOGIN;
                    }

                    String password = new String(console.readPassword("Please enter your password\n> "));
                    if (DB.verifyPassword(username, password)) {
                        System.out.println("Logged in Sucessfully!");
                        currentUser = DB.readUser(username);
                        return Pages.HOME;
                    } else {
                        System.out.println("This password does not match this username");
                    }
                }
            } else {
                System.out.println("This username is not registered, maybe you meant to sign up?");
            }
        }
    }

    private static menu.Pages Welcome() {
        System.out.println("");
        Pages exit = Pages.WELCOME;
        while (true) {
            System.out.println("");
            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Login");
            System.out.println("2. Sign up");
            System.out.print("3. Quit\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");
            switch (choice) {
                case 1:
                    exit = Login();
                    break;

                case 2:
                    exit = SignUp();
                    break;

                case 3:
                    exit = Pages.EXIT;
                    break;

                default:
                    System.out.println("Please enter a number between 1 & 3");
            }
            if (exit != Pages.WELCOME) {
                break;
            }

        }
        return exit;
    }

    // Playlist Management
    private static void displayMyPlaylists() {
        ArrayList<String> MyPlaylistNames = currentUser.readAllMyPlaylistNames();

        int maxLength = 0;

        for (String name : MyPlaylistNames) {
            int len = name.length();
            if (len > maxLength) {
                maxLength = len;
            }
        }

        for (int i = 0; i < MyPlaylistNames.size(); i++) {
            System.out.printf("%-" + maxLength + "s", MyPlaylistNames.get(i));

            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }

    private static void displaySongsInPlaylist(Playlist playlist) {
        ArrayList<String> songsInPlaylistNames = playlist.listSongs();

        int maxLength = 0;

        for (String name : songsInPlaylistNames) {
            int len = name.length();
            if (len > maxLength) {
                maxLength = len;
            }
        }

        for (int i = 0; i < songsInPlaylistNames.size(); i++) {
            System.out.printf("%-" + maxLength + "s", songsInPlaylistNames.get(i));

            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }

    private static void createPlaylist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your Playlist's name\n> ");
            String playlistName = scanner.nextLine();
            System.out.print("\n");

            if (!currentUser.lookUpPlaylist(playlistName)) {
                currentUser.createPlaylist(new Playlist(playlistName, new ArrayList<>()));

                if (DB.saveUsers()) {
                    System.out.println("Your playlist has been successfully created");
                    return;
                }
            } else {
                System.out.println("A playlist of this name already exists");
            }

        }
    }

    private static void deletePlaylist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your Playlist's index\n> ");
            int playlistIndex = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            currentUser.deletePlaylist(playlistIndex);

            if (DB.saveUsers()) {
                System.out.println("Your playlist has been successfully deleted");
                return;
            }

        }
    }

    private static void renamePlaylist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your Playlist's old name\n> ");
            String oldplaylistName = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your Playlist's new name\n> ");
            String newplaylistName = scanner.nextLine();
            System.out.print("\n");

            if (currentUser.lookUpPlaylist(oldplaylistName)) {
                currentUser.renamePlaylist(oldplaylistName, newplaylistName);

                if (DB.saveUsers()) {
                    System.out.println("Your playlist has been successfully renamed");
                    return;
                }
            } else {
                System.out.println("A playlist of the 'old playlist name' does not exist");
            }

        }
    }

    private static menu.Pages viewPlaylist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return Pages.PLAYLISTMANAGEMENT;
            }

            System.out.print("Please enter your Playlist's name\n> ");
            String name = scanner.nextLine();
            System.out.print("\n");

            if (currentUser.lookUpPlaylist(name)) {
                int index = currentUser.readAllMyPlaylistNames().indexOf(name);
                interfacedPlaylist = currentUser.myPlaylists.get(index);
                return Pages.PLAYLISTINTERFACE;
            } else {
                System.out.println("A playlist of that name does not exist");
            }

        }
    }

    private static void playlistByGenre() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your genre\n> ");
            String genre = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your Playlist's name\n> ");
            String playlistName = scanner.nextLine();
            System.out.print("\n");

            if (!currentUser.lookUpPlaylist(playlistName)) {
                currentUser.createPlaylist(new Playlist(playlistName, DB.songsWithGenre(genre)));

                if (DB.saveUsers()) {
                    System.out.println("Your playlist has been successfully created");
                    return;
                }
            } else {
                System.out.println("A playlist of this name already exists");
            }

        }
    }

    private static void playlistByArtist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your artist\n> ");
            String artist = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your Playlist's name\n> ");
            String playlistName = scanner.nextLine();
            System.out.print("\n");

            if (!currentUser.lookUpPlaylist(playlistName)) {
                currentUser.createPlaylist(new Playlist(playlistName, DB.songsWithArtist(artist)));

                if (DB.saveUsers()) {
                    System.out.println("Your playlist has been successfully created");
                    return;
                }
            } else {
                System.out.println("A playlist of this name already exists");
            }

        }
    }

    private static void playlistByTags() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.println("Enter your tags");
            System.out.println("Only songs having ALL the tags you enter will be added to the playlist");

            ArrayList<String> tags = new ArrayList<>();

            while (true) {
                System.out.print("Please enter your tag (leave empty to stop)\n> ");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    break;
                }

                tags.add(input);
            }

            System.out.print("Please enter your Playlist's name\n> ");
            String playlistName = scanner.nextLine();
            System.out.print("\n");

            if (!currentUser.lookUpPlaylist(playlistName)) {
                currentUser.createPlaylist(new Playlist(playlistName, DB.songsWithTags(tags)));

                if (DB.saveUsers()) {
                    System.out.println("Your playlist has been successfully created");
                    return;
                }
            } else {
                System.out.println("A playlist of this name already exists");
            }

        }
    }

    private static menu.Pages playlistManagement() {
        System.out.println("");
        System.out.println("****MY PLAYLISTS****");
        System.out.println("");
        Pages exit = Pages.PLAYLISTMANAGEMENT;
        while (true) {
            System.out.println("");
            displayMyPlaylists();
            System.out.println("");

            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Create Playlist");
            System.out.println("2. Delete playlist");
            System.out.println("3. Rename Playlist");
            System.out.println("4. View Playlist");
            System.out.println("5. Create Playlist by genre");
            System.out.println("6. Create Playlist by tags");
            System.out.println("7. Create Playlist by artist");
            System.out.println("8. Go back to home");
            System.out.print("9. Quit\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");

            switch (choice) {
                case 1:
                    createPlaylist();
                    break;

                case 2:
                    deletePlaylist();
                    break;

                case 3:
                    renamePlaylist();
                    break;
                case 4:
                    exit = viewPlaylist();
                    break;

                case 5:
                    playlistByGenre();
                    break;

                case 6:
                    playlistByTags();
                    break;

                case 7:
                    playlistByArtist();

                    break;

                case 8:
                    exit = Pages.HOME;
                    break;

                case 9:
                    exit = Pages.EXIT;
                    break;

                default:
                    System.out.println("Please enter a number between 1 & 7");
            }
            if (exit != Pages.PLAYLISTMANAGEMENT) {
                break;
            }

        }
        return exit;
    }

    // Home
    private static menu.Pages likedSongs() {
        interfacedPlaylist = new Playlist("likedSongs", currentUser.likedSongs);
        while (true) {
            System.out.println("");
            System.out.println("****LIKED SONGS****");
            System.out.println("");

            for (int i = 0; i < currentUser.likedSongs.size(); i++) {
                System.out.println(
                        i + ".) " + currentUser.likedSongs.get(i).name + " by " + currentUser.likedSongs.get(i).artist);
            }
            System.out.println("");
            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Play from Start");
            System.out.println("2. Play from index");
            System.out.println("3. Remove Song");
            System.out.print("4. Return to Home\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");
            switch (choice) {
                case 1:
                    playFromStart();
                    break;

                case 2:
                    playFromIndex();
                    break;

                case 3:
                    removeSongAtIndex(currentUser.likedSongs);
                    break;

                case 4:
                    return Pages.HOME;

                default:
                    System.out.println("Please enter a number between 1 & 4");
            }

        }
    }

    private static void changeUsername() {
        System.out.print("Please enter your new Username\n> ");
        String newUsername = scanner.nextLine();
        System.out.print("\n");

        currentUser.username = newUsername;
        DB.saveUsers();
    }

    private static void changePassword() {
        System.out.print("Please enter your new password\n> ");
        String newPassword = scanner.nextLine();
        System.out.print("\n");

        currentUser.username = PasswordHash.hashPassword(newPassword);
        DB.saveUsers();
    }

    private static void top10MostListenedTo() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("");
            System.out.println("****YOUR TOP 10 SONGS****");
            System.out.print("");

            ArrayList<Song> top10Songs = new ArrayList<>(currentUser.history.entrySet()
                    .stream()
                    .sorted(Map.Entry.<Song, Integer>comparingByValue().reversed())
                    .limit(10)
                    .map(Map.Entry::getKey)
                    .toList());
            for (Song song : top10Songs) {
                playSong(song.id);
            }

            System.out.print("");
            System.out.println("****PLAYBACK OVER****");
            System.out.print("");
            return;

        }
    }

    private static void clearHistory() {
        currentUser.clearHistory();
    }

    private static menu.Pages Home() {
        System.out.println("");
        System.out.println("****HOME****");
        System.out.println("");
        Pages exit = Pages.HOME;
        while (true) {
            DB.loadSongs();
            System.out.println("");
            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Discover new songs (Explore Page)");
            System.out.println("2. Liked songs");
            System.out.println("3. Top 10 'most listened to' songs");
            System.out.println("4. Change Username");
            System.out.println("5. Change Password");
            System.out.println("6. Clear History");
            System.out.println("7. Manage your playlists");
            System.out.println("8. Log out");
            System.out.print("9. Quit\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");

            switch (choice) {
                case 1:
                    exit = Pages.EXPLORE;
                    break;

                case 2:
                    exit = Pages.LIKEDSONGS;
                    break;

                case 3:
                    top10MostListenedTo();
                    break;

                case 4:
                    changeUsername();
                    break;

                case 5:
                    changePassword();
                    break;

                case 6:
                    clearHistory();
                    break;

                case 7:
                    exit = Pages.PLAYLISTMANAGEMENT;
                    break;

                case 8:
                    System.out.println("\n****Logging out****\n");
                    exit = Pages.WELCOME;
                    break;

                case 9:
                    exit = Pages.EXIT;
                    break;

                default:
                    System.out.println("Please enter a number between 1 & 9");
            }
            if (exit != Pages.HOME) {
                break;
            }

        }
        return exit;
    }

    // Explore
    private static void displaySongs() {
        if (DB.isEmpty("users.dat")) {
            System.out.println("Error : No songs found");
        }

        ArrayList<String> songNames = DB.readAllSongNames();

        int maxLength = 0;

        for (String name : songNames) {
            int len = name.length();
            if (len > maxLength) {
                maxLength = len;
            }
        }

        maxLength += 2;

        for (int i = 0; i < songNames.size(); i++) {
            System.out.printf("%-" + maxLength + "s", songNames.get(i));

            if ((i + 1) % 3 == 0) {
                System.out.println();
            }
        }

        System.out.println();
    }

    private static void addSongToDB() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's name\n> ");
            String songName = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your song's path\n> ");
            String path = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your song's artist\n> ");
            String artist = scanner.nextLine();
            System.out.print("\n");

            System.out.print("Please enter your song's genre\n> ");
            String genre = scanner.nextLine();
            System.out.print("\n");

            ArrayList<String> tags = new ArrayList<>();

            while (true) {
                System.out.print("Please enter your tag (leave empty to stop)\n> ");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    break;
                }

                tags.add(input);
            }

            if (DB.createSong(songName, artist, genre, path, tags, 0)) {
                System.out.println("Your song has been successfully added");
                return;
            }

        }
    }

    private static void addSongToPlaylist() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's id\n> ");
            int songID = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            System.out.print("Please enter your playlist's name\n> ");
            String playlistName = scanner.nextLine();
            System.out.print("\n");

            if (currentUser.lookUpPlaylist(playlistName)) {
                currentUser.addSongToPlaylist(playlistName, DB.readSong(songID));

                if (DB.saveUsers()) {
                    System.out.println("Your song has been successfully added to that playlist");
                    return;
                }
            } else {
                System.out.println("A playlist of the that name does not exist");
            }

            return;

        }
    }

    private static void addSongToPlaylist(String playlistName) {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's id\n> ");
            int songID = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            if (currentUser.lookUpPlaylist(playlistName)) {
                currentUser.addSongToPlaylist(playlistName, DB.readSong(songID));

                if (DB.saveUsers()) {
                    System.out.println("Your song has been successfully added to that playlist");
                    return;
                }
            } else {
                System.out.println("A playlist of the that name does not exist");
            }

            return;

        }
    }

    private static void updateSongInfo() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's id\n> ");
            int songID = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            if (DB.lookUpSong(songID)) {
                Song currentSong = DB.readSong(songID);
                System.out.println("a)Song name: " + currentSong.name);
                System.out.println("b)Song artist: " + currentSong.artist);
                System.out.println("c)Song genre: " + currentSong.genre);
                System.out.println("d)Song path: " + currentSong.path);
                System.out.println("e)Song tags:\n" + arrayListToNumberedList(currentSong.tags));

                System.out.print("Enter the letter [eg. 'a'] of the field you wish to modify\n> ");
                String field = scanner.nextLine();
                System.out.println("\n");

                List<String> letters = new ArrayList<>(List.of("a", "b", "c", "d"));

                ArrayList<String> valueTags = new ArrayList<>();
                String value = "";

                if (letters.contains(field.toLowerCase())) {
                    System.out.print("Please enter your value\n> ");
                    value = scanner.nextLine();
                    System.out.print("\n");

                } else {

                    while (true) {
                        System.out.print("Please enter your tags (leave empty to stop)\n> ");
                        String input = scanner.nextLine();

                        if (input.isEmpty()) {
                            break;
                        }

                        valueTags.add(input);
                    }
                }

                switch (field) {
                    case "a":
                        currentSong.name = value;
                        break;
                    case "b":
                        currentSong.artist = value;
                        break;
                    case "c":
                        currentSong.genre = value;
                        break;
                    case "d":
                        currentSong.path = value;
                        break;
                    case "e":
                        currentSong.tags = valueTags;
                        break;

                    default:
                        System.out.println("Please enter a letter between a & f");
                }
                if (DB.saveSongs()) {
                    return;
                } else {
                    System.out.println("Encoutnered error, try again, if error persists contact support");
                }
            }
        }

    }

    private static void playSongByID() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's id\n> ");
            int songID = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            playSong(songID);

            return;

        }
    }

    private static void likeSong() {
        while (true) {
            System.out.print("Do you wish to go back? (y/n)\n> ");
            String back = scanner.nextLine();
            System.out.println("\n");

            if (back.equals("y")) {
                return;
            }

            System.out.print("Please enter your song's id\n> ");
            int songID = scanner.nextInt();
            System.out.print("\n");
            scanner.nextLine();

            if (!currentUser.lookUpLikedSong(songID)) {
                currentUser.addSongToLikedSongs(DB.readSong(songID));
                if (DB.saveUsers()) {
                    System.out.println("Your song has been successfully added to Liked Songs ");
                    return;
                }
            } else {
                System.out.println("This song is already in Liked Songs");
            }

        }
    }

    private static menu.Pages Explore() {
        System.out.println("");
        System.out.println("****EXPLORE****");
        System.out.println("");
        Pages exit = Pages.EXPLORE;
        while (true) {
            System.out.println("");
            displaySongs();
            System.out.println("");

            System.out.println("Please enter the number of the option you wish to choose");
            System.out.println("1. Add song to playlist");
            System.out.println("2. Like Song");
            System.out.println("3. Add songs to database");
            System.out.println("4. Update song information");
            System.out.println("5. Play song by ID");
            System.out.println("6. Go to home");
            System.out.print("7. Quit\n> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.print("\n");

            switch (choice) {
                case 1:
                    addSongToPlaylist();
                    break;

                case 2:
                    likeSong();
                    break;

                case 3:
                    addSongToDB();
                    break;

                case 4:
                    updateSongInfo();
                    break;

                case 5:
                    playSongByID();
                    break;

                case 6:
                    exit = Pages.HOME;
                    break;

                case 7:
                    exit = Pages.EXIT;
                    break;

                default:
                    System.out.println("Please enter a number between 1 & 7");
            }
            if (exit != Pages.EXPLORE) {
                break;
            }

        }
        return exit;
    }

}