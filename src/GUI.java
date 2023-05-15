import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class GUI {

    private Engine engine;

    private JFrame frame;

    private JMenuBar menuBar;

    private boolean started = false;

    private BoardButton[][] buttonsArray;

    private int gridWidth = 15;
    private int gridHeight = 10;

    private int numBombs = 10;
    private int flags = 9;

    private final JPanel gameInformation = new JPanel();
    private final JPanel board = new JPanel();
    private final JPanel topSide = new JPanel();
    private final JPanel leftSide = new JPanel();
    private final JPanel rightSide = new JPanel();

    private final optionListener optionButton = new optionListener();
    private final gameListener gameButton = new gameListener();

    private Image[] boardImages;
    private static Image[] numberImages;
    private Image[] faceImages;

    private final int ImageWidth = 20;
    private final int ImageHeight = 20;

    private static int watch = 0;
    private static Timer time;

    private static JLabel timeh;
    private static JLabel timet; 
    private static JLabel timeo;
    private static JLabel timeth;

    private static JButton face;

    private static JLabel bombth;
    private static JLabel bombh;
    private static JLabel bombt;
    private static JLabel bombo;

    private static final int DEAD = 0;
    private static final int SMILE = 2;
    private static final int WIN = 3;

    public final int UNCLICKED = 0;
    public final int FLAGGED = 1;
    public final int QUESTION = 2;
    public final int CLICKED = 3;
    public final int BOMB = 4;
    public final int BOMBCLICKED = 5;
    public final int BOMBFLAGGEDWRONG = 6;
    public final int BOMBFLAGGEDCORRECT = 7;
    public final int PRESSEDBLANK = 8;

    public final int CLEAR = -1;


    // constructor
    public GUI() {

        readInImages();
        fillFrame();

        engine = new Engine(this, gridWidth, gridHeight, numBombs);
    }

    private void resizeFrame() {
        int totalHeight;
      
        int gameInformationWidth = gameInformation.getWidth();
        int gameInformationHeight = gameInformation.getHeight();

        int sideMargins = 10;
        int boardWidth = board.getWidth() + 2* sideMargins;
        int topMargin = 10;
        int bottomMargin = 10;
        int boardHeight = board.getHeight() + topMargin + bottomMargin;

        totalHeight = gameInformationHeight + boardHeight;

        frame.setSize(Math.max(boardWidth, gameInformationWidth), totalHeight);

        frame.pack();
    }

    private void fillFrame() {

        frame = new JFrame();

        frame.setTitle("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);


        createMenu();
                
        newBoard();
        createGamePanel();

        Container contentPane = frame.getContentPane();

        BorderLayout borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);

        contentPane.add(board,BorderLayout.CENTER);
        contentPane.add(gameInformation,BorderLayout.SOUTH);
        contentPane.add(rightSide, BorderLayout.LINE_END);
        contentPane.add(leftSide, BorderLayout.LINE_START);

        contentPane.add(topSide,BorderLayout.NORTH);

        frame.setJMenuBar(menuBar);


        frame.setVisible(true);
        resizeFrame();
    }

    private void createMenu() {

        //New Menu Bar at the top of the window
        menuBar = new JMenuBar();


        JMenu menu = new JMenu("Game");
        menuBar.add(menu);
        

        //New Game
        //Option
        //Exit
        JMenuItem menuItem = new JMenuItem("New Game");
        menuItem.addActionListener(e -> {
            //The timer and board are reset to prepare a new game
            endTime();
            resetGame();
        });
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("Options");
        menuItem.addActionListener(optionButton);
        menu.add(menuItem);

        menuItem = new JMenuItem("Help");
        menuItem.addActionListener(e -> {
            // gives a link to the wikipedia for minesweeper
            endTime();
            resetGame();
            JOptionPane.showConfirmDialog(null, """
                    This is a java implementation of the classic computer game Minesweeper.\s
                    The aim of the game is to clear the board without clicking on any mines.\s
                    A full explanation of the rules of minesweeper can be found when following\s
                    the following link: https://mathworld.wolfram.com/Minesweeper.html""","Help", JOptionPane.DEFAULT_OPTION);
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(e -> {
            //closes the frame
            frame.dispose();
        });
        menu.add(menuItem);
    }

    private void createGamePanel() {
        FlowLayout layout = new FlowLayout();
        gameInformation.setLayout(layout);

        bombth = new JLabel("");
        bombh = new JLabel("");
        bombt = new JLabel("");
        bombo = new JLabel("");

        gameInformation.add(bombth);
        gameInformation.add(bombh);
        gameInformation.add(bombt);
        gameInformation.add(bombo);
        gameInformation.add(Box.createHorizontalGlue());
        setBombs(numBombs);

        face = new JButton("");
        face.addActionListener(e -> {
            //if a game is has been started then reset the game,
            endTime();
            resetGame();
        });
        face.setBorderPainted(false);
        gameInformation.add(face);
        gameInformation.add(Box.createHorizontalGlue());
        updateFace(SMILE);

        timeth = new JLabel("");
        timeh = new JLabel("");
		timet = new JLabel("");
		timeo = new JLabel("");

        gameInformation.add(timeth);
        gameInformation.add(timeh);
        gameInformation.add(timet);
        gameInformation.add(timeo);
        gameInformation.add(Box.createHorizontalGlue());
        nullTime();
    }

    private void resetGame() {
        //want to get a new engine
        engine = null;
        engine = new Engine(this, gridWidth, gridHeight, numBombs);

        //rebuild the board
        newBoard();

        //reset the timer
        nullTime();

        //reset the bomb count
        setBombs(numBombs);

        //reset the face
        updateFace(SMILE);

        //started = false
        started = false;

    }


    private void newBoard() {

        GridLayout layout = new GridLayout(gridHeight, gridWidth, 0, 0);
        board.setLayout(layout);
        board.setPreferredSize(new Dimension((ImageWidth+1)*gridWidth,(ImageWidth+1)*gridHeight));
        board.setMinimumSize(board.getPreferredSize());
        board.setMaximumSize(board.getPreferredSize());
        board.setSize(board.getPreferredSize());
        

        board.removeAll();
        board.repaint();

        buttonsArray = new BoardButton[gridWidth][gridHeight];

        for (int position = 0; position<(gridHeight*gridWidth); position ++ ){
            int x = position%gridWidth;
            int y = (position - x) /gridWidth;

            //buttonsArray[x][y] = null;
            buttonsArray[x][y] = new BoardButton(this, (position));

            //changes the board to a blank image
            paintButton(buttonsArray[x][y],UNCLICKED);

            addPressedButtonAnimation(buttonsArray[x][y]);
            buttonsArray[x][y].setName(String.valueOf(position)); //Stores the position of the button in the array
            buttonsArray[x][y].addMouseListener(gameButton);

            board.add(buttonsArray[x][y]);

            frame.setVisible(true);
        }
    }


    /**
     * printBoard()
     * <p>
     * INPUTS: none
     * OUTPUT: none
     * <p>
     * Description: Used for debugging, when called will print out the current board to
     * the terminal showing the position of bombs with a 'B',
     * blank squares with the number of bombs they are adjacent to
     */
    /* Debugging Tool
    public void printBoard(){
        System.out.println();
        for (int y = 0; y< gridHeight; y++) {
            StringBuilder line = new StringBuilder();
            for (int x = 0; x<gridWidth; x++) {
                Square square = engine.getSquare(y*gridWidth+x);
                if(square.getIsBomb()){
                    line.append("B");
                } else{
                    int numBombs = square.getNumBombsAround();
                    line.append(numBombs);
                }
            }
            System.out.println(line);
        }
    }
    //*/
        

    private void paintButton(BoardButton button, int imageType) {
        ImageIcon image;
        Image scaledImage;

        switch (imageType) {
            case 0 -> {
                scaledImage = boardImages[UNCLICKED].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 1 -> {
                scaledImage = boardImages[FLAGGED].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 2 -> {
                scaledImage = boardImages[QUESTION].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 3 -> {
                scaledImage = boardImages[CLICKED].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 4 -> {
                scaledImage = boardImages[BOMB].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 5 -> {
                scaledImage = boardImages[BOMBCLICKED].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 6 -> {
                scaledImage = boardImages[BOMBFLAGGEDWRONG].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            case 7 -> {
                scaledImage = boardImages[BOMBFLAGGEDCORRECT].getScaledInstance(ImageWidth, ImageHeight, Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
            }
            default -> button.setIcon(null);
        }
        frame.setVisible(true);

    }

    private void numberBoard(BoardButton button, int numberOfBombs) {

        button.setFont(new Font(Font.DIALOG_INPUT,  Font.BOLD, 15));

        switch (numberOfBombs) {
            case 1 -> {
                button.setText("1");
                button.setForeground(new Color(10, 10, 10));
            }
            case 2 -> {
                button.setText("2");
                button.setForeground(new Color(10, 200, 100));
            }
            case 3 -> {
                button.setText("3");
                button.setForeground(new Color(10, 10, 200));
            }
            case 4 -> {
                button.setText("4");
                button.setForeground(new Color(10, 100, 10));
            }
            case 5 -> {
                button.setText("5");
                button.setForeground(new Color(10, 100, 10));
            }
            case 6 -> {
                button.setText("6");
                button.setForeground(new Color(200, 10, 10));
            }
            case 7 -> {
                button.setText("7");
                button.setForeground(new Color(100, 10, 10));
            }
            case 8 -> {
                button.setText("8");
                button.setForeground(new Color(70, 10, 10));
            }
            default -> button.setText(null);
        }
    }

    private void addPressedButtonAnimation(BoardButton button) {
        Image scaledImage = boardImages[PRESSEDBLANK].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(scaledImage);
        button.setPressedIcon(image);
    }

    private void removePressedButtonAnimation(BoardButton button) {
        button.setPressedIcon(null);
    }

    public int getGridHeight(){
        return gridHeight;
    }
    public int getGridWidth(){
        return gridWidth;
    }
    public void changeGridHeight(int newHeight){
        gridHeight = newHeight;
    }
    public void changeGridWidth(int newWidth){
        gridWidth = newWidth;
    }

    public void removeActionListener(int x, int y) {
        buttonsArray[x][y].removeMouseListener(gameButton);
    }

    public void updatePaintedSquare(int x, int y, int newFace) {
        BoardButton button = buttonsArray[x][y];
        paintButton(button, newFace);
        numberBoard(button, CLEAR);
    }

    public void updateNumberedSquare(int x, int y, int numberOfBombs) {
        BoardButton button = buttonsArray[x][y];
        paintButton(button, CLEAR);
        numberBoard(button, numberOfBombs);
    }

    public void endGame(boolean gameWon) {
        endTime();
        
        if(gameWon) {
            updateFace(WIN);
        } else {
            updateFace(DEAD);
        }

        for (int position = 0; position<(gridHeight*gridWidth); position ++ ){
            int x = position%gridWidth;
            int y = (position - x) /gridWidth;

            removeActionListener(x,y);

        }
    }

    




    //Reads in images to BufferedImages from Assets file
    private void readInImages(){

        //read in images for the board
        boardImages = new Image[9];
        String[] boardAssets = {""};

        try{
            boardAssets = (new File("src/Assests/Board")).list();
        } catch (NullPointerException e) {
            System.out.println("error with board Images");
        }

        for(int i = 0; i<boardImages.length; i++) {
            try{
                assert boardAssets != null;
                boardImages[i] = ImageIO.read(new File("src/Assests/Board/" + boardAssets[i]));
            } catch (IOException e) {
                System.out.println("error with board Images");
            }
        }

        //read in images for the numbers
        numberImages = new Image[11];
        String[] numberAssets = {""};
        try{
            numberAssets = (new File("src/Assests/Information/Time")).list();
        } catch (NullPointerException e) {
            System.out.println("error with number Images");
        }

        for(int i = 0; i<numberImages.length; i++) {
            try{
                assert numberAssets != null;
                numberImages[i] = ImageIO.read(new File("src/Assests/Information/Time/" + numberAssets[i]));
            } catch (IOException e) {
                System.out.println("error with number Images");
            }
        }

        //read in images for the face
        faceImages = new Image[4];
        String[] faceAssets = {""};
        try{
            faceAssets = (new File("src/Assests/Information/Face")).list();
        } catch (NullPointerException e) {
            System.out.println("error with face Images");
        }

        for(int i = 0; i<faceImages.length; i++) {
            try{
                assert faceAssets != null;
                faceImages[i] = ImageIO.read(new File("src/Assests/Information/Face/" + faceAssets[i]));
            } catch (IOException e) {
                System.out.println("error with face Images");
            }
        }
    }

    private void resetTime() {
		watch = 0;
		setTime();
	}
    public void startTime() {
		watch = 0;
		time = new Timer(1000, evt -> {
            watch++;
            setTime();
        });
		time.start();
	}
	private void setTime() {

        int th = (watch%10000)/1000;
		int h = (watch%1000)/100;
        int t = (watch%100)/10;
        int o = watch % 10;
        try{
            timeth.setIcon(new ImageIcon(numberImages[th].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            timeh.setIcon(new ImageIcon(numberImages[h].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            timet.setIcon(new ImageIcon(numberImages[t].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            timeo.setIcon(new ImageIcon(numberImages[o].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Run out of time");
        }
	}

    private void endTime() {
        try{
            time.stop();
        } catch (NullPointerException e) {
            System.out.println("Time has not been initiated, start a game to initiate time");
        }
    }

    private void nullTime(){
        timeth.setIcon(new ImageIcon(numberImages[10].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
        timeh.setIcon(new ImageIcon(numberImages[10].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
        timet.setIcon(new ImageIcon(numberImages[10].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
        timeo.setIcon(new ImageIcon(numberImages[10].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
    }


    private void setBombs(int bombs) {
        //int bombs = engine.getNoBombs();
        
        int th = (bombs%10000)/1000;
		int h = (bombs%1000)/100;
        int t = (bombs%100)/10;
        int o = bombs % 10;

        try{
            bombth.setIcon(new ImageIcon(numberImages[th].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            bombh.setIcon(new ImageIcon(numberImages[h].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            bombt.setIcon(new ImageIcon(numberImages[t].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
            bombo.setIcon(new ImageIcon(numberImages[o].getScaledInstance(15,30,java.awt.Image.SCALE_SMOOTH)));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Too many bombs");
        }
    }

    private int minNumberBombs(int height, int width) {
        //5% of the board is bombs
        int numSquares = height*width;
        return (int)(0.05*numSquares);
    }

    private int maxNumberBombs(int height, int width) {
        //65% of the board is bombs
        int numSquares = height*width;
        return (int)(0.65*numSquares);
    }

    private void updateFace(int faceType){
        Image scaledImage = faceImages[faceType].getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(scaledImage);
        face.setIcon(image);
    }

    public Engine getEngine() {
        return engine;
    }

    class optionListener implements ActionListener {
        private SpinnerNumberModel bombModel;
        private JSpinner bombSpinner;

        private JSpinner heightSpinner;

        private JSpinner widthSpinner;

        private JDialog options;


        public void actionPerformed(ActionEvent e) {

            endTime();

            options = new JDialog(frame, "Options", Dialog.ModalityType.DOCUMENT_MODAL);

            options.setSize(300, 200);
            options.setLayout(new FlowLayout());


            int MINGRIDHEIGHT = 10;
            int MAXGRIDHEIGHT = 1000;
            SpinnerNumberModel heightModel = new SpinnerNumberModel(getGridHeight(), MINGRIDHEIGHT, MAXGRIDHEIGHT, 1);
            heightSpinner = new JSpinner(heightModel);

            int MINGRIDWIDTH = 10;
            int MAXGRIDWIDTH = 1000;
            SpinnerNumberModel widthModel = new SpinnerNumberModel(getGridWidth(), MINGRIDWIDTH, MAXGRIDWIDTH, 1);
            widthSpinner = new JSpinner(widthModel);

            options.add(new JLabel("Height"));
            options.add(heightSpinner);
            options.add(new JLabel("Width"));
            options.add(widthSpinner);

            //number of bombs
            bombModel = new SpinnerNumberModel(numBombs, minNumberBombs(getGridHeight(),getGridWidth()), maxNumberBombs(getGridHeight(),getGridWidth()),1);
            bombSpinner = new JSpinner(bombModel);

            options.add(new JLabel("Bombs"));
            options.add(bombSpinner);
            //buttons to apply settings and cancel to revert to what was already there

            heightSpinner.addChangeListener(e1 -> {

                Object value = widthSpinner.getValue();
                int width = Integer.parseInt(value.toString());

                value = heightSpinner.getValue();
                int height = Integer.parseInt(value.toString());

                updateBombLimits(height,width);
            });
            widthSpinner.addChangeListener(e12 -> {

                Object value = widthSpinner.getValue();
                int width = Integer.parseInt(value.toString());

                value = heightSpinner.getValue();
                int height = Integer.parseInt(value.toString());

                updateBombLimits(height,width);
            });

            JButton apply = new JButton("Apply");
            apply.addActionListener(e13 -> {

                int newHeight = Integer.parseInt(heightSpinner.getValue().toString());
                int newWidth = Integer.parseInt(widthSpinner.getValue().toString());
                int newBombValue = Integer.parseInt(bombSpinner.getValue().toString());

                numBombs = newBombValue;

                setBombs(newBombValue);
                changeGridHeight(newHeight);
                changeGridWidth(newWidth);

                resetTime();
                resetGame();
                resizeFrame();
                options.dispose();
            });
            
            options.add(apply);

            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(e14 -> {
                resetTime();
                resetGame();
                resizeFrame();
                options.dispose();
            });

            options.add(cancel);


            options.pack();
            options.setVisible(true);
        }
        
        public void updateBombLimits(int height, int width){

            int minNum = minNumberBombs(height, width);
            int maxNum = maxNumberBombs(height, width);

            bombModel.setMinimum(minNum);
            bombModel.setMaximum(maxNum);

            int bombValue = Integer.parseInt(bombSpinner.getValue().toString());

            if(bombValue<minNum){
                bombModel.setValue(minNum);
            } else if (bombValue>maxNum) {
                bombModel.setValue(maxNum);
            }

            bombSpinner = new JSpinner(bombModel);
            //System.out.println(bombSpinner.getValue());
            options.pack();

        }
    }

    static class BoardButton extends JButton {
        private final boolean isClicked;
        private int blankFlagOrQuestion; //either 0,1 or 2. 0=blank, 1=flag, 2=question
        private final GUI gui;
        private final int position;

        public BoardButton(GUI newGUI, int name) {
            super();
            isClicked = false;
            blankFlagOrQuestion = 0;
            this.gui = newGUI; 
            position = name;
            this.setMargin(new Insets(0,0,0,0));
            this.setBackground(new Color(192,192,192));
        }

        public boolean getClicked() {
            return isClicked;
        }

        public int getBlankFlagOrQuestion(){
            return blankFlagOrQuestion;
        }

        public void updateBlankFlagOrQuestion(int number) {
            blankFlagOrQuestion = number;
        }

        public GUI getGUI(){
            return gui;
        }

        public int getPosition() {
            return position;
        }
    }

    class gameListener extends MouseAdapter {
        //add the appropriate listeners for left and right clicks

        GUI gui;

        @Override
        public void mousePressed(MouseEvent e) {

            BoardButton source = (BoardButton)(e.getSource());
            gui = source.getGUI();
            int position = source.getPosition();       

            
            if(!started) {
                started = true;
                flags = numBombs;
                resetTime();
                startTime();
                gui.getEngine().initialiseBoard(position);
            }

            if(SwingUtilities.isLeftMouseButton(e)) {

                //blankBoard();

                if(!(gui.getEngine().getSquare(position).getFlagged())) {
                    gui.getEngine().clickSquare(position);
                    
                }           
            } else if (SwingUtilities.isRightMouseButton(e)) {


                if (!(source.getClicked())){
                    switch (source.getBlankFlagOrQuestion()) {
                        case 0 -> {
                            if (flags != 0) {
                                source.updateBlankFlagOrQuestion(1);
                                paintButton(source, FLAGGED);
                                removePressedButtonAnimation(source);
                                flags--;
                                setBombs(flags);
                                gui.getEngine().getSquare(source.getPosition()).updateFlagged(true);
                            }
                        }
                        case 1 -> {
                            source.updateBlankFlagOrQuestion(2);
                            paintButton(source, QUESTION);
                            removePressedButtonAnimation(source);
                        }
                        case 2 -> {
                            source.updateBlankFlagOrQuestion(0);
                            paintButton(source, UNCLICKED);
                            addPressedButtonAnimation(source);
                            flags++;
                            setBombs(flags);
                            gui.getEngine().getSquare(source.getPosition()).updateFlagged(false);
                        }
                    }
                }
            }
        }
    }
}