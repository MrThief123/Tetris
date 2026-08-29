// J.java
package tetris;

import ch.aplu.jgamegrid.Location;

class J extends Piece {
    J(Tetris tetris) {
        super(tetris);
    }

    @Override
    public BlockPieces getBlockPiece() {
        return BlockPieces.J;
    }

    /**
     * Assigns rotation coordinates for rotating block
     * @return array with all locations
     */
    @Override
    protected Location[][] buildRotationLocations() {
        Location[][] rotationLocations = new Location[4][4];
        // rotationId 0
        rotationLocations[0][0] = new Location(0, 0);
        rotationLocations[1][0] = new Location(0, 1);
        rotationLocations[2][0] = new Location(1, 1);
        rotationLocations[3][0] = new Location(2, 1);
        // rotationId 1
        rotationLocations[0][1] = new Location(1, 0);
        rotationLocations[1][1] = new Location(0, 0);
        rotationLocations[2][1] = new Location(0, 1);
        rotationLocations[3][1] = new Location(0, 2);
        // rotationId 2
        rotationLocations[0][2] = new Location(0, 0);
        rotationLocations[1][2] = new Location(1, 0);
        rotationLocations[2][2] = new Location(2, 0);
        rotationLocations[3][2] = new Location(2, 1);
        // rotationId 3
        rotationLocations[0][3] = new Location(1, 0);
        rotationLocations[1][3] = new Location(1, 1);
        rotationLocations[2][3] = new Location(1, 2);
        rotationLocations[3][3] = new Location(0, 2);
        return rotationLocations;
    }

    /**
     * Creates locations for where each individual block of piece should be
     * @return array with all relative highlight locations
     */
    @Override
    protected Location[][] buildRelativeHighlightLocations() {
        Location[][] highlightLocation = new Location[3][2];
        highlightLocation[0][0] = new Location(0, 0);
        highlightLocation[1][0] = new Location(1, 0);
        highlightLocation[2][0] = new Location(2, 0);
        highlightLocation[0][1] = new Location(0, 1);
        highlightLocation[1][1] = new Location(1, 1);
        highlightLocation[2][1] = new Location(2, 1);
        return highlightLocation;
    }
}