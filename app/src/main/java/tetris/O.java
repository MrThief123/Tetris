// O.java
package tetris;

import ch.aplu.jgamegrid.Location;

class O extends Piece {
    O(Tetris tetris) {
        super(tetris);
    }

    @Override
    public BlockPieces getBlockPiece() {
        return BlockPieces.O;
    }

    /**
     * Assigns rotation coordinates for rotating block
     * @return array with all locations
     */
    @Override
    protected Location[][] buildRotationLocations() {
        Location[][] rotationLocation = new Location[4][4];
        // rotationId 0
        rotationLocation[0][0] = new Location(new Location(0, 0));
        rotationLocation[1][0] = new Location(new Location(1, 0));
        rotationLocation[2][0] = new Location(new Location(1, 1));
        rotationLocation[3][0] = new Location(new Location(0, 1));
        // rotationId 1
        rotationLocation[0][1] = new Location(new Location(0, 0));
        rotationLocation[1][1] = new Location(new Location(1, 0));
        rotationLocation[2][1] = new Location(new Location(1, 1));
        rotationLocation[3][1] = new Location(new Location(0, 1));
        // rotationId 2
        rotationLocation[0][2] = new Location(new Location(0, 0));
        rotationLocation[1][2] = new Location(new Location(1, 0));
        rotationLocation[2][2] = new Location(new Location(1, 1));
        rotationLocation[3][2] = new Location(new Location(0, 1));
        // rotationId 3
        rotationLocation[0][3] = new Location(new Location(0, 0));
        rotationLocation[1][3] = new Location(new Location(1, 0));
        rotationLocation[2][3] = new Location(new Location(1, 1));
        rotationLocation[3][3] = new Location(new Location(0, 1));
        return rotationLocation;
    }

    /**
     * Creates locations for where each individual block of piece should be
     * @return array with all relative highlight locations
     */
    @Override
    protected Location[][] buildRelativeHighlightLocations() {
        Location[][] highlightLocations = new Location[2][2];
        highlightLocations[0][0] = new Location(0, 0);
        highlightLocations[1][0] = new Location(1, 0);
        highlightLocations[0][1] = new Location(0, 1);
        highlightLocations[1][1] = new Location(1, 1);
        return highlightLocations;
    }
}