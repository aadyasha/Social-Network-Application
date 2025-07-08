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
    
    
    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    /**
     * ---  LIKES ---
     */
    public void addLike() {
        this.likes++;
    }

    
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


class User {
    private final String username;
    private String profileInfo;
    private final List<User> friends; 
    private final List<Post> posts; 

    public User(String username) {
        this.username = username;
        this.profileInfo = "No profile information set.";
        this.friends = new ArrayList<>(); 
        this.posts = new ArrayList<>(); 
    }


    public String getUsername() {
        return username;
    }

    public List<User> getFriends() {
        return friends;
    }
    
    public List<Post> getPosts() {
        return posts;
    }
    
    
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
            
            for (int i = posts.size() - 1; i >= 0; i--) {
                stringBuilder.append(posts.get(i).toString()).append("\n");
            }
        }
        stringBuilder.append("========================================");
        return stringBuilder.toString();
    }
    
   
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



class SocialNetwork {
    
    private final Map<String, User> users;
    
    private final Map<String, Post> posts;

    public SocialNetwork() {
        this.users = new HashMap<>();
        this.posts = new HashMap<>();
    }

    public User createUser(String username) {
        
        if (users.containsKey(username)) {
            System.out.println("!> Error: Username '" + username + "' already exists.");
            return null; 
        }
        User newUser = new User(username);
        users.put(username, newUser); 
        return newUser;
    }

    public User getUser(String username) {
        return users.get(username); 
    }
    
    public Post getPost(String postId) {
        return posts.get(postId); 
    }

    public void addFriend(String username1, String username2) {
        User user1 = getUser(username1);
        User user2 = getUser(username2);

        // Making sure both users exist and are not the same person.
        if (user1 != null && user2 != null && !username1.equals(username2)) {
            user1.addFriend(user2);
            user2.addFriend(user1); // Friendships are mutual
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
        Post post = getPost(postId); 
        User author = getUser(authorUsername);
        
        if (post != null && author != null) {
            Comment newComment = new Comment(author, text);
            post.addComment(newComment);
        } else {
            System.out.println("!> Error: Post or user not found. Cannot add comment.");
        }
    }
    

    public void likePost(String postId) {
        Post post = getPost(postId);
        if (post != null) {
            post.addLike();
            System.out.println("*> You liked " + post.getAuthor().getUsername() + "'s post!");
        } else {
            System.out.println("!> Error: Post with ID '" + postId + "' not found.");
        }
    }

    
    public List<Post> getNewsFeed(String username) {
        User user = getUser(username);
        if (user == null) {
            System.out.println("!> Error: User not found.");
            return new ArrayList<>(); 
        }

        List<Post> newsFeed = new ArrayList<>(user.getPosts()); 
       
        for (User friend : user.getFriends()) {
            newsFeed.addAll(friend.getPosts());
        }

        
        newsFeed.sort((p1, p2) -> p2.getPostId().compareTo(p1.getPostId()));
        return newsFeed;
    }
    
    public List<String> getAllUsernames() {
        return new ArrayList<>(users.keySet()); 
    }
}



public class SocialNetworkApplication {

    
    private static final SocialNetwork network = new SocialNetwork();
    private static User loggedInUser = null; 

    
    public static void main(String[] args) {
        setupInitialState(); 
        runCli(); .
    }

    
    private static void setupInitialState() {
        System.out.println("Initializing Social Network Simulation...");
        // Creating users
        network.createUser("alice");
        network.createUser("bob");
        network.createUser("charlie");
        network.createUser("diana");

        // Set profile info
        network.getUser("alice").setProfileInfo("Software developer and cat lover.");
        network.getUser("bob").setProfileInfo("Musician and coffee enthusiast.");

        // Establishing friendships
        network.addFriend("alice", "bob");
        network.addFriend("alice", "charlie");

        // Creating posts
        Post p1 = network.createPost("alice", "Just finished a great new feature for my project! #coding");
        Post p2 = network.createPost("bob", "My new song is out now! Check it out on SoundWave.");
        network.createPost("charlie", "Thinking about learning Java. Any tips?");
        network.createPost("alice", "My cat is being extra cute today.");
        
        // Adding comments
        network.addComment(p1.getPostId(), "bob", "Awesome! Can't wait to see it.");
        network.addComment(p2.getPostId(), "alice", "Listening now! It's fantastic!");
        network.addComment(p2.getPostId(), "charlie", "Great track, Bob!");
        System.out.println("Initialization complete. Welcome!");
    }

    
    private static void runCli() {
        Scanner scanner = new Scanner(System.in); 
        
        
        while (true) {
           
            if (loggedInUser == null) {
                displayLoginMenu();
                handleLogin(scanner);
            } else {
                displayMainMenu();
                String command = scanner.nextLine().trim(); 
                
                if (command.equalsIgnoreCase("logout")) {
                    loggedInUser = null; 
                    System.out.println("You have been logged out.");
                    continue; 
                }
                if (command.equalsIgnoreCase("exit")) {
                    System.out.println("Thank you for using the Social Network. Goodbye!");
                    break; 
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
            System.exit(0);
        }
        User user = network.getUser(username); 
        if (user != null) {
            loggedInUser = user; 
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

    
    private static void handleCommand(String choice, Scanner scanner) {
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
    
    
    private static void likePost(Scanner scanner) {
        System.out.println("--- Like a Post ---");
        System.out.println("Here are the recent posts from your feed:");
        viewNewsFeed(); 
        System.out.print("\nEnter the ID of the post you want to like: ");
        String postId = scanner.nextLine().trim();
        
        
        List<Post> feed = network.getNewsFeed(loggedInUser.getUsername());
        boolean postInFeed = feed.stream().anyMatch(p -> p.getPostId().equals(postId));
        
        if (postInFeed) {
            network.likePost(postId);
        } else {
            System.out.println("!> Error: That post ID is not in your feed or does not exist.");
        }
    }
}
