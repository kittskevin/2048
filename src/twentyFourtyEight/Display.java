package twentyFourtyEight;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Display extends JFrame {
	
	private Game currentGame;
	private JButton[][] spaces = new JButton[4][4];
	private int gridUnit = 100;
	private JLabel scoreDisplay = new JLabel();
	private ArrayList<Game> gameList = new ArrayList<Game>();
	
	public Display(){

		Game game = new Game();
		gameList.add(game);
		currentGame = game;

		setTitle("2048");
		setSize(500,500);
		setVisible(true);
		setResizable(false);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		currentGame = game;
		
		setLayout(null);
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				
				spaces[i][j] = new JButton();
				spaces[i][j].setSize(gridUnit, gridUnit);
				spaces[i][j].setFont(new Font("Tahoma", Font.BOLD, 30));
				spaces[i][j].setLocation(j*gridUnit, i*gridUnit);
				spaces[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
				spaces[i][j].setFocusable(false);
				spaces[i][j].setEnabled(true);
				for( MouseListener al : spaces[i][j].getMouseListeners() ) {
				    spaces[i][j].removeMouseListener( al );
				}
				spaces[i][j].setVisible(true);
			}
		}
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				getContentPane().add(spaces[j][i]);
			}
		}
		
		scoreDisplay.setBounds(420, 420, 60, 30);
		scoreDisplay.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		scoreDisplay.setText(new Integer(currentGame.getScore()).toString());
		getContentPane().add(scoreDisplay);
		updateDisplay();
		
		this.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				
				// Game moves: left, right, up, and down
				if (e.getKeyCode() == KeyEvent.VK_UP){
					System.out.println("Up");
					if (currentGame.move(Direction.UP) && !currentGame.getTerminated()){
						currentGame.spawnTile();
						updateDisplay();
						
					}
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN){
					System.out.println("Down");
					if (currentGame.move(Direction.DOWN) && !currentGame.getTerminated()){
						currentGame.spawnTile();
						updateDisplay();
						
					}
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT){
					System.out.println("Left");
					if (currentGame.move(Direction.LEFT) && !currentGame.getTerminated()){
						currentGame.spawnTile();
						updateDisplay();
						
					}
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
					System.out.println("Right");
					if (currentGame.move(Direction.RIGHT) && !currentGame.getTerminated()){
						currentGame.spawnTile();
						updateDisplay();
						
					}
				}
				
				// 'P' - Run one game using the AI
				else if (e.getKeyCode() == KeyEvent.VK_P){
					runAI();
				}
				
				// 'O' - Make one game move using the AI
				else if (e.getKeyCode() == KeyEvent.VK_O){
					makeMove();
				}
				
				// 'N' - Reset the board and score
				else if (e.getKeyCode() == KeyEvent.VK_N) {
					setUpNewGame();
				}
				
				// 'T' - Run 1000 games using the AI
				else if (e.getKeyCode() == KeyEvent.VK_T){
					testMultiple(1000);
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				// No effect
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				// No effect
			}
			
		});
		
	}
	
	public void runAI(){
	
//					if(currentGame.gameOver())
//						JOptionPane.showMessageDialog(this, "You lost", "You suck", JOptionPane.ERROR_MESSAGE);
	
//					currentGame.move(currentGame.findBestMoveFirst());
//					currentGame.spawnTile();
//					System.out.println("false");
					
//					
//		while(!currentGame.gameOver(currentGame.getGameState())) {
//			if (currentGame.move(currentGame.findBestMove())){
//				currentGame.spawnTile();
//			}
//			else{
//				currentGame.move(currentGame.findBestMoveFirst());
//				currentGame.spawnTile();
//			}
//		}
//					
					
					
					
		while(!currentGame.gameOver(currentGame.getGameState())) {
			currentGame.move(currentGame.findBestMoveFirst());
//			currentGame.move(currentGame.findBestMove(1));
			currentGame.spawnTile();
//			System.out.println("testst");
			
		}
		
					
		currentGame.markFinalTime();
		updateDisplay();


	}
	
	public void makeMove() {
		
		currentGame.move(currentGame.findBestMoveFirst());
		currentGame.spawnTile();
//		System.out.println("false");
		
		
		updateDisplay();
//		currentGame.printGameState();
		
	}
	
	public Game setUpNewGame() {
		Game newGame = new Game();
		gameList.add(newGame);
		currentGame = newGame;
		updateDisplay();
		return newGame;
	}
	
	public void testMultiple(int numberOfTests) {
		for (int i = 0; i < numberOfTests; i++) {
			setUpNewGame();
			runAI();
		}
		
		printGameData();
		
		
	}
	
	public void printGameData(){
		int totalScore = 0;
		int count = 0;
		double totalTime = 0.0;
		double averageTime = 0.0;
		int totalHighest = 0;
		int averageHighest = 0;
		int highestBlock = 0;
		int lowestBlock = 4000;
		int averageScore = 0;
		int highestScore = 0;
		int lowestScore = 1000000;
		
		int numberOf1024 = 0;
		int numberOf2048 = 0;
		int numberOf4096 = 0;
		
		for (Game game : gameList) {
			if (!game.getTerminated())
				continue;
			System.out.println(game.toString());
			count++;
			totalScore += game.getScore();
			averageScore = totalScore/count;
			if(game.getScore() < lowestScore)
				lowestScore = game.getScore();
			if(game.getScore() > highestScore){
				highestScore = game.getScore();
				this.setGame(game);
				updateDisplay();
			}
				
			
			if(game.getHighest() == 1024)
				numberOf1024++;
			if(game.getHighest() == 2048)
				numberOf2048++;
			if(game.getHighest() == 4096)
				numberOf4096++;
			
			totalHighest += (int) (Math.log(game.getHighest()) / Math.log(2));
			averageHighest = (int) Math.round(Math.pow(2, totalHighest / count));
			
			if(game.getHighest() < lowestBlock)
				lowestBlock = game.getHighest();
			if(game.getHighest() > highestBlock)
				highestBlock = game.getHighest();
			
			totalTime += (int) game.getTotalTime().getTime();
			averageTime = totalTime / count;
			
		}
		System.out.println();
		
		System.out.println("Average Score:\t" + averageScore + "\tHighestScore:\t" + highestScore + "\tLowestScore:\t" + lowestScore+ "\n" 
				+ "Average Block:\t" + averageHighest + "\tHighest Block:\t" + highestBlock + "\tLowest Block:\t"+ lowestBlock + "\n"
				+ "1024s:\t" + numberOf1024 + "\t2048s:\t" + numberOf2048 + "\t4096s:\t" + numberOf4096+"\n"
				+ "Total Time:\t" + totalTime + "\tAverage Time:\t" + averageTime);
		
		
	}
	
	public void setGame(Game game){
		this.currentGame = game;
	}
	
	private void updateDisplay(){
		int[][] vals = currentGame.getGameState();
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				if (vals[i][j] == 0){
					spaces[i][j].setText("");
					spaces[i][j].setBackground(new Color(190, 192, 180));
					spaces[i][j].setBackground(Color.gray.brighter());
					spaces[i][j].setForeground(Color.BLACK);
					
				}
				if(vals[i][j] == 2){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(238, 228, 218));
					spaces[i][j].setForeground(Color.BLACK);
				}
				
				if(vals[i][j] == 4){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 224, 200));
					spaces[i][j].setForeground(Color.BLACK);
				}
				
				if(vals[i][j] == 8){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(242, 177, 121));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 16){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(245, 149, 99));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 32){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(246, 124, 95));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 64){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(246, 94, 59));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 128){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 207, 114));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 256){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 204, 97));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 512){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 200, 80));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 1024){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 197, 63));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 2048){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(new Color(237, 197, 63));
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 4096){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(Color.BLACK);
					spaces[i][j].setForeground(Color.WHITE);
				}
				
				if(vals[i][j] == 4096 *2){
					spaces[i][j].setText(Integer.toString(vals[i][j]));
					spaces[i][j].setBackground(Color.BLACK);
					spaces[i][j].setForeground(Color.WHITE);
				}
					
			}
		}
		scoreDisplay.setText(new Integer(currentGame.getScore()).toString());
		
		repaint();
		
	}

}
