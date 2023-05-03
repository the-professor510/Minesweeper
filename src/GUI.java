import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GUI {

    private Engine engine;

    private JFrame frame;

    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;

    private JTextArea output;
    private JScrollPane scrollPane;
    private String newline = "\n";

    private boolean started = false;

    private BoardButton[][] buttonsArray;

    private int gridwidth = 15;
    private int gridheight = 15;

    private final int MINGRIDHEIGHT = 10;
    private final int MAXGRIDHEIGHT = 1000;

    private final int MINGRIDWIDTH = 10;
    private final int MAXGRIDWIDTH = 1000;

    private int numBombs = 25;
    private int flags = 9;

    private JPanel gameInformation = new JPanel();
    private JPanel board = new JPanel();

    private customListener action = new customListener();
    private optionListener optionButton = new optionListener();
    private gameListener gameButton = new gameListener();

    private Image[] boardImages;
    private static Image[] numberImages;
    private Image[] faceImages;

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

    private void fillFrame() {

        frame = new JFrame();

        frame.setTitle("Minesweeper");
        frame.setSize(new Dimension(1500,1500));
        //frame.setMinimumSize(new Dimension(19*gridwidth,20*gridheight+500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        createMenu();
        GridLayout layout = new GridLayout(gridheight, gridwidth,0,0);
        board.setLayout(layout);
        board.setPreferredSize(new Dimension(20*gridwidth,24*gridheight));
        board.setMinimumSize(board.getPreferredSize());
        board.setMaximumSize(board.getPreferredSize());
        board.setSize(board.getPreferredSize());
        
        newBoard();
        createGamePanel();


        Container contentPane = frame.getContentPane();
        
        contentPane.add(board,BorderLayout.CENTER);
        contentPane.add(gameInformation,BorderLayout.SOUTH);
        //contentPane.add(createContentPane(),BorderLayout.NORTH);

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

        //bombth = new JLabel("");
        bombh = new JLabel("");
        bombt = new JLabel("");
        bombo = new JLabel("");

        //gameInformation.add(bombth);
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
        face.setMargin(new Insets(0,0,0,0));
        //face.setBackground(new Color(0,0,0,0));
        face.setBorderPainted(false);
        gameInformation.add(face);
        gameInformation.add(Box.createHorizontalGlue());
        updateface(SMILE);

        //timeth = new JLabel("");
        timeh = new JLabel("");
		timet = new JLabel("");
		timeo = new JLabel("");

        //gameInformation.add(timeth);
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
    }



    private void newBoard() {

        board.removeAll();
        board.repaint();

        buttonsArray = new BoardButton[gridheight][gridwidth];

        for (int y = 0; y< gridheight; y++) {
            for (int x = 0; x<gridwidth; x++) {

                buttonsArray[y][x] = null;
                buttonsArray[y][x] = new BoardButton(this,(y*gridwidth+x));

                //changes the board to a blank image
                paintButton(buttonsArray[y][x],UNCLICKED);
                addPressedButtonAnimation(buttonsArray[y][x]);
                buttonsArray[y][x].setName(String.valueOf(y*gridwidth+x)); //Stores the position of the button in the array               
                buttonsArray[y][x].addMouseListener(gameButton);

                board.add(buttonsArray[y][x]);

                frame.setVisible(true);
            }
        }
    }


    private void paintButton(BoardButton button, int imageType) {
        ImageIcon image;
        
        switch (imageType) {
            case 0:
            image = new ImageIcon(boardImages[UNCLICKED]);
            button.setIcon(image);
            break;

            case 1:
            image = new ImageIcon(boardImages[FLAGGED]);
            button.setIcon(image);
            break;

            case 2:
            image = new ImageIcon(boardImages[QUESTION]);
            button.setIcon(image);
            break;

            case 3:
            image = new ImageIcon(boardImages[CLICKED]);
            button.setIcon(image);
            break;

            case 4:
            image = new ImageIcon(boardImages[BOMB]);
            button.setIcon(image);
            break;

            case 5:
            image = new ImageIcon(boardImages[BOMBCLICKED]);
            button.setIcon(image);
            break;

            case 6:
            image = new ImageIcon(boardImages[BOMBFLAGGEDWRONG]);
            button.setIcon(image);
            break;

            case 7:
            image = new ImageIcon(boardImages[BOMBFLAGGEDCORRECT]);
            button.setIcon(image);
            break;

            default:
            button.setIcon(null);
        }
    }

    private void numberBoard(BoardButton button, int numberOfBombs) {
        switch (numberOfBombs) {
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

            default:
            button.setText(null);

            
        }
    }

    private void addPressedButtonAnimation(BoardButton button) {
        button.setPressedIcon(new ImageIcon(boardImages[PRESSEDBLANK]));
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
        buttonsArray[y][x].removeMouseListener(gameButton);
    }

    public void updatePaintedSquare(int x, int y, int newFace) {
        BoardButton button = buttonsArray[y][x];
        
        paintButton(button, newFace);
        numberBoard(button, CLEAR);
    }

    public void updateNumberedSquare(int x, int y, int numberOfBombs) {
        BoardButton button = buttonsArray[y][x];
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
        boardImages = new BufferedImage[9];
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
        numberImages = new BufferedImage[11];
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
        faceImages = new BufferedImage[4];
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

        //int th = (watch%10000)/1000;
		int h = (watch%1000)/100;
        int t = (watch%100)/10;
        int o = watch % 10;
        try{
            //timeth.setIcon(new ImageIcon(numberImages[th]));
            timeh.setIcon(new ImageIcon(numberImages[h]));
            timet.setIcon(new ImageIcon(numberImages[t]));
            timeo.setIcon(new ImageIcon(numberImages[o]));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Run out of time");
        }
	}

    private void endTime() {
        time.stop();
    }

    private void nullTime(){
        //timeth.setIcon(new ImageIcon(numberImages[10]));
        timeh.setIcon(new ImageIcon(numberImages[10]));
        timet.setIcon(new ImageIcon(numberImages[10]));
        timeo.setIcon(new ImageIcon(numberImages[10]));
    }


    private void setBombs(int bombs) {
        //int bombs = engine.getNoBombs();
        
        //int th = (bombs%10000)/1000;
		int h = (bombs%1000)/100;
        int t = (bombs%100)/10;
        int o = bombs % 10;

        try{
            //bombth.setIcon(new ImageIcon(numberImages[th]));
            bombh.setIcon(new ImageIcon(numberImages[h]));
            bombt.setIcon(new ImageIcon(numberImages[t]));
            bombo.setIcon(new ImageIcon(numberImages[o]));
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
        face.setIcon(new ImageIcon(faceImages[facetype]));
    }

    public Engine getEngine() {
        return engine;
    }







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
    }

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

            widthModel = new SpinnerNumberModel(getGridheight(), MINGRIDWIDTH, MAXGRIDWIDTH,1);
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

                    //made a mistake somewhere where I confused my height and width, 
                    // but don't want to go throught the whole program changing everything
                    int newHeight = Integer.valueOf(widthSpinner.getValue().toString());
                    int newWidth = Integer.valueOf(heightSpinner.getValue().toString());
                    int newBombValue = Integer.valueOf(bombSpinner.getValue().toString());

                    setBombs(newBombValue);
                    changeGridheight(newHeight);
                    changeGridwidth(newWidth);

                    resetTime();
                    resetGame();
                    options.dispose();
                }
            });
            
            options.add(apply);

            cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    resetTime();
                    resetGame();
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

            if(!started) {
                started = true;
                flags = numBombs;
                resetTime();
                startTime();
                gui.getEngine().initialiseBoard(source.getPosition());
            }

            if(SwingUtilities.isLeftMouseButton(e)) {

                if(!(gui.getEngine().getSquare(source.getPosition()).getFlagged())) {
                    gui.getEngine().clickSquare(source.getPosition());
                }

                gui.getEngine().clickSquare(source.getPosition());
                




                if (source.getBlankFlagOrQuestion() == 0) {
                    source.changeClicked(true);
                    //removePressedButtonAnimation(source);

                    String s = "Action event detected."
                        + newline
                        + "    Event source: " + source.getName()
                        + " (an instance of " + getClassName(source) + ")";
                    output.append(s + newline);
                    output.setCaretPosition(output.getDocument().getLength());
                }

            
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
                
                
                if (!(source.getClicked())){
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
                }
            }
        }
    }


}

