public class Engine {

    private int width;
    private int height;

    private int numBombs;

    private Square square[][];

    private GUI gui;


    public Engine(GUI newGui, int newWidth, int newHeight, int bombs){

        width = newWidth;
        height = newHeight;
        numBombs = bombs;
        gui = newGui;

        square = new Square[width][height];

        for (int x = 0; x< width; x++) {
            for (int y = 0; y<height; y++) {
                square[x][y] = new Square();
            }
        }
    }

    public void initialiseBoard(int position){
        int totalBombsPlaced = 0;
        int randint;

        while (totalBombsPlaced < numBombs) {
            
            randint = (int) (Math.random()*width*height);
            if (randint != position){
                
                int x = (int)(randint%width);
                int y = (int) (randint - x)/width;
    
                if (!(square[x][y].getIsBomb())) {
                    square[x][y].updateIsBomb(true);
                    totalBombsPlaced ++;
                }

            }
        }

        int bombsAround;

        for( int y=0; y<height; y++) {
            for (int x = 0; x<width; x++) {
                
                bombsAround = 0;

                if(x == 0) {
                    if (y == 0) {
                        for (int i = 0; i<=1 ; i++){
                            for (int j = 0; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    } else if ( y == height-1) {
                        for (int i = 0; i<=1 ; i++){
                            for (int j = -1; j <= 0; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    } else {
                        for (int i = 0; i<=1 ; i++){
                            for (int j = -1; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    }
                } else if(x == width-1) {
                    if (y == 0) {
                        for (int i = -1; i<=0 ; i++){
                            for (int j = 0; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    } else if (y == height-1) {
                        for (int i = -1; i<=0 ; i++){
                            for (int j = -1; j <= 0; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    } else {
                        for (int i = -1; i<=0 ; i++){
                            for (int j = -1; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    }
                } else {
                    if (y == 0) {
                        for (int i = -1; i<=1 ; i++){
                            for (int j = 0; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    } else if (y == height-1) {
                        for (int i = -1; i<=1 ; i++){
                            for (int j = -1; j <= 0; j++){
                                if ( i == 0 && j == 0) {
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
    
                    } else {
                        //can check all 8
                        for (int i = -1; i<=1 ; i++){
                            for (int j = -1; j <= 1; j++){
                                if ( i == 0 && j == 0) {
                                    continue;
                                } else if (square[x+i][y+j].getIsBomb()){
                                    bombsAround++;
                                }
                            }
                        }
                    }
                }

                square[x][y].updateNumBombsAround(bombsAround);

                
            }
        }

        //place all of the bombs in a random position
        // once they are placed ensure that the clicked square is not a bomb
        // if it is then replace the bomb 
        // reveal the clicked square
    }

    public void clickSquare (int position) {

        //y*gridwidth+x
        int x = (int)(position%width);
        int y = (int) (position - x)/width;

        if (square[x][y].getIsBomb()){
            // game over
            gameOver(x,y);
        } else {
            // game not over
            // all squares that can be revealled are
            revealSquare(x, y);
        }

        if (checkWin()){
            gui.endGame(true);
        }
    }

    public void revealSquare(int x, int y){

        if (square[x][y].getRevealed()){
            return;
        } else if (square[x][y].getFlagged()){
            return;
        }

        //updates the square
        square[x][y].updateRevealed(true);

        //removes the actionlistener from the square so that it cannot be clicked
        gui.removeActionListener(x, y);

        //check if there are any bombs around so we know if to clear a greater area or not
        if (square[x][y].getNumBombsAround() == 0) {
            //there are no bombs around so clear a greater area
            gui.updatePaintedSquare( x, y, gui.CLEAR);
            
            

            //reveal all square around this square, if they are blank repeat if not already revealled
            if(x == 0) {
                if (y == 0) {
                    for (int i = 0; i<=1 ; i++){
                        for (int j = 0; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                } else if ( y == height-1) {
                    for (int i = 0; i<=1 ; i++){
                        for (int j = -1; j <= 0; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                } else {
                    for (int i = 0; i<=1 ; i++){
                        for (int j = -1; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                }
            } else if(x == width-1) {
                if (y == 0) {
                    for (int i = -1; i<=0 ; i++){
                        for (int j = 0; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                } else if (y == height-1) {
                    for (int i = -1; i<=0 ; i++){
                        for (int j = -1; j <= 0; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                } else {
                    for (int i = -1; i<=0 ; i++){
                        for (int j = -1; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                }
            } else {
                if (y == 0) {
                    for (int i = -1; i<=1 ; i++){
                        for (int j = 0; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                } else if (y == height-1) {
                    for (int i = -1; i<=1 ; i++){
                        for (int j = -1; j <= 0; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }

                } else {
                    //can check all 8
                    for (int i = -1; i<=1 ; i++){
                        for (int j = -1; j <= 1; j++){
                            if ( i == 0 && j == 0) {
                                continue;
                            } else {
                                revealSquare(x+i, y+j);
                            }
                        }
                    }
                }
            }
        } else {
            // there is a bomb next to this square, reveal the number of bombs and stop
            gui.updateNumberedSquare(x,y,square[x][y].getNumBombsAround());
            return;
        }
    }

    public boolean checkWin() {
        boolean won = true;


        for( int y=0; y<height; y++) {
            for (int x = 0; x<width; x++) {
                //if there is a square that is not a bomb and has not been revealled then the game is not over


                if (!(square[x][y].getIsBomb())){
                    if (!(square[x][y].getRevealed())){
                        won = false;
                        return won;
                    }
                }           
            }
        }

        return won;
    }

    // end the game revealling all of the bombs
    public void gameOver(int x, int y){
        for( int j=0; j<height; j++) {
            for (int i = 0; i<width; i++) {

                Square placeholder = square[i][j];

                if(!placeholder.getRevealed()){
                    if(placeholder.getIsBomb()){
                        if(placeholder.getFlagged()){
                            //need to show that it was correctly flagged
                            gui.updatePaintedSquare(i, j, gui.BOMBFLAGGEDCORRECT);
                        } else {
                            //need to show that there was a bomb there
                            gui.updatePaintedSquare(i, j, gui.BOMB);
                        }
                    } else if(placeholder.getFlagged()) {
                        // need to show that it was incorrrectly flagged
                        gui.updatePaintedSquare(i, j, gui.BOMBFLAGGEDWRONG);
                    }
                    //otherwise leave as was
                }
            }
        }

        //change the clicked square
        gui.updatePaintedSquare(x, y, gui.BOMBCLICKED);
        gui.endGame(false);
    }

    public Square getSquare(int position) {

        int x = (int)(position%width);
        int y = (int) (position - x)/width;

        return square[x][y];
    }
}
//On the press of a button i want a new board to be created. The square that is clicked cannot be a bomb