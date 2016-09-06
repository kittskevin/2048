package twentyFourtyEight;

import java.awt.Point;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

public class Game {
	
	private Random r = new Random();
	private int[][] vals;
	private int score;
	private boolean terminated;
	private Time startTime;
	private Time totalTime;
	private int highestBlock = 2;
	private static int counter = 0;
	private final int gameNumber;
	
	public Game(){
		gameNumber = counter;
		counter++;
		
		vals = new int[4][4];
		for (int i = 0; i < vals.length; i++){
			for (int j = 0; j < vals[i].length; j++){
				vals[i][j] = 0;
			}
		}
		spawnTile();
		spawnTile();
		score = 0;
		terminated = false;
		
		startTime = new Time(System.currentTimeMillis());
	}
	
	public void spawnTile(){
		int val;
		ArrayList<Point> possibles = new ArrayList<Point>();
		if (r.nextInt(10)+1 == 10)
			val = 4;
		else 
			val = 2;
		for (int i = 0; i < vals.length; i++){
			for (int j = 0; j < vals[i].length; j++){
				if(vals[i][j] == 0)
					possibles.add(new Point(i ,j));
			}
		}
		if (possibles.size() == 0){
			terminated= true;
			return;
		}
		Point target = possibles.get(r.nextInt(possibles.size()));
		vals[target.x][target.y] = val;
	}
	
	public int getHighest(){
		return highestBlock;
	}
	
	public boolean getTerminated(){
		return terminated;
	}
	
	public int[][] getGameState(){
		return vals;
	}
	
	public int getScore() {
		return score;
	}
	
	public Time getStartTime() {
		return startTime;
	}
	
	public Time markFinalTime() {
		totalTime = new Time(System.currentTimeMillis() - startTime.getTime());
		return totalTime;
	}
	
	public Time getTotalTime() {
		return totalTime;
	}
	
	public boolean move(Direction direction) {    
        int points = 0;
        
        int[][] original = vals;
        
        if(direction==Direction.UP) {
        	vals = rotateLeft(vals);
        }
        else if(direction==Direction.RIGHT) {
        	vals = rotateLeft(vals);
        	vals = rotateLeft(vals);
        }
        else if(direction==Direction.DOWN) {
            vals = rotateRight(vals);
        }
        else if(direction == Direction.LEFT){
        	vals = rotateLeft(vals);
        	vals = rotateLeft(vals);
            vals = rotateLeft(vals);
            vals = rotateLeft(vals);
        }
        
        for(int i=0; i < 4;++i) {
            int lastMergePosition=0;
            for(int j=1;j<4;++j) {
                if(vals[i][j]==0) {
                    continue;
                }
                
                int previousPosition = j-1;
                while(previousPosition>lastMergePosition && vals[i][previousPosition]==0) { 
                    --previousPosition;
                }
                
                if(vals[i][previousPosition]==0) {
                    vals[i][previousPosition]=vals[i][j];
                    vals[i][j]=0;
                }
                else if(vals[i][previousPosition]==vals[i][j]){
                    vals[i][previousPosition]*=2;
                    vals[i][j]=0;
                    points+=vals[i][previousPosition];
                    lastMergePosition=previousPosition+1;
                    
                }
                else if(vals[i][previousPosition]!=vals[i][j] && previousPosition+1!=j){
                    vals[i][previousPosition+1]=vals[i][j];
                    vals[i][j]=0;
                }
            }
        } 
        score+=points;
        
        if(direction==Direction.UP) {
        	vals = rotateRight(vals);
        }
        else if(direction==Direction.RIGHT) {
        	vals = rotateRight(vals);
        	vals = rotateRight(vals);
        }
        else if(direction==Direction.DOWN) {
            vals = rotateLeft(vals);
        }
        
        if (!checkArrayEquality(vals, original))
        	return true;
        else return false;
    }
	
	public boolean gameOver(int[][] gameState) {
		boolean movePossible = false;
		for(Direction direction : Direction.values()){
			if (!checkArrayEquality(checkMove(gameState, direction), gameState))
				movePossible = true;
		}
		
		if (!movePossible)
			terminated = true;
		return !movePossible;
	}
	
	public Direction findBestMoveFirst(){
		
		Direction best = null;
		int bestValue = 0;
		
		for(Direction direction : Direction.values()){
				
			if(heuristic(checkMove(vals, direction))  > bestValue && !checkArrayEquality(checkMove(vals, direction), vals)){
				best = direction;
				bestValue = heuristic(checkMove(vals, direction)) + checkMoveScore(direction);
			}
			
//			System.out.println(direction.toString() + heuristic(checkMove(vals, direction)));
	
		}
		
		return best;
		
	}
	
//	public Direction findBestMove(){
//		
//		Direction best = null;
//		int bestValue = 0;
//		
//		for(Direction d : Direction.values()){
//			
//			int value = 0;
//			int[][] gameState = checkMove(vals, d);
//			
//			if (!checkArrayEquality(gameState, vals)){
//				
//				ArrayList<Point> possibles = new ArrayList<Point>();
//				
//				for (int i = 0; i < gameState.length; i++){
//					for (int j = 0; j < gameState[i].length; j++){
//						if(gameState[i][j] == 0)
//							possibles.add(new Point(i ,j));
//					}
//				}	
//				
//			
//				for (Point p : possibles){
//					
//					int[][] newState = gameState.clone();
//					newState[p.x][p.y] = 2;
//					value += heuristic(newState);
//				}
//				
//				if(value > bestValue){
//					bestValue = value;
//					best = d;
//				}
//			}
//		}
//		
//		
//		return best;
//		
//	}
	
	
	public Direction findBestMove(int depth){
		
		Direction best = null;
		int bestValue = 0;
		
		for(Direction d : Direction.values()){
			if (!checkArrayEquality(checkMove(vals, d), vals)){
				int value = evaluateMoves((checkMove(vals, d)), depth - 1);
				if (value > bestValue){
					best = d;
					bestValue = value;
				}
			}
		}
		
		return best;
		
	}
	
	public int evaluateMoves(int[][] state, int depth){
		
//		printArray(state);
		
		if (depth == 0)
			return heuristic(state);
		
		int sum = 0;
		ArrayList<Point> possibles = new ArrayList<Point>();
		ArrayList<int[][]> nodes = new ArrayList<int[][]>();
		
		for (int i = 0; i < state.length; i++){
			for (int j = 0; j < state[i].length; j++){
				if(state[i][j] == 0)
					possibles.add(new Point(i ,j));
			}
		}	
		
		for (Point p : possibles){
			int[][] newState = new int[4][4];
			for (int i = 0; i < state.length; i++){
				for (int j = 0; j < state[i].length; j++){
					newState[i][j] = state[i][j];
				}
			}	
			newState[p.x][p.y] = 2;
			nodes.add(newState);
		}
		
		for(int[][] s : nodes){
			for(Direction d : Direction.values()){
				
				sum += evaluateMoves(checkMove(s, d), depth-1);
				
			}
		}
		
		return sum/10;

	}

	public int heuristic(int[][] gameState){
		
		if(gameOver(gameState))
			return 0;
		
		int value = 0;
		
//		return smoothnessTwo(gameState) + smoothness(gameState) + monotonicWeight(gameState)/15; // 11229 with 3000*
		
//		return smoothnessTwo(gameState) + smoothness(gameState) + monotonicWeight(gameState)/15 + cornerWeight(gameState); // 13462 with 4000*
		
//		return (int) (smoothnessTwo(gameState)*1.2 + smoothness(gameState) + monotonicWeight(gameState)/15 + cornerWeight(gameState)); // 13486 with 4000*
		
//		return (int) (smoothnessTwo(gameState)*1.2 + smoothness(gameState) + monotonicWeight(gameState)/15 + cornerWeight(gameState)*1.3); // 13800 with 3000*
		
//		return (int) (smoothnessTwo(gameState)*1.2 + smoothness(gameState)  + cornerWeight(gameState)*1.3); // 17195 with 3000*
		

		value = (int) (smoothnessTwo(gameState)*1.2 + smoothness(gameState)*1 + cornerWeight(gameState)*1.6); // 18500 with 3000*
		
//		if (forceDownMove(gameState))
//			value *= .8;
	
		return value;
		
//		return smoothnessTwo(gameState)*10 + monotonicWeight(gameState); // 9312 with 3000*

//		return monotonicWeight(gameState); // 4068
			
	}
	
	
	public int topWeight(int[][] gameState) {
		int total = 0;
		for (int i = 0; i < gameState.length; i++){
			for (int j = 0; j < gameState[i].length; j++){
				total += gameState[i][j] * (100 - (i*3));
			}
		}
		return total;
	}
	
	public int smoothness(int[][] gameState) {
		int total = 0;
		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length-1; j++) {
				total += (Math.abs(gameState[i][j] - gameState[i][j+1]));
			}
		}
		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length-1; j++) {
				total += (Math.abs(gameState[j][i] - gameState[j+1][i]));
			}
		}
		
		return 10000-total;
	}
	
	public int smoothnessTwo(int[][] gameState){
		int total = 0;
		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length-1; j++) {
				if (gameState[i][j] == gameState[i][j+1]) 
					total += gameState[i][j];
			}
		}
		for(int i = 0; i < gameState.length; i++) {
			for(int j = 0; j < gameState.length-1; j++) {
				if (gameState[j][i] == gameState[j+1][i]) 
					total += gameState[j][i];
			}
		}
		
		return total*10;
	}
	
	public int monotonicWeight(int[][] gameState){
		int total = 0;
		for(int i = 0; i < gameState.length; i++) {
			if(gameState[i][0] <= gameState[i][1] && gameState[i][1] <= gameState[i][2] && gameState[i][2] <= gameState[i][3]) {
				int rowTotal = 0;
				for (int j = 0; j < gameState[i].length; j++) {
					rowTotal += gameState[i][j];
				}
				
				total += rowTotal;
			}
				
			if(gameState[i][0] >= gameState[i][1] && gameState[i][1] >= gameState[i][2] && gameState[i][2] >= gameState[i][3]) {
				int rowTotal = 0;
				for (int j = 0; j < gameState[i].length; j++) {
					rowTotal += gameState[i][j];
				}
				total+= rowTotal;
			}	
		}
		
		for(int i = 0; i < gameState.length; i++) {
			if(gameState[0][i] <= gameState[1][i] && gameState[1][i] <= gameState[2][i] && gameState[2][i] <= gameState[3][i]) {
				int rowTotal = 0;
				for (int j = 0; j < gameState[i].length; j++) {
					rowTotal += gameState[j][i];
				}
				
				total += rowTotal;
			}
				
			if(gameState[0][i] >= gameState[1][i] && gameState[1][0] >= gameState[2][i] && gameState[2][i] >= gameState[3][i]) {
				int rowTotal = 0;
				for (int j = 0; j < gameState[i].length; j++) {
					rowTotal += gameState[j][i];
				}
				total+= rowTotal;
			}
		}
		return total * 50;
	}
	
	public int cornerWeight(int[][] gameState){
		int total = 0; 
		
		for (int i = 0; i < gameState.length; i++){
			for (int j = 0; j < gameState[i].length; j++){
				
				total += gameState[i][j] * (1000 - (i*4 + j));
				
//				if (i == 0)
//					total += gameState[i][j] * (100 - (i*4 + j));
//				else if (i == 1)
//					total += gameState[i][j] * (100 - ((i) + (4-j)));
//				else
//					total += gameState[i][j] * (100 - (i*4 + j));
				
				
//				if(gameState[0][0] > 100  && gameState[0][1] > 100 && gameState[0][2] > 100  && gameState[0][3] > 60)
//					total += gameState[i][j] * (100 - ((i) + (4-j)));
//				else
//					total += gameState[i][j] * (100 - (i*4 + j));
				
				
				
//				if (rowFull(gameState, 0) && (i == 1 || (i == 2 && rowFull(gameState, 1)) && largestOnLeft(gameState, 0) && monotonic(gameState, 0) && test(gameState)))
//					total += gameState[i][j] * (100 - ((i) + (4-j)));
//				
//				else
//					total += gameState[i][j] * (100 - (i*4 + j));
			}
		}
		return total ;
	}
	
	public boolean forceDownMove(int[][] state){
		if (checkArrayEquality(checkMove(state, Direction.UP), state) && checkArrayEquality(checkMove(state, Direction.RIGHT), state) &&checkArrayEquality(checkMove(state, Direction.LEFT), state)) 
			return true;
		return false;
	}
	
	public boolean largestOnLeft(int[][] state, int row){
		if (state[row][0] > state[row][1] && state[row][0] > state[row][2] && state[row][0] > state[row][3])
			return true;
		return false;
	}
	
	public boolean largestOnRight(int[][] state, int row){
		if (state[row][3] > state[row][0] && state[row][3] > state[row][1] && state[row][3] > state[row][2])
			return true;
		return false;
	}
	
	public boolean monotonic(int[][] gameState, int row){
		if(gameState[row][0] <= gameState[row][1] && gameState[row][1] <= gameState[row][2] && gameState[row][2] <= gameState[row][3]) 
			return true;
		if(gameState[row][0] >= gameState[row][1] && gameState[row][1] >= gameState[row][2] && gameState[row][2] >= gameState[row][3]) 
			return true;
		return false;
	}
	
	public boolean rowFull(int[][] gameState, int row){
		if(gameState[row][0] != 0 && gameState[row][1] != 0 && gameState[row][2] != 0 && gameState[row][3] != 0)
			return true;
		return false;
	}
	
	public boolean test(int[][] state){
		if (state[1][3] != 0 && state[1][3] >= state[0][3])
			return false;
		
		else return true;
	}
	
	public int cornerWeightTwo(int[][] gameState){
		int total = 0;
		
		for (int i = 0; i < gameState.length; i++){
			for (int j = 0; j < gameState[i].length; j++){
				
				//corners
				if((i == 0 && j == 0)  || (i == 3 && j == 0) || (i == 0 && j ==3) || (i ==3 && j == 3))
					total += 3*gameState[i][j];
				
				//edges
				if((i == 1 && j == 0) || (i == 0 && j == 1) || (i == 2 && j == 0) || (i == 0 && j == 2) || (i == 3) && (j == 1) || (i == 3 && j == 2) || (j == 3) && (i == 1) || (j == 3 && i == 2))  
					total += 2* gameState[i][j];
				
			}
		}
		return total ;
		
	}
	
	
	public int scoreBlanks(int[][] array){
		int count = 0;
		for (int i = 0; i < array.length; i++){
			for (int j = 0; j < array[i].length; j++){
				if(array[i][j] == 0)
					count++;
			}
		}
		return (((16 - count) * score)/ 10);
	}
	
	
	public int[][] checkMove(int[][] gameState, Direction direction) {    
        int points = 0;
        
        int[][] newState = gameState.clone();
        
        
        if(direction==Direction.UP) {
            newState = rotateLeft(newState);
        }
        else if(direction==Direction.RIGHT) {
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        }
        else if(direction==Direction.DOWN) {
        	newState = rotateRight(newState);
        }
        else if(direction == Direction.LEFT){
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        }
        
        for(int i=0; i < 4;++i) {
            int lastMergePosition=0;
            for(int j=1;j<4;++j) {
                if(newState[i][j]==0) {
                    continue; 
                }
                
                int previousPosition = j-1;
                while(previousPosition>lastMergePosition && newState[i][previousPosition]==0) { //skip all the zeros
                    --previousPosition;
                }
                
                if(previousPosition==j) {
                    
                }
                else if(newState[i][previousPosition]==0) {
                    
                    newState[i][previousPosition]=newState[i][j];
                    newState[i][j]=0;
                }
                else if(newState[i][previousPosition]==newState[i][j]){
                    
                    newState[i][previousPosition]*=2;
                    newState[i][j]=0;
                    points+=newState[i][previousPosition];
                    lastMergePosition=previousPosition+1;
                    
                }
                else if(newState[i][previousPosition]!=newState[i][j] && previousPosition+1!=j){
                    newState[i][previousPosition+1]=newState[i][j];
                    newState[i][j]=0;
                }
            }
        }
        
        if(direction==Direction.UP) {
        	newState = rotateRight(newState);
        }
        else if(direction==Direction.RIGHT) {
            newState = rotateRight(newState);
            newState = rotateRight(newState);
        }
        else if(direction==Direction.DOWN) {
        	newState = rotateLeft(newState);
        }
        
        return newState;
    }
	
	public int checkMoveScore(Direction direction) {    
        int points = 0;
        
        int[][] newState = vals;
        
        if(direction==Direction.UP) {
            newState = rotateLeft(newState);
        }
        else if(direction==Direction.RIGHT) {
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        }
        else if(direction==Direction.DOWN) {
        	newState = rotateRight(newState);
        }
        else if(direction == Direction.LEFT){
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        	newState = rotateLeft(newState);
        }
        
        for(int i=0; i < 4;++i) {
            int lastMergePosition=0;
            for(int j=1;j<4;++j) {
                if(newState[i][j]==0) {
                    continue; 
                }
                
                int previousPosition = j-1;
                while(previousPosition>lastMergePosition && newState[i][previousPosition]==0) { //skip all the zeros
                    --previousPosition;
                }
                
                if(previousPosition==j) {
                  
                }
                else if(newState[i][previousPosition]==0) {
                 
                    newState[i][previousPosition]=newState[i][j];
                    newState[i][j]=0;
                }
                else if(newState[i][previousPosition]==newState[i][j]){
                   
                    newState[i][previousPosition]*=2;
                    newState[i][j]=0;
                    points+=newState[i][previousPosition];
                    lastMergePosition=previousPosition+1;
                    
                }
                else if(newState[i][previousPosition]!=newState[i][j] && previousPosition+1!=j){
                    newState[i][previousPosition+1]=newState[i][j];
                    newState[i][j]=0;
                }
            }
        }
        
        return points;
    }
	
	 private int[][] rotateLeft(int[][] board) {
	        int[][] rotatedBoard = new int[4][4];
	        
	        for(int i=0;i<4;++i) {
	            for(int j=0;j<4;++j) {
	                rotatedBoard[4-j-1][i] = board[i][j];
	            }
	        }
	        
	        return rotatedBoard;
	    }
	    
	    private int[][] rotateRight(int[][] board) {
	        int[][] rotatedBoard = new int[4][4];
	        
	        for(int i=0;i<4;++i) {
	            for(int j=0;j<4;++j) {
	                rotatedBoard[i][j]=board[4-j-1][i];
	            }
	        }
	        
	        return rotatedBoard;
	    }
	
	public boolean checkArrayEquality(int[][] array1, int[][] array2){
		if (array1.length != array2.length) 
			return false;
		for (int i = 0; i < array1.length; i++){
			if (array1[i].length != array2[i].length)
				return false;
		}
		
		for (int i = 0; i < array1.length; i++){
			for (int j = 0; j < array1[i].length; j++){
				if(array1[i][j] != array2[i][j])
					return false;
			}
		}
		
		return true;
	}
	
	public void printGameState(){
		for (int i = 0; i < vals.length; i++){
			System.out.print(" | ");
			
			for (int j = 0; j < vals[i].length; j++){
				
				System.out.print(vals[i][j] + "\t");
				System.out.print("| ");
			}
			System.out.println();
//			System.out.println();
		}
		System.out.println();
	}
	
	public void printArray(int[][] state){
		for (int i = 0; i < state.length; i++){
			System.out.print(" | ");
			
			for (int j = 0; j < state[i].length; j++){
				
				System.out.print(state[i][j] + "\t");
				System.out.print("| ");
			}
			System.out.println();
//			System.out.println();
		}
		System.out.println();
	}
	
	public String toString() {
		findMaxBlock();
		if(totalTime != null)
			return "#" + gameNumber + "\t" + "Score:\t" + Integer.valueOf(score)+ "\t"
				+ "Time:\t" + Integer.valueOf((int) totalTime.getTime()) + "\t"
				+ "Highest:\t" + highestBlock; 
		return "Score: " + Integer.valueOf(score);
	}
	
	public void findMaxBlock(){
		
		for (int i = 0; i < vals.length; i++){
			for (int j = 0; j < vals[i].length; j++){
				if (vals[i][j] > highestBlock) 
					highestBlock = vals[i][j];
			}
		}
	}

}
