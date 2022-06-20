package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    /** PROMPTS FOR MENUS **/

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public int promptForPendingTransferMenuSelection(String prompt) {
        int pendingSelection;
        System.out.print(prompt);
        try {
            pendingSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            pendingSelection = -1;
        }
        return pendingSelection;
    }

    /** PRINT METHODS **/

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printPendingTransferMenu() {
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("---------");
        System.out.println();
    }

    public void printCurrentBalance(Account account){
        System.out.println("-------------------------------------------");
        if (account == null){
            System.out.println("No account to show");
        } else {
            System.out.println("Your current balance is " + account.getBalance());
        }
        System.out.println("-------------------------------------------");
    }

    public void printTransferHistory(Transfer[] transfers, Long accountId, AccountService accountService/*, TransferService transferService*/){
        System.out.println("--------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-15s %-20s %-20s\n", "ID", "From/To", "Amount");
        System.out.println("--------------------------------------------");
        for (Transfer t: transfers){
            String username = null;
            String currentUsername = accountService.getUserByAccountId(accountId).getUsername();
            if (t.getAccountFrom().compareTo(accountId) == 0) {
                username = (accountService.getUserByAccountId(t.getAccountTo()).getUsername());
                System.out.printf("%-15s %-20s %-20s\n", t.getId(), "To: " + username, t.getAmount()  );
            } else {
                username = (accountService.getUserByAccountId(t.getAccountFrom()).getUsername());
                System.out.printf("%-15s %-20s %-20s\n", t.getId(), "From: " + username, t.getAmount());
            }
        }
        System.out.println("--------------");
    }

    public void printTransferDetails(TransferDetails transferDetails) {
        System.out.println("--------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        System.out.println("Id: " + transferDetails.getId());
        System.out.println("From: " + transferDetails.getAccountFromUsername());
        System.out.println("To: " + transferDetails.getAccountToUsername());
        System.out.println("Type: " + transferDetails.getTransferType());
        System.out.println("Status: " + transferDetails.getTransferStatus());
        System.out.println("Amount: " + transferDetails.getAmount());
    }

    public void printUsers(User[] users, Long id){
        System.out.println("--------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-20s%-20s%n", "ID", "Name");
        System.out.println("--------------------------------------------");
        for (User u : users){
            if (!(u.getId().compareTo(id) == 0)){
                System.out.printf("%-20s%-20s%n", u.getId(), u.getUsername());
            }
        }
        System.out.println("--------------------------------------------");
    }

    /** PROMPT METHODS **/

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    /** CONVENIENCE METHODS **/

    public void pause() {
        System.out.print("\nPress Enter to continue... ");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
