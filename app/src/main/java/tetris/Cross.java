// Cross.java
package tetris;

import ch.aplu.jgamegrid.Location;

class Cross extends Piece {
    Cross(Tetris tetris) {
        super(tetris);
    }

    @Override
    public BlockPieces getBlockPiece() {
        return BlockPieces.CROSS;
    }

    // Stop block from rotating
    @Override
    public boolean isRotatable() {
        return false;
    }

    /**
     * Assigns rotation coordinates for rotating block
     * @return array with all locations
     */
    @Override
    protected Location[][] buildRotationLocations() {
        Location[][] rotationLocations = new Location[5][4];
        // rotationId 0
        rotationLocations[0][0] = new Location(0, 0);
        rotationLocations[1][0] = new Location(2, 0);
        rotationLocations[2][0] = new Location(1, 1);
        rotationLocations[3][0] = new Location(0, 2);
        rotationLocations[4][0] = new Location(2, 2);
        // rotationId 1
        rotationLocations[0][1] = new Location(0, 0);
        rotationLocations[1][1] = new Location(2, 0);
        rotationLocations[2][1] = new Location(1, 1);
        rotationLocations[3][1] = new Location(0, 2);
        rotationLocations[4][1] = new Location(2, 2);
        // rotationId 2
        rotationLocations[0][2] = new Location(0, 0);
        rotationLocations[1][2] = new Location(2, 0);
        rotationLocations[2][2] = new Location(1, 1);
        rotationLocations[3][2] = new Location(0, 2);
        rotationLocations[4][2] = new Location(2, 2);
        // rotationId 3
        rotationLocations[0][3] = new Location(0, 0);
        rotationLocations[1][3] = new Location(2, 0);
        rotationLocations[2][3] = new Location(1, 1);
        rotationLocations[3][3] = new Location(0, 2);
        rotationLocations[4][3] = new Location(2, 2);
        return rotationLocations;
    }

    /**
     * Creates locations for where each individual block of piece should be
     * @return array with all relative highlight locations
     */
    @Override
    protected Location[][] buildRelativeHighlightLocations() {
        Location[][] highlightLocations = new Location[3][3];
        highlightLocations[0][0] = new Location(0, 0);
        highlightLocations[1][0] = new Location(1, 0);
        highlightLocations[2][0] = new Location(2, 0);
        highlightLocations[0][1] = new Location(0, 1);
        highlightLocations[1][1] = new Location(1, 1);
        highlightLocations[2][1] = new Location(2, 1);
        highlightLocations[0][2] = new Location(0, 2);
        highlightLocations[1][2] = new Location(1, 2);
        highlightLocations[2][2] = new Location(2, 2);
        return highlightLocations;
    }
}