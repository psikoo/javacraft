import java.util.*;
import java.net.*;
import java.io.*;

public class JavaCraft { // Defines main variables
  // Block IDs
  private static int EMPTY_BLOCK = 0;
  private static final int AIR = 0;
  private static final int WOOD = 1;
  private static final int LEAVES = 2;
  private static final int STONE = 3;
  private static final int IRON_ORE = 4;
  // World dimension
  private static int NEW_WORLD_WIDTH = 25;
  private static int NEW_WORLD_HEIGHT = 15;
  // Recipes IDs
  private static final int CRAFT_WOODEN_PLANKS = 100;
  private static final int CRAFT_STICK = 101;
  private static final int CRAFT_IRON_INGOT = 102;
  // Crafted items IDs
  private static final int CRAFTED_WOODEN_PLANKS = 200;
  private static final int CRAFTED_STICK = 201;
  private static final int CRAFTED_IRON_INGOT = 202;
  // Ansi colors
  private static final String ANSI_BROWN = "\u001B[33m"; // Brown and yellow have the same color code for some reason (there is no base ansi code for brown (that I know))
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m"; // Brown and yellow have the same color code for some reason
  private static final String ANSI_CYAN = "\u001B[36m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_PURPLE = "\u001B[35m";
  private static final String ANSI_BLUE = "\u001B[34m";
  private static final String ANSI_GRAY = "\u001B[37m";
  private static final String ANSI_WHITE = "\u001B[97m";

  // Block ID - Name
  private static final String BLOCK_NUMBERS_INFO = "Block Numbers:\n" +
      "0 - Empty block\n" +
      "1 - Wood block\n" +
      "2 - Leaves block\n" +
      "3 - Stone block\n" +
      "4 - Iron ore block\n" +
      "5 - Wooden Planks (Crafted Item)\n" +
      "6 - Stick (Crafted Item)\n" +
      "7 - Iron Ingot (Crafted Item)";
  // World variables
  private static int[][] world;
  private static int worldWidth;
  private static int worldHeight;
  // Player variables
  private static int playerX;
  private static int playerY;
  private static List<Integer> inventory = new ArrayList<>();
  private static List<Integer> craftedItems = new ArrayList<>();
  private static boolean unlockMode = false;
  private static boolean secretDoorUnlocked = false;
  private static boolean inSecretArea = false;
  private static final int INVENTORY_SIZE = 100;

  public static void main(String[] args) { // Start function
    // Populates starting variables
    initGame(25, 15);
    // Randomizes the world generation
    generateWorld();
    // Shows start help text
    System.out.println(ANSI_GREEN + "Welcome to Simple Minecraft!" + ANSI_RESET);
    System.out.println("Instructions:");
    System.out.println(" - Use 'W', 'A', 'S', 'D', or arrow keys to move the player.");
    System.out.println(" - Press 'M' to mine the block at your position and add it to your inventory.");
    System.out.println(" - Press 'P' to place a block from your inventory at your position.");
    System.out.println(" - Press 'C' to view crafting recipes and 'I' to interact with elements in the world.");
    System.out.println(" - Press 'Save' to save the game state and 'Load' to load a saved game state.");
    System.out.println(" - Press 'Exit' to quit the game.");
    System.out.println(" - Type 'Help' to display these instructions again.");
    System.out.println();
    Scanner scanner = new Scanner(System.in);
    System.out.print("Start the game? (Y/N): ");
    String startGameChoice = scanner.next().toUpperCase();
    if (startGameChoice.equals("Y")) {
      // Starts the main game loop
      startGame();
    } else {
      System.out.println("Game not started. Goodbye!");
    }
  }

  public static void initGame(int worldWidth, int worldHeight) { // Populates starting variables
    // Set world width and height
    JavaCraft.worldWidth = worldWidth;
    JavaCraft.worldHeight = worldHeight;
    JavaCraft.world = new int[worldWidth][worldHeight];
    // Set player position to the center of the world
    playerX = worldWidth / 2;
    playerY = worldHeight / 2;
    // Create an empty array for the players inventory
    inventory = new ArrayList<>();
  }

  public static void generateWorld() { // Randomizes the world generation
    Random rand = new Random();
    // Loop through all the tiles of the world
    for (int y = 0; y < worldHeight; y++) {
      for (int x = 0; x < worldWidth; x++) {
        // Random value between 0-99
        int randValue = rand.nextInt(100);
        // If the value is < 20 the block is wood
        if (randValue < 20) {
          world[x][y] = WOOD;
        // If the value is < 35 the block is leaves
        } else if (randValue < 35) {
          world[x][y] = LEAVES;
        // If the value is < 50 the block is stone
        } else if (randValue < 50) {
          world[x][y] = STONE;
        // If the value is < 70 the block is iron ore
        } else if (randValue < 70) {
          world[x][y] = IRON_ORE;
        // If the value is > 69 the block is air
        } else {
          world[x][y] = AIR;
        }
      }
    }
  }

  public static void displayWorld() { // Displays the world array on the command line
    System.out.println(ANSI_CYAN + "World Map:" + ANSI_RESET);
    // Generates top border based on the world width
    System.out.println("╔══" + "═".repeat(worldWidth * 2 - 2) + "╗");
    // Loop through all of the columns of the world
    for (int y = 0; y < worldHeight; y++) {
      // Generates left border based on the world height
      System.out.print("║");
      // Loop through all of the lines of the world
      for (int x = 0; x < worldWidth; x++) {
        // If the current tile is were the player is and it is not the secret area display P(player position) in green
        if (x == playerX && y == playerY && !inSecretArea) {
          System.out.print(ANSI_GREEN + "P " + ANSI_RESET);
        // If the current tile is were the player is and it is the secret area display P(player position) in blue
        } else if (x == playerX && y == playerY && inSecretArea) {
          System.out.print(ANSI_BLUE + "P " + ANSI_RESET);
        // Display the correct block
        } else {
          // Transforms block name to block symbol color
          System.out.print(getBlockSymbol(world[x][y]));
        }
      }
      System.out.println("║");
      // Generates right border based on the world height
    }
    // Generates bottom border based on the world width
    System.out.println("╚══" + "═".repeat(worldWidth * 2 - 2) + "╝");
  }

  private static String getBlockSymbol(int blockType) { // Transforms block name to block symbol color
    String blockColor;
    switch (blockType) {
      // Air is colorless
      case AIR:
        return ANSI_RESET + "- ";
      // Wood is red
      case WOOD:
        blockColor = ANSI_RED;
        break;
      // Leaves are green
      case LEAVES:
        blockColor = ANSI_GREEN;
        break;
      // Stone is blue
      case STONE:
        blockColor = ANSI_BLUE;
        break;
      // Iron ore is white
      case IRON_ORE:
        blockColor = ANSI_WHITE;
        break;
      // Default case is colorless
      default:
        blockColor = ANSI_RESET;
        break;
    }
    // Returns the block symbol color and the block character
    return blockColor + getBlockChar(blockType) + " ";
  }

  private static char getBlockChar(int blockType) { // Transforms block name to block character
    switch (blockType) {
      // Wood is ▒
      case WOOD:
        return '\u2592';
      // Leaves are ?
      case LEAVES:
        return '\u00A7';
      // Stone is ▓
      case STONE:
        return '\u2593';
      // Iron ore is °
      case IRON_ORE:
        return '\u00B0';
      // Air/EMPTY_BLOCK/Default is -
      default:
        return '-';
    }
  }

  public static void startGame() { // Starts the main game loop
    Scanner scanner = new Scanner(System.in);
    // Set startup variables
    boolean unlockMode = false;
    boolean craftingCommandEntered = false;
    boolean miningCommandEntered = false;
    boolean movementCommandEntered = false;
    boolean openCommandEntered = false;
    // Loop forever
    while (true) {
      // Clears command line
      clearScreen();
      // Displays the legend on the command line
      displayLegend();
      // Displays the world array on the command line
      displayWorld();
      // Displays the inventory on the command line
      displayInventory();
      // Display the available actions
      System.out.println(ANSI_CYAN
          + "Enter your action: 'WASD': Move, 'M': Mine, 'P': Place, 'C': Craft, 'I': Interact, 'Save': Save, 'Load': Load, 'Exit': Quit, 'Unlock': Unlock Secret Door"
          + ANSI_RESET);

      // Possible inputs
      //
      // w/a/s/d    - move
      // m          - mine
      // p          - place
      // c          - craft
      // i          - interact
      // save/lead  - save/load
      // exit       - quit
      // unlock     - unlockMode
      // open       - open secret door
      // getflag    - API connection (NONFUNCTIONAL)
      // look       - look around (DEPRECATED)
      // check      - check unlockedMode progression (QOL)

      // Converts all of the inputs to lowercase
      String input = scanner.next().toLowerCase();
      // If the input is w/a/s/d
      if (input.equalsIgnoreCase("w") || input.equalsIgnoreCase("up") ||
          input.equalsIgnoreCase("s") || input.equalsIgnoreCase("down") ||
          input.equalsIgnoreCase("a") || input.equalsIgnoreCase("left") ||
          input.equalsIgnoreCase("d") || input.equalsIgnoreCase("right")) {
        // If unlockMode is true sets movementCommandEntered to true
        if (unlockMode) {
          movementCommandEntered = true;
        }
        // Moves the player depending on the input
        movePlayer(input);
      // If the input is m
      } else if (input.equalsIgnoreCase("m")) {
        // If unlockMode is true sets miningCommandEntered to true
        if (unlockMode) {
          miningCommandEntered = true;
        }
        // Mines (replace the block with air and add the block to the inventory) the block where the player is standing
        mineBlock();
      // If the input is p
      } else if (input.equalsIgnoreCase("p")) {
        // Displays the inventory on the command line
        displayInventory();
        // Prompts the player to place a block (ONLY INPUT BLOCK ID OR THE PROGRAM WILL CRASH)
        System.out.print("Enter the block type to place: ");
        int blockType = scanner.nextInt();
        // Replaces the block on the players position by the block ID provided
        placeBlock(blockType);
      // If the input is c
      } else if (input.equalsIgnoreCase("c")) {
        // Displays the crafting recipes on the command line
        displayCraftingRecipes();
        // Prompts the player to enter a recipe
        System.out.print("Enter the recipe number to craft: ");
        int recipe = scanner.nextInt();
        // Crafts (removes specific items and gives a different item) specified recipe
        craftItem(recipe);
      // If the input is i
      } else if (input.equalsIgnoreCase("i")) {
        // Collects one of the the block in the players position without removing the block
        interactWithWorld();
      // If the input is save
      } else if (input.equalsIgnoreCase("save")) {
        // Prompts the player to enter a save name
        System.out.print("Enter the file name to save the game state: ");
        String fileName = scanner.next();
        // Serialize game state data and write to a file
        saveGame(fileName);
      // If the input is load
      } else if (input.equalsIgnoreCase("load")) {
        // Prompts the player to enter a lead name
        System.out.print("Enter the file name to load the game state: ");
        String fileName = scanner.next();
        // Deserialize game state data from a file and loads it into the game
        loadGame(fileName);
      // If the input is exit
      } else if (input.equalsIgnoreCase("exit")) {
        // Breaks the main loop ending the program
        System.out.println("Exiting the game. Goodbye!");
        break;
      // DEPRECATED // If the input is look
      } else if (input.equalsIgnoreCase("look")) {
        // DEPRECATED // Prints the 3x3 area around the player
        lookAround();
      // If input is unlock
      } else if (input.equalsIgnoreCase("unlock")) {
        // Sets miningCommandEntered to true
        unlockMode = true;
      // NONFUNCTIONAL // If input is getflag
      } else if (input.equalsIgnoreCase("getflag")) {
        // NONFUNCTIONAL // Retrieves flag from an API endpoint
        getCountryAndQuoteFromServer();
        // Waits for player to press enter
        waitForEnter();
      // If input is open
      } else if (input.equalsIgnoreCase("open")) {
        // If unlockMode, craftingCommandEntered, miningCommandEntered and movementCommandEntered are true
        if (unlockMode && craftingCommandEntered && miningCommandEntered && movementCommandEntered) {
          // Sets miningCommandEntered to true
          secretDoorUnlocked = true;
          // Generate an empty world and place the player in the middle
          resetWorld();
          System.out.println("Secret door unlocked!");
          // Waits for player to press enter
          waitForEnter();
        // If one of unlockMode, craftingCommandEntered, miningCommandEntered and movementCommandEntered are false
        } else {
          System.out.println("Invalid passkey. Try again!");
          // Waits for player to press enter
          waitForEnter();
          // Resets all unlockMode variables
          unlockMode = false;
          craftingCommandEntered = false;
          miningCommandEntered = false;
          movementCommandEntered = false;
          openCommandEntered = false;
        }
      // QOL displays the unlockedMode progression                                // -------------------------- //
      } else if(input.equalsIgnoreCase("check")) {                  //                            //
        System.out.println("unlockMode: " + unlockMode);                          //                            //
        System.out.println("craftingCommandEntered: " + craftingCommandEntered);  //             QOL            //
        System.out.println("miningCommandEntered: " + miningCommandEntered);      //  unlockedMode progression  //
        System.out.println("movementCommandEntered: " + movementCommandEntered);  //                            //
        // Waits for player to press enter                                        //                            //
        waitForEnter();                                                           // -------------------------- //
      // If input is anything else
      } else {
        System.out.println(ANSI_YELLOW + "Invalid input. Please try again." + ANSI_RESET);
      }
      // If unlockMode is true
      if (unlockMode) {
        // If input is c
        if (input.equalsIgnoreCase("c")) {
          craftingCommandEntered = true;
        // If input is m
        } else if (input.equalsIgnoreCase("m")) {
          miningCommandEntered = true;
        // If input is open
        } else if (input.equalsIgnoreCase("open")) {
          openCommandEntered = true;
        }
      }
      // If secretDoorUnlocked is true
      if (secretDoorUnlocked) {
        // Clears command line
        clearScreen();
        System.out.println("You have entered the secret area!");
        System.out.println("You are now presented with a game board with a flag!");
        inSecretArea = true;
        // Generate an empty world and place the player in the middle
        resetWorld();
        secretDoorUnlocked = false;
        // Clears inventory and then adds INVENTORY_SIZE of each block from IDs 1-4
        fillInventory();
        // Waits for player to press enter
        waitForEnter();
      }
    }
  }

  private static void fillInventory() { // Clears inventory and then adds INVENTORY_SIZE of each block from IDs 1-4
    // Clears the inventory array
    inventory.clear();
    // Loops through block IDs 1-4 and adds INVENTORY_SIZE of each to the inventory
    for (int blockType = 1; blockType <= 4; blockType++) {
      for (int i = 0; i < INVENTORY_SIZE; i++) {
        inventory.add(blockType);
      }
    }
  }

  private static void resetWorld() { // Generates a world with the top third red (wood), middle third white (iron) and bottom third blue(stone) and place the player in the middle
    // Generates a world with the top third red (wood), middle third white (iron) and bottom third blue(stone)
    generateEmptyWorld();
    // Places the player in the middle
    playerX = worldWidth / 2;
    playerY = worldHeight / 2;
  }

  private static void generateEmptyWorld() { // Generates a world with the top third red (wood), middle third white (iron) and bottom third blue(stone)
    // Clears world array
    world = new int[NEW_WORLD_WIDTH][NEW_WORLD_HEIGHT];
    // Maps wood, iron and stone IDs to redBlock, whiteBlock and blueBlock
    int redBlock = 1;
    int whiteBlock = 4;
    int blueBlock = 3;
    // Divides the height into three equal parts
    int stripeHeight = NEW_WORLD_HEIGHT / 3;

    // Fills the top stripe with red blocks
    for (int y = 0; y < stripeHeight; y++) {
      for (int x = 0; x < NEW_WORLD_WIDTH; x++) {
        world[x][y] = redBlock;
      }
    }

    // Fills the middle stripe with white blocks
    for (int y = stripeHeight; y < stripeHeight * 2; y++) {
      for (int x = 0; x < NEW_WORLD_WIDTH; x++) {
        world[x][y] = whiteBlock;
      }
    }

    // Fills the bottom stripe with blue blocks
    for (int y = stripeHeight * 2; y < NEW_WORLD_HEIGHT; y++) {
      for (int x = 0; x < NEW_WORLD_WIDTH; x++) {
        world[x][y] = blueBlock;
      }
    }
  }

  private static void clearScreen() { // Clears command line
    try {
      // If the operating system is windows
      if (System.getProperty("os.name").contains("Windows")) {
        // Run command "cmd /c cls"
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      // If the operating system is macOS/Linux
      } else {
        // According to https://www.javatpoint.com/how-to-clear-screen-in-java:
        // \033[H moves the cursor to the top left of the screen/console
        // \033[2J clears the screen from the cursor to the end of the screen
        System.out.print("\033[H\\033[2J");
        System.out.flush();
      }
    // Catch and display any errors
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  private static void lookAround() { // DEPRECATED // Prints the 3x3 area around the player
    System.out.println("You look around and see:");
    // Gets the map values of the 3 x 3 area area around the player
    for (int y = Math.max(0, playerY - 1); y <= Math.min(playerY + 1, worldHeight - 1); y++) {
      for (int x = Math.max(0, playerX - 1); x <= Math.min(playerX + 1, worldWidth - 1); x++) {
        if (x == playerX && y == playerY) {
          System.out.print(ANSI_GREEN + "P " + ANSI_RESET);
        } else {
          System.out.print(getBlockSymbol(world[x][y]));
        }
      }
      System.out.println();
    }
    System.out.println();
    // Waits for player to press enter
    waitForEnter();
  }

  public static void movePlayer(String direction) { // Moves the player depending on the input
    switch (direction.toUpperCase()) {
      // Checks player can move up and moves 1 tile up
      case "W":
      case "UP":
        if (playerY > 0) {
          playerY--;
        }
        break;
      // Checks player can move down and moves 1 tile down
      case "S":
      case "DOWN":
        if (playerY < worldHeight - 1) {
          playerY++;
        }
        break;
      // Checks player can move left and moves 1 tile left
      case "A":
      case "LEFT":
        if (playerX > 0) {
          playerX--;
        }
        break;
      // Checks player can move right and moves 1 tile right
      case "D":
      case "RIGHT":
        if (playerX < worldWidth - 1) {
          playerX++;
        }
        break;
      default:
        break;
    }
  }

  public static void mineBlock() { // Mines (replace the block with air and add the block to the inventory) the block where the player is standing
    // Gets the block in the player position
    int blockType = world[playerX][playerY];
    // If the blockType is not air
    if (blockType != AIR) {
      // Add one of the mined block to the inventory
      inventory.add(blockType);
      // Replace block with air
      world[playerX][playerY] = AIR;
      System.out.println("Mined " + getBlockName(blockType) + ".");
    // If blockType is air
    } else {
      System.out.println("No block to mine here.");
    }
    // Waits for player to press enter
    waitForEnter();
  }

  public static void placeBlock(int blockType) { // Replaces the block on the players position by the block ID provided
    // If the block ID is between 0 and 7 (inclusive)
    if (blockType >= 0 && blockType <= 7) {
      // If the block ID is less or equal to 4
      if (blockType <= 4) {
        // If the inventory array contains the specified ID
        if (inventory.contains(blockType)) {
          // Removes the block from inventory
          inventory.remove(Integer.valueOf(blockType));
          // Replaces the block in the players position with the placed block
          world[playerX][playerY] = blockType;
          System.out.println("Placed " + getBlockName(blockType) + " at your position.");
        // If the inventory array does not contains the specified ID
        } else {
          System.out.println("You don't have " + getBlockName(blockType) + " in your inventory.");
        }
      // If the block ID is between 5 and 7 (inclusive)
      } else {
        int craftedItem = getCraftedItemFromBlockType(blockType);
        // If the craftedItems array contains the specified ID
        if (craftedItems.contains(craftedItem)) {
          // Removes the crafted item from craftedItems
          craftedItems.remove(Integer.valueOf(craftedItem));
          // Replaces the block in the players position with the placed crafted item
          world[playerX][playerY] = blockType;
          System.out.println("Placed " + getCraftedItemName(craftedItem) + " at your position.");
        // If the craftedItems array does not contains the specified ID
        } else {
          System.out.println("You don't have " + getCraftedItemName(craftedItem) + " in your crafted items.");
        }
      }
    // If the block ID is less than 0 or more than 7
    } else {
      System.out.println("Invalid block number. Please enter a valid block number.");
      System.out.println(BLOCK_NUMBERS_INFO);
    }
    // Waits for player to press enter
    waitForEnter();
  }

  private static int getBlockTypeFromCraftedItem(int craftedItem) { //UNUSED see getCraftedItemFromBlockType()
    switch (craftedItem) {
      case CRAFTED_WOODEN_PLANKS:
        return 5;
      case CRAFTED_STICK:
        return 6;
      case CRAFTED_IRON_INGOT:
        return 7;
      default:
        return -1;
    }
  }

  private static int getCraftedItemFromBlockType(int blockType) { //Transforms the given blockType to a crafted item name
    switch (blockType) {
      // Wooden planks
      case 5:
        return CRAFTED_WOODEN_PLANKS;
      // Sticks
      case 6:
        return CRAFTED_STICK;
      // Iron ingot
      case 7:
        return CRAFTED_IRON_INGOT;
      // Default
      default:
        return -1;
    }
  }

  public static void displayCraftingRecipes() { // Displays the crafting recipes on the command line
    // Displays crafting recipes
    System.out.println("Crafting Recipes:");
    System.out.println("1. Craft Wooden Planks: 2 Wood");
    System.out.println("2. Craft Stick: 1 Wood");
    System.out.println("3. Craft Iron Ingot: 3 Iron Ore");
  }

  public static void craftItem(int recipe) { // Crafts (removes specific items and gives a different item) specified recipe
    switch (recipe) {
      case 1:
        // Crafts wooden planks
        craftWoodenPlanks();
        break;
      case 2:
        // Crafts sticks
        craftStick();
        break;
      case 3:
        // Crafts iron ingots
        craftIronIngot();
        break;
      default:
        System.out.println("Invalid recipe number.");
    }
    // Waits for player to press enter
    waitForEnter();
  }

  public static void craftWoodenPlanks() { // Crafts wooden planks
    // If inventory has required materials (2x wood)
    if (inventoryContains(WOOD, 2)) {
      // Removes materials and adds the crafted item
      removeItemsFromInventory(WOOD, 2);
      addCraftedItem(CRAFTED_WOODEN_PLANKS);
      System.out.println("Crafted Wooden Planks.");
    // If inventory does not have required materials (2x wood)
    } else {
      System.out.println("Insufficient resources to craft Wooden Planks.");
    }
  }

  public static void craftStick() { // Crafts sticks
    // If inventory has required materials (1x wood)
    if (inventoryContains(WOOD)) {
      // Removes materials and adds the crafted item
      removeItemsFromInventory(WOOD, 1);
      addCraftedItem(CRAFTED_STICK);
      System.out.println("Crafted Stick.");
    // If inventory does not have required materials (1x wood)
    } else {
      System.out.println("Insufficient resources to craft Stick.");
    }
  }

  public static void craftIronIngot() { // Crafts iron ingots
    // If inventory has required materials (3x iron ore)
    if (inventoryContains(IRON_ORE, 3)) {
      // Removes materials and adds the crafted item
      removeItemsFromInventory(IRON_ORE, 3);
      addCraftedItem(CRAFTED_IRON_INGOT);
      System.out.println("Crafted Iron Ingot.");
    // If inventory does not have required materials (3x iron ore)
    } else {
      System.out.println("Insufficient resources to craft Iron Ingot.");
    }
  }

  public static boolean inventoryContains(int item) { // Checks if the inventory array contains the specified item
    return inventory.contains(item);
  }

  public static boolean inventoryContains(int item, int count) { // Checks if the inventory array contains the specified item quantity
    int itemCount = 0;
    // Loops though all the inventory
    for (int i : inventory) {
      // If the current item is the one looked for
      if (i == item) {
        itemCount++;
        // If there amount of items found is equal to the amount specified
        if (itemCount == count) {
          return true;
        }
      }
    }
    return false;
  }

  public static void removeItemsFromInventory(int item, int count) { // Removes the specified item quantity from inventory array
    int removedCount = 0;
    // Loops though all the inventory
    Iterator<Integer> iterator = inventory.iterator();
    while (iterator.hasNext()) {
      // If the current item is the one looked for
      int i = iterator.next();
      if (i == item) {
        // Remove the item from the inventory array
        iterator.remove();
        removedCount++;
        // If there amount of items removed is equal to the amount specified
        if (removedCount == count) {
          break;
        }
      }
    }
  }

  public static void addCraftedItem(int craftedItem) { // Adds the specified craftedItem to the craftedItems array
    // If the craftedItems array does not exit it creates it
    if (craftedItems == null) {
      craftedItems = new ArrayList<>();
    }
    // Adds the item
    craftedItems.add(craftedItem);
  }

  public static void interactWithWorld() { // Collects one of the the block in the players position without removing the block
    // Gets the block where the player is located
    int blockType = world[playerX][playerY];
    switch (blockType) {
      // Interaction with wood
      case WOOD:
        System.out.println("You gather wood from the tree.");
        inventory.add(WOOD);
        break;
      // Interaction with leaves
      case LEAVES:
        System.out.println("You gather leaves from the tree.");
        inventory.add(LEAVES);
        break;
      // Interaction with stone
      case STONE:
        System.out.println("You gather stones from the ground.");
        inventory.add(STONE);
        break;
      // Interaction with iron ore
      case IRON_ORE:
        System.out.println("You mine iron ore from the ground.");
        inventory.add(IRON_ORE);
        break;
      // Interaction with air
      case AIR:
        System.out.println("Nothing to interact with here.");
        break;
      // Default interaction
      default:
        System.out.println("Unrecognized block. Cannot interact.");
    }
    // Waits for player to press enter
    waitForEnter();
  }

  public static void saveGame(String fileName) { // Serialize game state data and write to a file
    try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
      // Serialize game state data and write to the file
      outputStream.writeInt(NEW_WORLD_WIDTH);
      outputStream.writeInt(NEW_WORLD_HEIGHT);
      outputStream.writeObject(world);
      outputStream.writeInt(playerX);
      outputStream.writeInt(playerY);
      outputStream.writeObject(inventory);
      outputStream.writeObject(craftedItems);
      outputStream.writeBoolean(unlockMode);

      System.out.println("Game state saved to file: " + fileName);
    } catch (IOException e) {
      System.out.println("Error while saving the game state: " + e.getMessage());
    }
    // Waits for player to press enter
    waitForEnter();
  }


  public static void loadGame(String fileName) { // Deserialize game state data from a file and loads it into the game
    // Implementation for loading the game state from a file goes here
    try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
      // Deserialize game state data from the file and load it into the program
      NEW_WORLD_WIDTH = inputStream.readInt();
      NEW_WORLD_HEIGHT = inputStream.readInt();
      world = (int[][]) inputStream.readObject();
      playerX = inputStream.readInt();
      playerY = inputStream.readInt();
      inventory = (List<Integer>) inputStream.readObject();
      craftedItems = (List<Integer>) inputStream.readObject();
      unlockMode = inputStream.readBoolean();

      System.out.println("Game state loaded from file: " + fileName);
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Error while loading the game state: " + e.getMessage());
    }
    // Waits for player to press enter
    waitForEnter();
  }

  private static String getBlockName(int blockType) { // Transforms blockType to block name
    switch (blockType) {
      // Air
      case AIR:
        return "Empty Block";
      // Wood
      case WOOD:
        return "Wood";
      // Leaves
      case LEAVES:
        return "Leaves";
      // Stone
      case STONE:
        return "Stone";
      // Iron ore
      case IRON_ORE:
        return "Iron Ore";
      // Default
      default:
        return "Unknown";
    }
  }

  public static void displayLegend() { // Displays the legend on the command line
    System.out.println(ANSI_BLUE + "Legend:");
    System.out.println(ANSI_WHITE + "-- - Empty block");
    System.out.println(ANSI_RED + "\u2592\u2592 - Wood block");
    System.out.println(ANSI_GREEN + "\u00A7\u00A7 - Leaves block");
    System.out.println(ANSI_BLUE + "\u2593\u2593 - Stone block");
    System.out.println(ANSI_WHITE + "\u00B0\u00B0- Iron ore block");
    System.out.println(ANSI_BLUE + "P - Player" + ANSI_RESET);
  }

  public static void displayInventory() { // Displays the inventory on the command line
    System.out.println("Inventory:");
    // If the inventory is empty
    if (inventory.isEmpty()) {
      System.out.println(ANSI_YELLOW + "Empty" + ANSI_RESET);
    // If the inventory is not empty
    } else {
      int[] blockCounts = new int[5];
      // Loop through the inventory and counts how many of each block there are
      for (int i = 0; i < inventory.size(); i++) {
        int block = inventory.get(i);
        blockCounts[block]++;
      }
      // Loop and display all blocks in the inventory and their quantity
      for (int blockType = 1; blockType < blockCounts.length; blockType++) {
        int occurrences = blockCounts[blockType];
        if (occurrences > 0) {
          System.out.println(getBlockName(blockType) + " - " + occurrences);
        }
      }
    }
    System.out.println("Crafted Items:");
    // If there are no crafted items
    if (craftedItems == null || craftedItems.isEmpty()) {
      System.out.println(ANSI_YELLOW + "None" + ANSI_RESET);
    // If there are crafted items
    } else {
      // Loop and display all crafted items
      for (int item : craftedItems) {
        System.out.print(getCraftedItemColor(item) + getCraftedItemName(item) + ", " + ANSI_RESET);
      }
      System.out.println();
    }
    System.out.println();
  }

  private static String getBlockColor(int blockType) { //UNUSED see getBlockSymbol()
    switch (blockType) {
      case AIR:
        return "";
      case WOOD:
        return ANSI_RED;
      case LEAVES:
        return ANSI_GREEN;
      case STONE:
        return ANSI_GRAY;
      case IRON_ORE:
        return ANSI_YELLOW;
      default:
        return "";
    }
  }

  private static void waitForEnter() { // Waits for player to press enter
    System.out.println("Press Enter to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
  }

  private static String getCraftedItemName(int craftedItem) { // Transform craftedItem to crafted item name
    switch (craftedItem) {
      // Wooden planks
      case CRAFTED_WOODEN_PLANKS:
        return "Wooden Planks";
      // Sticks
      case CRAFTED_STICK:
        return "Stick";
      // Iron ingot
      case CRAFTED_IRON_INGOT:
        return "Iron Ingot";
      // Default
      default:
        return "Unknown";
    }
  }

  private static String getCraftedItemColor(int craftedItem) { // NONFUNCTIONAL // Gets the color for the given crafted item
    switch (craftedItem) {
      case CRAFTED_WOODEN_PLANKS:
      case CRAFTED_STICK:
      case CRAFTED_IRON_INGOT:
        return ANSI_BROWN;
      default:
        return "";
    }
  }

  public static void getCountryAndQuoteFromServer() { // NONFUNCTIONAL // Retrieves flag from an API endpoint
    try {
      URL url = new URL(" ");
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);
      String payload = " ";
      OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
      writer.write(payload);
      writer.flush();
      writer.close();
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }
      String json = sb.toString();
      int countryStart = json.indexOf(" ") + 11;
      int countryEnd = json.indexOf(" ", countryStart);
      String country = json.substring(countryStart, countryEnd);
      int quoteStart = json.indexOf(" ") + 9;
      int quoteEnd = json.indexOf(" ", quoteStart);
      String quote = json.substring(quoteStart, quoteEnd);
      quote = quote.replace(" ", " ");
      System.out.println(" " + country);
      System.out.println(" " + quote);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Error connecting to the server");
    }
  }
}