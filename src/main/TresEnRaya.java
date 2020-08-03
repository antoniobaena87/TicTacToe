package main;

import java.awt.Frame;
import java.awt.Panel;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.GridLayout;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/**
 * I have assumed that the user always plays with crosses and the machine and player 2 with circles.<br>
 * I have also assumed that the user always plays first. This behaviour can be corrected by simply
 * using a random number from 0 to 1 (0 starts user, 1 starts machine or player 2) at the beginning of each game, and adding some
 * label to inform the user who shall begin (though if the machine starts first it would be evident, for it would simply place
 * the first mark on the board).<br>
 * The checkBoxes are disabled as soon as the user sets the first mark and will be enabled again only when the "restart"
 * button is pressed.<br>
 * 
 * @author Antonio Baena
 *
 */
public class TresEnRaya {
	
	Frame frame;
	Panel leftPanel, rightPanel;
	Button[][] buttons;
	CheckboxGroup cbg;
	Checkbox cbHuman, cbCPU;
	Label winnerLabel;
	Button resetButton;
	
	boolean userTurn = true;
	boolean userVScpu;
	boolean play = true;  // true when the game is being played, false when there is a winner or a tie
	
	final String EMPTY = "";
	final String CROSS = "X";
	final String CIRCLE = "O";
	final String CROSSES_WIN = "Cruces ganan";
	final String CIRCLES_WIN = "Círculos ganan";
	final String CPU_WINS = "La máquina gana";
	final String TIE = "Empate";
	
	final int SIZE = 3;
	int emptyCells = SIZE * SIZE;
	
	public TresEnRaya() {
		frame = new Frame("Tres en raya");
		frame.setResizable(false);
		frame.setLocation(250, 250);
		// I want to achieve both columns (panels) to have about the same width, for that reason, and after
		// researching the web, I decided to use GridLayout over GridBagLayout (which adapts its columns sizes to the content)
		frame.setLayout(new GridLayout(1, 2, 10, 0));
		// add listener for closing the program
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		initLeftPanel();
		initRightPanel();

		frame.add(leftPanel);
		frame.add(rightPanel);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Initialises the panel 
	 */
	private void initLeftPanel() {
		leftPanel = new Panel();
		leftPanel.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		
		// initialise the buttons that compose the left panel (and game area)
		buttons = new Button[SIZE][SIZE];
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				buttons[i][j] = new Button();
				buttons[i][j].addActionListener(new ActionListener() {  // I've decided to use anonymous classes for the action listener
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(play) {
							if(userTurn) {
								Button button = (Button) e.getSource();
								for(int i = 0; i < SIZE; i++) {
									for (int j = 0; j < SIZE; j++) {
										if(buttons[i][j] == button) {
											if(button.getLabel().equals(EMPTY)) {
												setLabel(i, j, CROSS);
												userTurn = false;
												if(userVScpu) CPUTurn();
												break;
											}
										}
									}
								}
								if(cbCPU.isEnabled()) {
									cbCPU.setEnabled(false);
									cbHuman.setEnabled(false);
								}
							}else if(!userTurn && !userVScpu) { // time for the other human player to make a move
								Button button = (Button) e.getSource();
								for(int i = 0; i < SIZE; i++) {
									for (int j = 0; j < SIZE; j++) {
										if(buttons[i][j] == button) {
											if(button.getLabel().equals(EMPTY)) {
												setLabel(i, j, CIRCLE);
												userTurn = true;
												break;
											}
										}
									}
								}
								if(cbCPU.isEnabled()) {
									cbCPU.setEnabled(false);
									cbHuman.setEnabled(false);
								}
							}
						}
					}
				});
				
				gbc.gridx = j;
				gbc.gridy = i;
				gbc.weightx = 1.0;
				gbc.weighty = 1.0;
				leftPanel.add(buttons[i][j], gbc);
			}
		}
		
		leftPanel.setVisible(true);
	}
	
	/**
	 * Initialises the panel that contains the check boxes, restart button and label with the winner info.
	 */
	private void initRightPanel() {
		rightPanel = new Panel();
		rightPanel.setLayout(new GridLayout(4, 1));
		
		cbg = new CheckboxGroup();
		
		ItemListener myItemListener = new ItemListener() {
			// In this case I've decided better to create a class within the method scope
			// because I will need to give the exact same listener to both check boxes
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(cbCPU.getState()) userVScpu = true;
				else userVScpu = false;
			}
		};
		
		cbCPU = new Checkbox("Humano vs Computadora", cbg, true);
		userVScpu = true;
		cbCPU.addItemListener(myItemListener);
		
		cbHuman = new Checkbox("Humano vs Humano", cbg, false);
		cbHuman.addItemListener(myItemListener);
		
		winnerLabel = new Label();
		
		resetButton = new Button("Reiniciar");
		resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		
		rightPanel.add(cbCPU);
		rightPanel.add(cbHuman);
		rightPanel.add(winnerLabel);
		rightPanel.add(resetButton);
	}
	
	/**
	 * Resets the board and all the variable to their initial values.
	 */
	private void reset() {
		// reset buttons state to label -> EMPTY
		for(int i = 0; i < SIZE; i++) {
			for(int j = 0; j < SIZE; j++) {
				setLabel(i, j, EMPTY);
			}
		}
		
		// set user turn to true
		userTurn = true;
		// enable game mode selection checkBoxes
		cbCPU.setEnabled(true);
		cbHuman.setEnabled(true);
		// set winnerLabel to empty string
		winnerLabel.setText(EMPTY);
		
		play = true;
	}
	
	private void CPUTurn() {
		if(play) {
			// Check whether there is a position to fill so the user can't win
			blockUser();
			// give back the turn to the user
			userTurn = true;
		}
	}
	
	/**
	 * 
	 * @param i The i position of the button in the grid.
	 * @param j The j position of the button in the grid.
	 * @param label The label that needs to be set as text in the button
	 */
	private void setLabel(int i, int j, String label) {
		if(!label.equals(EMPTY) && buttons[i][j].getLabel().equals(EMPTY)) {
			buttons[i][j].setLabel(label);
			emptyCells--;
			// check if this new move makes a win
			if (isWin(i, j)) {
				String winText = new String();
				if(label.equals(CROSS)) winText = CROSSES_WIN;
				else if(label.equals(CIRCLE)) winText = CIRCLES_WIN;
				winnerLabel.setText(winText);
				play = false;
				return;  // in case the last move is also a winning move, prevent emptyCells == 0 condition's code from executing
			}
			
		}else if(label.equals(EMPTY) && !buttons[i][j].getLabel().equals(EMPTY)) {
			buttons[i][j].setLabel(label);
			emptyCells++;
		}
		
		if(emptyCells == 0) {
			// tie
			winnerLabel.setText(TIE);
			play = false;
		}
	}
	
	/**
	 * Checks the row, column and diagonal where the last mark was places to check whether
	 * that move makes the player win.
	 * 
	 * @param i The i position of the button in the grid.
	 * @param j The j position of the button in the grid.
	 * @return True if the placed mark makes the player who placed it win.
	 */
	private boolean isWin(int i, int j) {
		
		int count = 0;
		String label;
		if(userTurn) label = CROSS;
		else label = CIRCLE;
		
		// check cells in same row
		for(int col = 0; col < SIZE; col++) {
			if(buttons[i][col].getLabel().contains(label)) count++;
		}
		if(count == 3) return true;
		
		// check cells in same column
		count = 0;
		for(int row = 0; row < SIZE; row++) {
			if(buttons[row][j].getLabel().contains(label)) count++;
		}
		if(count == 3) return true;
		
		// check cells in diagonal (if needed)
		count = 0;
		if(i == j) {  // the move was placed in a diagonal
			// check d1
			for(int d1 = 0; d1 < SIZE; d1++) {
				if(buttons[d1][d1].getLabel().equals(label)) count++;
				
				if(count == 3) return true;
			}
			count = 0;
			if(i == 1) {
				// also check d2
				if(buttons[0][2].getLabel().equals(label)) count++;
				if(buttons[1][1].getLabel().equals(label)) count++;
				if(buttons[2][0].getLabel().equals(label)) count++;
				
				if(count == 3) return true;
			}
		}else if((i == 0 && j == 2) || (i == 2 && j == 0)) {  // also a cell, in this case only in the diagonal 2
			// check d2
			if(buttons[0][2].getLabel().equals(label)) count++;
			if(buttons[1][1].getLabel().equals(label)) count++;
			if(buttons[2][0].getLabel().equals(label)) count++;
			
			if(count == 3) return true;
		}
		
		return false;
	}
	
	/**
	 * In this method, the computer checks whether the user can win the match with one more mark.
	 * If the user can win, then place a mark to prevent this.<br><br>
	 * 
	 * Basically, a player can win the game with just one extra mark if in a row, column or diagonal there
	 * are two of his marks. For example, a player would win with an extra mark in this cases:<br><br>
	 * 
	 * d0   0   1   2   d2
	 *     -------------
	 *  0  | X | O | X |
	 *     -------------
	 *  1  | X |   | O |
	 *     -------------
	 *  2  | O |   | X |
	 *     --------------
	 *  
	 *  <br><br>Here the player (crosses) can win by placing a mark in 11. The computer will place
	 *  a mark in this cell to prevent it.<br>
	 *  If no options for blocking the player are found, then the computer will place a mark randomly.
	 */
	private void blockUser() {
		// Check rows
		for(int i = 0; i < SIZE; i++) {
			int count = 0;
			int[] possibleMove = {-1, -1};
			for(int j = 0; j < SIZE; j++) {
				if(buttons[i][j].getLabel().equals(CROSS)) {
					count++;
				}else if(buttons[i][j].getLabel().equals(EMPTY)) {
					possibleMove[0] = i;
					possibleMove[1] = j;
				}
			}
			if (count == 2 && possibleMove[0] != -1) {
				setLabel(possibleMove[0], possibleMove[1], CIRCLE);
				return;  // return so the cpu won't be able to add another mark in this turn
			}
		}
		// Check columns
		for(int j = 0; j < SIZE; j++) {
			int count = 0;
			int[] possibleMove = {-1, -1};
			for(int i = 0; i < SIZE; i++) {
				if(buttons[i][j].getLabel().equals(CROSS)) {
					count++;
				}else if(buttons[i][j].getLabel().equals(EMPTY)) {
					possibleMove[0] = i;
					possibleMove[1] = j;
				}
			}
			if (count == 2 && possibleMove[0] != -1) {
				setLabel(possibleMove[0], possibleMove[1], CIRCLE);
				return;  // return so the CPU won't be able to add another mark in this turn
			}
		}
		// Check diagonals
		int countd1 = 0;
		int countd2 = 0;
		int[] possibleMoved1 = {-1, -1};
		int[] possibleMoved2 = {-1, -1};
		for(int i = 0; i < SIZE; i++) {
			if(buttons[i][i].getLabel().equals(CROSS)) {
				countd1++;
				if(i == 1) countd2++;
			}else if(buttons[i][i].getLabel().equals(EMPTY)) {
				possibleMoved1[0] = i;
				possibleMoved1[1] = i;
				if(i == 1) {
					possibleMoved2[0] = i;
					possibleMoved2[1] = i;
				}
			}
			switch(i) {
				case 0:
					if(buttons[i][2].getLabel().equals(CROSS)) countd2++;
					else if(buttons[i][2].getLabel().equals(EMPTY)) {
						possibleMoved2[0] = i;
						possibleMoved2[1] = 2;
					}
					break;
				case 2:
					if(buttons[i][0].getLabel().equals(CROSS)) countd2++;
					else if(buttons[i][0].getLabel().equals(EMPTY)) {
						possibleMoved2[0] = i;
						possibleMoved2[1] = 0;
					}
			}
		}
		if (countd1 == 2 && possibleMoved1[0] != -1) {
			setLabel(possibleMoved1[0], possibleMoved1[1], CIRCLE);
			return;  // return so the CPU won't be able to add another mark in this turn
		}
		if(countd2 == 2 && possibleMoved2[0] != -1) {
			setLabel(possibleMoved2[0], possibleMoved2[1], CIRCLE);
			return;  // return so the CPU won't be able to add another mark in this turn
		}
		
		// If we reach this line, it means there is no need to block the user
		// Pick a random cell and place a mark in it
		boolean validCell = false;
		while(!validCell) {
			int i = ThreadLocalRandom.current().nextInt(0, SIZE);
			int j = ThreadLocalRandom.current().nextInt(0, SIZE);
			if(buttons[i][j].getLabel().equals(EMPTY)){
				setLabel(i, j, CIRCLE);
				validCell = true;
			}
		}
	}

}
