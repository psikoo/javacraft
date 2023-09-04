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
  private static final String ANSI_BROWN = "\u001B[33m";
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_YELLOW = "\u001B[33m";
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
  // CONTINUE
  private static void clearScreen() { // Clears command line
    try {
      if (System.getProperty("os.name").contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (IOException | InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  private static void lookAround() { // DEPRECATED // Prints the 3x3 area around the player
    System.out.println("You look around and see:");
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
      case "W":
      case "UP":
        if (playerY > 0) {
          playerY--;
        }
        break;
      case "S":
      case "DOWN":
        if (playerY < worldHeight - 1) {
          playerY++;
        }
        break;
      case "A":
      case "LEFT":
        if (playerX > 0) {
          playerX--;
        }
        break;
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
    int blockType = world[playerX][playerY];
    if (blockType != AIR) {
      inventory.add(blockType);
      world[playerX][playerY] = AIR;
      System.out.println("Mined " + getBlockName(blockType) + ".");
    } else {
      System.out.println("No block to mine here.");
    }
    // Waits for player to press enter
    waitForEnter();
  }

  public static void placeBlock(int blockType) { // Replaces the block on the players position by the block ID provided
    if (blockType >= 0 && blockType <= 7) {
      if (blockType <= 4) {
        if (inventory.contains(blockType)) {
          inventory.remove(Integer.valueOf(blockType));
          world[playerX][playerY] = blockType;
          System.out.println("Placed " + getBlockName(blockType) + " at your position.");
        } else {
          System.out.println("You don't have " + getBlockName(blockType) + " in your inventory.");
        }
      } else {
        int craftedItem = getCraftedItemFromBlockType(blockType);
        if (craftedItems.contains(craftedItem)) {
          craftedItems.remove(Integer.valueOf(craftedItem));
          world[playerX][playerY] = blockType;
          System.out.println("Placed " + getCraftedItemName(craftedItem) + " at your position.");
        } else {
          System.out.println("You don't have " + getCraftedItemName(craftedItem) + " in your crafted items.");
        }
      }
    } else {
      System.out.println("Invalid block number. Please enter a valid block number.");
      System.out.println(BLOCK_NUMBERS_INFO);
    }
    // Waits for player to press enter
    waitForEnter();
  }

  private static int getBlockTypeFromCraftedItem(int craftedItem) {
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

  private static int getCraftedItemFromBlockType(int blockType) {
    switch (blockType) {
      case 5:
        return CRAFTED_WOODEN_PLANKS;
      case 6:
        return CRAFTED_STICK;
      case 7:
        return CRAFTED_IRON_INGOT;
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
        craftWoodenPlanks();
        break;
      case 2:
        craftStick();
        break;
      case 3:
        craftIronIngot();
        break;
      default:
        System.out.println("Invalid recipe number.");
    }
    // Waits for player to press enter
    waitForEnter();
  }

  public static void craftWoodenPlanks() {
    if (inventoryContains(WOOD, 2)) {
      removeItemsFromInventory(WOOD, 2);
      addCraftedItem(CRAFTED_WOODEN_PLANKS);
      System.out.println("Crafted Wooden Planks.");
    } else {
      System.out.println("Insufficient resources to craft Wooden Planks.");
    }
  }

  public static void craftStick() {
    if (inventoryContains(WOOD)) {
      removeItemsFromInventory(WOOD, 1);
      addCraftedItem(CRAFTED_STICK);
      System.out.println("Crafted Stick.");
    } else {
      System.out.println("Insufficient resources to craft Stick.");
    }
  }

  public static void craftIronIngot() {
    if (inventoryContains(IRON_ORE, 3)) {
      removeItemsFromInventory(IRON_ORE, 3);
      addCraftedItem(CRAFTED_IRON_INGOT);
      System.out.println("Crafted Iron Ingot.");
    } else {
      System.out.println("Insufficient resources to craft Iron Ingot.");
    }
  }

  public static boolean inventoryContains(int item) {
    return inventory.contains(item);
  }

  public static boolean inventoryContains(int item, int count) {
    int itemCount = 0;
    for (int i : inventory) {
      if (i == item) {
        itemCount++;
        if (itemCount == count) {
          return true;
        }
      }
    }
    return false;
  }

  public static void removeItemsFromInventory(int item, int count) {
    int removedCount = 0;
    Iterator<Integer> iterator = inventory.iterator();
    while (iterator.hasNext()) {
      int i = iterator.next();
      if (i == item) {
        iterator.remove();
        removedCount++;
        if (removedCount == count) {
          break;
        }
      }
    }
  }

  public static void addCraftedItem(int craftedItem) {
    if (craftedItems == null) {
      craftedItems = new ArrayList<>();
    }
    craftedItems.add(craftedItem);
  }

  public static void interactWithWorld() { // Collects one of the the block in the players position without removing the block
    int blockType = world[playerX][playerY];
    switch (blockType) {
      case WOOD:
        System.out.println("You gather wood from the tree.");
        inventory.add(WOOD);
        break;
      case LEAVES:
        System.out.println("You gather leaves from the tree.");
        inventory.add(LEAVES);
        break;
      case STONE:
        System.out.println("You gather stones from the ground.");
        inventory.add(STONE);
        break;
      case IRON_ORE:
        System.out.println("You mine iron ore from the ground.");
        inventory.add(IRON_ORE);
        break;
      case AIR:
        System.out.println("Nothing to interact with here.");
        break;
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

  private static String getBlockName(int blockType) {
    switch (blockType) {
      case AIR:
        return "Empty Block";
      case WOOD:
        return "Wood";
      case LEAVES:
        return "Leaves";
      case STONE:
        return "Stone";
      case IRON_ORE:
        return "Iron Ore";
      default:
        return "Unknown";
    }
  }

  public static void displayLegend() {
    System.out.println(ANSI_BLUE + "Legend:");
    System.out.println(ANSI_WHITE + "-- - Empty block");
    System.out.println(ANSI_RED + "\u2592\u2592 - Wood block");
    System.out.println(ANSI_GREEN + "\u00A7\u00A7 - Leaves block");
    System.out.println(ANSI_BLUE + "\u2593\u2593 - Stone block");
    System.out.println(ANSI_WHITE + "\u00B0\u00B0- Iron ore block");
    System.out.println(ANSI_BLUE + "P - Player" + ANSI_RESET);
  }

  public static void displayInventory() {
    System.out.println("Inventory:");
    if (inventory.isEmpty()) {
      System.out.println(ANSI_YELLOW + "Empty" + ANSI_RESET);
    } else {
      int[] blockCounts = new int[5];
      for (int i = 0; i < inventory.size(); i++) {
        int block = inventory.get(i);
        blockCounts[block]++;
      }
      for (int blockType = 1; blockType < blockCounts.length; blockType++) {
        int occurrences = blockCounts[blockType];
        if (occurrences > 0) {
          System.out.println(getBlockName(blockType) + " - " + occurrences);
        }
      }
    }
    System.out.println("Crafted Items:");
    if (craftedItems == null || craftedItems.isEmpty()) {
      System.out.println(ANSI_YELLOW + "None" + ANSI_RESET);
    } else {
      for (int item : craftedItems) {
        System.out.print(getCraftedItemColor(item) + getCraftedItemName(item) + ", " + ANSI_RESET);
      }
      System.out.println();
    }
    System.out.println();
  }

  private static String getBlockColor(int blockType) {
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

  private static void waitForEnter() {
    System.out.println("Press Enter to continue...");
    Scanner scanner = new Scanner(System.in);
    scanner.nextLine();
  }

  private static String getCraftedItemName(int craftedItem) {
    switch (craftedItem) {
      case CRAFTED_WOODEN_PLANKS:
        return "Wooden Planks";
      case CRAFTED_STICK:
        return "Stick";
      case CRAFTED_IRON_INGOT:
        return "Iron Ingot";
      default:
        return "Unknown";
    }
  }

  private static String getCraftedItemColor(int craftedItem) {
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