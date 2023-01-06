public class Square {
    
    private boolean revealed;
    private boolean hasBomb;
    private boolean flagged;
    private int numBombsAround;

    public Square(){
        revealed = false;
        hasBomb = false;
        flagged = false;
        numBombsAround = 0;
    }

    public Square(boolean seen, boolean isBomb, int BombsAround, boolean isflagged){
        revealed = seen;
        hasBomb = isBomb;
        numBombsAround = BombsAround;
        flagged = isflagged;
    }

    public boolean getRevealed() {
        return revealed;
    }

    public void updateRevealed(boolean newValue) {
        revealed = newValue;
    }

    public boolean getIsBomb() {
        return hasBomb;
    }

    public void updateIsBomb(boolean newValue) {
        hasBomb = newValue;
    }

    public boolean getFlagged() {
        return flagged;
    }

    public void updateFlagged(boolean newValue) {
        flagged = newValue;
    }

    public int getNumBombsAround() {
        return numBombsAround;
    }

    public void updateNumBombsAround(int newValue) {
        numBombsAround = newValue;
    }



}
