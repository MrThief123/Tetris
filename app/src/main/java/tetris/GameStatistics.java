package tetris;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tracks game statistics like score and piece frequency and writes to
 * output file when game ends
 */
public class GameStatistics {
    private final String filePath;
    private int currentRound = 0;
    private String log = ""; // Log of block placements and coordinates
    private int roundScore = 0;
    private final Map<BlockPieces, Integer> roundPieceCounts = new LinkedHashMap<>(); // Piece frequency within round

    public GameStatistics(String filePath) {
        this.filePath = filePath;
        startNewRound();
    }

    /**
     * Increase round count, reset round score and piece frequencies for new round
     */
    public void startNewRound() {
        currentRound++;
        roundScore = 0;
        roundPieceCounts.clear();
        for (BlockPieces piece : BlockPieces.values()) {
            roundPieceCounts.put(piece, 0); // reset frequency count for each piece
        }
    }

    /**
     * Record and increment frequency count for each block shape / piece spawned into game
     * @param shape Block shape
     */
    public void recordPiece(BlockPieces shape) {
        if (roundPieceCounts.containsKey(shape)) {
            roundPieceCounts.put(shape, roundPieceCounts.get(shape) + 1);
        } else {
            roundPieceCounts.put(shape, 1);
        }
    }

    // Record round score
    public void recordScore(int score) {
        this.roundScore = score;
    }

    /**
     * Adds round scores and piece frequency to log once round has ended
     */
    public void endRound() {
        log += "Round #" + currentRound + "\n\n"; // Add current round
        log += "Score: " + roundScore + "\n\n"; // Add round score
        for (Map.Entry<BlockPieces, Integer> entry: roundPieceCounts.entrySet()) {
            // For each key value pair in frequency count (entry), append to log
            log += entry.getKey().getBlockName() + ": " + entry.getValue() + "\n";
        }
        log += "\n-----\n\n";

        // Round is over so reset round attributes
        roundScore = 0;
        resetCounts();
    }

    /**
     * Resets piece frequency to 0
     */
    private void resetCounts() {
        roundPieceCounts.clear();
        for (BlockPieces piece : BlockPieces.values()) {
            roundPieceCounts.put(piece, 0);
        }
    }

    /**
     * Write all rounds into file once game is over
     */
    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.print(log); // Write log to file
            writer.flush(); // Save file
        } catch (IOException e) {
            // Error in creating writer object
            System.err.println("Failed to write statistics: " + e.getMessage());
        }
    }
}