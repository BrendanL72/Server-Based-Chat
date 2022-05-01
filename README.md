# CS4390_Project: Server-Based Chat

Group 8:

Brendan Lim, Kiara Madeam, Cloyd Anacan, Thomas Rogers

# How to Run

1. Create one command prompt for the server, and any number of command prompts equal to the number of clients you wish to run.
2. Navigate all command prompts to the folder containing the project files/where this README is located
3. Run the server using the following command: 'java Server'. If that does not work, try compiling the file using javac Server.java
4. Run each of the users using the following command: 'java User <server IP> <user file>',where the server IP is just the server IP, no port. The user file specified must be a valid subscriber file with the subscriber's user ID and secret key.
  NOTE: For connecting to a server hosted on the same machine, use 'localhost' as the server IP argument.
5. Connection to the server should occur automatically, and you should be able to type chat messages.
6. To chat with another user, you must first create a chat session. You can do this by typing 'Chat <target user ID>' and press Enter, where the target user ID is the user ID of the user you would like to talk to. Make sure that the user you are attempting to chat with has also connected successfully.
7. If connected succesfully, you should see some sort of confirmation message. If so, you may now begin typing message to each other.
8. To end a chat session, type 'End chat' and press Enter.
9. To end your connection to the server, type "Log off" and press Enter.

# TO DO
Finish Server Connection Phase - Brendan : FINISHED

Finish User Connection + Chat Phase - Kiara : FINISHED

Implement Connection.java's message handling (multithreading) - Cloyd + Thomas

Get it to work - all

-----

Authentification : FINISHED

Encryption : FINISHED

Minor: Chat History 
  
Minor: Activity Timer

Documentation!!!!
