package gameClient;

public class Ex2 {

    public static int userID = 0;
    public static int level = 0;

    public static void main(String[] args) {
        if (args.length == 0){
            Thread client = new Thread(new PGame());
            client.start();
        } else {
            userID = Integer.parseInt(args[0]);
            level = Integer.parseInt(args[1]);
            Thread client = new Thread(new PGame(userID,level));
            client.start();
        }
    }
}
