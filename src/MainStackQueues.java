import java.util.*;
import java.io.*;

public class MainStackQueues {
    private static Scanner reader;
    private static String fileDirectory;
    private static String fileName;
    private static double markup = 0.40;
    private static double highestPrice;
    private static int totalQuantityOfCans;
    private static String bookKeeper = "Actual cost: ";

    private static Stack<CansStock> shipmentStack = new Stack<CansStock>();
    private static Queue<CansStock> orderQueue = new LinkedList<CansStock>();
    private static  Queue<CansStock> backOrderQueue = new LinkedList<CansStock>();

    public static void main (String args[]){
        readFile();
        while (!backOrderQueue.isEmpty() || !orderQueue.isEmpty()){
            orderProcessing();
        }
    }

    //Method that takes the input of the file location from the user then reads each line, if the Line starts with a R then it reads the 3 fields, else it reads the two fields
    public static void readFile(){
        System.out.println("Please Enter the directory of the text file you would like to encrypt or decrypt (without the filename)");
        Scanner keyboard = new Scanner(System.in);
        fileDirectory = keyboard.nextLine();
        System.out.println("Please enter the name of the file you are trying to encrypt or decrypt (Including the extension)");
        fileName = keyboard.nextLine();

        try {
            reader = new Scanner(new File(fileDirectory + "\\" +fileName));
            while (reader.hasNext()){
                String operationType = reader.next().toUpperCase();
                if (operationType.charAt(0) == 'R'){
                    String quantitiy = reader.next();
                    String price = reader.next();
                    shipmentProcessing(quantitiy, price);
                }
                //since the queued order will sell the highest price of our cans it will just peek from our stack for the price
                else if(operationType.charAt(0) == 'S'){
                    String quantity = reader.next();
                    double price = shipmentStack.peek().getPrice();
                    orderReceived(quantity, price);
                }
                else{
                    System.out.println("Error operation: "+operationType +" is not identified");
                }
            }
        }
        catch (Exception e) {
            System.out.println("File not found");
        }
    }
    public static void shipmentProcessing(String quantityOfCans, String pricePerCan){
        int quantity = Integer.parseInt(quantityOfCans);
        double price = Double.parseDouble(pricePerCan);
        if(price > highestPrice) {
            highestPrice = price;
        }
        totalQuantityOfCans += quantity;
        CansStock shipment = new CansStock(quantity, price);
        shipmentStack.push(shipment);
        System.out.println("Shipment has been processed");
    }

    public static void orderReceived(String quantityOfCans, double pricePerCan){
        int quantity = Integer.parseInt(quantityOfCans);
        CansStock order = new CansStock(quantity, pricePerCan);
        orderQueue.add(order);
        System.out.println("order has been received");
        orderProcessing();
    }

    //method that processes orders and prints recipts for customer and bookkeeper
    public static void orderProcessing(){
        //checks if backorder queue is empty and can be processed
        if(!backOrderQueue.isEmpty() && backOrderQueue.peek().getQuantity() <= totalQuantityOfCans ){
            backOrderProcessing();
        }
        //checks if the order in orderqueue can be processed, if not order is sent to backorder
        if (orderQueue.peek().getQuantity() > totalQuantityOfCans){
            backOrderQueue.add(orderQueue.remove());
            System.out.println("Order too large, added to backorder");
        }
        //executes order processing if order can be fulfilled
        else{
            System.out.println("=========================================Processing order===============================================================\n");
            System.out.println("Customer Recipt: $" + (highestPrice + (highestPrice * markup)) * orderQueue.peek().getQuantity() + " For " + orderQueue.peek().getQuantity());
            //as long as the order is not fully executed it will keep running
            while(orderQueue.peek().getQuantity()>0){
                //if the item at the top of shipment stack is less then the order quantity
                if(shipmentStack.peek().getQuantity() <= orderQueue.peek().getQuantity()){
                    bookKeeper += shipmentStack.peek().toString() + " = $" + (shipmentStack.peek().getPrice() * shipmentStack.peek().getQuantity());
                    orderQueue.peek().setQuantity(orderQueue.peek().getQuantity() - shipmentStack.peek().getQuantity());
                    totalQuantityOfCans -= shipmentStack.peek().getQuantity();
                    shipmentStack.pop();
                    System.out.println(bookKeeper);
                    bookKeeper = "";
                }
                else{
                    bookKeeper += orderQueue.peek().getQuantity() + " cans " + " for $"+ shipmentStack.peek().getPrice() + " per can" + " = $" + (shipmentStack.peek().getPrice() * orderQueue.peek().getQuantity());
                    shipmentStack.peek().setQuantity(shipmentStack.peek().getQuantity() - orderQueue.peek().getQuantity());
                    totalQuantityOfCans -= orderQueue.peek().getQuantity();
                    orderQueue.remove();
                    System.out.println(bookKeeper + "\n\n");
                    bookKeeper = "";
                    break;
                }
            }
            bookKeeper = "Actual Cost: ";
            System.out.println("=========================================Finished Processing order================================================================ \n\n");
        }
    }

    public static void backOrderProcessing(){
        System.out.println("=========================================Processing back order===============================================================\n");
        System.out.println("Customer Recipt: $" + (backOrderQueue.peek().getPrice() + (backOrderQueue.peek().getPrice() * markup)) * backOrderQueue.peek().getQuantity() + " for " + backOrderQueue.peek().getQuantity() + " Cans" );
        while(backOrderQueue.peek().getQuantity()>0){
            //if the item at the top of shipment stack is less then the order quantity
            if(shipmentStack.peek().getQuantity() <= backOrderQueue.peek().getQuantity()){
                bookKeeper += shipmentStack.peek().toString() + " = $" + backOrderQueue.peek().getPrice() * shipmentStack.peek().getQuantity();
                backOrderQueue.peek().setQuantity(backOrderQueue.peek().getQuantity() - shipmentStack.peek().getQuantity());
                totalQuantityOfCans -= shipmentStack.peek().getQuantity();
                shipmentStack.pop();
                System.out.println(bookKeeper);
                bookKeeper = "";
            }
            else{
                bookKeeper += backOrderQueue.peek().getQuantity() + " cans " + " for $" + backOrderQueue.peek().getPrice() + " per can" + " = $" + backOrderQueue.peek().getPrice() * backOrderQueue.peek().getQuantity();
                shipmentStack.peek().setQuantity(shipmentStack.peek().getQuantity() - backOrderQueue.peek().getQuantity());
                totalQuantityOfCans -= backOrderQueue.peek().getQuantity();
                backOrderQueue.remove();
                System.out.println(bookKeeper);
                bookKeeper = "";
                break;
            }
        }
        bookKeeper = "Actual Cost: ";
        System.out.println("=========================================Finished Processing Back order=============================================================== \n\n");

    }
}
