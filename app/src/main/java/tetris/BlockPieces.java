package tetris;

/**
 * The enum to represent different block pieces and have different attributes for each block piece.
 * Each block has a factory method for easy construction in Tetris class
 */
public enum BlockPieces {
    // All possible pieces with overridden constructors to create that specific piece
    I (0, "I") {
        @Override public Piece newPiece(Tetris t) {
            return new I(t);
        }
    },

    J (1, "J") {
        @Override public Piece newPiece(Tetris t) {
            return new J(t);
        }
    },

    L (2, "L") {
        @Override public Piece newPiece(Tetris t) {
            return new L(t);
        }
    },

    O (3, "O") {
        @Override public Piece newPiece(Tetris t) {
            return new O(t);
        }
    },

    S (4, "S") {
        @Override public Piece newPiece(Tetris t) {
            return new S(t);
        }
    },

    T (5, "T") {
        @Override public Piece newPiece(Tetris t) {
            return new T(t);
        }
    },

    Z (6, "Z") {
        @Override public Piece newPiece(Tetris t) {
            return new Z(t);
        }
    },

    // New madness blocks
    PLUS (7, "+") {
        @Override public Piece newPiece(Tetris t) {
            return new Plus(t);
        }
    },

    CROSS(8, "X") {
        @Override public Piece newPiece(Tetris t) {
            return new Cross(t);
        }
    },

    SLASH(9, "/") {
        @Override public Piece newPiece(Tetris t) {
            return new Slash(t);
        }
    };

    private final int blockIndex;
    private final String blockName;

    /**
     * Constructor for creating enum value
     * @param blockIndex corresponding block number
     * @param blockName corresponding block name
     */
    BlockPieces(int blockIndex, String blockName) {
        this.blockIndex = blockIndex;
        this.blockName = blockName;
    }

    public int getBlockIndex() {
        return blockIndex;
    }
    public String getBlockName() {
        return blockName;
    }

    /**
     * Factory method for creating new instance of each block
     * @param tetris current game
     * @return Block Piece
     */
    public abstract Piece newPiece(Tetris tetris);

    public static BlockPieces getBlockPiece(String blockName) {
        for (BlockPieces piece : BlockPieces.values()) {
            if (piece.blockName.equals(blockName)) {
                return piece;
            }
        }
        return BlockPieces.I;
    }

    @Override
    public String toString() {
        return blockName;
    }
}