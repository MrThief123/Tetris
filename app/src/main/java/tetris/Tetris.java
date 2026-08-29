package tetris;

import ch.aplu.jgamegrid.*;
import tetris.utility.Logger;

import java.util.*;
import java.awt.event.KeyEvent;
import java.awt.*;
import javax.swing.*;

public class Tetris extends JFrame implements GGActListener {
    public static final String statisticsFilePath = "statistics.txt";

    private Piece currentBlock = null;    // Currently active block
    private Piece blockPreview = null;    // block in preview window

    private int score = 0;
    private Random random = new Random(0);

    private boolean isAuto = false;

    // Feature flags properties file
    private boolean feature1Active = false;
    private boolean feature2Active = false;

    private int MANUAL_SIMULATION_PERIOD = 300;
    private int MANUAL_DROP_SIMULATION_PERIOD = 50;
    private int AUTO_SIMULATION_PERIOD = 50;

    // For testing mode, the block will be moved automatically based on the blockActions.
    // L is for Left, R is for Right, T is for turning (rotating), and D for down
    private String[] blockActions = null;
    private int blockActionIndex = 0;

    private String[] blockPieces = null;
    private int blockPieceIndex = 0;

    // Locations and speeds from the properties file
    private String[] blockLocations = null;
    private String[] blockSpeeds = null;

    Logger logger = new Logger();

    // stats record
    private GameStatistics statistics;

    /**
     * Initialise all the properties from file
     */
    private void initWithProperties(Properties properties) {
        random = new Random(30006);
        isAuto = Boolean.parseBoolean(properties.getProperty("isAuto"));

        feature1Active = "active".equalsIgnoreCase(properties.getProperty("features.1", "inactive"));
        feature2Active = "active".equalsIgnoreCase(properties.getProperty("features.2", "inactive"));

        // parse properties from file
        blockActions = properties.getProperty("actions",   "").split(",");
        blockPieces = properties.getProperty("pieces",    "").split(",");
        blockLocations = properties.getProperty("locations", "").split(";");
        blockSpeeds = properties.getProperty("speed",     "").split(",");
    }

    public Tetris(Properties properties) {
        initWithProperties(properties);
        blockActionIndex = 0;

        tetrisComponents = new TetrisComponents();
        tetrisComponents.initComponents(this);
        gameGrid1.addActListener(this);
        gameGrid1.setSimulationPeriod(defaultSimulationPeriod());

        // create the statistics class for saving
        statistics = new GameStatistics(statisticsFilePath);

        // Add the first block
        currentBlock = createRandomTetrisBlock();
        gameGrid1.addActor(currentBlock, currentBlock.getPlannedStartLocation());
        gameGrid1.doRun();

        gameGrid2.setFocusable(false);
        setTitle("SWEN30006 Tetris Madness");
        score = 0;
        showScore(score);
    }

    /**
     * The game is called in a run loop, this method sleeps for 500 milliseconds
     * and only check if the game is over after 500 milliseconds
     */
    public String runApp() {
        setVisible(true);
        while (gameGrid1.isRunning()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // save game stats once the game loop stops
        statistics.save();
        return logger.getAllLog();
    }

    /**
     * Get the next block. If there is a value in the properties file,
     *  getting the next available block in the list, otherwise getting a random block
     * @return BlockPieces
     */
    private BlockPieces getNextBlock() {
        if (blockPieceIndex < blockPieces.length && !blockPieces[blockPieceIndex].isEmpty()) {
            return BlockPieces.getBlockPiece(blockPieces[blockPieceIndex++]);
        }
        // Piece random next piece
        int poolSize = feature1Active ? BlockPieces.values().length : 7;
        int rnd = random.nextInt(poolSize);
        return BlockPieces.values()[rnd];
    }

    /**
     * Create piece, setup preview and add random speed if on
     */
    Piece createRandomTetrisBlock() {
        if (blockPreview != null)
            blockPreview.removeSelf();

        // If the game is in auto test mode, then the block will be moved according to the blockActions
        String currentBlockMove = "";
        if (blockActions.length > blockActionIndex
                && !blockActions[blockActionIndex].isEmpty()) {
            currentBlockMove = blockActions[blockActionIndex];
        }
        blockActionIndex++;

        int pieceCursorForLookup = blockPieceIndex;
        BlockPieces shape = getNextBlock();

        // Create new block piece
        Piece active = shape.newPiece(this);
        if (isAuto) {
            active.setAutoBlockMove(currentBlockMove);
        }

        // set random fall speed
        if (feature2Active) {
            active.setFallSpeed(pickFallSpeed(pieceCursorForLookup));
        }

        Piece preview = shape.newPiece(this);
        preview.display(gameGrid2, new Location(2, 1));
        blockPreview = preview;

        // start location on the piece so callers can use it later
        active.setPlannedStartLocation(pickStartLocation(pieceCursorForLookup, active));

        return active;
    }

    /**
     * Choose the start location for a piece.
     */
    private Location pickStartLocation(int pieceCursor, Piece piece) {
        // Always chosen (6,0) when feature 2 is disabled
        if (!feature2Active) {
            return new Location(6, 0);
        }

        // Feature 2 enabled and there's already predefined starting locations from properties file
        if (pieceCursor >= 0 && pieceCursor < blockLocations.length
                && !blockLocations[pieceCursor].isEmpty()) {
            String[] parts = blockLocations[pieceCursor].split("-");
            try {
                int x = Integer.parseInt(parts[0].trim());
                int y = Integer.parseInt(parts[1].trim());
                return new Location(x, y);
            } catch (Exception e) {
                // Unable to parse
            }
        }
        // Fall in random 15x15 grid
        int nbCols = Math.min(15, gameGrid1.getNbHorzCells());
        int x = random.nextInt(nbCols);
        int y = random.nextInt(15);
        return new Location(x, y);
    }

    /**
     * Choose the fall speed for a piece.
     */
    private int pickFallSpeed(int pieceCursorBeforeDraw) {
        int lookup = pieceCursorBeforeDraw;
        // Use speed if already provided
        if (lookup >= 0 && lookup < blockSpeeds.length
                && !blockSpeeds[lookup].isEmpty()) {
            try {
                // Return fall speed
                return Integer.parseInt(blockSpeeds[lookup].trim());
            } catch (Exception e) {
                // Unable to parse so use default speed
            }
        }
        return 1;
    }

    /**
     * Record statistics, spawn the next piece, and restore
     * the default simulation period when piece has landed
     */
    void onPieceLanded(Piece landed, Actor next) {
        // Record piece which just landed
        statistics.recordPiece(landed.getBlockPiece());
        logger.logEvent(currentBlock.toString()); // Log piece which landed
        if (next instanceof Piece nextPiece) {
            // Make next block the current active piece
            currentBlock = nextPiece;
            gameGrid1.addActor(nextPiece, nextPiece.getPlannedStartLocation());
        }
        gameGrid1.setSimulationPeriod(defaultSimulationPeriod()); // reset back to default settings
    }

    public void speedup() {
        gameGrid1.setSimulationPeriod(MANUAL_DROP_SIMULATION_PERIOD);
    }

    /**
     * highlight for a specific location. Check and use the appropriate color
     * @param location
     * @param highlight
     */
    void highlightLocations(Location location, boolean highlight) {
        if (highlight) {
            gameGrid1.getBg().fillCell(location, Color.YELLOW);
        } else {
            gameGrid1.setGridColor(new java.awt.Color(255, 3, 0));
        }
    }

    private int defaultSimulationPeriod() {
        return isAuto ? AUTO_SIMULATION_PERIOD : MANUAL_SIMULATION_PERIOD;
    }

    /**
     * Check if a specific location is within the game grid
     * @param location
     * @return
     */
    public boolean isInsideBoundary(Location location) {
        if (location.getX() < 0) {
            return false;
        }
        return location.getX() < gameGrid1.getNbHorzCells();
    }


    /**
     * Handle user input to move block. Arrow left to move left,
     * Arrow right to move right, Arrow up to rotate and
     * Arrow down for going down
     */
    private void moveBlock(int keyEvent) {
        if (currentBlock == null) return;
        switch (keyEvent) {
            case KeyEvent.VK_UP: currentBlock.rotate(); break;
            case KeyEvent.VK_LEFT: currentBlock.left(); break;
            case KeyEvent.VK_RIGHT: currentBlock.right(); break;
            case KeyEvent.VK_DOWN: currentBlock.drop(); break;
            default: return;
        }
    }

    /**
     * The game is called in a run loop, this method for a tetris is called every
     * 1/30 seconds as the starting point
     */
    public void act() {
        removeFilledLine();
        moveBlock(gameGrid1.getKeyCode());
        logger.logEvent(currentBlock.toString());
    }

    /**
     * Check if a line is completely filled and clear the line and update the score
     */
    private void removeFilledLine() {
        for (int y = 0; y < gameGrid1.nbVertCells; y++) {
            boolean isLineComplete = true;
            TetroBlock[] blocks = new TetroBlock[gameGrid1.nbHorzCells]; // One line
            // Calculate if a line is complete
            for (int x = 0; x < gameGrid1.nbHorzCells; x++) {
                blocks[x] = (TetroBlock) gameGrid1.getOneActorAt(
                        new Location(x, y), TetroBlock.class);
                if (blocks[x] == null){
                    isLineComplete = false;
                    break;
                }
            }
            if (isLineComplete) {
                // If a line is complete, we remove the component block of the shape that belongs to that line
                for (int x = 0; x < gameGrid1.nbHorzCells; x++) {
                    gameGrid1.removeActor(blocks[x]);
                }
                ArrayList<Actor> allBlocks = gameGrid1.getActors(TetroBlock.class);
                for (Actor a : allBlocks) {
                    int z = a.getY();
                    if (z < y) {
                        a.setY(z + 1);
                    }
                }
                gameGrid1.refresh();
                score++;
                showScore(score);
                logger.logEvent("Score: " + score);
                statistics.recordScore(score);
            }
        }
    }

    /**
     * Show Score
     */
    private void showScore(final int score) {
        scoreText.setText(score + " points");
    }

    /**
     * Display the game over
     */
    void gameOver() {
        gameGrid1.addActor(new Actor("sprites/gameover.gif"), new Location(5, 5));
        gameGrid1.doPause();
        if (isAuto) gameGrid1.doPause();

        // count the piece that caused game over
        if (currentBlock != null) {
            statistics.recordPiece(currentBlock.getBlockPiece());
        }

        // save to file
        statistics.endRound();
        statistics.save();
    }

    /**
     * Start a new game
     */
    public void startBtnActionPerformed(java.awt.event.ActionEvent evt) {
        gameGrid1.doPause();
        gameGrid1.removeAllActors();
        gameGrid2.removeAllActors();
        gameGrid1.refresh();
        gameGrid2.refresh();
        gameGrid2.delay(getDelayTime());

        // close out the previous round, then start a new stats counter
        statistics.endRound();
        statistics.startNewRound();

        blockActionIndex = 0;
        blockPieceIndex = 0;
        currentBlock = createRandomTetrisBlock();
        gameGrid1.addActor(currentBlock, currentBlock.getPlannedStartLocation());
        gameGrid1.doRun();
        gameGrid1.requestFocus();
        score = 0;
        showScore(score);
    }

    private int getDelayTime() {
        return isAuto ? 500 : 2000;
    }

    // AUTO GENERATED - do not modify//GEN-BEGIN:variables
    public ch.aplu.jgamegrid.GameGrid gameGrid1;
    public ch.aplu.jgamegrid.GameGrid gameGrid2;
    public javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel3;
    public javax.swing.JPanel jPanel4;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextField scoreText;
    public javax.swing.JButton startBtn;
    private TetrisComponents tetrisComponents;
    // End of variables declaration//GEN-END:variables
}