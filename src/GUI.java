import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GUI {

    private Engine engine;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;

    /*
    private JTextArea output;
    private JScrollPane scrollPane;
    private String newline = "\n";
    */

    private boolean started = false;

    private BoardButton[][] buttonsArray;

    private int gridwidth = 15;
    private int gridheight = 10;

    private final int MINGRIDHEIGHT = 10;
    private final int MAXGRIDHEIGHT = 1000;

    private final int MINGRIDWIDTH = 10;
    private final int MAXGRIDWIDTH = 1000;

    private int numBombs = 10;
    private int flags = 9;

    private JPanel gameInformation = new JPanel();
    private JPanel board = new JPanel();
    private JPanel leftside = new JPanel();
    private JPanel rightside = new JPanel();

    private GridLayout layout;
    private BorderLayout borderLayout;

    //private customListener action = new customListener();
    private optionListener optionButton = new optionListener();
    private gameListener gameButton = new gameListener();

    private Image[] boardImages;
    private static Image[] numberImages;
    private Image[] faceImages;

    private int ImageWidth = 20;
    private int ImageHeight = 20;
    private int topMargin = 10;
    private int bottomMargin = 10;
    private int sideMargins = 10;

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

    private static final int OOH = 1;
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

        engine = new Engine(this, gridwidth, gridheight, numBombs);
    }

    private void resizeFrame() {
        //int totalWidth;
        int totalHeight;
      
        int gameInformationWidth = gameInformation.getWidth();
        int gameInformationHeight = gameInformation.getHeight();

        int boardWidth = board.getWidth() + 2*sideMargins;
        int boardHeight = board.getHeight() + topMargin + bottomMargin;

        totalHeight = gameInformationHeight + boardHeight;

        if(boardWidth>gameInformationWidth){
            frame.setSize(boardWidth, totalHeight);
        } else {
            frame.setSize(gameInformationWidth, totalHeight);
        }

        frame.pack();
    }

    private void fillFrame() {

        frame = new JFrame();

        frame.setTitle("Minesweeper");
        //frame.setSize(new Dimension(1500,1500));
        //frame.setMinimumSize(new Dimension(19*gridwidth,20*gridheight+500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);


        createMenu();
                
        newBoard();
        createGamePanel();

        Container contentPane = frame.getContentPane();
        
        borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);

        contentPane.add(board,BorderLayout.CENTER);
        contentPane.add(gameInformation,BorderLayout.SOUTH);
        contentPane.add(rightside, BorderLayout.LINE_END);
        contentPane.add(leftside, BorderLayout.LINE_START);

        //contentPane.add(createContentPane(),BorderLayout.NORTH);

        resizeFrame();
        frame.pack();
        

        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }

    private void createMenu() {

        //New Menu Bar at the top of the window
        menuBar = new JMenuBar();

        
        menu = new JMenu("Game");
        menuBar.add(menu);
        

        //New Game
        //Option
        //Exit
        menuItem = new JMenuItem("New Game");
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                //The timer and board are reset to prepare a new game
                endTime();
                resetGame();
            }
        });
        menu.add(menuItem);
        menu.addSeparator();

        menuItem = new JMenuItem("Options");
        menuItem.addActionListener(optionButton);
        menu.add(menuItem);

        menuItem = new JMenuItem("Help");
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                // gives a link to the wikipedia for minesweeper
                JOptionPane.showConfirmDialog(null, "This is a java implementaion of the classic computer game Minesweeper. \n"
                                                    +"The aim of the game is to clear the board without clicking on any mines. \n" 
                                                    +"A full explanation of the rules of minesweeper can be found when following \n"
                                                    +"the following link: https://mathworld.wolfram.com/Minesweeper.html","Help", JOptionPane.CLOSED_OPTION);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                //closes the frame 
                frame.dispose();
            }
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
        face.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                //if a game is has been started then reset the game,
                endTime();
                resetGame();
            }
        });
        //face.setMargin(new Insets(0,0,0,0));
        //face.setBackground(new Color(0,0,0,0));
        face.setBorderPainted(false);
        gameInformation.add(face);
        gameInformation.add(Box.createHorizontalGlue());
        updateface(SMILE);

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
        engine = new Engine(this, gridwidth, gridheight, numBombs);

        //rebuild the board
        newBoard();

        //reset the timer
        nullTime();

        //reset the bomb count
        setBombs(numBombs);

        //reset the face
        updateface(SMILE);

        //started = false
        started = false;

    }


    protected String getClassName(Object o) {
        String classString = o.getClass().getName();
        int dotIndex = classString.lastIndexOf(".");
        return classString.substring(dotIndex+1);
    }

    /*
    public Container createContentPane() {
        //Create the content-pane-to-be.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        contentPane.setSize(new Dimension(2,100));
        

        
        //Create a scrolled text area.
        output = new JTextArea(5, 10);
        output.setEditable(false);
        scrollPane = new JScrollPane(output);

        //Add the text area to the content pane.
        contentPane.add(scrollPane, BorderLayout.CENTER);
        

        return contentPane;
    }*/



    private void newBoard() {

        layout = new GridLayout(gridheight, gridwidth,0,0);
        board.setLayout(layout);
        board.setPreferredSize(new Dimension((ImageWidth+1)*gridwidth,(ImageWidth+1)*gridheight));
        board.setMinimumSize(board.getPreferredSize());
        board.setMaximumSize(board.getPreferredSize());
        board.setSize(board.getPreferredSize());
        

        board.removeAll();
        board.repaint();

        buttonsArray = new BoardButton[gridwidth][gridheight];

        for (int y = 0; y<gridheight; y++) {
            for (int x = 0; x< gridwidth; x++) {

                buttonsArray[x][y] = null;
                buttonsArray[x][y] = new BoardButton(this,(y*gridwidth+x));

                //changes the board to a blank image
                paintButton(buttonsArray[x][y],UNCLICKED);

                addPressedButtonAnimation(buttonsArray[x][y]);
                buttonsArray[x][y].setName(String.valueOf(y*gridwidth+x)); //Stores the position of the button in the array               
                buttonsArray[x][y].addMouseListener(gameButton);

                board.add(buttonsArray[x][y]);

                frame.setVisible(true);
            }
        }
    }


    public void blankBoard(){
        System.out.println("");
        for (int y = 0; y< gridheight; y++) {
            String line = "";
            for (int x = 0; x<gridwidth; x++) {

                //buttonsArray[x][y] = null;
                //buttonsArray[x][y] = new BoardButton(this,(y*gridwidth+x));
                
                Square square = engine.getSquare(y*gridwidth+x);
                if(square.getIsBomb()){
                    line += "B";
                } else{
                    //paintButton(buttonsArray[x][y], CLICKED);
                    int numBombs = square.getNumBombsAround();
                    //numberBoard(buttonsArray[x][y], numBombs);
                    line+= numBombs;
                }
            }
            System.out.println(line);
        }
        return;
    }
        

    private void paintButton(BoardButton button, int imageType) {
        ImageIcon image;
        Image scaledImage;

        switch (imageType) {
            case 0:
                scaledImage = boardImages[UNCLICKED].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);             
                break;

            case 1:
                //image = new ImageIcon(boardImages[FLAGGED]);
                scaledImage = boardImages[FLAGGED].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 2:
                //image = new ImageIcon(boardImages[QUESTION]);
                scaledImage = boardImages[QUESTION].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 3:
                //image = new ImageIcon(boardImages[CLICKED]);
                scaledImage = boardImages[CLICKED].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 4:
                //image = new ImageIcon(boardImages[BOMB]);
                scaledImage = boardImages[BOMB].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 5:
                //image = new ImageIcon(boardImages[BOMBCLICKED]);
                scaledImage = boardImages[BOMBCLICKED].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 6:
                //image = new ImageIcon(boardImages[BOMBFLAGGEDWRONG]);
                scaledImage = boardImages[BOMBFLAGGEDWRONG].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            case 7:
                //image = new ImageIcon(boardImages[BOMBFLAGGEDCORRECT]);
                scaledImage = boardImages[BOMBFLAGGEDCORRECT].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
                image = new ImageIcon(scaledImage);
                button.setIcon(image);
                break;

            default:
                button.setIcon(null);
        }
        frame.setVisible(true);

    }

    private void numberBoard(BoardButton button, int numberOfBombs) {
        switch (numberOfBombs) {
            ///*
            case 1:
                button.setText("1");
                button.setForeground(new Color(10,10,10));
                break;

            case 2:
                button.setText("2");
                button.setForeground(new Color(10,200,100));
                button.setFont(new Font(Font.DIALOG_INPUT,  Font.BOLD, 15));
                break;

            case 3:
                button.setText("3");
                button.setForeground(new Color(10,10,200));
                break;

            case 4:
                button.setText("4");
                button.setForeground(new Color(10,100,10));
                break;

            case 5:
                button.setText("5");
                button.setForeground(new Color(10,100,10));
                break;

            case 6:
                button.setText("6");
                button.setForeground(new Color(200,10,10));
                break;

            case 7:
                button.setText("7");
                button.setForeground(new Color(100,10,10));
                break;

            case 8:
                button.setText("8");
                button.setForeground(new Color(70,10,10));
                break;
            //*/
            

            default:
                
                //button.setText(String.valueOf(numberOfBombs));
                button.setText(null);

            
        }
        //frame.setVisible(true);
    }

    private void addPressedButtonAnimation(BoardButton button) {
        //button.setPressedIcon(new ImageIcon(boardImages[PRESSEDBLANK]));
        Image scaledImage = boardImages[PRESSEDBLANK].getScaledInstance(ImageWidth, ImageHeight, java.awt.Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(scaledImage);
        button.setPressedIcon(image);
    }

    private void removePressedButtonAnimation(BoardButton button) {
        button.setPressedIcon(null);
    }

    public int getGridheight(){
        return gridheight;
    }
    public int getGridwidth(){
        return gridwidth;
    }
    public void changeGridheight(int newHeight){
        gridheight = newHeight;
    }
    public void changeGridwidth(int newWidth){
        gridwidth = newWidth;
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
        if(gameWon) {
            updateface(WIN);
        } else {
            updateface(DEAD);
        }
        endTime();

        for (int y = 0; y< gridheight; y++) {
            for (int x = 0; x<gridwidth; x++) {
                removeActionListener(x,y);
            }
        }
    }

    




    //Reads in images to BufferedImages from Assests file
    private void readInImages(){

        //read in images for the board
        boardImages = new Image[9];
        String[] boardAssests = {""};

        try{
            boardAssests = (new File("src/Assests/Board")).list();
        } catch (NullPointerException e) {
            System.out.println("error with board Images");
        }

        for(int i = 0; i<boardImages.length; i++) {
            try{
                boardImages[i] = ImageIO.read(new File("src/Assests/Board/" + boardAssests[i]));
            } catch (IOException e) {
                System.out.println("error with board Images");
            }
        }

        //read in images for the numbers
        numberImages = new Image[11];
        String[] numberAssests = {""};
        try{
            numberAssests = (new File("src/Assests/Information/Time")).list();
        } catch (NullPointerException e) {
            System.out.println("error with number Images");
        }

        for(int i = 0; i<numberImages.length; i++) {
            try{
                numberImages[i] = ImageIO.read(new File("src/Assests/Information/Time/" + numberAssests[i]));
            } catch (IOException e) {
                System.out.println("error with number Images");
            }
        }

        //read in images for the face
        faceImages = new Image[4];
        String[] faceAssests = {""};
        try{
            faceAssests = (new File("src/Assests/Information/Face")).list();
        } catch (NullPointerException e) {
            System.out.println("error with face Images");
        }

        for(int i = 0; i<faceImages.length; i++) {
            try{
                faceImages[i] = ImageIO.read(new File("src/Assests/Information/Face/" + faceAssests[i]));
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
		time = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	watch++;
            	setTime();
            }
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
        int minBombs = (int)(0.05*numSquares);
        return minBombs;
    }

    private int maxNumberBombs(int height, int width) {
        //65% of the board is bombs
        int numSquares = height*width;
        int maxBombs = (int)(0.65*numSquares);
        return maxBombs;
    }

    private void updateface(int facetype){
        //face.setIcon(new ImageIcon(faceImages[facetype]));
        Image scaledImage = faceImages[facetype].getScaledInstance(35, 35, java.awt.Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(scaledImage);
        face.setIcon(image);
    }

    public Engine getEngine() {
        return engine;
    }






    /*
    class customListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JMenuItem source = (JMenuItem)(e.getSource());
            String s = "Action event detected."
                        + newline
                        + "    Event source: " + source.getText()
                        + " (an instance of " + getClassName(source) + ")";
            output.append(s + newline);
            output.setCaretPosition(output.getDocument().getLength());
        }
    }*/

    class optionListener implements ActionListener {
        private SpinnerNumberModel bombModel;
        private JSpinner bombSpinner;

        private SpinnerNumberModel heightModel;
        private JSpinner heightSpinner;

        private SpinnerNumberModel widthModel;
        private JSpinner widthSpinner;

        private JDialog options;

        private JButton apply;
        private JButton cancel;


        public void actionPerformed(ActionEvent e) {

            options = new JDialog(frame, "Options", Dialog.ModalityType.DOCUMENT_MODAL);

            options.setSize(300, 200);
            options.setLayout(new FlowLayout());


            heightModel = new SpinnerNumberModel(getGridheight(), MINGRIDHEIGHT, MAXGRIDHEIGHT,1);
            heightSpinner = new JSpinner(heightModel);

            widthModel = new SpinnerNumberModel(getGridwidth(), MINGRIDWIDTH, MAXGRIDWIDTH,1);
            widthSpinner = new JSpinner(widthModel);

            options.add(new JLabel("Height"));
            options.add(heightSpinner);
            options.add(new JLabel("Width"));
            options.add(widthSpinner);

            //number of bombs
            bombModel = new SpinnerNumberModel(numBombs, minNumberBombs(getGridheight(),getGridwidth()), maxNumberBombs(getGridheight(),getGridwidth()),1);
            bombSpinner = new JSpinner(bombModel);

            options.add(new JLabel("Bombs"));
            options.add(bombSpinner);
            //buttons to apply settings and cancel to revert to what was already there

            heightSpinner.addChangeListener(new ChangeListener() {
                
                public void stateChanged(ChangeEvent e) {

                    Object value = widthSpinner.getValue();
                    int width = Integer.valueOf(value.toString());

                    value = heightSpinner.getValue();
                    int height = Integer.valueOf(value.toString());

                    updateBombLimits(height,width);
                }
            });
            widthSpinner.addChangeListener(new ChangeListener() {
                
                public void stateChanged(ChangeEvent e) {

                    Object value = widthSpinner.getValue();
                    int width = Integer.valueOf(value.toString());

                    value = heightSpinner.getValue();
                    int height = Integer.valueOf(value.toString());

                    updateBombLimits(height,width);
                }
            });

            apply = new JButton("Apply");
            apply.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){

                    int newHeight = Integer.valueOf(heightSpinner.getValue().toString());
                    int newWidth = Integer.valueOf(widthSpinner.getValue().toString());
                    int newBombValue = Integer.valueOf(bombSpinner.getValue().toString());

                    numBombs = newBombValue;

                    setBombs(newBombValue);
                    changeGridheight(newHeight);
                    changeGridwidth(newWidth);

                    resetTime();
                    resetGame();
                    resizeFrame();
                    options.dispose();
                }
            });
            
            options.add(apply);

            cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    resetTime();
                    resetGame();
                    resizeFrame();
                    options.dispose();
                }
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

            int bombvalue = Integer.valueOf(bombSpinner.getValue().toString());

            if(bombvalue<minNum){
                bombModel.setValue(minNum);
            } else if (bombvalue>maxNum) {
                bombModel.setValue(maxNum);
            }

            bombSpinner = new JSpinner(bombModel);
            System.out.println(bombSpinner.getValue());
            options.pack();

        }
    }

    class BoardButton extends JButton {
        private boolean isClicked;
        private int blankFlagOrQuestion; //either 0,1 or 2. 0=blank, 1=flag, 2=question
        private GUI gui;
        private int position;

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

        public void changeClicked(boolean newState) {
            isClicked = newState;
        }

        public int getBlankFlagOrQuestion(){
            return blankFlagOrQuestion;
        }

        public void updageBlankFlagOrQuestion(int number) {
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
            //y*gridwidth+x
            int x = (int)(position%gui.getGridwidth());
            int y = (int) (position - x)/gui.getGridwidth();
            
            System.out.println(position);
            System.out.println("x is " + x);
            System.out.println(position-x);
            System.out.println("y is " + y);
            

            
            if(!started) {
                started = true;
                flags = numBombs;
                resetTime();
                startTime();
                gui.getEngine().initialiseBoard(position);
            }

            if(SwingUtilities.isLeftMouseButton(e)) {

                blankBoard();

                if(!(gui.getEngine().getSquare(position).getFlagged())) {
                    gui.getEngine().clickSquare(position);
                    gui.removeActionListener(x, y);
                    
                }

                //gui.getEngine().clickSquare(source.getPosition());
            




                /*if (source.getBlankFlagOrQuestion() == 0) {
                    source.changeClicked(true);
                    //removePressedButtonAnimation(source);

                    String s = "Action event detected."
                        + newline
                        + "    Event source: " + source.getName()
                        + " (an instance of " + getClassName(source) + ")";
                    output.append(s + newline);
                    output.setCaretPosition(output.getDocument().getLength());
                }*/

            
            } else if (SwingUtilities.isRightMouseButton(e)) {


                if (!(source.getClicked())){
                    switch (source.getBlankFlagOrQuestion()) {
                        case 0:
                            if(flags!=0){
                                source.updageBlankFlagOrQuestion(1);
                                paintButton(source, FLAGGED);
                                removePressedButtonAnimation(source);
                                flags--;
                                setBombs(flags);
                                gui.getEngine().getSquare(source.getPosition()).updateFlagged(true);
                            }

                            break;

                        case 1:
                            source.updageBlankFlagOrQuestion(2);
                            paintButton(source, QUESTION);
                            removePressedButtonAnimation(source);
                            break;

                        case 2:
                            source.updageBlankFlagOrQuestion(0);
                            paintButton(source, UNCLICKED);
                            addPressedButtonAnimation(source);
                            flags++;
                            setBombs(flags);
                            gui.getEngine().getSquare(source.getPosition()).updateFlagged(false);
                            break;
                    }
                }
                
                
                
                /*if (!(source.getClicked())){
                    String s = "Action event detected."
                            + newline
                            + "    Event source1: " + source.getName()
                            + " (an instance of " + getClassName(source) + ")";
                    output.append(s + newline);
                    output.setCaretPosition(output.getDocument().getLength());
                    //source.changeClicked(true);
                } else {
                    String s = "Action event detected."
                            + newline
                            + "    Event source1: " + source.getName()
                            + " Already clicked";
                    output.append(s + newline);
                    output.setCaretPosition(output.getDocument().getLength());
                    
                }*/
            }
        }
    }
}