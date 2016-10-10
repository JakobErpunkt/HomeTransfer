import view.HomeTransferView;

/**
 * Created by jakobriga on 01.12.15.
 */
public class Main {

    /**
     * The initial entry point for the whole program.
     * When the program is initiated it looks out for available servers and displays the found ones in a list.
     * Also there is a server started to make the program visible for other clients in the network.
     * @param args
     */
    public static void main(String[] args) {

/*        // Choose 4443 to be the port number as it is not reserved by any service officially
        int port = 4443;

        // When an argument is passed we
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch Exception(e) {
                System.out.println("Error: " + args[0] + " cannot be used as a port number. " + port + " taken instead.");
            }
        }
*/
        HomeTransferView.getInstance().init();
    }

}