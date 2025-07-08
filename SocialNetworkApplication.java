import java. time.LocalDateTime; 
import java.time.format.DateTimeFormatter; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 
import java.util.Map; 
import java.util.Scanner; 
import java.util.UUID; 
import java.util.stream.Collectors; 


class Comment {
    
    private final String commentId;
    private final User author; 
    private final String text; 
    private final LocalDateTime timestamp;

    
    public Comment(User author, String text) {
        this.commentId = UUID.randomUUID().toString(); 
        this.author = author;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    
    public User getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    
    @Override
    public String toString() {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "    > " + author.getUsername() + " (" + timestamp.format(formatter) + "): " + text;
    }
}


class Post {
    private final String postId;
    private final User author;
    private final String content;
    private final LocalDateTime timestamp;
    private final List<Comment> comments; 
    
    private int likes;

    
    public Post(User author, String content) {
        this.postId = UUID.randomUUID().toString();
        this.author = author;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.comments = new ArrayList<>(); 
        this.likes = 0; 
    }
    
    
    public String getPostId() {
        return postId;
    }

    public User getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public List<Comment> getComments() {
        return comments;
    }
    
    // --- Other methods ---
    /**
     * Adds a new comment to this post's list of comments.
     */
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    /**
     * --- NEW FEATURE: LIKES ---
     * Increments the like counter for this post.
     */
    public void addLike() {
        this.likes++;
    }

    /**
     * Creates a nice, multi-line string to display the post and its comments.
     * --- UPDATED --- to show the Post ID and Like count.
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // Using a StringBuilder is more efficient for building strings piece by piece.
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("--------------------------------------------------\n");
        stringBuilder.append("| Post by: ").append(author.getUsername()).append(" (").append(timestamp.format(formatter)).append(")\n");
        stringBuilder.append("| Post ID: ").append(postId).append("\n"); // Show the ID
        stringBuilder.append("|-------------------------------------------------\n");
        stringBuilder.append("| ").append(content).append("\n");
        stringBuilder.append("|-------------------------------------------------\n");
        stringBuilder.append("| Likes: ").append(likes).append(" | Comments: ").append(comments.size()).append("\n");
        stringBuilder.append("|-------------------------------------------------\n");


        if (comments.isEmpty()) {
            stringBuilder.append("|   No comments yet.\n");
        } else {
            // Loop through each comment in the list and add it to our string.
            for (Comment comment : comments) {
                stringBuilder.append(comment.toString()).append("\n");
            }
        }
        stringBuilder.append("--------------------------------------------------");
        return stringBuilder.toString();
    }
}

/**
 * Represents a user in the social network.
 * Each user has a username, profile info, a list of friends, and a list of posts they've made.
 */
class User {
    private final String username;
    private String profileInfo;
    private final List<User> friends; // A list of other User objects.
    private final List<Post> posts; // A list of Post objects this user has created.

    public User(String username) {
        this.username = username;
        this.profileInfo = "No profile information set.";
        this.friends = new ArrayList<>(); // Start with an empty friends list.
        this.posts = new ArrayList<>(); // Start with an empty list of posts.
    }

    // --- Getter methods ---
    public String getUsername() {
        return username;
    }

    public List<User> getFriends() {
        return friends;
    }
    
    public List<Post> getPosts() {
        return posts;
    }
    
    // --- Setter and other methods ---
    public void setProfileInfo(String profileInfo) {
        this.profileInfo = profileInfo;
    }

    public void addFriend(User friend) {
        // Avoid adding the same friend twice.
        if (!friends.contains(friend)) {
            friends.add(friend);
        }
    }

    public void createPost(Post post) {
        posts.add(post);
    }
    
    /**
     * Builds a string that represents the user's profile page.
     */
    public String getProfileView() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("========================================\n");
        stringBuilder.append("          User Profile: ").append(username).append("\n");
        stringBuilder.append("========================================\n");
        stringBuilder.append("Info: ").append(profileInfo).append("\n");
        stringBuilder.append("Friends (").append(friends.size()).append("): ");
        if (friends.isEmpty()) {
            stringBuilder.append("No friends yet.\n");
        } else {
            // This is a more advanced way to join the names of all friends into one string.
            String friendNames = friends.stream().map(User::getUsername).collect(Collectors.joining(", "));
            stringBuilder.append(friendNames).append("\n");
        }
        stringBuilder.append("----------------------------------------\n");
        stringBuilder.append("Posts by ").append(username).append(":\n");
        if (posts.isEmpty()) {
            stringBuilder.append("No posts yet.\n");
        } else {
            // Loop backwards to show the newest posts first.
            for (int i = posts.size() - 1; i >= 0; i--) {
                stringBuilder.append(posts.get(i).toString()).append("\n");
            }
        }
        stringBuilder.append("========================================");
        return stringBuilder.toString();
    }
    
    /**
     * The equals() and hashCode() methods are important for checking if two User objects are the "same".
     * Here, we decide that two Users are the same if they have the same username.
     * This is crucial for methods like `friends.contains()`.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }
}


// --- SERVICE CLASS (The "Engine" of the Social Network) ---
/**
 * This class acts as the central manager for our social network.
 * It holds all the data (users, posts) and contains the logic for interactions.
 * In a real-world application, this logic would run on a backend server.
 */
class SocialNetwork {
    // We use Maps to store our data. A Map lets us look up data with a "key".
    // Here, we use the username (a String) to quickly find a User object.
    private final Map<String, User> users;
    // We also store all posts in a map, using the post's unique ID as the key.
    private final Map<String, Post> posts;

    public SocialNetwork() {
        this.users = new HashMap<>();
        this.posts = new HashMap<>();
    }

    public User createUser(String username) {
        // Check if the username is already taken.
        if (users.containsKey(username)) {
            System.out.println("!> Error: Username '" + username + "' already exists.");
            return null; // Return nothing to indicate failure.
        }
        User newUser = new User(username);
        users.put(username, newUser); // Add the new user to our map.
        return newUser;
    }

    public User getUser(String username) {
        return users.get(username); // Look up a user by their name.
    }
    
    public Post getPost(String postId) {
        return posts.get(postId); // Look up a post by its ID.
    }

    public void addFriend(String username1, String username2) {
        User user1 = getUser(username1);
        User user2 = getUser(username2);

        // Make sure both users exist and they are not the same person.
        if (user1 != null && user2 != null && !username1.equals(username2)) {
            user1.addFriend(user2);
            user2.addFriend(user1); // Friendships are mutual, so add both ways.
            System.out.println("*> " + username1 + " and " + username2 + " are now friends!");
        } else {
            System.out.println("!> Error: One or both users not found, or you tried to friend yourself.");
        }
    }

    public Post createPost(String username, String content) {
        User author = getUser(username);
        if (author != null) {
            Post newPost = new Post(author, content);
            author.createPost(newPost); // Add the post to the user's personal list of posts.
            posts.put(newPost.getPostId(), newPost); // Also save it in the global posts map.
            return newPost;
        } else {
            System.out.println("!> Error: User '" + username + "' not found. Cannot create post.");
            return null;
        }
    }
    
    public void addComment(String postId, String authorUsername, String text) {
        Post post = getPost(postId); // Find the post by its ID.
        User author = getUser(authorUsername);
        
        if (post != null && author != null) {
            Comment newComment = new Comment(author, text);
            post.addComment(newComment);
        } else {
            System.out.println("!> Error: Post or user not found. Cannot add comment.");
        }
    }
    
    /**
     * --- NEW FEATURE: LIKES ---
     * Finds a post by its ID and adds a like to it.
     */
    public void likePost(String postId) {
        Post post = getPost(postId);
        if (post != null) {
            post.addLike();
            System.out.println("*> You liked " + post.getAuthor().getUsername() + "'s post!");
        } else {
            System.out.println("!> Error: Post with ID '" + postId + "' not found.");
        }
    }

    /**
     * Generates a "news feed" for a user.
     * The feed contains the user's own posts plus all posts from their friends.
     */
    public List<Post> getNewsFeed(String username) {
        User user = getUser(username);
        if (user == null) {
            System.out.println("!> Error: User not found.");
            return new ArrayList<>(); // Return an empty list.
        }

        List<Post> newsFeed = new ArrayList<>(user.getPosts()); // Start with the user's own posts.
        // Go through each friend and add all of their posts to the feed.
        for (User friend : user.getFriends()) {
            newsFeed.addAll(friend.getPosts());
        }

        // Sort the collected posts by their ID (which is based on creation time) to show newest first.
        newsFeed.sort((p1, p2) -> p2.getPostId().compareTo(p1.getPostId()));
        return newsFeed;
    }
    
    public List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet()); // Get a list of all registered usernames.
    }
}


// --- MAIN APPLICATION CLASS (The User Interface) ---
/**
 * This is the main class that runs our application.
 * It provides a command-line interface (CLI) for a human user to interact
 * with the SocialNetwork simulation.
 */
public class SocialNetworkApplication {

    // 'static' means these variables belong to the class itself, not to any one object.
    // There will only be one 'network' and one 'loggedInUser' for the whole application.
    private static final SocialNetwork network = new SocialNetwork();
    private static User loggedInUser = null; // At the start, no one is logged in.

    /**
     * The `main` method is the entry point of any Java application.
     * Execution starts here.
     */
    public static void main(String[] args) {
        setupInitialState(); // First, create some sample data.
        runCli(); // Then, start the user interface loop.
    }

    /**
     * Populates the network with some dummy users, friends, and posts
     * so the application isn't empty when it starts.
     */
    private static void setupInitialState() {
        System.out.println("Initializing Social Network Simulation...");
        // Create users
        network.createUser("alice");
        network.createUser("bob");
        network.createUser("charlie");
        network.createUser("diana");

        // Set profile info
        network.getUser("alice").setProfileInfo("Software developer and cat lover.");
        network.getUser("bob").setProfileInfo("Musician and coffee enthusiast.");

        // Establish friendships
        network.addFriend("alice", "bob");
        network.addFriend("alice", "charlie");

        // Create posts
        Post p1 = network.createPost("alice", "Just finished a great new feature for my project! #coding");
        Post p2 = network.createPost("bob", "My new song is out now! Check it out on SoundWave.");
        network.createPost("charlie", "Thinking about learning Java. Any tips?");
        network.createPost("alice", "My cat is being extra cute today.");
        
        // Add comments
        network.addComment(p1.getPostId(), "bob", "Awesome! Can't wait to see it.");
        network.addComment(p2.getPostId(), "alice", "Listening now! It's fantastic!");
        network.addComment(p2.getPostId(), "charlie", "Great track, Bob!");
        System.out.println("Initialization complete. Welcome!");
    }

    /**
     * Runs the main command-line interface loop.
     * It will keep running until the user types 'exit'.
     */
    private static void runCli() {
        Scanner scanner = new Scanner(System.in); // Create a tool to read user input.
        
        // A "while (true)" loop runs forever until we explicitly 'break' out of it.
        while (true) {
            // The program behaves differently depending on whether a user is logged in or not.
            if (loggedInUser == null) {
                displayLoginMenu();
                handleLogin(scanner);
            } else {
                displayMainMenu();
                String command = scanner.nextLine().trim(); // Read the user's choice.
                
                if (command.equalsIgnoreCase("logout")) {
                    loggedInUser = null; // Log the user out.
                    System.out.println("You have been logged out.");
                    continue; // Skip the rest of the loop and start from the top.
                }
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Thank you for using the Social Network. Goodbye!");
                    break; // Exit the 'while' loop, which will end the program.
                }
                handleCommand(command, scanner);
            }
        }
        scanner.close(); // Good practice to close the scanner when we're done with it.
    }
    
    private static void displayLoginMenu() {
        System.out.println("\n===== LOGIN =====");
        System.out.println("Available users: " + String.join(", ", network.getAllUsernames()));
        System.out.print("Enter username to log in (or 'exit'): ");
    }
    
    private static void handleLogin(Scanner scanner) {
        String username = scanner.nextLine().trim();
        if (username.equalsIgnoreCase("exit")) {
            System.out.println("Goodbye!");
            System.exit(0); // A forceful way to stop the entire program immediately.
        }
        User user = network.getUser(username); // Try to find the user.
        if (user != null) {
            loggedInUser = user; // If found, set them as the logged-in user.
            System.out.println("\nWelcome, " + loggedInUser.getUsername() + "!");
        } else {
            System.out.println("!> User not found. Please try again.");
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n===== Main Menu (Logged in as: " + loggedInUser.getUsername() + ") =====");
        System.out.println("1. View My News Feed");
        System.out.println("2. View My Profile");
        System.out.println("3. View Someone Else's Profile");
        System.out.println("4. Create a Post");
        System.out.println("5. Add a Friend");
        // --- NEW MENU OPTION ---
        System.out.println("6. Like a Post");
        System.out.println("logout - Log out and return to login screen");
        System.out.println("exit - Exit the application");
        System.out.print("Enter your choice: ");
    }

    /**
     * Takes the user's menu choice and calls the appropriate method.
     */
    private static void handleCommand(String choice, Scanner scanner) {
        // A 'switch' statement is a clean way to handle multiple fixed choices.
        switch (choice) {
            case "1":
                viewNewsFeed();
                break;
            case "2":
                viewProfile(loggedInUser.getUsername());
                break;
            case "3":
                System.out.print("Enter username to view their profile: ");
                String userToView = scanner.nextLine().trim();
                viewProfile(userToView);
                break;
            case "4":
                createPost(scanner);
                break;
            case "5":
                addFriend(scanner);
                break;
            // --- NEW MENU LOGIC ---
            case "6":
                likePost(scanner);
                break;
            default:
                System.out.println("!> Invalid choice. Please try again.");
                break;
        }
    }
    
    private static void viewNewsFeed() {
        System.out.println("\n===== Your News Feed =====");
        List<Post> feed = network.getNewsFeed(loggedInUser.getUsername());
        if (feed.isEmpty()) {
            System.out.println("Your feed is empty. Add friends or create a post!");
        } else {
            for (Post post : feed) {
                System.out.println(post);
            }
        }
    }
    
    private static void viewProfile(String username) {
        User user = network.getUser(username);
        if (user != null) {
            System.out.println(user.getProfileView());
        } else {
            System.out.println("!> User '" + username + "' not found.");
        }
    }
    
    private static void createPost(Scanner scanner) {
        System.out.println("What's on your mind? (Enter your post content below)");
        String content = scanner.nextLine();
        network.createPost(loggedInUser.getUsername(), content);
        System.out.println("*> Post created successfully!");
    }
    
    private static void addFriend(Scanner scanner) {
        System.out.println("Available users: " + String.join(", ", network.getAllUsernames()));
        System.out.print("Enter username to add as a friend: ");
        String friendName = scanner.nextLine().trim();
        network.addFriend(loggedInUser.getUsername(), friendName);
    }
    
    /**
     * --- NEW INTERACTIVE FEATURE ---
     * Handles the logic for liking a post.
     */
    private static void likePost(Scanner scanner) {
        System.out.println("--- Like a Post ---");
        System.out.println("Here are the recent posts from your feed:");
        viewNewsFeed(); // Show the feed so the user can see post IDs.
        System.out.print("\nEnter the ID of the post you want to like: ");
        String postId = scanner.nextLine().trim();
        
        // Check if the post is actually in the user's feed to prevent liking a random post
        List<Post> feed = network.getNewsFeed(loggedInUser.getUsername());
        boolean postInFeed = feed.stream().anyMatch(p -> p.getPostId().equals(postId));
        
        if (postInFeed) {
            network.likePost(postId);
        } else {
            System.out.println("!> Error: That post ID is not in your feed or does not exist.");
        }
    }
}
