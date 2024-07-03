import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        WaterSortGame game;
        while(true){
            String command = scan.nextLine();
            if(!command.equals("start")){
                break;
            }
            else{
                System.out.println("Enter colors:");
                String[] colors = scan.nextLine().split(" ");
                System.out.println("Enter max bottle size:");
                int maxBottleSize = scan.nextInt();
                scan.nextLine();
                game = new WaterSortGame(colors, maxBottleSize);
                game.display();

                while(!game.hasWon()){
                    System.out.println("Enter the Action");
                    command = scan.nextLine();
                    switch (command){
                        case "select":
                            int bottleNumber = scan.nextInt();
                            scan.nextLine();
                            game.select(bottleNumber);
                            break;
                        case "deSelect":
                            game.deSelected();
                            break;
                        case "selectNext":
                            game.selectNext();
                            break;
                        case "selectPrevious":
                            game.selectPrevious();
                            break;
                        case "pour":
                            System.out.println("Enter the number of bottle that u want to pour in");
                            int pourNum = scan.nextInt();
                            scan.nextLine();
                            game.pour(pourNum);
                            break;
                        case "swap":
                            System.out.println("Enter the number of bottle that u want to swap");
                            int swapNum = scan.nextInt();
                            scan.nextLine();
                            game.swap(swapNum);
                            break;
                        case "replaceColor":
                            System.out.println("Enter two color that u want to replace");
                            String first = scan.nextLine();
                            String second = scan.nextLine();
                            game.replaceColor(first,second);
                            break;
                        case "addEmptyBottle":
                            game.addEmptyBottle();
                            break;
                        case "undo":
                            game.undo();
                            break;
                        case "redo":
                            game.redo();
                            break;
                    }
                }
                System.out.println("YOU WON");
            }
        }
    }
}
