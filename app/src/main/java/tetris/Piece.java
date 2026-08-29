package tetris;

import ch.aplu.jgamegrid.*;
import java.util.ArrayList;

/**
 * Abstract class for every tetris piece and shape spawned within the game.
 * Contains information and methods about block layout, falling, rotation
 * and collision logic.
 */
public abstract class Piece extends Actor {
    protected final Tetris tetris;
    private final Location[][] rotationLocation;
    private final Location[][] relativeHighlightLocations;
    private final Location[][] absoluteHighlightLocations;
    protected final ArrayList<TetroBlock> blocks = new ArrayList<>();
    private boolean isStarting = true;
    private int rotationId = 0;
    private int nb;
    private Actor nextTetrisBlock = null;
    private String autoBlockMove = "";
    private int autoBlockIndex = 0;
    private int fallSpeed = 1;
    private Location plannedStartLocation = new Location(6, 0); // Where piece is starts before random falling

    protected Piece(Tetris tetris) {
        super();
        this.tetris = tetris;
        this.rotationLocation = buildRotationLocations();
        this.relativeHighlightLocations = buildRelativeHighlightLocations();
        this.absoluteHighlightLocations =
                new Location[relativeHighlightLocations.length][relativeHighlightLocations[0].length];
        int blockIndex = getBlockPiece().getBlockIndex();
        for (int i = 0; i < rotationLocation.length; i++) {
            blocks.add(new TetroBlock(blockIndex, rotationLocation[i]));
        }
    }

    public abstract BlockPieces getBlockPiece();
    protected abstract Location[][] buildRotationLocations(); // Create rotation coordinates for piece
    protected abstract Location[][] buildRelativeHighlightLocations(); // Create coordinates for piece shape

    // Getters and setters
    @Override
    public boolean isRotatable() {
        return true;
    }

    public void setFallSpeed(int fallSpeed) {
        this.fallSpeed = Math.max(1, fallSpeed); // Ensure fall speed is at least 1
    }

    public void setPlannedStartLocation(Location location) {
        this.plannedStartLocation = location;
    }

    public Location getPlannedStartLocation() {
        return plannedStartLocation;
    }

    public void setAutoBlockMove(String autoBlockMove) {
        this.autoBlockMove = autoBlockMove;
    }

    /**
     * The game is called in a run loop, this method for a block is called every 1/30 seconds as the starting point
     */
    public void act() {
        if (isStarting) {
            for (TetroBlock a : blocks) {
                Location loc = new Location(
                        getX() + a.getRelativeLocation(0).x,
                        getY() + a.getRelativeLocation(0).y);
                gameGrid.addActor(a, loc);
            }
            hightlightLocation(true);
            isStarting = false;
            nb = 0;
        } else if (canAutoPlay()) {
            autoMove();
        } else {
            setDirection(90);
            if (nb == 1)
                nextTetrisBlock = tetris.createRandomTetrisBlock();

            // Make piece fall by fall speed instead of 1 row at a time
            boolean landed = false;
            for (int i = 0; i < fallSpeed; i++) {
                if (!advance()) {
                    // Piece collides with another piece during fall
                    landed = true;
                    break;
                }
            }

            if (landed) {
                if (nb == 0) {
                    // Game is over when tetrisBlock cannot fall down
                    tetris.gameOver();
                } else {
                    // Block landed and board is not full
                    setActEnabled(false);
                    tetris.onPieceLanded(this, nextTetrisBlock); // Spawn a new piece
                }
            }
            nb++;
        }

        if (nb == 4) {
            hightlightLocation(false);
        }
    }

    /**
     * Turn on and off highlight to show the surrounding rectangle of a block
     * @param isHighlight turn on or off the highlight
     */
    private void hightlightLocation(boolean isHighlight) {
        for (int i = 0; i < relativeHighlightLocations.length; i++) {
            for (int j = 0; j < relativeHighlightLocations[i].length; j++) {
                if (isHighlight) {
                    Location rel = relativeHighlightLocations[i][j];
                    absoluteHighlightLocations[i][j] =
                            new Location(getX() + rel.getX(), getY() + rel.getY());
                }
                tetris.highlightLocations(absoluteHighlightLocations[i][j], isHighlight);
            }
        }
    }

    /**
     * Based on the input in the properties file, the block can move automatically
     */
    private void autoMove() {
        String moveString = autoBlockMove.substring(autoBlockIndex, autoBlockIndex + 1);
        switch (moveString) {
            case "L": left(); break;
            case "R": right(); break;
            case "T": rotate(); break;
            default: break;
        }
        autoBlockIndex++;
    }

    /**
     * Check if the block can be played automatically based on the properties file
     */
    private boolean canAutoPlay() {
        return autoBlockMove != null
                && !autoBlockMove.isEmpty()
                && autoBlockMove.length() > autoBlockIndex;
    }

    void display(GameGrid gg, Location location) {
        for (TetroBlock a : blocks) {
            Location loc = new Location(
            location.x + a.getRelativeLocation(0).x,
            location.y + a.getRelativeLocation(0).y);
            gg.addActor(a, loc);
        }
    }

    // Actual actions on the block: move the block left, right, drop and rotate the block
    void left() {
        if (isStarting) return;
        setDirection(180);
        advance();
    }

    void right() {
        if (isStarting) return;
        setDirection(0);
        advance();
    }

    void rotate() {
        if (isStarting) return;
        if (!isRotatable()) return;

        int oldRotationId = rotationId;
        rotationId = (rotationId + 1) % 4;

        if (canRotate(rotationId)) {
            for (TetroBlock a : blocks) {
                Location loc = new Location(
                        getX() + a.getRelativeLocation(rotationId).x,
                        getY() + a.getRelativeLocation(rotationId).y);
                a.setLocation(loc);
            }
        } else {
            rotationId = oldRotationId;
        }
    }

    void drop() {
        if (isStarting) return;
        tetris.speedup();
    }

    private boolean canRotate(int rotationId) {
        // Check for every rotated tetroBlock within the tetrisBlock
        for (TetroBlock a : blocks) {
            int lx = getX() + a.getRelativeLocation(rotationId).x;
            int ly = getY() + a.getRelativeLocation(rotationId).y;
            Location loc = new Location(lx, ly);
            TetroBlock block = (TetroBlock) gameGrid.getOneActorAt(loc, TetroBlock.class);

            if (!tetris.isInsideBoundary(loc)) {
                // outside the grid boundary
                return false;
            }
            if (blocks.contains(block)) {
                // in same tetrisBlock->skip
                continue;
            }
            if (block != null) {
                // Another tetroBlock->not permitted
                return false;
            }
        }
        return true;
    }

    // Logic to check if the block has been removed (as winning a line) or drop to the bottom
    private boolean advance() {
        boolean canMove = false;
        for (TetroBlock a : blocks) {
            if (!a.isRemoved()) {
                canMove = true; break;
            }
        }
        for (TetroBlock a : blocks) {
            if (a.isRemoved()) continue;
            if (!gameGrid.isInGrid(a.getNextMoveLocation())) {
                canMove = false; break;
            }
        }
        for (TetroBlock a : blocks) {
            if (a.isRemoved()) continue;
            TetroBlock block = (TetroBlock) gameGrid.getOneActorAt(
                    a.getNextMoveLocation(), TetroBlock.class);
            if (block != null && !blocks.contains(block)) {
                canMove = false; break;
            }
        }

        if (canMove) {
            move();
            return true;
        }
        return false;
    }

    /**
     * Override Actor.setDirection()
     */
    @Override
    public void setDirection(double dir) {
        super.setDirection(dir);
        for (TetroBlock a : blocks) a.setDirection(dir);
    }

    /**
     * Override Actor.move()
     */
    @Override
    public void move() {
        if (isRemoved()) return;
        super.move();
        for (TetroBlock a : blocks) {
            if (a.isRemoved()) break;
            a.move();
        }
    }

    /**
     * Override Actor.removeSelf()
     */
    @Override
    public void removeSelf() {
        super.removeSelf();
        for (TetroBlock a : blocks) a.removeSelf();
    }

    /**
     * Override Actor.toString() and return block piece's type, location and rotation id
     */
    @Override
    public String toString() {
        return "Block: " + getBlockPiece().getBlockName()
                + ". Location: " + getX() + "-" + getY()
                + ". Rotation: " + rotationId;
    }
}