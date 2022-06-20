package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;
import io.cucumber.java.en_old.Ac;

import java.math.BigDecimal;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private TransferService transferService;
    private AccountService accountService;
    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    /**OPENS ACCOUNT AND TRANSFER SERVICE SESSIONS TO INCLUDE AUTHORIZATIONS**/
    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        accountService = new AccountService(API_BASE_URL, currentUser);
        transferService = new TransferService(API_BASE_URL, currentUser);
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
		consoleService.printCurrentBalance(currentAccount);
	}

	private void viewTransferHistory() {
		// TODO Auto-generated method stub

        /**CREATES LIST OF TRANSFERS CONSTRAINED TO CURRENT USER**/
        Transfer[] transfers = transferService.getAllTransfers();

        /**GETS CURRENT ACCOUNT INFORMATION FOR CURRENT USER**/
        // would this be better placed as a method at login as well so instead of rerunning get account its just done?
        Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId());

        /**PRINT LIST OF TRANSFERS**/
        consoleService.printTransferHistory(transfers, currentAccount.getId(), accountService);

        /**COLLECT TRANSFER ID TO DISPLAY DETAILS OR 0 TO EXIT**/
        Long transferId = (long) consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        if (transferId == 0L) return;

        /**COLLECT AND PRINT TRANSFER DETAILS**/
        TransferDetails transferDetails = transferService.getTransferDetails(transferId);
        consoleService.printTransferDetails(transferDetails);
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
        /**PART ONE - HANDLE LIKE VIEW TRANSFER HISTORY BUT ONLY FOR PENDING STATUS**/
        Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
        Transfer[] pendingTransfers = transferService.getAllPendingTransfers();
        consoleService.printTransferHistory(pendingTransfers, currentAccount.getId(), accountService);

        /**PART TWO - APPROVE OR REJECT AND UPDATE TRANSFER RECORD TO REFLECT NEW STATUS**/
        Long transferId = (long) consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        if (transferId == 0L) return;

        /**PRINT PENDING TRANSFER MENU**/
        /**COLLECT AND EXECUTE PENDING TRANSFER ACTION**/

        int pendingSelection = -1;
        while (pendingSelection != 0) {
            consoleService.printPendingTransferMenu();
            pendingSelection = consoleService.promptForPendingTransferMenuSelection("Please choose an option: ");
            if (pendingSelection == 1) {
                consoleService.printTransferDetails(transferService.approveTransfer(transferService.getTransfer(transferId)));
            } else if (pendingSelection == 2) {
                consoleService.printTransferDetails(transferService.rejectTransfer(transferService.getTransfer(transferId)));
            } else if (pendingSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            return;
        }
	}

	private void sendBucks() {
        // TODO Auto-generated method stub
        /**CREATE MASTER LIST OF USERS, PRINT ALL EXCLUDING CURRENT USER**/
        Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
        User[] users = accountService.getAllUsers();
        consoleService.printUsers(users, currentUser.getUser().getId());

        /**COLLECT USER ID OF USER TO RECEIVE TRANSFER**/
        int userSelection = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if (userSelection == 0) return;
        Long receiverId = (long) userSelection;
        Account receiverAccount = accountService.getAccountByUserId(receiverId);

        /**COLLECT AMOUNT TO TRANSFER AND CONFIRM IT IS NOT MORE THAN USER HAS AND IS NOT 0 OR LESS**/
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) == -1) {
            System.out.println("Transfer can not be negative or zero.");
            return;
        }
        if ((amount.compareTo(currentAccount.getBalance()) == 1)) {
            System.out.println("Transfer can not be greater than user balance.");
            return;
        }

        /**CREATE NEW TRANSFER OBJECT**/
        Transfer transfer = new Transfer();
        transfer.setAccountTo(receiverAccount.getId());
        transfer.setAccountFrom(currentAccount.getId());
        transfer.setTransferTypeId(2L);
        transfer.setTransferStatusId(2L);
        transfer.setAmount(amount);

        /**SEND TRANSFER, RETURN TRUE IF SUCCESSFUL**/
        boolean transferSuccess = transferService.sendTransfer(transfer);
        if (transferSuccess) {
            System.out.println("Transfer complete. View details in Transfer History.");
        } else {
            System.out.println("Transfer unable to complete. Please try again.");
        }
    }

        private void requestBucks () {
            // TODO Auto-generated method stub
            /**DISPLAY LIST OF USERS TO REQUEST FROM EXCLUDING CURRENT USER**/
            Account currentAccount = accountService.getAccountByUserId(currentUser.getUser().getId());
            User[] users = accountService.getAllUsers();
            consoleService.printUsers(users, currentUser.getUser().getId());

            /**ENTER USER ID OF USER TO REQUEST FROM**/
            int userSelection = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
            if (userSelection == 0) return;
            Long requestUserId = (long) userSelection;
            Account requestAccount = accountService.getAccountByUserId(requestUserId);

            /**ENTER AMOUNT TO REQUEST AND CONFIRM REQUEST IS >0**/
            BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
            if (amount.compareTo(BigDecimal.ZERO) == 0 || amount.compareTo(BigDecimal.ZERO) == -1) {
                System.out.println("Request can not be negative or zero.");
            }

            /**CREATE TRANSFER BODY WITH STATUS REQUEST AND TYPE PENDING**/
            Transfer transfer = new Transfer();
            transfer.setAccountTo(currentAccount.getId());
            transfer.setAccountFrom(requestAccount.getId());
            transfer.setTransferTypeId(1L);
            transfer.setTransferStatusId(1L);
            transfer.setAmount(amount);

            /**SEND REQUEST AND RETURN - SOMETHING**/
            boolean requestSent = transferService.requestTransfer(transfer);
            if (requestSent) {
                System.out.println("Request sent. View details in pending requests.");
            } else {
                System.out.println("Request unable to be sent. Please try again.");
            }
        }

}
